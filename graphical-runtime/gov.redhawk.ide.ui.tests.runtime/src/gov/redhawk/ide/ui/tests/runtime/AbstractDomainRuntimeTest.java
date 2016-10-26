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

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;

import gov.redhawk.ide.sdr.nodebooter.DebugLevel;
import gov.redhawk.ide.sdr.nodebooter.DomainManagerLauncherUtil;
import gov.redhawk.ide.sdr.nodebooter.DomainManagerLaunchConfiguration;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public abstract class AbstractDomainRuntimeTest extends UIRuntimeTest {

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
	
	/**
	 * Launches a domain, but doesn't connect to it (i.e. it won't be in the explorer view, just in the console)
	 * @param name
	 * @throws CoreException
	 */
	protected void launchDomainManager(String name) throws CoreException {
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
	
	
	protected void launchViaNewDomainWizard(String domainName, String[] deviceManagers) {
		explorerView = bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
		explorerView.show();
		viewBot = explorerView.bot();
		
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
	
	public SWTBotView getExplorerView() {
		return explorerView;
	}
	
	public SWTBot getViewBot() {
		return viewBot;
	}
}
