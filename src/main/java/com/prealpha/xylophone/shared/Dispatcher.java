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

package com.prealpha.xylophone.shared;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * The synchronous interface for event dispatching. Most users of this library
 * will not need to write an implementation of this interface.
 * <p>
 * 
 * All methods throw {@link NullPointerException} if any parameters are
 * {@code null}.
 * 
 * @author Meyer Kizner
 * @see DispatcherAsync
 * 
 */
public interface Dispatcher extends RemoteService {
	/**
	 * Executes an {@link Action}, returning a {@link Result} whose type is
	 * consistent with the action's type parameter. If the action has partial
	 * results, none will be returned; the action will be executed until a
	 * complete result is obtained. Therefore, the result of this method will
	 * always be a {@linkplain Result#isComplete() complete} {@code Result}. An
	 * {@link ActionException} may be thrown if the action cannot be dispatched
	 * or executed for any reason.
	 * 
	 * @param <R>
	 *            the result type for the action
	 * @param action
	 *            an action to execute
	 * @return the complete result of the action
	 * @throws ActionException
	 *             thrown to indicate a problem during dispatch or execution
	 */
	<R extends Result> R execute(Action<R> action) throws ActionException;

	/**
	 * Establishes a subscription to results, both partial and incomplete,
	 * resulting from certain actions. Any results arising from actions which
	 * match the predicate given will be included in the subscription.
	 * Subsequently, the returned {@link Subscription} can be
	 * {@linkplain #check(Subscription) checked} to obtain the actual result
	 * objects. Alternatively, it may be {@linkplain #cancel(Subscription)
	 * cancelled} at any time. The dispatcher may not free resources associated
	 * with a subscription until it is cancelled, so it is recommended that all
	 * clients do so.
	 * 
	 * @param <A>
	 *            the action type for the subscription
	 * @param <R>
	 *            the result type for the subscription
	 * @param predicate
	 *            a predicate matching actions to which a subscription is
	 *            desired
	 * @return a subscription to all results arising from actions matching
	 *         {@code predicate}
	 */
	<A extends Action<R>, R extends Result> Subscription<R> subscribe(
			Predicate<? super A> predicate);

	/**
	 * Checks the specified subscription for any results which may have been
	 * published since the last check. The results will be returned as a list,
	 * in the order in which they were created. If there are no published
	 * results pending, this method will block until a result is published.
	 * Therefore, an empty list will only be returned if the subscription is
	 * cancelled while the method is waiting.
	 * <p>
	 * 
	 * There is no way to obtain the exact action object which generated any
	 * particular result. The client is only guaranteed that the action
	 * fulfilled the predicate specified when the subscription was created.
	 * Clients are advised to create multiple subscriptions, each with a
	 * restrictive predicate, if this information is required.
	 * 
	 * @param <R>
	 *            the result type for the subscription
	 * @param subscription
	 *            the subscription to check
	 * @return a list of results, in chronological order, which have been
	 *         published since the last time the subscription was checked
	 * @throws ActionException
	 *             thrown to indicate a problem during dispatch or execution in
	 *             the time since the last check of this subscription
	 */
	<R extends Result> ImmutableList<R> check(Subscription<R> subscription)
			throws ActionException;

	/**
	 * Cancels a subscription, freeing any resources associated with it. Any
	 * results which have been published since the last check are discarded.
	 * 
	 * @param subscription
	 *            the subscription to cancel
	 */
	void cancel(Subscription<?> subscription);
}
