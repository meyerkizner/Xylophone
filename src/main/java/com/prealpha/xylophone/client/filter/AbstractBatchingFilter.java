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

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.DispatcherAsync;
import com.prealpha.xylophone.shared.Result;
import com.prealpha.xylophone.shared.filter.BatchAction;
import com.prealpha.xylophone.shared.filter.BatchResult;
import com.prealpha.xylophone.shared.filter.BatchedActionResult;

/**
 * Provides a skeletal implementation of a batching action filter, which queues
 * actions to be executed and sends them to the server in a group, or batch, at
 * a later time. To implement a batching action filter, subclasses must provide
 * an implementation of the {@code protected} {@code abstract} method
 * {@link #scheduleFlush()}, which should take some action that leads to the
 * {@link #flush()} method being called. Until {@code flush()} is actually
 * called, the filter is guaranteed not to actually execute any actions using
 * its backing {@code DispatcherAsync}; instead, it queues actions for future
 * execution when {@code flush()} is called.
 * 
 * @author Meyer Kizner
 * 
 */
public abstract class AbstractBatchingFilter implements ActionFilter {
	/**
	 * The list of currently queued actions. Calling
	 * {@link #execute(Action, AsyncCallback)} creates and adds a new
	 * {@code QueuedAction} to the list, and calling {@link #flush()} clears the
	 * list and executes a new {@link BatchAction} which sends its contents to
	 * the server.
	 */
	private final List<QueuedAction<?>> queuedActions;

	/**
	 * The backing dispatcher which is used by {@link #flush()} to send actions
	 * to the server. Until {@link #init(DispatcherAsync)} is called, this value
	 * is {@code null}.
	 */
	private DispatcherAsync dispatcher;

	/**
	 * A flag indicating whether or not a flush has already been scheduled.
	 * While this flag is {@code true}, no additional flushes will be scheduled.
	 * The flag is reset to {@code false} once a flush is completed.
	 */
	private boolean flushPending;

	/**
	 * Constructs a new {@code AbstractBatchingFilter} with an empty action
	 * queue. Note that {@link #init(DispatcherAsync)} must be called before
	 * this instance may be used.
	 */
	protected AbstractBatchingFilter() {
		queuedActions = Lists.newArrayList();
	}

	@Override
	public final void init(DispatcherAsync dispatcher) {
		checkNotNull(dispatcher);
		checkState(!isInitialized());
		this.dispatcher = dispatcher;
	}

