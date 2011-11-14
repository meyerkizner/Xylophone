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
import java.util.ListIterator;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prealpha.xylophone.shared.Action;
import com.prealpha.xylophone.shared.DispatcherAsync;
import com.prealpha.xylophone.shared.Result;

/**
 * An {@link ActionFilter} chain, itself a filter which is usually backed by a
 * non-filter {@link DispatcherAsync}. Filter chains perform no processing of
 * their own, instead passing actions onto a provided list of included action
 * filters, each of which is the previous filter's backing dispatcher. Chains
 * must be provided a list of uninitialized action filters, and then initialized
 * with an actual {@code DispatcherAsync} capable of sending actions to the
 * server. Once initialization is complete, each action sent to the chain will
 * be passed to the first filter, which will then pass it to the second, and so
 * on until reaching the chain's backing dispatcher.
 * 
 * @author Meyer Kizner
 * 
 */
public final class FilterChain implements ActionFilter {
	/**
	 * The list of filters. Incoming actions are sent to the first element in
	 * the list, and the chain proceeds in the list's order. May be empty, but
	 * not {@code null}.
	 */
	private final List<ActionFilter> filters;

	/**
	 * The head dispatcher. If the filter chain is empty, this is the backing
	 * dispatcher which directly executes actions on the server. Otherwise, it
	 * is the same instance as {@code filters.get(0)}.
	 * <p>
	 * 
	 * When the chain has not been initialized, this field is {@code null}. The
	 * field is used as a flag to determine whether or not the chain has been
	 * initialized.
	 */
	private DispatcherAsync head;

	/**
	 * Constructs a new {@code FilterChain} with the specified list of action
	 * filters. The first filter in the list is the first filter which will
	 * receive actions sent to this chain. The last filter in the list will send
	 * executed actions directly to the server.
	 * 
	 * @param filters
	 *            the list of filters in this chain
	 * @throws NullPointerException
	 *             if {@code filters} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code filters} contains {@code null} elements or filters
	 *             which have already been initialized
	 */
	public FilterChain(List<? extends ActionFilter> filters) {
		checkNotNull(filters);
		checkArgument(!filters.contains(null));
		for (ActionFilter filter : filters) {
			checkArgument(!filter.isInitialized());
		}
		this.filters = ImmutableList.copyOf(filters);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * Additionally, when this method is called on a {@code FilterChain}, the
	 * {@link ActionFilter#init(DispatcherAsync)} method will be called for all
	 * filters in the filter chain. The last filter in the chain will be
	 * initialized with the argument to this method. Each other filter will be
	 * initialized with the filter which follows it in the chain; so the first
	 * filter will be initialized with the second, the second with the third,
	 * and so on.
	 * 
	 * @throws IllegalStateException
	 *             {@inheritDoc}, or if any filters in this chain are already
	 *             initialized
	 */
	@Override
	public void init(DispatcherAsync dispatcher) {
		checkNotNull(dispatcher);
		checkState(!isInitialized());
		ListIterator<ActionFilter> i1 = filters.listIterator(filters.size());
		head = dispatcher;
		while (i1.hasPrevious()) {
			ActionFilter filter = i1.previous();
			filter.init(head);
			head = filter;
		}
	}

	@Override
	public boolean isInitialized() {
		return (head != null);
	}

	@Override
	public <R extends Result> void execute(Action<R> action,
			AsyncCallback<R> callback) {
		checkState(isInitialized());
		head.execute(action, callback);
	}
}
