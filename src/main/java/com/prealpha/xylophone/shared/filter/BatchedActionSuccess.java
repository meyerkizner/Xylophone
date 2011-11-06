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
import com.prealpha.xylophone.shared.Result;

/**
 * Represents a successful result for an action executed as part of a
 * {@link BatchAction}.
 * 
 * @param <R>
 *            the result type for the action
 * @author Meyer Kizner
 * @see BatchedActionResult
 * @see BatchedActionFailure
 * 
 */
public final class BatchedActionSuccess<R extends Result> implements
		BatchedActionResult<R> {
	/**
	 * The result we are delivering back to the client. Non-{@code final} to
	 * allow for GWT serialization, but never altered in practice.
	 */
	private R result;

	// serialization support
	@SuppressWarnings("unused")
	private BatchedActionSuccess() {
	}

	/**
	 * Constructs a new {@code BatchedActionSuccess} object to encapsulate the
	 * specified result. The result object may be {@code null}.
	 * 
	 * @param result
	 *            the result to deliver
	 */
	public BatchedActionSuccess(R result) {
		this.result = result;
	}

	@Override
	public void deliver(AsyncCallback<? super R> callback) {
		callback.onSuccess(result);
	}
}
