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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.logging.ui.LogLevels;

public class DevManagerSandboxContextMenuTest extends AbstractDeviceManagerSandboxTest {

	/**
	 * IDE-661, IDE-662, IDE-663, IDE-664, IDE-665, IDE-666, IDE-667, IDE-1038, IDE-1065, IDE-1325
	 * Test that context menu options appear in Graphiti during runtime,
	 * ensures that the proper views appear based on selection and that views are interactive
	 */
	@Test
	public void runtimeContextMenuTest() {
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		editor.setFocus();
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// Start the component
		DiagramTestUtils.startComponentFromDiagram(editor, DEVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// Test Log Levels
		DiagramTestUtils.changeLogLevelFromDiagram(editor, DEVICE_STUB_1, LogLevels.TRACE);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, DEVICE_STUB_1, LogLevels.TRACE);

		DiagramTestUtils.changeLogLevelFromDiagram(editor, DEVICE_STUB_1, LogLevels.FATAL);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, DEVICE_STUB_1, LogLevels.FATAL);

		//plot port data
		DiagramTestUtils.plotPortDataOnComponentPort(editor, DEVICE_STUB_1, null);
		ViewUtils.getPlotView(bot).close();

		// SRI view test
		DiagramTestUtils.displaySRIDataOnComponentPort(editor, DEVICE_STUB_1, null);
		ViewUtils.getSRIView(bot).close();;

		// Audio/Play port view test
		DiagramTestUtils.playPortDataOnComponentPort(editor, DEVICE_STUB_1, null);
		//wait until audio view populates
		ViewUtils.waitUntilAudioViewPopulates(bot);
		//get audio view
		SWTBotView audioView = ViewUtils.getAudioView(bot);
		String item = audioView.bot().list().getItems()[0];
		Assert.assertTrue(DEVICE_STUB + " not found in Audio Port Playback", item.matches(DEVICE_STUB_1 + ".*"));
		audioView.close();

		//open data list view
		DiagramTestUtils.displayDataListViewOnComponentPort(editor, DEVICE_STUB_1, null);
		ViewUtils.waitUntilDataListViewDisplays(bot);
		ViewUtils.startAquireOnDataListView(bot);
		ViewUtils.getDataListView(bot).close();

		// Snapshot view test
		DiagramTestUtils.displaySnapshotDialogOnComponentPort(editor, DEVICE_STUB_1, null);
		ViewUtils.waitUntilSnapshotDialogDisplays(bot);
		ViewUtils.getSnapshotDialog(bot).close();

		// Monitor ports test
		DiagramTestUtils.displayPortMonitorViewOnComponentPort(editor, DEVICE_STUB_1, null);
		ViewUtils.waitUntilPortMonitorViewPopulates(bot, DEVICE_STUB_1);
		ViewUtils.getPortMonitorView(bot).close();

		//stop component
		DiagramTestUtils.stopComponentFromDiagram(editor, DEVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
	}

	/**
	 * Test terminate from the context menu
	 */
	@Test
	public void terminate() {
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		editor.setFocus();
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(DEVICE_STUB_1));
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
	}
}
