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
 * The asynchronous version of {@link PublishingDispatcher}.
 * 
 * @author Meyer Kizner
 * @see PublishingDispatcher
 * 
 */
public interface PublishingDispatcherAsync extends DispatcherAsync {
	/**
	 * The asynchronous version of
	 * {@link PublishingDispatcher#subscribe(Predicate)}.
	 * 
	 * @param predicate
	 *            a predicate matching actions to which a subscription is
	 *            desired
	 * @param callback
	 *            a callback to receive the subscription ID
	 * @see PublishingDispatcher#subscribe(Predicate)
	 */
	void subscribe(Predicate<? super Action<?>> predicate,
			AsyncCallback<Long> callback);

	/**
	 * The asynchronous version of {@link PublishingDispatcher#check(long)}.
	 * 
	 * @param subscriptionId
	 *            the subscription ID to check
	 * @param callback
	 *            a callback to receive the list of results
	 * @see PublishingDispatcher#check(long)
	 */
	void check(long subscriptionId,
			AsyncCallback<ImmutableList<Result>> callback);

	/**
	 * The asynchronous version of {@link PublishingDispatcher#cancel(long)}.
	 * 
	 * @param subscription
	 *            the subscription ID to cancel
	 * @param callback
	 *            a callback to be notified on completion
	 * @see PublishingDispatcher#cancel(long)
	 */
	void cancel(long subscriptionId, AsyncCallback<Void> callback);
}
