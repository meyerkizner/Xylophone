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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous version of {@link Dispatcher}.
 * 
 * @author Meyer Kizner
 * @see Dispatcher
 * 
 */
public interface DispatcherAsync {
	/**
	 * The asynchronous version of {@link Dispatcher#execute(Action)}.
	 * 
	 * @param <R>
	 *            the result type for the action
	 * @param action
	 *            an action to execute
	 * @param callback
	 *            a callback to receive the complete result of the action
	 * @throws NullPointerException
	 *             if either {@code action} or {@code callback} is {@code null}
	 * @see Dispatcher#execute(Action)
	 */
	<R extends Result> void execute(Action<R> action, AsyncCallback<R> callback);
}
