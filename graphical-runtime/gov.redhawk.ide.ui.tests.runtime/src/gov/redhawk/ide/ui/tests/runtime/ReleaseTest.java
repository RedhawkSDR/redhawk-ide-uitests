/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaDomainManager;
import mil.jpeojtrs.sca.util.CorbaUtils;

public class ReleaseTest extends UIRuntimeTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";

	private static final String SHARED_ADDRESS_COMP = "rh.SharedAddyComp";
	private static final String SHARED_ADDRESS_COMP_1 = "SharedAddyComp_1";
	private static final String SHARED_ADDRESS_COMP_2 = "SharedAddyComp_2";
	private static final String COMPONENT_HOST_1 = "ComponentHost_1";

	/**
	 * IDE-946 Ensure there's details about the process exit in the terminal
	 */
	@Test
	public void exitCodeInTerminal() {
		// Launch and then release a component
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, "cpp");
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SIG_GEN_1);
		component.contextMenu("Release").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SIG_GEN_1);

		// Check the console output
		SWTBotView consoleView = ConsoleUtils.showConsole(bot, SIG_GEN_1 + " [Sandbox Component]");
		String consoleText = consoleView.bot().styledText().getText();
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("exited normally"));
	}

	/**
	 * IDE-1828 - Test release operation for shared address space components
	 */
	@Ignore
	@Test
	public void exitCodeInTerminal_SharedAddressSpace() {
		// Launch two components and then release a component
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SHARED_ADDRESS_COMP, "cpp");
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SHARED_ADDRESS_COMP_1);
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SHARED_ADDRESS_COMP, "cpp");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SHARED_ADDRESS_COMP_2);

		try {
			ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMPONENT_HOST_1);
			Assert.fail("Component host resource should not be visible in the REDHAWK explorer");
		} catch (TimeoutException e) {
			// PASS - component host should not appear in the REDHAWK Explorer
		}

		component.contextMenu("Release").click();

		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SHARED_ADDRESS_COMP_1);

		try {
			ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SHARED_ADDRESS_COMP_2);
			Assert.fail("Releasing a shared address space component should not affect sibling components");
		} catch (TimeoutException e) {
			// PASS - this component should not be released
		}

		// Check the console output - Releasing a child component should not release the ComponentHost
		SWTBotView consoleView = ConsoleUtils.showConsole(bot, COMPONENT_HOST_1 + " [Chalkboard]");
		String consoleText = consoleView.bot().styledText().getText();
		Assert.assertFalse("Process should not have exited on thread release", consoleText.contains("The IDE detected"));
		Assert.assertFalse("Process should not have exited on thread release", consoleText.contains("SIGTERM"));

		// Release the Chalkboard, which should release the ComponentHost
		SWTBotTreeItem chalkboard = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
		chalkboard.contextMenu("Release").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SHARED_ADDRESS_COMP_2);

		// Check the console output
		consoleView = ConsoleUtils.showConsole(bot, COMPONENT_HOST_1 + " [Chalkboard]");
		consoleText = consoleView.bot().styledText().getText();
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("exited normally"));
	}

	/**
	 * IDE-2021 Release an event channel
	 */
	@Test
	public void releaseEventChannel() throws InterruptedException, java.util.concurrent.TimeoutException, CoreException {
		final String DOMAIN_NAME = ReleaseTest.class.getSimpleName();
		ScaExplorerTestUtils.launchDomainViaWizard(bot, DOMAIN_NAME);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);
		SWTBotTreeItem domMgrTreeItem = ScaExplorerTestUtils.getDomain(bot, DOMAIN_NAME);

		// Create an event channel, refresh the model
		ScaDomainManager[] domMgr = new ScaDomainManager[1];
		bot.getDisplay().syncExec(() -> {
			domMgr[0] = (ScaDomainManager) domMgrTreeItem.widget.getData();
		});
		CorbaUtils.invoke(() -> {
			domMgr[0].eventChannelMgr().create("release_me");
			return null;
		}, SWTBotPreferences.TIMEOUT);
		domMgrTreeItem.contextMenu().menu("Refresh").click();

		SWTBotTreeItem eventChanTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { DOMAIN_NAME, "Event Channels" },
			"release_me");
		eventChanTreeItem.contextMenu().menu("Release").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { DOMAIN_NAME, "Event Channels" }, "release_me");
	}
}
