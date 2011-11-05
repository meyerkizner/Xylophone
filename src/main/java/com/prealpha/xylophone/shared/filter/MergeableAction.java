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

import com.prealpha.xylophone.client.filter.MergingActionFilter;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.Result;

/**
 * A marker interface for actions which, when executed multiple times by a
 * client, can be combined into a single server-side action. Two mergeable
 * actions are combined only if they are equal according to the
 * {@link #equals(Object)} method. Generally, actions which fetch data from the
 * server without modifying the server's state are good candidates for
 * implementing {@code MergeableAction}.
 * 
 * @param <R>
 *            the result type for the action
 * @author Meyer Kizner
 * @see MergingActionFilter
 * 
 */
public interface MergeableAction<R extends Result> extends Action<R> {
}
