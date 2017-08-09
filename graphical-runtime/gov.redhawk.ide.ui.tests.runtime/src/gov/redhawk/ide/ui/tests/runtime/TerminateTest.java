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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class TerminateTest extends UIRuntimeTest {

	private static final String[] SANDBOX_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	private static final String[] CHALKBOARD_PATH = { "Sandbox", CHALKBOARD };
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
		// Launch and then terminate a component
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, "cpp");
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIG_GEN_1);
		component.contextMenu("Terminate").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, CHALKBOARD_PATH, SIG_GEN_1);

		// Check the console output
		SWTBotView consoleView = ConsoleUtils.showConsole(bot, SIG_GEN_1 + " [Sandbox Component]");
		String consoleText = consoleView.bot().styledText().getText();
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("The IDE detected"));
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("SIGTERM"));
	}

	/**
	 * IDE-1828 - Test terminate operation for shared address space components
	 */
	@Ignore
	@Test
	public void exitCodeInTerminal_SharedAddressSpace() {
		// Launch two components and then terminate a component
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SHARED_ADDRESS_COMP, "cpp");
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_1);
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SHARED_ADDRESS_COMP, "cpp");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_2);

		try {
			ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, COMPONENT_HOST_1);
			Assert.fail("Component host resource should not be visible in the REDHAWK explorer");
		} catch (TimeoutException e) {
			// PASS - component host should not appear in the REDHAWK Explorer
		}

		component.contextMenu("Terminate").click();

		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_1);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_2);

		// Check the console output -
		// Terminating a child component should also terminate the ComponentHost and any siblings
		SWTBotView consoleView = ConsoleUtils.showConsole(bot, COMPONENT_HOST_1 + " [Chalkboard]");
		String consoleText = consoleView.bot().styledText().getText();
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("The IDE detected"));
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("SIGTERM"));
	}

	/**
	 * IDE-2010 Terminate a Sandbox Chalkboard that only contains a ComponentHost
	 */
	@Ignore
	@Test
	public void terminateEmptyChalkboard() {
		// Launch a component and then release it to get a running Component Host with no threaded components
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SHARED_ADDRESS_COMP, "cpp");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_1);
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_1);

		// ComponentHost should not terminate on release
		final SWTBotView consoleView = ConsoleUtils.showConsole(bot, COMPONENT_HOST_1 + " [Chalkboard]");
		try {
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					String consoleText = consoleView.bot().styledText().getText();
					Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("The IDE detected"));
					Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("SIGTERM"));
					return true;
				}

				@Override
				public String getFailureMessage() {
					return "This should fail";
				}
			});
		} catch (TimeoutException e) {
			// PASS
		}

		// Terminate the Chalkboard and confirm the ComponentHost was terminated
		ScaExplorerTestUtils.terminate(bot, SANDBOX_PATH, "Chalkboard");
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				String consoleText = consoleView.bot().styledText().getText();
				Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("The IDE detected"));
				Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("SIGTERM"));
				return true;
			}

			@Override
			public String getFailureMessage() {
				return "ComponentHost failed to terminate";
			}
		});
	}

	/**
	 * IDE-1828 - Test that terminating the component host cleans up child components
	 */
	@Ignore
	@Test
	public void terminateComponentHost() {
		// Launch two components and then terminate the component host
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SHARED_ADDRESS_COMP, "cpp");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_1);
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SHARED_ADDRESS_COMP, "cpp");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_2);

		// Terminate the ComponentHost and make sure the threaded components are removed
		ConsoleUtils.terminateProcess(bot, COMPONENT_HOST_1 + " [Chalkboard]");
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_1);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, CHALKBOARD_PATH, SHARED_ADDRESS_COMP_2);

		// Check the console output for termination message
		SWTBotView consoleView = ConsoleUtils.showConsole(bot, COMPONENT_HOST_1 + " [Chalkboard]");
		String consoleText = consoleView.bot().styledText().getText();
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("The IDE detected"));
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("SIGTERM"));
	}

	/**
	 * IDE-955 After terminating the chalkboard, or all its components, it should no longer be started.
	 */
	@Test
	public void chalkboardStopWhenEmpty() {
		// Try terminating the chalkboard
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, "rh.SigGen", "cpp");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIG_GEN_1);
		ScaExplorerTestUtils.startResourceInExplorer(bot, SANDBOX_PATH, CHALKBOARD);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_PATH, CHALKBOARD);
		ScaExplorerTestUtils.terminate(bot, SANDBOX_PATH, CHALKBOARD);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, SANDBOX_PATH, CHALKBOARD);

		// Try terminating a component in the chalkboard
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, "rh.SigGen", "cpp");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIG_GEN_1);
		ScaExplorerTestUtils.startResourceInExplorer(bot, SANDBOX_PATH, CHALKBOARD);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_PATH, CHALKBOARD);
		ScaExplorerTestUtils.terminate(bot, CHALKBOARD_PATH, SIG_GEN_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, SANDBOX_PATH, CHALKBOARD);
	}
}
