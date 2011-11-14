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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.DispatcherAsync;
import com.prealpha.xylophone.shared.Result;
import com.prealpha.xylophone.shared.filter.CacheableAction;

/**
 * Implements a caching action filter, which stores the results of
 * {@link CacheableAction}s executed through the filter in an
 * {@link ActionCache} and returns them in response to future queries as long as
 * the result is in the cache. The {@code ActionCache} used by this filter is
 * passed to the constructor for this class, providing flexibility in the
 * caching implementation and strategy while retaining the same filter code.
 * <p>
 * 
 * Actions which do not implement {@code CacheableAction} are silently forwarded
 * to the backing dispatcher.
 * 
 * @author Meyer Kizner
 * @see CacheableAction
 * @see ActionCache
 * 
 */
public final class CachingActionFilter implements ActionFilter {
	/**
	 * The cache which this instance uses to store results.
	 */
	private final ActionCache cache;

	/**
	 * The backing dispatcher used to send uncacheable or uncached actions to
	 * the server. Until {@link #init(DispatcherAsync)} is called, this value is
	 * {@code null}.
	 */
	private DispatcherAsync dispatcher;

	/**
	 * Constructs a new, uninitialized {@code CachingActionFilter} using the
	 * specified cache.
	 * 
	 * @param cache
	 *            the cache to be used by this filter instance
	 */
	@Inject
	public CachingActionFilter(ActionCache cache) {
		this.cache = cache;
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
			final AsyncCallback<R> callback) {
		checkState(isInitialized());
		checkNotNull(action);
		checkNotNull(callback);
		if (action instanceof CacheableAction<?>) {
			final CacheableAction<R> cacheableAction = (CacheableAction<R>) action;
			R cachedResult = cache.get(cacheableAction);

			if (cachedResult == null) {
				dispatcher.execute(cacheableAction, new AsyncCallback<R>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(R result) {
						cache.put(cacheableAction, result);
						callback.onSuccess(result);
					}
				});
			} else {
				callback.onSuccess(cachedResult);
			}
		} else {
			dispatcher.execute(action, callback);
		}
	}
}
