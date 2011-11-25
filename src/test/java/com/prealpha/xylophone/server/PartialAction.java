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

package com.prealpha.xylophone.server;

import java.util.Map;

import com.google.common.collect.Maps;
import com.prealpha.xylophone.server.PartialAction.PartialResult;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.Result;

final class PartialAction implements Action<PartialResult> {
	static final class PartialResult implements Result {
		private final boolean complete;

		PartialResult(int n) {
			complete = (n >= 5);
		}

		@Override
		public boolean isComplete() {
			return complete;
		}
	}

	static final class PartialHandler implements
			ActionHandler<PartialAction, PartialResult> {
		private final Map<PartialAction, Integer> nMap;

		PartialHandler() {
			nMap = Maps.newHashMap();
		}

		@Override
		public PartialResult execute(PartialAction action) {
			int n;
			if (nMap.containsKey(action)) {
				n = nMap.get(action) + 1;
			} else {
				n = 0;
			}
			nMap.put(action, n);
			return new PartialResult(n);
		}
	}
}
