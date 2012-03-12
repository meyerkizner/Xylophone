/*
 * Copyright 2012 Meyer Kizner
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

package com.prealpha.xylophone.client.filter;

import static com.google.common.base.Preconditions.*;

import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.DispatcherAsync;
import com.prealpha.xylophone.shared.PublishingDispatcherAsync;
import com.prealpha.xylophone.shared.Result;

/**
 * An {@link ActionFilter} chain, itself a filter which is usually backed by a
 * non-filter {@link DispatcherAsync}. Filter chains perform no processing of
 * their own, instead passing actions onto a provided list of included action
 * filters, each of which is the previous filter's backing dispatcher. Chains
 * must be provided a list of uninitialized action filters, and then initialized
 * with an actual {@code DispatcherAsync} capable of sending actions to the
 * server. Once initialization is complete, each action sent to the chain will
 * be passed to the first filter, which will then pass it to the second, and so
 * on until reaching the chain's backing dispatcher.
 * <p>
 * 
 * As of 0.3.1-beta, {@code FilterChain} implements
 * {@link PublishingDispatcherAsync} and supports
 * {@linkplain #init(PublishingDispatcherAsync) initialization} with a
 * publishing dispatcher. If a chain is initialized with a publishing
 * dispatcher, the implementations of
 * {@link PublishingDispatcherAsync#subscribe(Predicate, AsyncCallback)
 * subscribe}, {@link PublishingDispatcherAsync#check(long, AsyncCallback)
 * check}, and {@link PublishingDispatcherAsync#cancel(long, AsyncCallback)}
 * will delegate to the backing publishing dispatcher. Note, however, that
 * {@link ActionFilter} does not extend {@code PublishingDispatcherAsync}, so
 * the elements of the chain themselves will be unable to interact with these
 * method calls in any way.
 * 
 * @author Meyer Kizner
 * 
 */
public final class FilterChain implements ActionFilter,
		PublishingDispatcherAsync {
	/**
	 * The list of filters. Incoming actions are sent to the first element in
	 * the list, and the chain proceeds in the list's order. May be empty, but
	 * not {@code null}.
	 */
	private final List<ActionFilter> filters;

	/**
	 * The head dispatcher. If the filter chain is empty, this is the backing
	 * dispatcher which directly executes actions on the server. Otherwise, it
	 * is the same instance as {@code filters.get(0)}.
	 * <p>
	 * 
	 * When the chain has not been initialized, this field is {@code null}. The
	 * field is used as a flag to determine whether or not the chain has been
	 * initialized.
	 */
	private DispatcherAsync head;

	/**
	 * If this filter chain was {@linkplain #init(PublishingDispatcherAsync)
	 * initialized} with a {@code PublishingDispatcherAsync}, that dispatcher
	 * will be stored here so that the publishing methods can be delegated. If
	 * this chain is uninitialized or initialized with another dispatcher, this
	 * field is {@code null}.
	 * 
	 * @since 0.3.1-beta
	 */
	private PublishingDispatcherAsync publisher;

	/**
	 * Constructs a new {@code FilterChain} with the specified list of action
	 * filters. The first filter in the list is the first filter which will
	 * receive actions sent to this chain. The last filter in the list will send
	 * executed actions directly to the server.
	 * 
	 * @param filters
	 *            the list of filters in this chain
	 * @throws NullPointerException
	 *             if {@code filters} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code filters} contains {@code null} elements or filters
	 *             which have already been initialized
	 */
	public FilterChain(List<? extends ActionFilter> filters) {
		checkNotNull(filters);
		checkArgument(!filters.contains(null));
		for (ActionFilter filter : filters) {
			checkArgument(!filter.isInitialized());
		}
		this.filters = ImmutableList.copyOf(filters);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * Additionally, when this method is called on a {@code FilterChain}, the
	 * {@link ActionFilter#init(DispatcherAsync)} method will be called for all
	 * filters in the filter chain. The last filter in the chain will be
	 * initialized with the argument to this method. Each other filter will be
	 * initialized with the filter which follows it in the chain; so the first
	 * filter will be initialized with the second, the second with the third,
	 * and so on.
	 * 
	 * @throws IllegalStateException
	 *             {@inheritDoc}, or if any filters in this chain are already
	 *             initialized
	 * @see #init(PublishingDispatcherAsync)
	 */
	@Override
	public void init(DispatcherAsync dispatcher) {
		checkNotNull(dispatcher);
		checkState(!isInitialized());
		ListIterator<ActionFilter> i1 = filters.listIterator(filters.size());
		head = dispatcher;
		while (i1.hasPrevious()) {
			ActionFilter filter = i1.previous();
			filter.init(head);
			head = filter;
		}
	}

	/**
	 * Initializes this filter chain with the publishing dispatcher provided, in
	 * exactly the same manner as by a call to {@link #init(DispatcherAsync)}.
	 * However, the publishing dispatcher will be used to execute calls to the
	 * {@code PublishingDispatcherAsync} methods of the chain. If an ordinary
	 * dispatcher is passed for initialization, those methods will throw
	 * {@code UnsupportedOperationException}.
	 * 
	 * @param publisher
	 *            the dispatcher and publisher this filter should use internally
	 * @throws IllegalStateException
	 *             if this method has already been called on this instance, or
	 *             if any filters in this chain are already initialized
	 * @see #init(DispatcherAsync)
	 * @since 0.3.1-beta
	 */
	public void init(PublishingDispatcherAsync publisher) {
		init(publisher);
		this.publisher = publisher;
	}

	@Override
	public boolean isInitialized() {
		return (head != null);
	}

	@Override
	public <R extends Result> void execute(Action<R> action,
			AsyncCallback<R> callback) {
		checkState(isInitialized());
		head.execute(action, callback);
	}

	/**
	 * @since 0.3.1-beta
	 */
	@Override
	public void subscribe(Predicate<? super Action<?>> predicate,
			AsyncCallback<Long> callback) {
		checkState(isInitialized());
		if (publisher != null) {
			publisher.subscribe(predicate, callback);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @since 0.3.1-beta
	 */
	@Override
	public void check(long subscriptionId,
			AsyncCallback<ImmutableList<Result>> callback) {
		checkState(isInitialized());
		if (publisher != null) {
			publisher.check(subscriptionId, callback);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @since 0.3.1-beta
	 */
	@Override
	public void cancel(long subscriptionId, AsyncCallback<Void> callback) {
		checkState(isInitialized());
		if (publisher != null) {
			publisher.cancel(subscriptionId, callback);
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
