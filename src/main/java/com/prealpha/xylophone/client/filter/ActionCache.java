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

import java.util.Iterator;

import com.prealpha.xylophone.shared.Result;
import com.prealpha.xylophone.shared.filter.CacheableAction;

/**
 * A common interface used by {@link CachingActionFilter} to potentially enable
 * the use of a variety of caching strategies. By binding an implementation of
 * this interface with GIN, the caching filter can be made to use a different
 * caching strategy. A simple default implementation is provided in
 * {@link MemoryActionCache}.
 * <p>
 * 
 * Implementations of this interface use the {@link Object#equals(Object)}
 * method to determine whether two actions are equal. Any actions which might be
 * cached should implement these methods correctly.
 * <p>
 * 
 * If a result is not removed beforehand, it is considered to have expired when
 * {@link System#currentTimeMillis()} is greater than or equal to
 * {@link CacheableAction#getCacheExpiry(Result)}. The cache will not return
 * expired results through the {@link #get(CacheableAction)} or
 * {@link #iterator()} methods.
 * <p>
 * 
 * This interface uses {@code null} as a marker for actions and results which
 * are not in the cache. As a result, all methods of this interface which take
 * arguments throw {@link NullPointerException} when any one of them is
 * {@code null}. This exception is repeated in each method specification.
 * 
 * @author Meyer Kizner
 * @see CacheableAction
 * @see CachingActionFilter
 * @see MemoryActionCache
 * 
 */
public interface ActionCache extends Iterable<CacheableAction<?>> {
	/**
	 * Returns the result stored with the action provided. If there is no result
	 * in the cache, {@code null} is returned; valid results are guaranteed to
	 * be non-{@code null}. If the cached result has expired, as determined by
	 * the {@link CacheableAction#getCacheExpiry(Result)} method on the action,
	 * then the cache must behave as if there is no result.
	 * 
	 * @param <R>
	 *            the action's result type
	 * @param action
	 *            the action whose result should be fetched
	 * @return the cached result of the action, or {@code null} if no valid
	 *         result is stored
	 * @throws NullPointerException
	 *             if {@code action} is {@code null}
	 */
	<R extends Result> R get(CacheableAction<R> action);

	/**
	 * Adds an action-result pair to the cache, overwriting any previous result
	 * for the action. Both the action and result must be non-{@code null}, and
	 * the action must implement the {@link CacheableAction} interface to return
	 * a proper expiry time for the entry. Note that the implementation is free
	 * to retain the entry, even if expired, until an attempt at access is made.
	 * This behavior may result in delays in the action and result's garbage
	 * collection.
	 * 
	 * @param <R>
	 *            the action's result type
	 * @param action
	 *            the action whose result is to be stored
	 * @param result
	 *            the result of the action
	 * @throws NullPointerException
	 *             if {@code action} or {@code result} is {@code null}
	 */
	<R extends Result> void put(CacheableAction<R> action, R result);

	/**
	 * Explicitly removes the provided action and its result from the cache.
	 * This will prevent future calls to {@link get(CacheableAction)} with an
	 * equal argument from returning a non-{@code null} result until they are
	 * re-added. In addition, if no references to them remain outside of the
	 * cache, the action and result will both become eligible for garbage
	 * collection. Attempts to remove actions which are not in the cache are
	 * ignored.
	 * 
	 * @param action
	 *            the action to remove from the cache
	 * @throws NullPointerException
	 *             if {@code action} is {@code null}
	 */
	void remove(CacheableAction<?> action);

	/**
	 * Returns an iterator over the actions stored in this cache. The iterator
	 * only returns actions with results that are valid at the time the
	 * {@link Iterator#next()} method is called. Note, however, that actions are
	 * not guaranteed to be valid at any time after that method call, and as a
	 * result, the {@link #get(CacheableAction)} method should still be used to
	 * check for validity.
	 * <p>
	 * 
	 * Typically, the cache will prohibit any structural modifications to the
	 * data underlying the iterator while iteration is in progress, except using
	 * the iterator's own methods (see below). Specifics should be documented by
	 * the cache implementation, but such implementations may restrict use of
	 * any of their methods during iteration, including {@code get()}.
	 * <p>
	 * 
	 * The iterator may, but is not required to implement the
	 * {@link Iterator#remove()} method. Cache implementations should document
	 * support for this method. In the event that it is unsupported by a
	 * particular implementation, clients will have to store actions to remove
	 * in a temporary, external set to avoid violating the requirement above.
	 * 
	 * @return an iterator over the actions whose results are stored in this
	 *         cache
	 */
	@Override
	Iterator<CacheableAction<?>> iterator();
}
