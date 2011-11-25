/*
 * Copyright 2011 Meyer Kizner
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prealpha.xylophone.server;

import static com.google.common.base.Preconditions.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.AsyncContext;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.LinkedKeyBinding;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.ActionException;
import com.prealpha.xylophone.shared.Dispatcher;
import com.prealpha.xylophone.shared.HandlerNotFoundException;
import com.prealpha.xylophone.shared.PublishingDispatcher;
import com.prealpha.xylophone.shared.Result;

/**
 * An implementation of {@link Dispatcher} and {@link PublishingDispatcher}
 * designed for use with {@link ActionModule}. {@code ActionModule} and its
 * subclasses always bind the {@code Dispatcher} and
 * {@code PublishingDispatcher} interfaces to this class. Actions are handled
 * using the {@link ActionHandler} implementations bound using
 * {@link ActionModule#bindAction(Class)}.
 * <p>
 * 
 * This dispatcher's mechanism for locating handlers is highly specific to the
 * implementation of {@code bindAction} in {@code ActionModule}. As a result,
 * this class and {@code ActionModule} should be considered a single unit to be
 * used together; this class is essentially an implementation detail of
 * {@code ActionModule}.
 * 
 * @author Meyer Kizner
 * 
 */
final class PublishingDispatcherImpl implements PublishingDispatcher {
	/**
	 * The injector we use to obtain action handlers.
	 */
	private final Injector injector;

	/**
	 * The provider we use to obtain active {@code AsyncContext} instances. Note
	 * that the provider is necessary because {@code AsyncContext} inherently
	 * must be request scoped.
	 * 
	 * @see ActionModule#getAsyncContext(javax.servlet.http.HttpServletRequest)
	 */
	private final Provider<AsyncContext> contextProvider;

	/**
	 * A map of subscription IDs to active subscription objects. Canceled
	 * subscriptions are removed from the map so that they can be garbage
	 * collected. The map must be thread safe to ensure the thread safety of the
	 * {@code Dispatcher}.
	 */
	private final ConcurrentMap<Long, Subscription> subscriptions;

	/**
	 * The next unique subscription ID which should be assigned.
	 */
	private final AtomicLong nextSubscriptionId;

