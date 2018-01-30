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
package gov.redhawk.ide.properties.view.runtime.dcd.connections.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractConnectionPropertiesTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainDeviceConnectionPropertyTest extends AbstractConnectionPropertiesTest {

	protected static final String DEVICE_MANAGER = "Usrp2Usrp";
	protected static final String RESOURCE = "USRP_UHD_1";
	protected static final String USES_PORT = "dataShort_out";
	protected static final String CONNECTION = "connection_1";

	private String domain = getClass().getSimpleName() + "_" + (int) (1000.0 * Math.random());

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
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { domain, "Device Managers", DEVICE_MANAGER, RESOURCE, USES_PORT }, CONNECTION);
		treeItem.select();
		return TransportType.SHMIPC;
	}

	protected String getDomain() {
		return domain;
	}
}
