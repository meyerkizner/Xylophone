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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.prealpha.xylophone.shared.DispatcherAsync;

/**
 * Implements a batching filter which defers action execution for a fixed period
 * of time. This is accomplished by extending {@link AbstractBatchingFilter} and
 * implementing the {@link #scheduleFlush()} method to call
 * {@link Scheduler#scheduleFixedDelay(RepeatingCommand, int)}, passing a
 * command which calls the {@link #flush()} method.
 * 
 * @author Meyer Kizner
 * 
 */
public final class DelayedBatchingFilter extends AbstractBatchingFilter {
	/**
	 * Binding annotation to mark a value as the batch delay for the purposes of
	 * this class. The value should be an integer; it will be interpreted as the
	 * fixed batch delay in milliseconds.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@BindingAnnotation
	public static @interface BatchDelay {
	}

	/**
	 * The scheduler which is used to defer flushing the internal action queue.
	 */
	private final Scheduler scheduler;

	/**
	 * The fixed batch delay in milliseconds. Configured using dependency
	 * injection with the {@link BatchDelay} annotation.
	 */
	private final int batchDelay;

	/**
	 * Constructs a new {@code DelayedBatchingFilter}. Note that
	 * {@link #init(DispatcherAsync)} must be called before this instance may be
	 * used.
	 * 
	 * @param scheduler
	 *            the scheduler used to delay flushing for a fixed time period
	 * @param batchDelay
	 *            the time to delay in milliseconds
	 */
	@Inject
	private DelayedBatchingFilter(Scheduler scheduler,
			@BatchDelay int batchDelay) {
		checkArgument(batchDelay >= 0);
		this.scheduler = scheduler;
		this.batchDelay = batchDelay;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * The implementation of this method in {@code DelayedBatchingFilter} uses
	 * {@link Scheduler#scheduleFixedDelay(RepeatingCommand, int)} to schedule
	 * the flush.
	 */
	@Override
	protected void scheduleFlush() {
		scheduler.scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				flush();
				return false;
			}
		}, batchDelay);
	}
}
