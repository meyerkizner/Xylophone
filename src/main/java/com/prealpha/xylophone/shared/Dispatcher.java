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

package com.prealpha.xylophone.shared;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * The synchronous interface for event dispatching. Most users of this library
 * will not need to write an implementation of this interface.
 * 
 * @author Meyer Kizner
 * @see DispatcherAsync
 * 
 */
public interface Dispatcher extends RemoteService {
	/**
	 * Executes an {@link Action}, returning a {@link Result} whose type is
	 * consistent with the action's type parameter. If the action has partial
	 * results, none will be returned; the action will be executed until a
	 * complete result is obtained. Therefore, the result of this method will
	 * always be a {@linkplain Result#isComplete() complete} {@code Result}. An
	 * {@link ActionException} may be thrown if the action cannot be dispatched
	 * or executed for any reason.
	 * 
	 * @param <R>
	 *            the result type for the action
	 * @param action
	 *            an action to execute
	 * @return the complete result of the action
	 * @throws ActionException
	 *             thrown to indicate a problem during dispatch or execution
	 * @throws NullPointerException
	 *             if {@code action} is {@code null}
	 */
	<R extends Result> R execute(Action<R> action) throws ActionException;
}
