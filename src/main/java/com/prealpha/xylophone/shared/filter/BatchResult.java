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

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.prealpha.xylophone.client.filter.AbstractBatchingFilter;
import com.prealpha.xylophone.shared.Result;

/**
 * The result class for {@link BatchAction}. Results are returned, as
 * {@link BatchedActionResult} instances, in the same order as the corresponding
 * actions were submitted in {@code BatchAction}.
 * 
 * @author Meyer Kizner
 * @see AbstractBatchingFilter
 * 
 */
public final class BatchResult implements Result {
	/**
	 * The list of results in this batch. Non-{@code final} to allow for GWT
	 * serialization, but never altered in practice.
	 */
	private ImmutableList<BatchedActionResult<?>> results;

	// serialization support
	@SuppressWarnings("unused")
	private BatchResult() {
	}

	/**
	 * Constructs a new {@code BatchResult} from the specified list of results.
	 * A copy of the provided list is created and stored internally.
	 * 
	 * @param results
	 *            the results to return as a batch
	 */
	public BatchResult(List<? extends BatchedActionResult<?>> results) {
		this.results = ImmutableList.copyOf(results);
	}

	/**
	 * @return the results in this batch
	 */
	public List<BatchedActionResult<?>> getResults() {
		return results;
	}
}
