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
 * The parent interface for objects which serve as results of an {@link Action}.
 * Though each action class must map to a specific result type, the reverse need
 * not be true; a single result type might have multiple corresponding action
 * classes. Arbitrary objects cannot be allowed as action results; otherwise,
 * GWT RPC would attempt to compile many unnecessary classes into the JavaScript
 * output, including classes which are unserializable.
 * <p>
 * 
 * A {@code Result} object may represent a partial or complete result for the
 * action. A partial result means that the action is not yet complete, but that
 * the client should be aware of some progress. Clients can elect, through the
 * {@link Dispatcher}, to only accept complete results and discard any partial
 * results created during the execution process.
 * <p>
 * 
 * Note that implementations of this interface must meet the requirements for
 * RPC-serializable objects. In particular, implementation classes must declare
 * a default constructor of any visibility, and they must be designed with the
 * knowledge that final fields will not be serialized.
 * 
 * @author Meyer Kizner
 * 
 */
public interface Result extends IsSerializable {
	/**
	 * Indicates whether or not this result indicates the completion of an
	 * action. Actions and action handlers may return any number of partial
	 * results, and the dispatcher will continue execution until a complete
	 * result is obtained.
	 * 
	 * @return {@code true} if the result is complete; {@code false} otherwise
	 */
	boolean isComplete();
}
