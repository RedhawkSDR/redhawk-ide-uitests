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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * 
 */
public class DomainInteractionTest extends AbstractDomainRuntimeTest {
	static final String DOMAIN_NAME = "REDHAWK_DEV";
	static final String DEVICE_MANAGER = "DevMgr_"; // start of Device Manager name
	static final String DOMAIN_NAME_CONNECTED = DOMAIN_NAME + " CONNECTED";
	
	private SWTBot viewBot;
	private SWTBotView explorerView;

	@Before
	public void before() throws Exception {
		super.before();

		StandardTestActions.cleanUpLaunches();

		StandardTestActions.cleanUpConnections();

		explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		viewBot = explorerView.bot();
	}
	
	@AfterClass
	public static void afterClassCleanup() throws Exception {
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
	}

	/**
	 * Tests using the wizard to connect to a running domain.
	 */
	@Test
	public void testConnectWithWizard() {
		launchDomainManager(DOMAIN_NAME);

		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();

		SWTBotShell shell = bot.activeShell();
		Assert.assertEquals("New Domain Manager", shell.getText());
		SWTBot wizardBot = shell.bot();

		wizardBot.textWithLabel("Display Name:").setText("REDHAWK");
		Assert.assertEquals("REDHAWK", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN");
		Assert.assertEquals("REDHAWK_DOMAIN", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01");
		Assert.assertEquals("REDHAWK_DOMAIN_TEST01", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		wizardBot.textWithLabel("Display Name:").setText(DOMAIN_NAME);
		Assert.assertEquals(DOMAIN_NAME, wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.button("Finish").click();

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").select();
	}

	/**
	 * Test using the wizard to launch a new domain manager.
	 */
	@Test
	public void testLaunchDomMgr() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK");
		Assert.assertTrue(bot.button("OK").isEnabled());

		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN");
		Assert.assertTrue(bot.button("OK").isEnabled());

		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST02");
		Assert.assertTrue(bot.button("OK").isEnabled());
		bot.button("OK").click();

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, "REDHAWK_DOMAIN_TEST02");
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST02 CONNECTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST02 CONNECTED").select();
	}

	/**
	 * Test using the wizards to launch a domain and then later launch a node in that domain.
	 */
	@Test
	public void testLaunchDomMgrThenDevMgr() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST03");
		bot.button("OK").click();

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, "REDHAWK_DOMAIN_TEST03");

		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").expand();
		viewBot.tree().getTreeItem("Target SDR").expand();
		viewBot.tree().getTreeItem("Target SDR").getNode("Nodes").expand();
		viewBot.tree().getTreeItem("Target SDR").getNode("Nodes").getNode("ExampleExecutableNode01").select();
		viewBot.tree().getTreeItem("Target SDR").getNode("Nodes").getNode("ExampleExecutableNode01").contextMenu("Launch Device Manager").click();
		bot.button("OK").click();

		explorerView.show();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").getNode("ExampleExecutableNode01").expand();
		viewBot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").getNode("ExampleExecutableNode01").getNode(
					"ExampleExecutableDevice01_1 STARTED");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "REDHAWK_DOMAIN_TEST03 never connected";
			}

		}, 30000, 1000);
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").getNode("ExampleExecutableNode01").getNode(
			"ExampleExecutableDevice01_1 STARTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").select();
	}

	/**
	 * Test using the wizard do launch both a domain and device manager at the same time.
	 */
	@Test
	public void testLaunchDomMgrAndDevMgr() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST04");
		bot.tree().getTreeItem("ExampleExecutableNode01 (/nodes/ExampleExecutableNode01/)").check();
		bot.button("OK").click();

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, "REDHAWK_DOMAIN_TEST04");

		explorerView.show();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST04 CONNECTED").expand();

		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST04 CONNECTED", "Device Managers", "ExampleExecutableNode01");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Device Manager never connected.";
			}

		}, 30000, 1000);
		viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST04 CONNECTED", "Device Managers", "ExampleExecutableNode01").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST04 CONNECTED").select();
	}

	/**
	 * Ensure the user can't create two domain entries with the same display name (although they can target the same domain).
	 */
	@Test
	public void testDisplayName() {
		launchDomainManager("REDHAWK_DOMAIN_TEST01x");
		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01x");
		bot.button("Finish").click();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST01x CONNECTED").expand();

		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01x");
		Assert.assertFalse("Finish should not be enabled.", bot.button("Finish").isEnabled());
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01x_2");
		bot.textWithLabel("Domain Name:").setText("REDHAWK_DOMAIN_TEST01x");
		bot.button("Finish").click();

		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST01x_2 CONNECTED").expand();
	}

	/**
	 * Test being able to connect to multiple domain managers.
	 */
	@Test
	public void testConnectManyDomMgr() {
		for (int i = 1; i < 5; i++) {
			String domainName = "REDHAWK_DOMAIN_TEST0" + i + "x";
			launchDomainManager(domainName);
			StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
			bot.textWithLabel("Display Name:").setText(domainName);
			bot.button("Finish").click();
			ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
			ScaExplorerTestUtils.getDomain(bot, domainName).select();
		}

		for (int i = 1; i < 5; i++) {
			String domainName = "REDHAWK_DOMAIN_TEST0" + i + "x";
			ScaExplorerTestUtils.getDomain(bot, domainName).select();
		}
	}

	/**
	 * Test launching a domain manager with multiple nodes.
	 */
	@Test
	public void testLaunchDomMgrAndManyDevMgr() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();

		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST06");
		bot.tree().getTreeItem("ExampleExecutableNode01 (/nodes/ExampleExecutableNode01/)").select();
		bot.tree().getTreeItem("ExampleExecutableNode01 (/nodes/ExampleExecutableNode01/)").check();
		bot.tree().getTreeItem("ExampleExecutableNode02 (/nodes/ExampleExecutableNode02/)").select();
		bot.tree().getTreeItem("ExampleExecutableNode02 (/nodes/ExampleExecutableNode02/)").check();
		bot.button("OK").click();

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, "REDHAWK_DOMAIN_TEST06");
		ScaExplorerTestUtils.getDomain(bot, "REDHAWK_DOMAIN_TEST06").expand();

		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST06 CONNECTED", "Device Managers", "ExampleExecutableNode01");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Failed to find REDHAWK_DOMAIN_TEST06/Device Managers/ExampleExecutableNode01";
			}

		}, 30000, 1000);
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST06 CONNECTED", "Device Managers", "ExampleExecutableNode02");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Failed to find REDHAWK_DOMAIN_TEST06/Device Managers/ExampleExecutableNode02";
			}

		}, 30000, 1000);
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST06 CONNECTED").select();
	}

	/**
	 * Tests validation in the launch domain manager wizard. Should enable/disable based on the chosen domain name.
	 */
	@Test
	public void testLaunchDomMgrValidation() {
		ScaExplorerTestUtils.launchDomain(bot, DOMAIN_NAME, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);

		SWTBotTreeItem deviceMangersTreeItem = viewBot.tree().expandNode(DOMAIN_NAME_CONNECTED, "Device Managers").expand();
		deviceMangersTreeItem.getNode(0).expand();
		
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();

		// Choose an acceptable domain name. The OK button should enable.
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DEV_2");
		bot.waitWhile(new ICondition() {

			@Override
			public boolean test() throws Exception {
				return !bot.button("OK").isEnabled();
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "OK button did not enable";
			}
		}, 5000, 1000);

		// Choose an unacceptable domain name. The OK button should disable.
		bot.textWithLabel("Domain Name: ").setText(DOMAIN_NAME);
		bot.waitWhile(new ICondition() {

			@Override
			public boolean test() throws Exception {
				return bot.button("OK").isEnabled();
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "OK button did not disable";
			}

		}, 5000, 1000);
		bot.button("Cancel").click();
	}

	@Test
	public void testPortsPlotData() {
		ScaExplorerTestUtils.launchDomain(bot, "REDHAWK_DEV_ports", "DevMgr_localhost");
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, "REDHAWK_DEV_ports");
		ScaExplorerTestUtils.launchWaveformFromDomain(bot, "REDHAWK_DEV_ports", "ExampleWaveform05");
		SWTBotTreeItem sigGenTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] {"REDHAWK_DEV_ports", "Waveforms", "ExampleWaveform05"}, "SigGen_1");

		sigGenTreeItem.select();
		sigGenTreeItem.contextMenu("Start").click();
		SWTBotTreeItem outPortTreeItem = sigGenTreeItem.expandNode("dataFloat_out");
		outPortTreeItem.select();
		Assert.assertEquals("Port connections", 0, outPortTreeItem.getItems().length);
		outPortTreeItem.contextMenu("Plot Port Data").click();

		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				SWTBotView view = bot.activeView();
				String viewTitle = view.getViewReference().getTitle();
				String viewId = view.getViewReference().getId();
				if ("dataFloat_out".equals(viewTitle) && "gov.redhawk.ui.port.nxmplot.PlotView2".equals(viewId)) {
					return true;
				}
				return false;
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "Plot View failed to open";
			}
		}, 30000, 1000);

		explorerView.show();
		Assert.assertEquals("Port connections", 1, outPortTreeItem.getItems().length);
	}
}
