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

import com.prealpha.xylophone.client.filter.ActionCache;
import com.prealpha.xylophone.client.filter.CachingActionFilter;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.Result;

/**
 * An interface for actions which may be cached in an {@link ActionCache},
 * usually by a {@link CachingActionFilter}. As a result, the cache will be
 * allowed to return the same results for interchangeable actions. Two cacheable
 * actions are considered interchangeable only if they are equal according to
 * the {@link #equals(Object)} method. Generally, actions which fetch data from
 * the server without modifying the server's state are good candidates for
 * implementing {@code CacheableAction}.
 * <p>
 * 
 * Any code which makes use of {@code CacheableAction} should honor the
 * {@link #getCacheExpiry(Result)} method.
 * 
 * @param <R>
 *            the result type for the action
 * @author Meyer Kizner
 * @see ActionCache
 * @see CachingActionFilter
 * 
 */
public interface CacheableAction<R extends Result> extends Action<R> {
	/**
	 * Returns the expiry time for a cached result, expressed as an absolute
	 * timestamp in milliseconds since the epoch. Caches must not return results
	 * whose expiry times are after the current time.
	 * 
	 * @param result
	 *            the result which should be checked for expiry
	 * @return the expiry time for {@code result}, in milliseconds since the
	 *         epoch
	 */
	long getCacheExpiry(R result);
}
