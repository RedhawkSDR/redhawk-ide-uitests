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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Tests for the connec to domain wizard (i.e. the new button in the explorer view).
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
