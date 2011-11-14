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

import static com.google.common.base.Preconditions.*;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.DispatcherAsync;
import com.prealpha.xylophone.shared.Result;
import com.prealpha.xylophone.shared.filter.MergeableAction;

/**
 * An {@link ActionFilter} which merges identical actions implementing
 * {@link MergeableAction} into a single-server side request. Two actions are
 * considered identical if they are {@linkplain Object#equals(Object) equal}. In
 * effect, if an identical mergeable action is submitted through this filter
 * while one is already in progress, the second action will not be submitted to
 * the server, and instead its callback will receive the result object returned
 * from the first action. This filter does not cache action results for actions
 * which are already complete.
 * 
 * @author Meyer Kizner
 * @see MergeableAction
 * 
 */
public final class MergingActionFilter implements ActionFilter {
	/**
	 * A map of active (incomplete) mergeable actions to the corresponding
	 * {@link MergedCallback} objects. This map allows us to effectively add new
	 * callbacks to an action without re-submitting it.
	 */
	private final Map<MergeableAction<?>, MergedCallback<?>> active;

	/**
	 * The backing dispatcher for this filter.
	 */
	private DispatcherAsync dispatcher;

	/**
	 * Constructs a new, uninitialized {@code MergingActionFilter}.
	 */
	public MergingActionFilter() {
		active = Maps.newHashMap();
	}

	@Override
	public void init(DispatcherAsync dispatcher) {
		checkNotNull(dispatcher);
		checkState(!isInitialized());
		this.dispatcher = dispatcher;
	}

	@Override
	public boolean isInitialized() {
		return (dispatcher != null);
	}

	@Override
	public <R extends Result> void execute(Action<R> action,
			AsyncCallback<R> callback) {
		checkNotNull(action);
		checkNotNull(callback);
		checkState(isInitialized());
		if (action instanceof MergeableAction) {
			MergeableAction<R> mergeableAction = (MergeableAction<R>) action;
			if (active.containsKey(mergeableAction)) {
				/*
				 * The map is guaranteed to contain callbacks with the same
				 * result type as the corresponding actions. The else block
				 * below honors this guarantee; it is the only place where
				 * mappings are added or modified.
				 */
				@SuppressWarnings("unchecked")
				MergedCallback<R> mergedCallback = (MergedCallback<R>) active
						.get(mergeableAction);
				mergedCallback.addCallback(callback);
			} else {
				MergedCallback<R> mergedCallback = new MergedCallback<R>(
						mergeableAction);
				active.put(mergeableAction, mergedCallback);
				mergedCallback.addCallback(callback);
				dispatcher.execute(mergeableAction, mergedCallback);
			}
		} else {
			dispatcher.execute(action, callback);
		}
	}

	/**
	 * A helper class which is used as the "actual" callback sent to the server
	 * when a mergeable action is submitted. This allows the filter to "add"
	 * additional callbacks to the original without re-submitting the action.
	 * When the action is complete, {@code MergedCallback} removes itself and
	 * its action from the {@link #active} map. All registered callbacks
	 * (submitted through {@link #execute(Action, AsyncCallback)}) are then
	 * notified of the result.
	 * 
	 * @param <R>
	 *            the result type for the action
	 * @author Meyer Kizner
	 * 
	 */
	private final class MergedCallback<R extends Result> implements
			AsyncCallback<R> {
		/**
		 * The action submitted along with this callback.
		 */
		private final MergeableAction<R> action;

		/**
		 * The set of callbacks which will be notified when this action is
		 * complete.
		 */
		private final Set<AsyncCallback<? super R>> callbacks;

		/**
		 * Constructs a new {@code MergedCallback} for the specified
		 * {@link MergeableAction}. The result type for the action must be
		 * identical to the result type for this callback.
		 * 
		 * @param action
		 *            the action submitted with this callback
		 */
		public MergedCallback(MergeableAction<R> action) {
			checkNotNull(action);
			this.action = action;
			callbacks = Sets.newHashSet();
		}

		/**
		 * Adds a callback to the set of callbacks which will be notified when
		 * the corresponding action is complete.
		 * 
		 * @param callback
		 *            the callback to add
		 */
		public void addCallback(AsyncCallback<? super R> callback) {
			checkNotNull(callback);
			checkArgument(callback != this);
			callbacks.add(callback);
		}

		@Override
		public void onFailure(Throwable caught) {
			active.remove(action);
			for (AsyncCallback<? super R> callback : callbacks) {
				callback.onFailure(caught);
			}
		}

		@Override
		public void onSuccess(R result) {
			active.remove(action);
			for (AsyncCallback<? super R> callback : callbacks) {
				callback.onSuccess(result);
			}
		}
	}
}
