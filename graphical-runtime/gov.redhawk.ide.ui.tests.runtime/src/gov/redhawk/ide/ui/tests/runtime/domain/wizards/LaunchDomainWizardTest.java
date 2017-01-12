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
package gov.redhawk.ide.ui.tests.runtime.domain.wizards;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.sdr.ui.internal.handlers.LaunchDomainManagerWithOptionsDialog;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Tests for the launch domain wizard.
 */
public class LaunchDomainWizardTest extends UIRuntimeTest {

	@After
	public void cleanup() throws Exception {
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
	}

	/**
	 * IDE-1372
	 * Test using the launch domain wizard to launch a domain manager.
	 */
	@Test
	public void launchDomMgrViaWizard() {
		final String DOMAIN_NAME = "launchDomMgrViaWizard";
		launchViaNewDomainWizard(DOMAIN_NAME, new String[0]);
	}

	/**
	 * IDE-1372
	 * Test using the launch domain wizard to launch a domain, and then later launch a node in that domain.
	 */
	@Test
	public void launchDomMgrThenDevMgrViaWizards() {
		final String DOMAIN_NAME = "launchDomMgrThenDevMgrViaWizards";
		final String DEV_MGR_NAME = "ExampleExecutableNode01";
		final String DEVICE_INSTANCE = "ExampleExecutableDevice01_1";

		launchViaNewDomainWizard(DOMAIN_NAME, new String[0]);

		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Target SDR", "Nodes" }, DEV_MGR_NAME).select();
		treeItem.contextMenu("Launch Device Manager").click();
		SWTBotShell shell = bot.shell("Launch Device Manager");

		shell.bot().table().select(DOMAIN_NAME);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { DOMAIN_NAME, "Device Managers", DEV_MGR_NAME }, DEVICE_INSTANCE);
		ConsoleUtils.assertConsoleTitleExists(bot, "Device Manager " + DEV_MGR_NAME + " .*");
	}

	/**
	 * IDE-1372
	 * Test using the wizard to launch both a domain and device manager at the same time.
	 */
	@Test
	public void launchDomMgrAndDevMgrViaWizard() {
		final String DOMAIN_NAME = "launchDomMgrAndDevMgrViaWizard";
		final String DEV_MGR_NAME = "ExampleExecutableNode01";
		launchViaNewDomainWizard(DOMAIN_NAME, new String[] { DEV_MGR_NAME });
	}

	/**
	 * IDE-1372
	 * Test launching a domain manager with multiple nodes.
	 */
	@Test
	public void launchDomMgrAndManyDevMgr() {
		final String DOMAIN_NAME = "launchDomMgrAndManyDevMgr";
		final String DEV_MGR_1_NAME = "ExampleExecutableNode01";
		final String DEV_MGR_2_NAME = "ExampleExecutableNode02";

		launchViaNewDomainWizard(DOMAIN_NAME, new String[] { DEV_MGR_1_NAME, DEV_MGR_2_NAME });
	}

	/**
	 * Tests validation in the launch domain manager wizard. Should enable/disable based on a clash with existing
	 * domain connections.
	 */
	@Test
	public void launchDomMgrWizardValidation() {
		final String DOMAIN_NAME = "testLaunchDomMgrValidation";
		final String DEVICE_MANAGER = "DevMgr_localhost";

		ScaExplorerTestUtils.launchDomainViaWizard(bot, DOMAIN_NAME, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);

		SWTBotView explorerView = bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
		SWTBotTreeItem treeItem = explorerView.bot().tree().getTreeItem("Target SDR").select();
		treeItem.contextMenu("Launch Domain ...").click();
		SWTBotShell shell = bot.shell("Launch Domain Manager");

		// Check error message for blank domain name entry
		shell.bot().textWithLabel("Domain Name: ").setText("");
		bot.waitWhile(Conditions.widgetIsEnabled(shell.bot().button("OK")));
		Assert.assertEquals("Expected error message did not display", LaunchDomainManagerWithOptionsDialog.INVALID_DOMAIN_NAME_ERR,
			shell.bot().clabel(0).getText());

		// Choose an acceptable domain name. The OK button should enable.
		shell.bot().textWithLabel("Domain Name: ").setText("REDHAWK_DEV_2");
		bot.waitUntil(Conditions.widgetIsEnabled(shell.bot().button("OK")));
		Assert.assertEquals("Error message should have disappeared", "", shell.bot().clabel(0).getText());

		// Choose an unacceptable domain name. The OK button should disable.
		shell.bot().textWithLabel("Domain Name: ").setText(DOMAIN_NAME);
		bot.waitWhile(Conditions.widgetIsEnabled(shell.bot().button("OK")));
		Assert.assertEquals("Expected error message did not display", LaunchDomainManagerWithOptionsDialog.DUPLICATE_NAME, shell.bot().clabel(0).getText());

		shell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	private void launchViaNewDomainWizard(String domainName, String[] deviceManagers) {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, deviceManagers);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		ConsoleUtils.assertConsoleTitleExists(bot, "Domain Manager " + domainName + " .*"); // IDE-1372
		for (String deviceManager : deviceManagers) {
			ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { domainName, "Device Managers" }, deviceManager);
			ConsoleUtils.assertConsoleTitleExists(bot, "Device Manager " + deviceManager + " .*");
		}
	}
}
