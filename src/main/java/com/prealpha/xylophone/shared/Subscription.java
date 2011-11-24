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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a subscription to partial and complete {@link Result}s from an
 * {@link Action}, usually one that is long-running. Subscriptions should only
 * be created by and used with a specific {@link PublishingDispatcher}.
 * 
 * @param <R>
 *            the result type for this subscription
 * @author Meyer Kizner
 * @see PublishingDispatcher
 * 
 */
public final class Subscription<R extends Result> implements IsSerializable {
	/**
	 * A unique identifier for this subscription which is assigned by the
	 * server. Non-{@code final} to allow for GWT serialization, but never
	 * altered in practice.
	 */
	private int identifier;

	// serialization support
	@SuppressWarnings("unused")
	private Subscription() {
	}

	/**
	 * Constructs a new {@code Subscription} with the specified unique
	 * identifier. To preserve the uniqueness of these identifiers, only
	 * {@link PublishingDispatcher} implementations should construct new
	 * {@code Subscription} objects.
	 * 
	 * @param identifier
	 *            the unique identifier for this subscription
	 */
	public Subscription(int identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the unique identifier for this subscription
	 */
	public int getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + identifier;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Subscription)) {
			return false;
		}
		Subscription<?> other = (Subscription<?>) obj;
		if (identifier != other.identifier) {
			return false;
		}
		return true;
	}
}
