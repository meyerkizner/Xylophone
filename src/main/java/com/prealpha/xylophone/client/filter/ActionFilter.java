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

package com.prealpha.xylophone.client.filter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.DispatcherAsync;
import com.prealpha.xylophone.shared.Result;

/**
 * An extension of {@link DispatcherAsync} which is used to provide
 * optimizations for a backing dispatcher. In particular, an action filter
 * provides a layer of indirection to the
 * {@link #execute(Action, AsyncCallback)} method. This indirection allows an
 * action filter, or a "chain" of action filters, to provide services such as
 * result caching, merging of equivalent results, and execution of multiple
 * actions with a single server request.
 * 
 * @author Meyer Kizner
 * 
 */
public interface ActionFilter extends DispatcherAsync {
	/**
	 * Sets the dispatcher which this filter uses internally to send RPC
	 * requests to the server. An action filter may not be used before this
	 * method is called. This method should only be invoked once per
	 * {@code ActionFilter} instance.
	 * 
	 * @param dispatcher
	 *            the dispatcher this filter should use internally
	 * @throws NullPointerException
	 *             if {@code dispatcher} is {@code null}
	 * @throws IllegalStateException
	 *             if this method has already been called on this instance
	 */
	void init(DispatcherAsync dispatcher);

	/**
	 * Asynchronously executes an {@link Action}, returning to the callback a
	 * {@link Result} whose type is consistent with the action's type parameter.
	 * Unlike a normal {@link DispatcherAsync}, the filter does not necessarily
	 * send an RPC request to the server. Instead, it may return a result
	 * immediately or execute the RPC call through a layer of indirection.
	 * <p>
	 * 
	 * Each action filter maintains a {@link DispatcherAsync} internally which
	 * it uses to execute all calls requiring an RPC request. The internal
	 * dispatcher can be set with the {@link #init(DispatcherAsync)} method.
	 * Action filters should not be used before the dispatcher is set.
	 * <p>
	 * 
	 * Note that the internal dispatcher may itself be an action filter. A
	 * common use case for action filters is to set up a sequence of multiple
	 * filters followed by an actual dispatcher so that all executed actions are
	 * run through several optimizations.
	 * 
	 * @throws IllegalStateException
	 *             if the backing {@code DispatcherAsync} has not yet been
	 *             initialized with the {@link #init(DispatcherAsync)} method
	 */
	@Override
	<R extends Result> void execute(Action<R> action, AsyncCallback<R> callback);
}
