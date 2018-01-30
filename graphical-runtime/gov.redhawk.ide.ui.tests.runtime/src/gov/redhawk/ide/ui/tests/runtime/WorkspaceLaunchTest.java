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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.forms.finder.SWTFormsBot;
import org.eclipse.swtbot.forms.finder.widgets.SWTBotImageHyperlink;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForBuild.BuildType;
import gov.redhawk.ide.swtbot.condition.WaitForCppIndexer;
import gov.redhawk.ide.swtbot.condition.WaitForSeverityMarkers;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Test launching components in the workspace via the link on the Overview tab, the context menu, and a run
 * configuration.
 */
public class WorkspaceLaunchTest extends UIRuntimeTest {

	private static final String PROJECT_NAME = "cppComponent";
	private static final String PROJECT_NAME_1 = PROJECT_NAME + "_1";

	private Boolean oldAutomatedMode;

	@After
	public void after() throws CoreException {
		if (oldAutomatedMode != null) {
			// Switch error dialogs back to previous setting
			ErrorDialog.AUTOMATED_MODE = oldAutomatedMode;
			oldAutomatedMode = null;
		}
		super.after();
	}

	/**
	 * IDE-1965 Launch a shared address space component in run mode, but without generating it; check error message
	 */
	@Test
	public void badWorkspaceRunLaunchTest() {
		badWorkspaceLaunchTest("Launch resource in the sandbox");
	}

	/**
	 * IDE-1965 Launch a shared address space component in debug mode, but without generating it; check error message
	 */
	@Test
	public void badWorkspaceDebugLaunchTest() {
		badWorkspaceLaunchTest("Debug resource in the sandbox");
		SWTBotShell shell = bot.shell("Confirm Perspective Switch");
		shell.bot().button("No").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	private void badWorkspaceLaunchTest(String linkText) {
		createProject(PROJECT_NAME, false);

		// Disable hiding errors
		oldAutomatedMode = ErrorDialog.AUTOMATED_MODE;
		ErrorDialog.AUTOMATED_MODE = false;

		// Attempt launch via the Overview tab
		ProjectExplorerUtils.openProjectInEditor(bot, PROJECT_NAME, PROJECT_NAME + ".spd.xml");
		final SWTBotEditor editor = bot.editorByTitle(PROJECT_NAME);
		editor.bot().cTabItem("Overview").activate();
		SWTFormsBot formsBot = new SWTFormsBot(editor.getWidget());
		SWTBotImageHyperlink link = formsBot.imageHyperlink(linkText);
		link.click();

		// Assert error
		bot.waitUntil(Conditions.shellIsActive("Problem Occurred"), 15000);
		SWTBotShell problemDialog = bot.shell("Problem Occurred");
		String errorMsg = problemDialog.bot().label(1).getText();
		Assert.assertTrue("Error message didn't seem descriptive enough", errorMsg.contains("does not exist (error number CF_EEXIST)"));
		problemDialog.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(problemDialog));

		// Terminate ComponentHost
		ConsoleUtils.removeTerminatedLaunches(bot);
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				ConsoleUtils.terminateProcess(WorkspaceLaunchTest.this.bot, "ComponentHost");
				return true;
			}

