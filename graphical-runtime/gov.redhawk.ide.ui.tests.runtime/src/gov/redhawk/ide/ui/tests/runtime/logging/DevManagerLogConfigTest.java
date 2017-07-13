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
package gov.redhawk.ide.ui.tests.runtime.logging;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Logging tests against a device belonging to a device manager in a domain.
 */
public class DevManagerLogConfigTest extends AbstractLogConfigTest {

	private static final String DEVICE_MANAGER = "DevMgr_localhost";
	private static final String GPP_LOCALHOST = "GPP_localhost";

	private String domainName = null;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		domainName = "SWTBOT_TEST_" + (int) (1000.0 * Math.random());

		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		String[] parentPath = new String[] { domainName, "Device Managers", DEVICE_MANAGER };
		return ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, GPP_LOCALHOST);
	}

	@After
	public void after() {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;

			ScaExplorerTestUtils.deleteDomainInstance(bot, localDomainName);
			NodeBooterLauncherUtil.getInstance().terminateAll();
			ConsoleUtils.removeTerminatedLaunches(bot);
		}
	}

	@Override
	protected String getConsoleTitle() {
		return DEVICE_MANAGER;
	}
}