	@Override
	public final boolean isInitialized() {
		return (dispatcher != null);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * This implementation adds the action and its callback to the internal
	 * action queue. The {@link #scheduleFlush()} method is then called to
	 * signal to the subclass implementation that an action has been added.
	 * However, {@code scheduleFlush} is only called when the subclass is not
	 * yet aware of any pending actions; if the method has already been called
	 * without an intervening flush, it is not called again when a new action is
	 * submitted.
	 */
	@Override
	public final <R extends Result> void execute(Action<R> action,
			AsyncCallback<R> callback) {
		checkState(isInitialized());
		checkNotNull(action);
		checkNotNull(callback);
		QueuedAction<R> queuedAction = new QueuedAction<R>(action, callback);
		queuedActions.add(queuedAction);
		if (!flushPending) {
			flushPending = true;
			scheduleFlush();
		}
	}

	/**
	 * Flushes this filter's action queue, submitting the actions to the backing
	 * dispatcher for execution. As a result, some time after this method is
	 * called, each callback submitted to this filter with the
	 * {@link #execute(Action, AsyncCallback)} method will be handled by calling
	 * either its {@link AsyncCallback#onFailure(Throwable)
	 * onFailure(Throwable)} or {@link AsyncCallback#onSuccess(Object)
	 * onSuccess(Object)} methods. In addition, the internal action queue is
	 * guaranteed to be empty after this method executes.
	 * 
	 * @throws IllegalStateException
	 *             if the backing DispatcherAsync has not yet been initialized
	 *             with the {@link #init(DispatcherAsync)} method
	 */
	protected final void flush() {
		checkState(isInitialized());
		flushPending = false;
		List<Action<?>> toBatch = Lists.newArrayList();
		for (QueuedAction<?> queuedAction : queuedActions) {
			toBatch.add(queuedAction.action);
		}
		BatchAction batchAction = new BatchAction(toBatch);
		dispatcher.execute(batchAction, new BatchCallback(queuedActions));
		queuedActions.clear();
	}

	/**
	 * Schedules an invocation of the {@link #flush()} method to occur either
	 * immediately or at some future time. This method will be invoked by the
	 * {@link #execute(Action, AsyncCallback)} method, as is detailed in that
	 * method's specification. The superclass makes the guarantee that it will
	 * only call this method once the backing {@code DispatcherAsync} is
	 * initialized with the {@link #init(DispatcherAsync)} method.
	 */
	protected abstract void scheduleFlush();

	/**
	 * The callback class used internally to execute {@link BatchAction}s with
	 * the {@code DispatcherAsync}. The callback keeps a copy of the executed
	 * action queue, which it uses to match results to callbacks if the batch
	 * action is successful. If the batch action fails, the exception is passed
	 * on to every callback in the action queue, ensuring that all queued
	 * callbacks have either their {@code onSuccess()} or {@code onFailure()}
	 * methods called as required by {@link #execute(Action, AsyncCallback)} and
	 * {@link #flush()}.
	 * 
	 * @author Meyer Kizner
	 * 
	 */
	private static final class BatchCallback implements
			AsyncCallback<BatchResult> {
		/**
		 * An internal defensive copy of the action queue which was executed by
		 * the {@code BatchAction}. The queue's order is the same order in which
		 * results will be retrieved from the {@code BatchResult}.
		 */
		private final List<QueuedAction<?>> actions;

		/**
		 * Constructs a new {@code BatchCallback} with the specified action
		 * queue.
		 * 
		 * @param actions
		 *            the action queue executed by the {@code BatchAction}
		 * @throws NullPointerException
		 *             if {@code actions} is {@code null}, or if any element of
		 *             {@code actions} is {@code null}
		 */
		public BatchCallback(List<QueuedAction<?>> actions) {
			checkNotNull(actions);
			checkArgument(!actions.contains(null));
			this.actions = ImmutableList.copyOf(actions);
		}

		/**
		 * Iterates through the action queue and calls the
		 * {@link AsyncCallback#onFailure(Throwable)} method on each callback
		 * with the parameter to this method.
		 */
		@Override
		public void onFailure(Throwable caught) {
			for (QueuedAction<?> queuedAction : actions) {
				queuedAction.callback.onFailure(caught);
			}
		}

		/**
		 * Iterates through the list of results in the {@code BatchResult} and
		 * calls the {@link BatchedActionResult#deliver(AsyncCallback)} method
		 * on each result, with the callback for the corresponding action as the
		 * argument. As a result, each queued callback receives the correct
		 * result, as long as the {@code BatchAction} handler on the server side
		 * behaves correctly.
		 * <p>
		 * 
		 * If the number of results does not equal the number of callbacks
		 * queued, a {@link BatchCountException} is constructed and passed to
		 * the {@code #onFailure(Throwable)} method, which will pass the
		 * exception onto all queued callbacks.
		 * 
		 * @param result
		 *            the {@code BatchResult} produced by the
		 *            {@code BatchAction} that executed this callback's action
		 *            queue
		 */
		/*
		 * Both the AsyncCallback and the BatchedActionResult will have the same
		 * type parameter if the handling of BatchAction on the server side is
		 * correct. Given the erased nature of generic types, we have no way of
		 * verifying its correctness here.
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void onSuccess(BatchResult result) {
			List<BatchedActionResult<?>> results = result.getResults();

			if (actions.size() != results.size()) {
				Throwable exception = new BatchCountException(actions.size(),
						results.size());
				onFailure(exception);
			} else {
				for (int i = 0; i < actions.size(); i++) {
					AsyncCallback<?> callback = actions.get(i).callback;
					BatchedActionResult<?> actionResult = results.get(i);
					((BatchedActionResult) actionResult).deliver(callback);
				}
			}
		}
	}

	/**
	 * An internal helper class which provides a simple way of storing
	 * action-callback pairs. Actions and callbacks are guaranteed to have the
	 * same result type. Instances of this class are immutable.
	 * 
	 * @author Meyer Kizner
	 * 
	 * @param <R>
	 *            the result type of both the action and the callback in this
	 *            pair
	 */
	private static final class QueuedAction<R extends Result> {
		/**
		 * The action component of this pair. Guaranteed to be non-{@code null}
		 * and unchanged after construction.
		 */
		final Action<R> action;

		/**
		 * The callback component of this pair. Guaranteed to be non-{@code null}
		 * and unchanged after construction.
		 */
		final AsyncCallback<R> callback;

		/**
		 * Constructs a new {@code QueuedAction} with the specified
		 * action-callback pair.
		 * 
		 * @param action
		 *            the action component of this pair
		 * @param callback
		 *            the callback component of this pair
		 * @throws NullPointerException
		 *             if {@code action} or {@code callback} is {@code null}
		 */
		public QueuedAction(Action<R> action, AsyncCallback<R> callback) {
			checkNotNull(action);
			checkNotNull(callback);
			this.action = action;
			this.callback = callback;
		}
	}
}
