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

import static com.google.common.base.Preconditions.*;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prealpha.xylophone.shared.Result;

/**
 * Represents a failure for an action executed as part of a {@link BatchAction}.
 * In this case, a failure means that the action handler method threw an
 * exception, checked or unchecked.
 * 
 * @param <R>
 *            the result type for the action
 * @author Meyer Kizner
 * @see BatchedActionResult
 * @see BatchedActionSuccess
 * 
 */
public final class BatchedActionFailure<R extends Result> implements
		BatchedActionResult<R> {
	/**
	 * The exception we are delivering back to the client. Non-{@code final} to
	 * allow for GWT serialization, but never altered in practice.
	 */
	private Throwable exception;

	// serialization support
	@SuppressWarnings("unused")
	private BatchedActionFailure() {
	}

	/**
	 * Constructs a new {@code BatchedActionFailure} object to encapsulate the
	 * specified {@code Throwable}. The exception may not be null.
	 * 
	 * @param exception
	 *            the exception to deliver
	 */
	public BatchedActionFailure(Throwable exception) {
		checkNotNull(exception);
		this.exception = exception;
	}

	@Override
	public void deliver(AsyncCallback<? super R> callback) {
		callback.onFailure(exception);
	}
}
