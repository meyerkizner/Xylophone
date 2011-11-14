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

package com.prealpha.xylophone.server.filter;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.prealpha.xylophone.server.ActionHandler;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.ActionException;
import com.prealpha.xylophone.shared.Dispatcher;
import com.prealpha.xylophone.shared.Result;
import com.prealpha.xylophone.shared.filter.BatchAction;
import com.prealpha.xylophone.shared.filter.BatchResult;
import com.prealpha.xylophone.shared.filter.BatchedActionFailure;
import com.prealpha.xylophone.shared.filter.BatchedActionResult;
import com.prealpha.xylophone.shared.filter.BatchedActionSuccess;

/**
 * The server-side handler for {@link BatchAction}. This handler is essentially
 * an internal implementation detail of {@link BatchActionModule}, and should
 * only be used through that module.
 * 
 * @author Meyer Kizner
 * @see BatchActionModule
 * 
 */
final class BatchActionHandler implements
		ActionHandler<BatchAction, BatchResult> {
	/**
	 * The dispatcher we use to execute batched actions.
	 */
	private final Dispatcher dispatcher;

	/**
	 * Constructs a new {@code BatchActionHandler}, using the specified
	 * {@link Dispatcher} to execute batched actions.
	 * 
	 * @param dispatcher
	 *            the dispatcher with which this handler should execute batched
	 *            actions
	 */
	@Inject
	private BatchActionHandler(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public BatchResult execute(BatchAction action) throws ActionException {
		List<BatchedActionResult<?>> results = Lists.transform(
				action.getActions(),
				new Function<Action<?>, BatchedActionResult<?>>() {
					@Override
					public BatchedActionResult<?> apply(Action<?> action) {
						return executeBatched(action);
					}
				});
		return new BatchResult(results);
	}

	/**
	 * Executes a batched action, returning a result in the form of a
	 * {@link BatchedActionResult}. This method must be separate so that it can
	 * guarantee a result with a proper type parameter, one matching the result
	 * type for the action.
	 * 
	 * @param action
	 *            the action to execute
	 * @return either a result object or an exception, in the form of a
	 *         {@code BatchedActionResult}
	 */
	private <R extends Result> BatchedActionResult<R> executeBatched(
			Action<R> action) {
		try {
			R result = dispatcher.execute(action);
			return new BatchedActionSuccess<R>(result);
		} catch (ActionException ax) {
			return new BatchedActionFailure<R>(ax);
		}
	}
}
