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

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Maps;
import com.prealpha.xylophone.shared.Result;
import com.prealpha.xylophone.shared.filter.CacheableAction;

/**
 * A simple {@link ActionCache} implementation which stores entries in memory.
 * Entries are retained in the cache until an attempt at access is made or the
 * entry is removed using the {@link #remove(CacheableAction)} method. Because
 * the cache is maintained in client-side memory only, its contents are reset
 * when the page is refreshed. This class does not attempt to use any type of
 * persistent storage to maintain entries across multiple requests or pages.
 * 
 * @author Meyer Kizner
 * 
 */
public final class MemoryActionCache implements ActionCache {
	/**
	 * Map of cacheable actions to cached results. Entries are retained until
	 * they are removed or an attempt at accessing an expired entry is made.
	 * This requires removing expired entries from the map in the
	 * {@link #get(CacheableAction)} method.
	 */
	private final Map<CacheableAction<?>, Result> cache;

	/**
	 * Constructs a new {@code MemoryActionCache} containing no entries.
	 */
	public MemoryActionCache() {
		cache = Maps.newHashMap();
	}

	@Override
	public <R extends Result> R get(CacheableAction<R> action) {
		checkNotNull(action);
		if (cache.containsKey(action)) {
			R result = getValidResult(action);
			if (result == null) {
				cache.remove(result);
			} else {
				return result;
			}
		}

		// no cache hit or the hit was expired
		return null;
	}

	@Override
	public <R extends Result> void put(CacheableAction<R> action, R result) {
		checkNotNull(action);
		checkNotNull(result);
		cache.put(action, result);
	}

	@Override
	public void remove(CacheableAction<?> action) {
		checkNotNull(action);
		cache.remove(action);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * In the implementation of this method in {@code MemoryActionCache}, none
	 * of the other cache methods (including {@link #get(CacheableAction)}) can
	 * be called while iteration is in progress. In general, attempting to
	 * iterate during such method calls will result in failure.
	 * <p>
	 * 
	 * The {@link Iterator#remove()} method on returned iterators is not
	 * supported.
	 */
	@Override
	public Iterator<CacheableAction<?>> iterator() {
		return new AbstractIterator<CacheableAction<?>>() {
			private final Iterator<CacheableAction<?>> delegate = cache
					.keySet().iterator();

			@Override
			protected CacheableAction<?> computeNext() {
				while (delegate.hasNext()) {
					CacheableAction<?> action = delegate.next();
					if (getValidResult(action) == null) {
						delegate.remove();
					} else {
						return action;
					}
				}
				return endOfData();
			}
		};
	}

	/**
	 * Returns the valid, unexpired result associated with the action, if one is
	 * in the cache.
	 * 
	 * @param action
	 *            the action to look up
	 * @return a valid, unexpired result for the action, or {@code null} if none
	 *         exists
	 */
	private <R extends Result> R getValidResult(CacheableAction<R> action) {
		/*
		 * The map is guaranteed to contain results of the same type as the type
		 * parameter of each respective key. The guarantee is enforced in the
		 * put() method, which is the only place where mappings are added or
		 * modified.
		 */
		@SuppressWarnings("unchecked")
		R result = (R) cache.get(action);
		long expiry = (result == null ? 0 : action.getCacheExpiry(result));

		if (System.currentTimeMillis() < expiry) {
			// the result is valid
			return result;
		} else {
			// the result is expired
			return null;
		}
	}
}
