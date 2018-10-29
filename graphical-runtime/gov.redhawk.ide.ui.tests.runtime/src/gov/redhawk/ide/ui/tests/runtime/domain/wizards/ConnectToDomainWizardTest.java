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

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.sdr.nodebooter.DebugLevel;
import gov.redhawk.ide.sdr.nodebooter.DomainManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.nodebooter.DomainManagerLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForWidgetEnablement;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Tests for the connect to domain wizard (i.e. the new button in the explorer view).
 */
public class ConnectToDomainWizardTest extends UIRuntimeTest {

	private SWTBotView explorerView;

	@Before
	public void before() throws Exception {
		super.before();
		explorerView = bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
	}

	@After
	public void cleanup() throws Exception {
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
	}

	/**
	 * IDE-1777 Browse the existing domain(s) in the wizard and connect to one.
	 * @throws CoreException
	 */
	@Test
	public void connectViaWizardBrowseToExistingDomain() throws CoreException {
		final String DOMAIN_NAME = "connectViaWizardBrowseToExistingDomain";
		launchDomainManager(DOMAIN_NAME);

		explorerView.toolbarPushButton("New Domain Connection").click();
		SWTBotShell shell = bot.shell("New Domain Manager");

		// Wait for domains to load, click the button
		final SWTBotButton button = shell.bot().button();
		shell.bot().waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return "Click to select a running domain".equals(button.getToolTipText());
			}

			@Override
			public String getFailureMessage() {
				return "Domains were not found";
			}
		}, 15000);
		button.click();

		// Select our domain, click OK
		SWTBotShell selectShell = bot.shell("Domain selection");
		selectShell.bot().table().select(DOMAIN_NAME);
		selectShell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(selectShell));

		// Ensure the display name / domain name are correct
		Assert.assertEquals("Unexpected display name", DOMAIN_NAME, shell.bot().textWithLabel("Display Name:").getText());
		Assert.assertEquals("Unexpected domain name", DOMAIN_NAME, shell.bot().textWithLabel("Domain Name:").getText());

		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);
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

		shell.bot().textWithLabel("Domain Name:").setText(DOMAIN_NAME_1);
		Assert.assertFalse("Finish should not be enabled.", shell.bot().button("Finish").isEnabled());

		shell.bot().textWithLabel("Display Name:").setText(DOMAIN_NAME_2);
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
	 * IDE-1777, IDE-1778 Test validation in the wizard.
	 */
	@Test
	public void validation() {
		explorerView.toolbarPushButton("New Domain Connection").click();
		SWTBotShell shell = bot.shell("New Domain Manager");

		SWTBotText displayName = shell.bot().textWithLabel("Display Name:");
		SWTBotText domainName = shell.bot().textWithLabel("Domain Name:");
		SWTBotText nameService = shell.bot().textWithLabel("Name Service:");
		SWTBotButton finish = shell.bot().button("Finish");

		// We start out disabled (no display name / domain name yet)
		Assert.assertFalse(finish.isEnabled());

		// Set domain name -> sets display name. Everything should be ok.
		domainName.setText("abc");
		shell.bot().waitUntil(Conditions.widgetIsEnabled(finish));

		// Clear the domain name
		String oldText = domainName.getText();
		domainName.setText("");
		shell.bot().waitUntil(new WaitForWidgetEnablement(finish, false));

		// Set the domain name back to what it was
		domainName.setText(oldText);
		shell.bot().waitUntil(Conditions.widgetIsEnabled(finish));

		// Clear the display name
		displayName.setText("");
		shell.bot().waitUntil(new WaitForWidgetEnablement(finish, false));

		// Set the display name again
		displayName.setText("def");
		shell.bot().waitUntil(Conditions.widgetIsEnabled(finish));

		// Set an invalid naming service reference
		oldText = nameService.getText();
		nameService.setText("bad name service");
		shell.bot().waitUntil(new WaitForWidgetEnablement(finish, false));
		Assert.assertEquals("Enter a valid name service reference", shell.bot().button().getToolTipText());

		// Set a valid naming service reference
		nameService.setText(oldText);
		shell.bot().waitUntil(Conditions.widgetIsEnabled(finish));

		shell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	/**
	 * IDE-1584 - Launch a device manager in a domain that has a unique display name
	 * IDE-1932 - Ensure extra ODM channel does not display
	 * @throws CoreException
	 */
	@Test
	public void launchDevMgrViaDisplayName() throws CoreException {
		final String domainName = "testDomain";
		final String displayName = "displayName";
		final String nodeName = "DevMgr_localhost";

		launchDomainManager(domainName);
		connectViaNewDomainWizard(domainName, displayName);

		SWTBotTreeItem nodeTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Target SDR", "Nodes" }, nodeName);
		nodeTreeItem.contextMenu("Launch Device Manager").click();

		SWTBotShell wizard = bot.shell("Launch Device Manager");
		wizard.bot().table().select(displayName);
		wizard.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(wizard));

		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { displayName, "Device Managers" }, nodeName);
		try {
			ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { displayName, "Event Channels" }, displayName + ".ODM_Channel", 5000);
			Assert.fail("Superfluous ODM event channel created");
		} catch (TimeoutException e) {
			// PASS, this node shouldn't appear
		}
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

	private void connectViaNewDomainWizard(String domainName, String displayName) {
		explorerView.toolbarPushButton("New Domain Connection").click();
		SWTBotShell shell = bot.shell("New Domain Manager");

		// Set the domain name. Should set the display name to the same thing
		shell.bot().textWithLabel("Domain Name:").setText(domainName);
		Assert.assertEquals(domainName, shell.bot().textWithLabel("Display Name:").getText());
		Assert.assertTrue(shell.bot().button("Finish").isEnabled());

		// If domain name and display name are different, also set the display name
		// The domain name should not change
		if (!domainName.equals(displayName)) {
			shell.bot().textWithLabel("Display Name:").setText(displayName);
			Assert.assertEquals(domainName, shell.bot().textWithLabel("Domain Name:").getText());
		}

		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, displayName);
	}

	/**
	 * Launches a domain, but doesn't connect to it (i.e. it won't be in the explorer view, just in the console)
	 * @param name
	 * @throws CoreException
	 */
	private void launchDomainManager(String name) throws CoreException {
		IFileStore store = EFS.getStore(URI.create("sdrdom:///mgr/DomainManager.spd.xml"));
		Assert.assertTrue("The domain manager profile was not found", store.fetchInfo().exists());

		final DomainManagerLaunchConfiguration domMgr = new DomainManagerLaunchConfiguration();
		domMgr.setArguments("");
		domMgr.setDebugLevel(DebugLevel.Error);
		domMgr.setDomainName(name);
		domMgr.setLaunchConfigName(name);
		domMgr.setLocalDomainName(name);
		domMgr.setSpdPath("/mgr/DomainManager.spd.xml");

		DomainManagerLauncherUtil.launchDomainManager(domMgr, new NullProgressMonitor());
	}
}
