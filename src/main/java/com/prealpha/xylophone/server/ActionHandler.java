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

import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.ActionException;
import com.prealpha.xylophone.shared.Dispatcher;
import com.prealpha.xylophone.shared.Result;

/**
 * An interface for objects which execute a specific {@link Action} type. As
 * such, they need only handle that action type and return results of the
 * corresponding result type. The implementation of {@link Dispatcher} provided
 * by {@link ActionModule} uses action handlers to perform all action execution.
 * 
 * @param <A>
 *            the action type handled
 * @param <R>
 *            the result type for the action
 * @author Meyer Kizner
 * @see ActionModule
 * 
 */
public interface ActionHandler<A extends Action<R>, R extends Result> {
	/**
	 * Executes an {@link Action} appropriate to this handler, returning a
	 * {@link Result} whose type is consistent with the action's type parameter.
	 * An {@link ActionException} may be thrown if the action cannot be
	 * dispatched or executed for any reason. A {@link Dispatcher} is provided
	 * so that the handler may reuse other actions as a means to provide a
	 * correct result for its own.
	 * 
	 * @param action
	 *            an action to execute
	 * @param dispatcher
	 *            a {@code Dispatcher} which may be used to execute other
	 *            actions as necessary
	 * @return the result of the action
	 * @throws ActionException
	 *             thrown to indicate a problem during dispatch or execution
	 */
	R execute(A action, Dispatcher dispatcher) throws ActionException;
}