	/**
	 * Constructs a new {@code DispatcherImpl}, using the specified
	 * {@link Injector} to obtain action handlers and the specified provider to
	 * obtain {@code AsyncContext} instances.
	 * 
	 * @param injector
	 *            the injector to use to obtain action handlers
	 * @param contextProvider
	 *            the provider to use to obtain active {@code AsyncContext}
	 *            instances
	 */
	@Inject
	private PublishingDispatcherImpl(Injector injector,
			Provider<AsyncContext> contextProvider) {
		this.injector = injector;
		this.contextProvider = contextProvider;
		subscriptions = new MapMaker().makeMap();
		nextSubscriptionId = new AtomicLong();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * The implementation of this method in {@code DispatcherImpl} locates an
	 * {@link ActionHandler} for the action, which it assumes was already bound
	 * using {@link ActionModule#bindAction(Class)}. If no handler can be found,
	 * {@link HandlerNotFoundException} is thrown.
	 * 
	 * @throws HandlerNotFoundException
	 *             if no {@code ActionHandler} could be found for the action
	 */
	@Override
	public <R extends Result> R execute(Action<R> action)
			throws ActionException {
		ActionHandler<Action<R>, R> handler = locateHandler(action);
		if (handler != null) {
			R result;
			do {
				result = handler.execute(action);
				for (Subscription subscription : subscriptions.values()) {
					subscription.publish(action, result);
				}
			} while (!result.isComplete());
			return result;
		} else {
			throw new HandlerNotFoundException(action);
		}
	}

	/**
	 * Locates the handler required to execute the specified action. The handler
	 * is located with the assumption that it was already bound using
	 * {@link ActionModule#bindAction(Class)}. If no handler can be found, the
	 * result is {@code null}.
	 * 
	 * @param action
	 *            the action to execute
	 * @return an {@code ActionHandler} bound to execute the action, or
	 *         {@code null} if none could be located
	 */
	/*
	 * See the implementation comment for ActionModule.bindAction(Class).
	 */
	/*
	 * Warnings are suppressed because generics are essentially ignored by
	 * Guice's classes. All keys and bindings for Action are treated as if there
	 * were no type parameter; no distinction is made between actions with
	 * different type parameters. The only place where the warning would be
	 * relevant is in the return statement, where an ActionHandler without type
	 * parameters is implicitly converted to an ActionHandler<A, R>. However,
	 * the implementation of ActionModule.bindAction(Class) ensures that this
	 * conversion is always valid.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <A extends Action<R>, R extends Result> ActionHandler<A, R> locateHandler(
			A action) {
		Annotation annotation = null;
		List<Binding<Action>> bindings = injector
				.findBindingsByType(TypeLiteral.get(Action.class));
		for (Binding<Action> binding : bindings) {
			LinkedKeyBinding<Action> linkedBinding = (LinkedKeyBinding<Action>) binding;
			Key<? extends Action> linkedKey = linkedBinding.getLinkedKey();
			if (linkedKey.getTypeLiteral().getRawType() == action.getClass()) {
				annotation = linkedBinding.getKey().getAnnotation();
			}
		}

		if (annotation != null) {
			Key<ActionHandler> key = Key.get(ActionHandler.class, annotation);
			return injector.getInstance(key);
		} else {
			return null;
		}
	}

	@Override
	public long subscribe(Predicate<? super Action<?>> predicate) {
		Subscription subscription = new Subscription(predicate);
		long subscriptionId = nextSubscriptionId.getAndIncrement();
		subscriptions.put(subscriptionId, subscription);
		return subscriptionId;
	}

	@Override
	public ImmutableList<Result> check(long subscriptionId) {
		Subscription subscription = subscriptions.get(subscriptionId);
		checkArgument(subscription != null);
		return subscription.check();
	}

	@Override
	public void cancel(long subscriptionId) {
		Subscription subscription = subscriptions.remove(subscriptionId);
		checkArgument(subscription != null);
		subscription.cancel();
	}

	/**
	 * Stores the state associated with a specific subscription.
	 * 
	 * @author Meyer Kizner
	 * 
	 */
	private final class Subscription {
		/**
		 * Published results must originate from an action which matches this
		 * predicate. Otherwise, the result is ignored by this subscription.
		 */
		private final Predicate<? super Action<?>> predicate;

		/**
		 * A list of results which match the predicate and have been published,
		 * but have not yet been sent to the client through the {@link #check()}
		 * method.
		 */
		private final List<Result> published;

		/**
		 * The {@code AsyncContext} which is currently waiting for results to be
		 * published. If no request is waiting, this field is {@code null}.
		 */
		private AsyncContext waiting;

		/**
		 * Constructs a new {@code Subscription} which uses the specified
		 * predicate to determine which actions' results are to be included.
		 * 
		 * @param predicate
		 *            a predicate to limit action results which are included
		 */
		private Subscription(Predicate<? super Action<?>> predicate) {
			checkNotNull(predicate);
			this.predicate = predicate;
			published = Lists.newArrayList();
		}

		/**
		 * Indicates to this subscription object that the specified result has
		 * been published as a result of the specified action. If the action
		 * does not match this subscription's predicate, the result will be
		 * ignored. If it does match, the result will be added to the list of
		 * pending results, and if there is a request awaiting new results, it
		 * will be awakened.
		 * 
		 * @param action
		 *            the action whose execution resulted in {@code result}
		 * @param result
		 *            the result which was published
		 */
		private synchronized void publish(Action<?> action, Result result) {
			if (predicate.apply(action)) {
				published.add(result);
				if (waiting != null) {
					waiting.dispatch();
					waiting = null;
				}
			}
		}

		/**
		 * Returns an {@code ImmutableList} containing all {@code Result}
		 * objects which have been published but which have not yet been
		 * returned through this method. However, if that list is empty, an
		 * {@link AsyncContext} is created for the current request, and the
		 * request is suspended until a result is published, the subscription is
		 * canceled, or a new request takes the place of the current request in
		 * awaiting results.
		 * 
		 * @return a list of {@code Result} objects which have been published
		 *         but not yet sent
		 */
		private synchronized ImmutableList<Result> check() {
			if (published.isEmpty()) {
				if (waiting != null) {
					waiting.complete();
				}
				waiting = contextProvider.get();
			}
			ImmutableList<Result> toReturn = ImmutableList.copyOf(published);
			published.clear();
			return toReturn;
		}

		/**
		 * Cancels this subscription, releasing the currently waiting request.
		 * That request will return an empty list of results.
		 */
		private synchronized void cancel() {
			if (waiting != null) {
				waiting.complete();
				waiting = null;
			}
		}
	}
}
