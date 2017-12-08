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

import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import CF.Application;
import CF.DataType;
import CF.DeviceAssignmentType;
import gov.redhawk.ide.sdr.nodebooter.DebugLevel;
import gov.redhawk.ide.sdr.nodebooter.DeviceManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.nodebooter.DeviceManagerLauncherUtil;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.sca.ScaPlugin;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.util.CorbaUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * Tests for the event data provider (gov.redhawk.sca.model.provider.event plugin)
 */
public class EventDataProviderTest extends UITest {

	private static final String DEV_MGR = "DevMgr_localhost";
	private static final String GPP = "GPP_localhost";

	private static final String[] DEV_MGRS_PATH = new String[] { EventDataProviderTest.class.getSimpleName(), "Device Managers" };
	private static final String[] DEV_MGR_PATH = new String[] { EventDataProviderTest.class.getSimpleName(), "Device Managers", DEV_MGR };
	private static final String[] EVENT_CHANNELS_PATH = new String[] { EventDataProviderTest.class.getSimpleName(), "Event Channels" };
	private static final String[] WAVEFORMS_PATH = new String[] { EventDataProviderTest.class.getSimpleName(), "Waveforms" };

	private static final String ODM_CHANNEL = "ODM_Channel";
	private static final String WAVEFORM_SDR_PATH = "/waveforms/ExampleWaveform01/ExampleWaveform01.sad.xml";

	@Before
	public void before() throws Exception {
		super.before();
		ScaExplorerTestUtils.launchDomainViaWizard(bot, EventDataProviderTest.class.getSimpleName());
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, EventDataProviderTest.class.getSimpleName());
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, EVENT_CHANNELS_PATH, ODM_CHANNEL);
		SWTBotTreeItem domain = ScaExplorerTestUtils.getDomain(bot, EventDataProviderTest.class.getSimpleName());
		domain.contextMenu().menu("Auto Refresh").click();
	}

	/**
	 * Tests handling of the event for add/remove of applications
	 */
	@Test
	public void applications() {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaDomainManager domain = registry.findDomain(EventDataProviderTest.class.getSimpleName());
		final String APP_NAME = "def";

		// Launch a dev mgr with a GPP
		launchDevMgr();
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DEV_MGR_PATH, GPP);

		// Perform a CORBA call on the dom mgr to launch an application (outside the IDE's knowledge)
		Application app;
		try {
			app = CorbaUtils.invoke(() -> {
				return domain.getObj().createApplication(WAVEFORM_SDR_PATH, APP_NAME, new DataType[0], new DeviceAssignmentType[0]);
			}, 30000);
		} catch (CoreException | InterruptedException | TimeoutException e) {
			Assert.fail(e.toString());
			return;
		}
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, WAVEFORMS_PATH, APP_NAME);
		String fullName = treeItem.getText();

		// Perform a CORBA call on the application and tell it to release (outside the IDE's knowledge)
		try {
			CorbaUtils.invoke(() -> {
				app.releaseObject();
				app._release();
				return null;
			}, 30000);
		} catch (CoreException | InterruptedException | TimeoutException e) {
			Assert.fail(e.toString());
		}
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, WAVEFORMS_PATH, fullName);
	}

	/**
	 * Tests handling of the event for add/remove of device managers
	 */
	@Test
	public void deviceManagers() {
		launchDevMgr();
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DEV_MGRS_PATH, DEV_MGR);

		killDevMgr();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, DEV_MGRS_PATH, DEV_MGR);
	}

	/**
	 * Tests handling of the event for add/remove of an event channel
	 */
	@Test
	public void eventChannels() {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaDomainManager domain = registry.findDomain(EventDataProviderTest.class.getSimpleName());
		final String EVENT_CHANNEL_NAME = "abc";

		// Perform a CORBA call to the event channel manager and add an event channel (outside the IDE's knowledge)
		try {
			CorbaUtils.invoke(() -> {
				domain.eventChannelMgr().create(EVENT_CHANNEL_NAME);
				return null;
			}, 10000);
		} catch (CoreException | InterruptedException | TimeoutException e) {
			Assert.fail(e.toString());
		}
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, EVENT_CHANNELS_PATH, EVENT_CHANNEL_NAME);

		// Perform a CORBA call to the event channel manager and remove an event channel (outside the IDE's knowledge)
		try {
			CorbaUtils.invoke(() -> {
				domain.eventChannelMgr().release(EVENT_CHANNEL_NAME);
				return null;
			}, 10000);
		} catch (CoreException | InterruptedException | TimeoutException e) {
			Assert.fail(e.toString());
		}
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, EVENT_CHANNELS_PATH, EVENT_CHANNEL_NAME);
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.deleteDomainInstance(bot, EventDataProviderTest.class.getSimpleName());
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	/**
	 * Launches the dev mgr with a GPP. Uses methods that don't automatically update the model.
	 */
	private void launchDevMgr() {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		URI uri = URI.createURI("sdrdev:/nodes/DevMgr_localhost/DeviceManager.dcd.xml");
		Resource resource = resourceSet.getResource(uri, true);
		DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(resource);

		// Launch device manager with underlying utility (i.e. this method won't update the model)
		DeviceManagerLaunchConfiguration cfg = new DeviceManagerLaunchConfiguration(EventDataProviderTest.class.getSimpleName(), dcd, DebugLevel.Info, "",
			EventDataProviderTest.class.getSimpleName() + "_devmgr");
		DeviceManagerLauncherUtil.launchDeviceManager(cfg, new NullProgressMonitor());
	}

	private void killDevMgr() {
		final String configName = EventDataProviderTest.class.getSimpleName() + "_devmgr";
		for (ILaunch launch : DebugPlugin.getDefault().getLaunchManager().getLaunches()) {
			if (configName.equals(launch.getLaunchConfiguration().getName())) {
				try {
					launch.terminate();
				} catch (DebugException e) {
					// PASS
				}
				return;
			}
		}
		Assert.fail("Couldn't find the device manager launch");
	}
}
