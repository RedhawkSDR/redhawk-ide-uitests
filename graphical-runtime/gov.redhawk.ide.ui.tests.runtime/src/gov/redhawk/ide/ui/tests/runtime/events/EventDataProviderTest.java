/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.ui.tests.runtime.events;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.SystemException;

import CF.EventChannelManagerPackage.ChannelAlreadyExists;
import CF.EventChannelManagerPackage.ChannelDoesNotExist;
import CF.EventChannelManagerPackage.OperationFailed;
import CF.EventChannelManagerPackage.OperationNotAllowed;
import CF.EventChannelManagerPackage.RegistrationsExists;
import CF.EventChannelManagerPackage.ServiceUnavailable;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.sca.ScaPlugin;

/**
 * Tests for the event data provider (gov.redhawk.sca.model.provider.event plugin)
 */
public class EventDataProviderTest extends UITest {

	private static final String[] EVENT_CHANNELS_PATH = new String[] { EventDataProviderTest.class.getSimpleName(), "Event Channels" };
	private static final String ODM_CHANNEL = "ODM_Channel";

	@Before
	public void before() throws Exception {
		super.before();
		ScaExplorerTestUtils.launchDomainViaWizard(bot, EventDataProviderTest.class.getSimpleName());
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, EventDataProviderTest.class.getSimpleName());
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, EVENT_CHANNELS_PATH, ODM_CHANNEL);
		SWTBotTreeItem domain = ScaExplorerTestUtils.getDomain(bot, EventDataProviderTest.class.getSimpleName());
		domain.contextMenu().menu("Auto Refresh").click();
	}

	@Test
	public void eventChannels() {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaDomainManager domain = registry.findDomain(EventDataProviderTest.class.getSimpleName());

		// Add an event channel
		try {
			domain.eventChannelMgr().create("abc");
		} catch (ChannelAlreadyExists | OperationNotAllowed | OperationFailed | ServiceUnavailable | SystemException e) {
			Assert.fail(e.toString());
		}
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, EVENT_CHANNELS_PATH, "abc");

		// Remove an event channel
		try {
			domain.eventChannelMgr().release("abc");
		} catch (ChannelDoesNotExist | RegistrationsExists | OperationNotAllowed | OperationFailed | ServiceUnavailable | SystemException e) {
			Assert.fail(e.toString());
		}
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, EVENT_CHANNELS_PATH, "abc");
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.deleteDomainInstance(bot, EventDataProviderTest.class.getSimpleName());
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}
}
