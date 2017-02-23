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
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.forms.finder.SWTFormsBot;
import org.eclipse.swtbot.forms.finder.widgets.SWTBotImageHyperlink;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForSeverityMarkers;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Test launching components in the workspace via the link on the Overview tab, the context menu, and a run
 * configuration.
 */
public class WorkspaceLaunchTest extends UIRuntimeTest {

	/**
	 * IDE-1858 - test various ways of launching a shared address space component from the workspace
	 * @param projectName
	 */
	@Test
	public void workspaceLaunchTest() {
		String projectName = "cppComponent";
		String projectNameOne = projectName + "_1";
		createProject(projectName);

		// Launch via the Overview tab
		ProjectExplorerUtils.openProjectInEditor(bot, projectName, projectName + ".spd.xml");
		SWTBotEditor editor = bot.editorByTitle(projectName);
		editor.bot().cTabItem("Overview").activate();

		SWTFormsBot formsBot = new SWTFormsBot();
		SWTBotImageHyperlink link = formsBot.imageHyperlink("Launch resource in the sandbox");
		link.click();
		assertLaunch(projectNameOne);

		// Launch via the context menu
		SWTBotTreeItem node = ProjectExplorerUtils.selectNode(bot, projectName);
		node.contextMenu("Run As").menu("1 Component in the Sandbox").click();
		assertLaunch(projectNameOne);

		// Launch the toolbar "Run" button
		ProjectExplorerUtils.selectNode(bot, projectName).select();
		bot.toolbarDropDownButtonWithTooltip("Run As...").click();
		bot.waitUntil(Conditions.shellIsActive("Run As"));
		SWTBotShell shell = bot.shell("Run As");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertLaunch(projectNameOne);
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
		bot.waitUntil(new WaitForBuild(), 60000);
		bot.waitUntil(new WaitForSeverityMarkers(IMarker.SEVERITY_WARNING), 120000);
	}

	private void assertLaunch(String projectNameOne) {
		SWTBotTreeItem runtimeNode = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, projectNameOne);
		runtimeNode.contextMenu("Terminate").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, projectNameOne);
	}
}
