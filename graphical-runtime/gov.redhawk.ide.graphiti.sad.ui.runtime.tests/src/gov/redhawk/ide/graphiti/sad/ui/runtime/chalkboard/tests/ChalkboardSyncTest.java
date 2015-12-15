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
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ChalkboardSyncTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	/**
	 * IDE-659
	 * Adds, then removes a component via chalkboard diagram. Verify its no
	 * longer present in REDHAWK Explorer or Diagram
	 */
	@Test
	public void addRemoveComponentInChalkboardDiagram() {
		editor = openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		// wait for component to show up in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// RELEASE component from diagram
		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));

		// wait until hard limit component not present in REDHAWK Explorer & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		Assert.assertNull(editor.getEditPart(HARD_LIMIT_1));
	}
	
	/**
	 * IDE-659
	 * Adds, then terminates a component via chalkboard diagram. Verify it's no
	 * longer present in REDHAWK Explorer or Diagram
	 */
	@Test
	public void addTerminateComponentInChalkboardDiagram() {
		editor = openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		// wait for component to show up in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// TERMINATE component from diagram
		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));

		// wait until hard limit component not present in REDHAWK Explorer & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		Assert.assertNull(editor.getEditPart(HARD_LIMIT_1));
	}

	/**
	 * IDE-659
	 * Adds, then removes a component connections via chalkboard diagram. Verify its no
	 * longer present in REDHAWK Explorer
	 */
	@Test
	public void addRemoveComponentConnectionInChalkboardDiagram() {
		editor = openChalkboardDiagram(gefBot);

		// Add two components to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		// wait for component to show up in REDHAWK Explorer (connections don't always work correctly if you don't
		// wait.
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIGGEN_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);

		// Draw connection
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		// wait for connection to show up in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1, "dataFloat_out", "connection_1");

		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}

		// wait until connection not present in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1, "dataFloat_out", "connection_1");
	}

	/**
	 * IDE-659
	 * Adds components, starts/stops them from Chalkboard Diagram and verifies
	 * components in REDHAWK Explorer reflect changes
	 * 
	 */
	@Test
	public void startStopComponentsFromChalkboardDiagram() {
		editor = openChalkboardDiagram(gefBot);

		// Add two components to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// verify stopped
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIGGEN_1);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// start both components
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// cleanup
		ScaExplorerTestUtils.terminateWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
	}

	/**
	 * IDE-659
	 * Adds, then removes a component via REDHAWK Explorer. Verify its no
	 * longer present in Diagram
	 */
	@Test
	public void addRemoveComponentInScaExplorer() {
		editor = openChalkboardDiagram(gefBot);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);

		// delete component from REDHAWK Explorer
		ScaExplorerTestUtils.terminateLocalResourceInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, HARD_LIMIT_1);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);

		// terminate chalkboard
		ScaExplorerTestUtils.terminateWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, HARD_LIMIT_1);
	}

	/**
	 * IDE-659
	 * Adds, then removes component connections via REDHAWK Explorer. Verify its no
	 * longer present in Chalkboard Diagram
	 */
	@Test
	public void addRemoveComponentConnectionInScaExplorer() {
		editor = openChalkboardDiagram(gefBot);

		// Launch two components from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");

		// verify components were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIGGEN_1);

		// create connection between components via REDHAWK Explorer
		ScaExplorerTestUtils.connectComponentPortsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, "connection_1", SIGGEN_1, "dataFloat_out", HARD_LIMIT_1,
			"dataFloat_in");
		DiagramTestUtils.waitUntilConnectionDisplaysInDiagram(bot, editor, HARD_LIMIT);

		// disconnect connection_1 via REDHAWK Explorer
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, "connection_1", SIGGEN_1, "dataFloat_out");
		DiagramTestUtils.waitUntilConnectionDisappearsInDiagram(bot, editor, HARD_LIMIT);
	}

	/**
	 * IDE-659
	 * Adds components, starts/stops them from REDHAWK Explorer and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromScaExplorer() {
		editor = openChalkboardDiagram(gefBot);

		// Launch two components from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");

		// verify components were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIGGEN);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// verify hard limit stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT_1);

		// start hard limit from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		// verify hardlimit started but siggen did not
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN_1);

		// start SigGen from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// verify SigGen started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN_1);

		// stop hard limit from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		// verify hardlimit stopped, SigGen started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN_1);

		// stop SigGen from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// verify SigGen stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN_1);

		// start both components
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);

		// verify both started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN_1);

		// stop chalkboard
		ScaExplorerTestUtils.stopResourceInExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// verify both components stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN_1);

		// start chalkboard
		ScaExplorerTestUtils.startResourceInExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// verify both components started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN_1);

		// terminate, then stop chalkboard
		ScaExplorerTestUtils.terminateWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// wait for Chalkboard to be empty
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// stop chalkboard
		ScaExplorerTestUtils.stopResourceInExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// wait for Chalkboard to be stopped
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformStopped(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
	}
	
	/**
	 * IDE-1205 Make sure properties match whether component is selected in diagram or REDHAWK Explorer.
	 */
	@Test
	public void changePropertiesInScaExplorer() {
		editor = openChalkboardDiagram(gefBot);
		
		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		
		// Select component in REDHAWK explorer tree first
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1).select().click();
		
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Component Properties").bot().tree();
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
	public void changePropertiesInChalkboardDiagram() {
		editor = openChalkboardDiagram(gefBot);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIGGEN, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, SIGGEN_1);
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		
		// Click in diagram outside of component first
		// Workaround for issue where diagram component does not populate 
		// properties view if selected right after creation
		editor.rootEditPart().click();
		editor.click(SIGGEN_1);
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Component Properties").bot().tree();
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
