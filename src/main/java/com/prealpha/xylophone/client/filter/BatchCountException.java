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

/**
 * Indicates that an {@link AbstractBatchingFilter} received a different number
 * of actions from the server than it expected for a particular batch. As a
 * result, it has no means of determining which results correspond to which
 * queued callbacks. This exception usually indicates a serious error in the
 * handling of batch actions on the server side.
 * <p>
 * 
 * Instances of this class are immutable.
 * 
 * @author Meyer Kizner
 * @see AbstractBatchingFilter
 * 
 */
public final class BatchCountException extends RuntimeException {
	private static final long serialVersionUID = -722224029402674404L;

	/**
	 * The number of results the batching filter was expecting.
	 */
	private final int expected;

	/**
	 * The actual number of results received by the batching filter.
	 */
	private final int actual;

	/**
	 * Constructs a new {@code BatchCountException} with the specified expected
	 * and actual result counts. Both counts must be non-negative, and they
	 * cannot be equal to each other, given that the purpose of this exception
	 * is to indicate unequal counts.
	 * 
	 * @param expected
	 *            the expected result count
	 * @param actual
	 *            the actual result count
	 * @throws IllegalArgumentException
	 *             if either {@code expected} or {@code actual} is negative, or
	 *             if both are equal
	 */
	BatchCountException(int expected, int actual) {
		// prevent initCause() invocation by passing null
		super("batch action count does not match; expected: " + expected
				+ ", actual: " + actual, null);

		if (expected < 0 || actual < 0 || expected == actual) {
			throw new IllegalArgumentException();
		}

		this.expected = expected;
		this.actual = actual;
	}

	/**
	 * @return the expected result count
	 */
	public int getExpected() {
		return expected;
	}

	/**
	 * @return the actual result count
	 */
	public int getActual() {
		return actual;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + actual;
		result = prime * result + expected;
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
		if (!(obj instanceof BatchCountException)) {
			return false;
		}
		BatchCountException other = (BatchCountException) obj;
		if (actual != other.actual) {
			return false;
		}
		if (expected != other.expected) {
			return false;
		}
		return true;
	}
}
