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

/**
 * Superclass for exceptions which occur during the {@link Action} dispatching
 * or execution process. Any class which is responsible for handling actions may
 * throw an {@code ActionException} as part of its use.
 * 
 * @author Meyer Kizner
 * @see Dispatcher
 * 
 */
public class ActionException extends Exception {
	private static final long serialVersionUID = 8448065289627127477L;

	/**
	 * Constructs a new {@code ActionException} with no detail message or cause.
	 */
	public ActionException() {
		super();
	}

	/**
	 * Constructs a new {@code ActionException} with the specified detail
	 * message but no cause.
	 * 
	 * @param message
	 *            the detail message
	 */
	public ActionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code ActionException} with the specified cause but no
	 * detail message.
	 * 
	 * @param cause
	 *            the cause
	 */
	public ActionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code ActionException} with the specified detail
	 * message and cause.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause
	 */
	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}
}
