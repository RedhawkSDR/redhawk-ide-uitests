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

import java.util.Arrays;

import org.eclipse.core.resources.IMarker;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.forms.finder.SWTFormsBot;
import org.eclipse.swtbot.forms.finder.widgets.SWTBotImageHyperlink;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForBuild.BuildType;
import gov.redhawk.ide.swtbot.condition.WaitForSeverityMarkers;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Test launching components in the workspace via the link on the Overview tab, the context menu, and a run
 * configuration.
 */
public class WorkspaceLaunchTest extends UIRuntimeTest {
	private static final String PROJECT_NAME = "cppComponent";
	private static final String PROJECT_NAME_1 = PROJECT_NAME + "_1";

	@Before
	@Override
	public void before() throws Exception {
		super.before();
		createProject(PROJECT_NAME);
	}

	/**
	 * IDE-1858 - test various ways of launching a shared address space component from the workspace in run mode
	 * @param projectName
	 */
	@Test
	public void workspaceRunLaunchTest() {

		// Launch via the Overview tab
		ProjectExplorerUtils.openProjectInEditor(bot, PROJECT_NAME, PROJECT_NAME + ".spd.xml");
		final SWTBotEditor editor = bot.editorByTitle(PROJECT_NAME);
		editor.bot().cTabItem("Overview").activate();

		SWTFormsBot formsBot = new SWTFormsBot();
		SWTBotImageHyperlink link = formsBot.imageHyperlink("Launch resource in the sandbox");
		link.click();
		assertLaunch(PROJECT_NAME_1);

		// Launch via the context menu
		SWTBotTreeItem node = ProjectExplorerUtils.selectNode(bot, PROJECT_NAME);
		node.contextMenu("Run As").menu("1 Component in the Sandbox").click();
		assertLaunch(PROJECT_NAME_1);

		// Launch the toolbar "Run" button
		ProjectExplorerUtils.selectNode(bot, PROJECT_NAME).select();
		bot.waitUntil(new DefaultCondition() {

			private String errorMsg = "Failed to click \"Run As... \" toolbar button";

			@Override
			public boolean test() throws Exception {
				// The specific tooltip that pops is inconsistent, need to check for all three possibilities
				try {
					bot.toolbarDropDownButtonWithTooltip("Run").click();
					return true;
				} catch (WidgetNotFoundException e) {
					errorMsg = e.getMessage();
				}

				try {
					bot.toolbarDropDownButtonWithTooltip("Run " + PROJECT_NAME + ".spd.xml").click();
					return true;
				} catch (WidgetNotFoundException e) {
					errorMsg = e.getMessage();
				}

				try {
					bot.toolbarDropDownButtonWithTooltip("Run As...").click();

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
			bot.waitUntil(Conditions.shellIsActive("Run As"));
			SWTBotShell shell = bot.shell("Run As");
			shell.bot().button("OK").click();
			bot.waitUntil(Conditions.shellCloses(shell));
		} catch (TimeoutException e) {
			// PASS, This dialog may not show, depending on which Run version was selected above
		}

		assertLaunch(PROJECT_NAME_1);
	}

	/**
	 * IDE-1710 - test various ways of launching a shared address space component from the workspace in debug mode
	 * @param projectName
	 */
	@Test
	public void workspaceDebugLaunchTest() {
		// Launch via the Overview tab
		ProjectExplorerUtils.openProjectInEditor(bot, PROJECT_NAME, PROJECT_NAME + ".spd.xml");
		final SWTBotEditor editor = bot.editorByTitle(PROJECT_NAME);
		editor.bot().cTabItem("Overview").activate();

		SWTFormsBot formsBot = new SWTFormsBot();
		SWTBotImageHyperlink link = formsBot.imageHyperlink("Debug resource in the sandbox");
		link.click();
		assertDebugLaunch(PROJECT_NAME_1);

		// Launch via the context menu
		SWTBotTreeItem node = ProjectExplorerUtils.selectNode(bot, PROJECT_NAME);
		node.contextMenu("Debug As").menu("1 Component in the Sandbox").click();
		assertDebugLaunch(PROJECT_NAME_1);

		// Launch the toolbar "Debug" button
		ProjectExplorerUtils.selectNode(bot, PROJECT_NAME).select();
		bot.waitUntil(new DefaultCondition() {

			private String errorMsg = "Failed to click \"Debug As... \" toolbar button";

			@Override
			public boolean test() throws Exception {
				// The specific tooltip that pops is inconsistent, need to check for all three possibilities
				try {
					bot.toolbarDropDownButtonWithTooltip("Debug").click();
					return true;
				} catch (WidgetNotFoundException e) {
					errorMsg = e.getMessage();
				}

				try {
					bot.toolbarDropDownButtonWithTooltip("Debug " + PROJECT_NAME + ".spd.xml").click();
					return true;
				} catch (WidgetNotFoundException e) {
					errorMsg = e.getMessage();
				}

				try {
					bot.toolbarDropDownButtonWithTooltip("Debug As...").click();
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
			bot.waitUntil(Conditions.shellIsActive("Debug As"));
			SWTBotShell shell = bot.shell("Debug As");
			shell.bot().button("OK").click();
			bot.waitUntil(Conditions.shellCloses(shell));
		} catch (TimeoutException e) {
			// PASS, This dialog may not show, depending on which Debug version was selected above
		}

		assertDebugLaunch(PROJECT_NAME_1);
	}

	private void createProject(String projectName) {
		// Create an generate a generic C++ shared address space component
		ComponentUtils.createComponentProject(bot, projectName, "C++");

		// Generate
		SWTBotEditor editor = bot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, editor);

		// Default file editor should open
		bot.editorByTitle(projectName + ".cpp");

		// Wait for the build to finish and any error markers to go away, then close editors
		bot.waitUntil(new WaitForBuild(BuildType.CODEGEN), 60000);
		bot.waitUntil(new WaitForSeverityMarkers(IMarker.SEVERITY_WARNING), 120000);
	}

	private void assertLaunch(String projectName) {
		SWTBotTreeItem runtimeNode = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, projectName);
		runtimeNode.contextMenu("Terminate").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, projectName);
	}

	// Need a slightly more complicated check here, since the code will likely get caught debugging
	private void assertDebugLaunch(final String projectName) {
		try {
			SWTBotShell shell = bot.shell("Confirm Perspective Switch");
			shell.bot().checkBox().click();
			shell.bot().button("No").click();
		} catch (WidgetNotFoundException e) {
			// Should only pop once, since we checked the box for "don't show again"
		}

		final String[] parentPath = new String[] { "Sandbox", "Chalkboard" };

		bot.waitUntil(new DefaultCondition() {

			private WidgetNotFoundException lastException = null;

			@Override
			public String getFailureMessage() {
				if (lastException != null) {
					return "Failed waiting for a tree item in the explorer view: " + lastException.toString();
				} else {
					return String.format("Unknown failure while waiting for a tree item in the explorer view. Parent path: %s. Tree item: %s.",
						Arrays.deepToString(parentPath), projectName);
				}
			}

			@Override
			public boolean test() throws Exception {
				try {
					// Hammer F8 to make sure we are not caught in the debug process
					bot.activeShell().pressShortcut(Keystrokes.F8);
					ScaExplorerTestUtils.getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, parentPath, projectName + " < DEBUGGING > ");
				} catch (WidgetNotFoundException e) {
					lastException = e;
					return false;
				}
				lastException = null;
				return true;
			}
		}, 30000);

		SWTBotTreeItem runtimeNode = ScaExplorerTestUtils.getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, parentPath, projectName + " < DEBUGGING > ");
		runtimeNode.contextMenu("Terminate").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, projectName);
	}
}
