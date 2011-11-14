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

package com.prealpha.xylophone.server.filter;

import com.prealpha.xylophone.server.ActionModule;
import com.prealpha.xylophone.shared.filter.BatchAction;

/**
 * A server-side action handling module to handle {@link BatchAction}s. Some
 * handler must be registered, through this module or another module, for that
 * action in order for {@link AbstractBatchingFilter} subclasses to be used.
 * 
 * @author Meyer Kizner
 * 
 */
public final class BatchActionModule extends ActionModule {
	/**
	 * Constructs a new {@code BatchActionModule}.
	 */
	public BatchActionModule() {
	}

	@Override
	protected void configureActions() {
		bindAction(BatchAction.class).to(BatchActionHandler.class);
	}
}
