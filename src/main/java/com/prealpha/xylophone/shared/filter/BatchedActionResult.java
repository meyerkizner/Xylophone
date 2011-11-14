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

package com.prealpha.xylophone.shared.filter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.prealpha.xylophone.shared.Result;

/**
 * Represents the result, success or failure, of an action submitted as part of
 * a {@link BatchAction}. On the client side, the callback for the action is
 * passed into the {@code BatchedActionResult}, rather than submitting the
 * result to the callback.
 * 
 * @param <R>
 *            the result type for the action
 * @author Meyer Kizner
 * @see BatchedActionSuccess
 * @see BatchedActionFailure
 * 
 */
public interface BatchedActionResult<R extends Result> extends IsSerializable {
	/**
	 * Delivers the result to the callback. If the result is a success, the
	 * delivery will usually involve calling
	 * {@link AsyncCallback#onSuccess(Object)}; if the result is a failure, it
	 * will usually involve calling {@link AsyncCallback#onFailure(Throwable)}.
	 * 
	 * @param callback
	 *            the callback for the action
	 */
	void deliver(AsyncCallback<? super R> callback);
}
