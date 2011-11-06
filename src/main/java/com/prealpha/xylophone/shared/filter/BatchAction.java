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
import com.prealpha.xylophone.shared.Action;

/**
 * An action class which combines multiple actions together as a batch, to be
 * executed separately on the server. The {@link BatchResult} will return
 * results in the same order as they are submitted to
 * {@linkplain #BatchAction(List) the constructor}.
 * 
 * @author Meyer Kizner
 * @see AbstractBatchingFilter
 * 
 */
public final class BatchAction implements Action<BatchResult> {
	/**
	 * The list of actions in this batch. Non-{@code final} to allow for GWT
	 * serialization, but never altered in practice.
	 */
	private ImmutableList<Action<?>> actions;

	// serialization support
	@SuppressWarnings("unused")
	private BatchAction() {
	}

	/**
	 * Constructs a new {@code BatchAction} from the specified list of actions.
	 * A copy of the provided list is created and stored internally.
	 * 
	 * @param actions
	 *            the actions to submit as a batch
	 */
	public BatchAction(List<? extends Action<?>> actions) {
		this.actions = ImmutableList.copyOf(actions);
	}

	/**
	 * @return the actions in this batch
	 */
	public List<Action<?>> getActions() {
		return actions;
	}
}
