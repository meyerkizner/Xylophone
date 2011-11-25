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

import com.prealpha.xylophone.server.CompleteAction.CompleteResult;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.ActionException;
import com.prealpha.xylophone.shared.Result;

final class CompleteAction implements Action<CompleteResult> {
	private final boolean exception;

	CompleteAction(boolean exception) {
		this.exception = exception;
	}

	static final class CompleteResult implements Result {
		@Override
		public boolean isComplete() {
			return true;
		}
	}

	static final class CompleteHandler implements
			ActionHandler<CompleteAction, CompleteResult> {
		@Override
		public CompleteResult execute(CompleteAction action)
				throws ActionException {
			if (action.exception) {
				throw new ActionException();
			} else {
				return new CompleteResult();
			}
		}
	}
}
