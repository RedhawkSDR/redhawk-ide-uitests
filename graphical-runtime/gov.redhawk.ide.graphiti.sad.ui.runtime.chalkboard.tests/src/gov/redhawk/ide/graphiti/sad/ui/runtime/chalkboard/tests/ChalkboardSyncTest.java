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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * IDE-659 Tests the model map (i.e. sync between diagram and model state) by comparing with the explorer.
 */
public class ChalkboardSyncTest extends AbstractGraphitiChalkboardTest {

	/**
	 * Add then release a component via chalkboard diagram. Verify its no longer present in REDHAWK Explorer or diagram.
	 */
	@Test
	public void addReleaseInDiagram() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		// Release component in diagram
		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
	}

	/**
	 * Add then terminate a component via chalkboard diagram. Verify it's no longer present in REDHAWK Explorer or
	 * diagram.
	 */
	@Test
	public void addTerminateInDiagram() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		// Terminate component in diagram
		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
	}

	/**
	 * Add then removes a connection via chalkboard diagram. Verify it's no longer present in REDHAWK Explorer.
	 */
	@Test
	public void addRemoveConnectionInDiagram() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add two components to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		// Get port edit parts and draw connection
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1, "dataFloat_out", "connection_1");

		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		DiagramTestUtils.deleteFromDiagram(editor, sourceConnections.get(0));
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1, "dataFloat_out", "connection_1");
	}

	/**
	 * Adds components, starts/stops them from Chalkboard Diagram and verifies against REDHAWK Explorer.
	 */
	@Test
	public void startStopInDiagram() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add two components to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		// verify stopped
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// TODO: start both components

		// TODO: stop chalkboard via menu

		// TODO: start chalkboard via menu

		// cleanup
		ScaExplorerTestUtils.terminate(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
	}

	/**
	 * Add then removes a component via REDHAWK Explorer. Verify it's no longer present in the diagram.
	 */
	@Test
	public void addReleaseInExplorer() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);

		// Release component from REDHAWK Explorer
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, HARD_LIMIT_1);
	}

	@Test
	public void addTerminateInExplorer() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);

		// terminate chalkboard
		ScaExplorerTestUtils.terminateWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, HARD_LIMIT_1);
	}

	/**
	 * Add then remove a connection via REDHAWK Explorer. Verify it's no longer present in the diagram.
	 */
	@Test
	public void addRemoveConnectionInExplorer() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Launch two components from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");

		// verify components were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIGGEN_1);

		// create connection between components via REDHAWK Explorer
		ScaExplorerTestUtils.connectPortsInScaExplorer(bot, CHALKBOARD_PATH, "connection_1", SIGGEN_1, "dataFloat_out", HARD_LIMIT_1, "dataFloat_in");
		DiagramTestUtils.waitUntilConnectionDisplaysInDiagram(bot, editor, HARD_LIMIT);

		// disconnect connection_1 via REDHAWK Explorer
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, "connection_1", SIGGEN_1, "dataFloat_out");
		DiagramTestUtils.waitUntilConnectionDisappearsInDiagram(bot, editor, HARD_LIMIT);
	}

	/**
	 * Adds components, starts/stops them from REDHAWK Explorer and verifies components in diagram reflect appropriate
	 * state.
	 */
	@Test
	public void startStopFromExplorer() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Launch two components from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIGGEN_1);

		// verify stopped
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);

		// start hard limit from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// start SigGen from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// stop hard limit from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// stop SigGen from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// start both components
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// stop chalkboard
		ScaExplorerTestUtils.stopResourceInExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);

		// start chalkboard
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);

		// terminate, then stop chalkboard
		ScaExplorerTestUtils.terminate(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
	}

	/**
	 * IDE-1205 Make sure properties match whether component is selected in diagram or REDHAWK Explorer.
	 */
	@Test
	public void changePropertiesViaExplorerSelection() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();

		// Select component in REDHAWK explorer tree first
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1).select().click();

		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties").tree();
		SWTBotTreeItem magItem = propTable.getTreeItem("magnitude");
		Assert.assertEquals(magItem.cell(1), "100.0");
		magItem.select().click(1);
		gefBot.viewByTitle("Properties").bot().text().setText("50");

		// Click in diagram outside of component first
		// Workaround for issue where diagram component does not populate
		// properties view if selected right after creation
		editor.rootEditPart().click();
		editor.click(SIGGEN_1);
		propTable = gefBot.viewByTitle("Properties").bot().tree();
		magItem = propTable.getTreeItem("magnitude");
		Assert.assertEquals("Property has wrong value", "50.0", magItem.cell(1));
	}

	/**
	 * IDE-1205 Make sure properties match whether component is selected in diagram or REDHAWK Explorer.
	 */
	@Test
	public void changePropertiesViaDiagramSelection() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();

		// Click in diagram outside of component first
		// Workaround for issue where diagram component does not populate
		// properties view if selected right after creation
		editor.rootEditPart().click();
		editor.click(SIGGEN_1);
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties").tree();
		SWTBotTreeItem magItem = propTable.getTreeItem("magnitude");
		Assert.assertEquals(magItem.cell(1), "100.0");
		magItem.select().click(1);
		gefBot.viewByTitle("Properties").bot().text().setText("50");

		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1).select().click();
		propTable = gefBot.viewByTitle("Properties").bot().tree();
		magItem = propTable.getTreeItem("magnitude");
		Assert.assertEquals("Property has wrong value", "50.0", magItem.cell(1));
	}

}
