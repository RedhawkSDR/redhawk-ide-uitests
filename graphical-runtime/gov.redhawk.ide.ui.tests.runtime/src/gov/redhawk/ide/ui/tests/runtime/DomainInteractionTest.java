/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.sdr.ui.internal.handlers.LaunchDomainManagerWithOptionsDialog;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainInteractionTest extends AbstractDomainRuntimeTest {

	private SWTBotView explorerView;
	private SWTBot viewBot;

	@Before
	public void before() throws Exception {
		super.before();

		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();

		explorerView = bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
		explorerView.show();
		viewBot = explorerView.bot();
	}

	@AfterClass
	public static void afterClassCleanup() throws Exception {
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
	}

	/**
	 * Tests using the domain connection wizard (via toolbar button) to connect to a running domain.
	 * @throws CoreException
	 */
	@Test
	public void connectViaWizardToExistingDomain() throws CoreException {
		final String DOMAIN_NAME = "connectViaWizardToExistingDomain_1";

		launchDomainManager(DOMAIN_NAME);
		connectViaNewDomainWizard(DOMAIN_NAME, DOMAIN_NAME);
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
	 * Test using the launch domain wizard to launch a domain, and then the to launch a domain and then later launch a
	 * node in that domain.
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
	 * Ensure the user can't create two domain entries with the same display name (although they can target the same
	 * domain).
	 * @throws CoreException
	 */
	@Test
	public void displayName() throws CoreException {
		final String DOMAIN_NAME_1 = "displayName_first_instance";
		final String DOMAIN_NAME_2 = "displayName_second_instance";

		launchViaNewDomainWizard(DOMAIN_NAME_1, new String[0]);

		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		SWTBotShell shell = bot.shell("New Domain Manager");

		shell.bot().textWithLabel("Display Name:").setText(DOMAIN_NAME_1);
		Assert.assertFalse("Finish should not be enabled.", shell.bot().button("Finish").isEnabled());

		shell.bot().textWithLabel("Display Name:").setText(DOMAIN_NAME_2);
		shell.bot().textWithLabel("Domain Name:").setText(DOMAIN_NAME_1);
		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME_2);
	}

	/**
	 * Test being able to connect to multiple domain managers.
	 * @throws CoreException
	 */
	@Test
	public void connectViaWizardToManyExistingDomains() throws CoreException {
		final String DOMAIN_NAME_FORMAT = "testConnectManyDomMgr_%d";
		for (int i = 1; i < 5; i++) {
			String domainName = String.format(DOMAIN_NAME_FORMAT, i);
			launchDomainManager(domainName);
			connectViaNewDomainWizard(domainName, domainName);
		}

		// Ensure everybody is still present and connected
		for (int i = 1; i < 5; i++) {
			String domainName = String.format(DOMAIN_NAME_FORMAT, i);
			ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		}
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

		ScaExplorerTestUtils.launchDomain(bot, DOMAIN_NAME, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);

		SWTBotTreeItem treeItem = viewBot.tree().getTreeItem("Target SDR").select();
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

	@Test
	public void plotPort() {
		final String DOMAIN_NAME = "plotPort";
		final String DEV_MGR_NAME = "DevMgr_localhost";
		final String WAVEFORM = "ExampleWaveform05";
		final String SIG_GEN_1 = "SigGen_1";

		ScaExplorerTestUtils.launchDomain(bot, DOMAIN_NAME, DEV_MGR_NAME);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);
		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN_NAME, WAVEFORM);
		SWTBotTreeItem sigGenTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { DOMAIN_NAME, "Waveforms", WAVEFORM },
			SIG_GEN_1);

		sigGenTreeItem.select();
		sigGenTreeItem.contextMenu("Start").click();
		final SWTBotTreeItem outPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { DOMAIN_NAME, "Waveforms", WAVEFORM, SIG_GEN_1 }, "dataFloat_out");
		outPort.select();
		outPort.expand();
		Assert.assertEquals("Port connections", 0, outPort.getItems().length);
		outPort.contextMenu("Plot Port Data").click();

		SWTBotView plotView = bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
		Assert.assertEquals("dataFloat_out", plotView.getReference().getTitle());

		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return outPort.getItems().length == 1;
			}

			@Override
			public String getFailureMessage() {
				return "Port connection did not appear";
			}
		});
	}

	private void launchViaNewDomainWizard(String domainName, String[] deviceManagers) {
		SWTBotTreeItem treeItem = viewBot.tree().getTreeItem("Target SDR").select();
		treeItem.contextMenu("Launch Domain ...").click();
		SWTBotShell shell = bot.shell("Launch Domain Manager");

		shell.bot().textWithLabel("Domain Name: ").setText(domainName);
		bot.waitWhile(Conditions.treeHasRows(shell.bot().tree(), 0));
		StandardTestActions.selectNamespacedTreeItems(viewBot, shell.bot().tree(), deviceManagers);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		ConsoleUtils.assertConsoleTitleExists(bot, "Domain Manager " + domainName + " .*"); // IDE-1372
		for (String deviceManager : deviceManagers) {
			ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { domainName, "Device Managers" }, deviceManager);
			ConsoleUtils.assertConsoleTitleExists(bot, "Device Manager " + deviceManager + " .*");
		}
	}

	private void connectViaNewDomainWizard(String domainName, String displayName) {
		explorerView.toolbarPushButton("New Domain Connection").click();
		SWTBotShell shell = bot.shell("New Domain Manager");

		shell.bot().textWithLabel("Display Name:").setText(displayName);
		Assert.assertEquals(displayName, shell.bot().textWithLabel("Domain Name:").getText());
		Assert.assertTrue(shell.bot().button("Finish").isEnabled());

		if (!domainName.equals(displayName)) {
			shell.bot().textWithLabel("Display Name:").setText(domainName);
			Assert.assertEquals(domainName, shell.bot().textWithLabel("Domain Name:").getText());
		}

		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
	}
}
