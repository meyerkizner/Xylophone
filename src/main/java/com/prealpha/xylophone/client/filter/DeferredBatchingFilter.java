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

import static com.google.common.base.Preconditions.*;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.Inject;
import com.prealpha.xylophone.shared.DispatcherAsync;

/**
 * Implements a batching filter which defers action execution until the browser
 * event loop returns. This is accomplished by extending
 * {@link AbstractBatchingFilter} and implementing the {@link #scheduleFlush()}
 * method to call {@link Scheduler#scheduleDeferred(ScheduledCommand)}, passing
 * a command which calls the {@link #flush()} method.
 * 
 * @author Meyer Kizner
 * 
 */
public final class DeferredBatchingFilter extends AbstractBatchingFilter {
	/**
	 * The scheduler which is used to defer flushing the internal action queue.
	 */
	private final Scheduler scheduler;

	/**
	 * Constructs a new {@code DeferredBatchingFilter}. Note that
	 * {@link #init(DispatcherAsync)} must be called before this instance may be
	 * used.
	 * 
	 * @param scheduler
	 *            the scheduler used to defer flushing until the event loop
	 *            returns
	 */
	@Inject
	private DeferredBatchingFilter(Scheduler scheduler) {
		checkNotNull(scheduler);
		this.scheduler = scheduler;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * The implementation of this method in {@code DeferredBatchingFilter} uses
	 * {@link Scheduler#scheduleDeferred(ScheduledCommand)} to schedule the
	 * flush.
	 */
	@Override
	protected void scheduleFlush() {
		scheduler.scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				flush();
			}
		});
	}
}
