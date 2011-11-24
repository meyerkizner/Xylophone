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
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous interface for event dispatching. Most users of this library
 * will not need to write an implementation of this interface.
 * <p>
 * 
 * All methods throw {@link NullPointerException} if any parameters are
 * {@code null}.
 * 
 * @author Meyer Kizner
 * @see Dispatcher
 * 
 */
public interface DispatcherAsync {
	/**
	 * The asynchronous version of {@link Dispatcher#execute(Action)}.
	 * 
	 * @param <R>
	 *            the result type for the action
	 * @param action
	 *            an action to execute
	 * @param callback
	 *            a callback to receive the complete result of the action
	 * @see Dispatcher#execute(Action)
	 */
	<R extends Result> void execute(Action<R> action, AsyncCallback<R> callback);

	/**
	 * The asynchronous version of {@link Dispatcher#subscribe(Predicate)}.
	 * 
	 * @param <A>
	 *            the action type for the subscription
	 * @param <R>
	 *            the result type for the subscription
	 * @param predicate
	 *            a predicate matching actions to which a subscription is
	 *            desired
	 * @param callback
	 *            a callback to receive the subscription
	 * @see Dispatcher#subscribe(Predicate)
	 */
	<A extends Action<R>, R extends Result> void subscribe(
			Predicate<? super A> predicate,
			AsyncCallback<Subscription<R>> callback);

	/**
	 * The asynchronous version of {@link Dispatcher#check(Subscription)}.
	 * 
	 * @param <R>
	 *            the result type for the subscription
	 * @param subscription
	 *            the subscription to check
	 * @param callback
	 *            a callback to receive the list of results
	 * @see Dispatcher#check(Subscription)
	 */
	<R extends Result> void check(Subscription<R> subscription,
			AsyncCallback<ImmutableList<R>> callback);

	/**
	 * The asynchronous version of {@link Dispatcher#cancel(Subscription)}.
	 * 
	 * @param subscription
	 *            the subscription to cancel
	 * @param callback
	 *            a callback to be notified on completion
	 * @see Dispatcher#cancel(Subscription)
	 */
	void cancel(Subscription<?> subscription, AsyncCallback<Void> callback);
}
