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

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.easymock.Mock;
import com.mycila.testing.plugin.guice.Bind;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.prealpha.xylophone.server.CompleteAction.CompleteHandler;
import com.prealpha.xylophone.server.CompleteAction.CompleteResult;
import com.prealpha.xylophone.server.PartialAction.PartialHandler;
import com.prealpha.xylophone.server.PartialAction.PartialResult;
import com.prealpha.xylophone.shared.ActionException;
import com.prealpha.xylophone.shared.Dispatcher;

@RunWith(MycilaJunitRunner.class)
public final class DispatcherImplTest {
	@SuppressWarnings("unused")
	@ModuleProvider
	private Module getModule() {
		return new ActionModule() {
			@Override
			protected void configureActions() {
				bindScope(RequestScoped.class, Scopes.NO_SCOPE);
				bindAction(PartialAction.class).to(PartialHandler.class).in(
						Singleton.class);
				bindAction(CompleteAction.class).to(CompleteHandler.class);
			}
		};
	}

	@Inject
	private Dispatcher dispatcher;

	@SuppressWarnings("unused")
	@Mock(Mock.Type.NICE)
	@Bind
	private HttpServletRequest request;

	@Test
	public void testPartialAction() throws ActionException {
		PartialAction action = new PartialAction();
		PartialResult result = dispatcher.execute(action);
		assertNotNull(result);
		assertTrue(result.isComplete());
	}

	@Test
	public void testCompleteAction() throws ActionException {
		CompleteAction action = new CompleteAction(false);
		CompleteResult result = dispatcher.execute(action);
		assertNotNull(result);
		assertTrue(result.isComplete());
	}

	@Test(expected = ActionException.class)
	public void testException() throws ActionException {
		CompleteAction action = new CompleteAction(true);
		dispatcher.execute(action);
	}
}
