/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sharedlibrary.ui.runtime.tests;

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForBuild.BuildType;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.condition.WaitForTargetSdrRootLoad;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Test exporting a Shared Library project to the Target SDR
 */
public class SharedLibraryExportTest extends UIRuntimeTest {

	public static final String SHARED_LIB_PROJ_NAME = SharedLibraryExportTest.class.getSimpleName() + "_sharedLib";

	public static final String WAVEFORM_PROJ_NAME = SharedLibraryExportTest.class.getSimpleName() + "_waveform";

	/**
	 * IDE-1141
	 * Shared Library projects should be exported to the appropriately named container in the Target SDR
	 */
	@Test
	public void exportSharedLibraryTest() {
		addSdrDomCleanupPath(new Path("/deps/" + SHARED_LIB_PROJ_NAME));

		// Create and Generate
		SharedLibraryUtils.createSharedLibraryProject(bot, SHARED_LIB_PROJ_NAME, "C++ Library");
		SWTBotEditor editor = bot.editorByTitle(SHARED_LIB_PROJ_NAME);
		StandardTestActions.generateProject(bot, editor);
		bot.waitUntil(new WaitForBuild(BuildType.CODEGEN), WaitForBuild.TIMEOUT);
		bot.closeAllEditors();

		// Export to SDR
		SWTBotTreeItem projectNode = ProjectExplorerUtils.waitUntilNodeAppears(bot, SHARED_LIB_PROJ_NAME);
		projectNode.contextMenu("Export to SDR").click();
		bot.waitUntil(new WaitForLaunchTermination(), WaitForBuild.TIMEOUT);

		// Confirm export worked
		findTreeItemAndDelete(new String[] { "Target SDR", "Shared Libraries" }, SHARED_LIB_PROJ_NAME);
	}

	/**
	 * IDE-1141
	 * Make sure any project type that is dragged onto the Shared Libraries container installs to expected location
	 */
	@Test
	public void dndExportTest() {
		addSdrDomCleanupPath(new Path("/deps/" + SHARED_LIB_PROJ_NAME));
		addSdrDomCleanupPath(new Path("/waveforms/" + WAVEFORM_PROJ_NAME));

		// Create and generate shared library project
		SharedLibraryUtils.createSharedLibraryProject(bot, SHARED_LIB_PROJ_NAME, "C++ Library");
		SWTBotEditor editor = bot.editorByTitle(SHARED_LIB_PROJ_NAME);
		StandardTestActions.generateProject(bot, editor);
		bot.waitUntil(new WaitForBuild(BuildType.CODEGEN), WaitForBuild.TIMEOUT);
		bot.closeAllEditors();

		// Create a waveform
		WaveformUtils.createNewWaveform(bot, WAVEFORM_PROJ_NAME, "rh.SigGen");
		bot.editorByTitle(WAVEFORM_PROJ_NAME);
		bot.closeAllEditors();

		// Drag-and-drop both projects onto the Shared Libraries container
		// Confirm projects are installed into their correct locations
		// Clean up
		SWTBotTreeItem sharedLibContainer = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Target SDR" }, "Shared Libraries");

		SWTBotTreeItem sharedLibPrExpNode = ProjectExplorerUtils.selectNode(bot, SHARED_LIB_PROJ_NAME);
		sharedLibPrExpNode.dragAndDrop(sharedLibContainer);
		bot.waitUntil(new WaitForLaunchTermination(), WaitForBuild.TIMEOUT);
		bot.waitUntil(new WaitForTargetSdrRootLoad(), WaitForTargetSdrRootLoad.TIMEOUT);
		findTreeItemAndDelete(new String[] { "Target SDR", "Shared Libraries" }, SHARED_LIB_PROJ_NAME);

		SWTBotTreeItem waveformPrExpNode = ProjectExplorerUtils.selectNode(bot, WAVEFORM_PROJ_NAME);
		waveformPrExpNode.dragAndDrop(sharedLibContainer);
		findTreeItemAndDelete(new String[] { "Target SDR", "Waveforms" }, WAVEFORM_PROJ_NAME);
	}

	private void findTreeItemAndDelete(String[] parentPath, String name) {
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, name);
		treeItem.contextMenu("Delete").click();
		SWTBotShell deleteShell = bot.shell("Delete");
		deleteShell.bot().button("Yes").click();
		bot.waitUntil(Conditions.shellCloses(deleteShell));
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, parentPath, name);
	}
}
