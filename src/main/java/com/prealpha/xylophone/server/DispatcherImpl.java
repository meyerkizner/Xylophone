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

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.LinkedKeyBinding;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.ActionException;
import com.prealpha.xylophone.shared.Dispatcher;
import com.prealpha.xylophone.shared.HandlerNotFoundException;
import com.prealpha.xylophone.shared.Result;

/**
 * An implementation of {@link Dispatcher} designed for use with
 * {@link ActionModule}. {@code ActionModule} and its subclasses always bind the
 * {@code Dispatcher} interface to this class. Actions are handled using the
 * {@link ActionHandler} implementations bound using
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
final class DispatcherImpl implements Dispatcher {
	/**
	 * The injector we use to obtain action handlers.
	 */
	private final Injector injector;

	/**
	 * Constructs a new {@code DispatcherImpl}, using the specified
	 * {@link Injector} to obtain action handlers.
	 * 
	 * @param injector
	 *            the injector to use to obtain action handlers
	 */
	@Inject
	private DispatcherImpl(Injector injector) {
		this.injector = injector;
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
			return handler.execute(action, this);
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
}