			@Override
			public String getFailureMessage() {
				return "Failed to terminate ComponentHost";
			}
		});
	}

	/**
	 * IDE-1989
	 * Check that an error is not thrown when terminating a C++ workspace component launched in debug mode
	 */
	@Test
	public void terminateDebugLaunch() {
		createProject(PROJECT_NAME, true);

		// Launch via the context menu
		SWTBotTreeItem node = ProjectExplorerUtils.selectNode(bot, PROJECT_NAME);
		node.contextMenu("Debug As").menu("1 Component in the Sandbox").click();
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, PROJECT_NAME_1 + " < DEBUGGING > ");
		ConsoleUtils.removeTerminatedLaunches(bot);
		ConsoleUtils.terminateProcess(bot, "ComponentHost_1 < DEBUGGING >");
		SWTBotShell shell = bot.shell("Confirm Perspective Switch");
		shell.bot().button("No").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	/**
	 * IDE-1858 - test various ways of launching a shared address space component from the workspace in run mode
	 * @param projectName
	 */
	@Test
	public void workspaceRunLaunchTest() {
		workspaceLaunchTest("Launch resource in the sandbox", "Run", false);
	}

	/**
	 * IDE-1710 - test various ways of launching a shared address space component from the workspace in debug mode
	 * @param projectName
	 */
	@Test
	public void workspaceDebugLaunchTest() {
		workspaceLaunchTest("Debug resource in the sandbox", "Debug", true);
	}

	private void workspaceLaunchTest(final String linkText, final String mode, final boolean isDebug) {
		final String modeAs = mode + " As";

		createProject(PROJECT_NAME, true);

		// Launch via the Overview tab
		ProjectExplorerUtils.openProjectInEditor(bot, PROJECT_NAME, PROJECT_NAME + ".spd.xml");
		final SWTBotEditor editor = bot.editorByTitle(PROJECT_NAME);
		editor.bot().cTabItem("Overview").activate();

		SWTFormsBot formsBot = new SWTFormsBot(editor.bot().getFinder());
		SWTBotImageHyperlink link = formsBot.imageHyperlink(linkText);
		link.click();
		if (isDebug) {
			assertDebugLaunch(PROJECT_NAME_1);
		} else {
			assertLaunch(PROJECT_NAME_1);
		}

		// Launch via the context menu
		SWTBotTreeItem node = ProjectExplorerUtils.selectNode(bot, PROJECT_NAME);
		node.contextMenu(modeAs).menu("1 Component in the Sandbox").click();
		if (isDebug) {
			assertDebugLaunch(PROJECT_NAME_1);
		} else {
			assertLaunch(PROJECT_NAME_1);
		}

		// Launch via the toolbar button
		ProjectExplorerUtils.selectNode(bot, PROJECT_NAME).select();
		bot.waitUntil(new DefaultCondition() {

			private String errorMsg = "Failed to click \"" + modeAs + "... \" toolbar button";

			@Override
			public boolean test() throws Exception {
				// The specific tooltip that pops is inconsistent, need to check for all three possibilities
				try {
					bot.toolbarDropDownButtonWithTooltip(mode).click();
					return true;
				} catch (WidgetNotFoundException e) {
					errorMsg = e.getMessage();
				}

				try {
					bot.toolbarDropDownButtonWithTooltip(mode + " " + PROJECT_NAME + ".spd.xml").click();
					return true;
				} catch (WidgetNotFoundException e) {
					errorMsg = e.getMessage();
				}

				try {
					bot.toolbarDropDownButtonWithTooltip(modeAs + "...").click();
					return true;
				} catch (WidgetNotFoundException e) {
					errorMsg = e.getMessage();
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return errorMsg;
			}
		});

		try {
			bot.waitUntil(Conditions.shellIsActive(modeAs));
			SWTBotShell shell = bot.shell(modeAs);
			shell.bot().button("OK").click();
			bot.waitUntil(Conditions.shellCloses(shell));
		} catch (TimeoutException e) {
			// PASS, This dialog may not show, depending on which Run version was selected above
		}

		if (isDebug) {
			assertDebugLaunch(PROJECT_NAME_1);
		} else {
			assertLaunch(PROJECT_NAME_1);
		}
	}

	private void createProject(String projectName, boolean generateCode) {
		// Create an generate a generic C++ shared address space component
		ComponentUtils.createComponentProject(bot, projectName, "C++");

		// Generate
		SWTBotEditor editor = bot.editorByTitle(projectName);
		if (!generateCode) {
			return;
		}

		StandardTestActions.generateProject(bot, editor);

		// Default file editor should open
		bot.editorByTitle(projectName + ".cpp");

		// Wait for the build to finish and any error markers to go away, then close editors
		bot.waitUntil(new WaitForBuild(BuildType.CODEGEN), WaitForBuild.TIMEOUT);
		bot.waitUntil(new WaitForCppIndexer(), WaitForCppIndexer.TIMEOUT);
		bot.waitUntil(new WaitForSeverityMarkers(IMarker.SEVERITY_WARNING), WaitForSeverityMarkers.TIMEOUT);
	}

	// Wait for the component to register and appear in the explorer view
	private void assertLaunch(String projectName) {
		SWTBotTreeItem runtimeNode = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, projectName);
		runtimeNode.contextMenu("Terminate").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, projectName);
	}

	// Wait for the component to register and appear in the explorer view
	private void assertDebugLaunch(String projectName) {
		final String[] parentPath = new String[] { "Sandbox", "Chalkboard" };
		SWTBotTreeItem runtimeNode = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, projectName + " < DEBUGGING > ");
		runtimeNode.contextMenu("Terminate").click();
		SWTBotShell shell = bot.shell("Confirm Perspective Switch");
		shell.bot().button("No").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, parentPath, projectName);
	}
}
