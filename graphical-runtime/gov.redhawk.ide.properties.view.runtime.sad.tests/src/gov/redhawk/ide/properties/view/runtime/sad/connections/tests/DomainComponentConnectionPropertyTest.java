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
package gov.redhawk.ide.properties.view.runtime.sad.connections.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractConnectionPropertiesTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainComponentConnectionPropertyTest extends AbstractConnectionPropertiesTest {

	protected static final String DEVICE_MANAGER = "DevMgr_localhost";
	protected static final String WAVEFORM = "ExampleWaveform06";
	protected static final String RESOURCE = "SigGen_1";
	protected static final String USES_PORT = "dataFloat_out";
	protected static final String CONNECTION = "connection_1";

	private String domain = getClass().getSimpleName() + "_" + (int) (1000.0 * Math.random());
	private String waveformInstanceName;

	@After
	public void afterTest() {
		ScaExplorerTestUtils.deleteDomainInstance(bot, domain);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
	}

	@Override
	protected TransportType prepareConnection() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM, RESOURCE, USES_PORT };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, CONNECTION);
		treeItem.select();
		return TransportType.SHMIPC;
	}

	protected String getWaveformInstanceName() {
		return waveformInstanceName;
	}
}
