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
 * The parent interface for actions which are executed by a {@link Dispatcher}
 * or {@link DispatcherAsync}. All actions must have a specific {@link Result}
 * type which is used as the type parameter for the action. This allows for
 * typesafe action dispatching. Arbitrary objects cannot be allowed as actions;
 * otherwise, GWT RPC would attempt to compile many unnecessary classes into the
 * JavaScript output, including classes which are unserializable.
 * <p>
 * 
 * Note that implementations of this interface must meet the requirements for
 * RPC-serializable objects. In particular, implementation classes must declare
 * a default constructor of any visibility, and they must be designed with the
 * knowledge that final fields will not be serialized.
 * 
 * @param <R>
 *            the result type for the action
 * @author Meyer Kizner
 * @see Dispatcher
 * @see DispatcherAsync
 * 
 */
public interface Action<R extends Result> extends IsSerializable {
}
