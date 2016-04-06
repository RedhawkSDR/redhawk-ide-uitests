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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Tests that actions performed on a local sandbox waveform in the diagram get reflected in the REDHAWK Explorer view,
 * and
 * vice versa.
 */
public class LocalWaveformRuntimeSyncTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	private static final String SIGGEN_1 = "SigGen_1";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";

	/**
	 * Adds, then removes a component via diagram. Verify the REDHAWK Explorer reflects actions.
	 */
	@Test
	public void addRemoveComponentInDiagram() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		// wait for component to show up in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);

		// delete component from diagram
		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));

		// wait until hard limit component not present in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
	}

	/**
	 * IDE-659
	 * Adds, then removes a port connection via chalkboard diagram. Verify the REDHAWK Explorer reflects actions.
	 */
	@Test
	public void addRemoveConnectionInDiagram() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		// Add two components to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		// wait for component to show up in REDHAWK Explorer (connections don't always work correctly if you don't wait.
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);

		// Draw connection
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		// wait for connection to show up in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN_1, "dataFloat_out",
			"connection_1");

		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}

		// wait until connection not present in REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN_1, "dataFloat_out",
			"connection_1");
	}

	/**
	 * IDE-659
	 * Adds components, starts/stops them from Diagram. Verify the REDHAWK Explorer reflects actions.
	 */
	@Test
	public void startStopComponentsFromDiagram() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		// Add one additional component to the diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);

		// verify both are stopped
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, getWaveformPath(), SIGGEN_1);

		// start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, getWaveformPath(), SIGGEN_1);

		// start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, getWaveformPath(), SIGGEN_1);

		// stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, getWaveformPath(), SIGGEN_1);

		// stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, getWaveformPath(), SIGGEN_1);

		// start both components
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT_1);
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, getWaveformPath(), SIGGEN_1);
	}

	/**
	 * IDE-659
	 * Adds, then removes component connections via REDHAWK Explorer. Verify its no longer present in Diagram
	 */
	@Test
	public void addRemoveConnectionInExplorer() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		// Add an additional component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);

		// create connection between components via REDHAWK Explorer
		List<String> parentPath = new ArrayList<String>();
		Collections.addAll(parentPath, LOCAL_WAVEFORM_PARENT_PATH);
		parentPath.add(getWaveFormFullName());
		ScaExplorerTestUtils.connectPortsInScaExplorer(bot, parentPath.toArray(new String[parentPath.size()]), "connection_1", SIGGEN_1, "dataFloat_out",
			HARD_LIMIT_1, "dataFloat_in");

		// verify connection exists in diagram
		DiagramTestUtils.waitUntilConnectionDisplaysInDiagram(bot, editor, HARD_LIMIT_1);

		// disconnect connection_1 via REDHAWK Explorer
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, getWaveFormFullName(), "connection_1", SIGGEN_1,
			"dataFloat_out");

		// verify connection does NOT exist in diagram
		DiagramTestUtils.waitUntilConnectionDisappearsInDiagram(bot, editor, HARD_LIMIT_1);
	}

	/**
	 * IDE-659
	 * Adds components, starts/stops them from the explorer view and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromExplorer() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		// Launch an additional component from TargetSDR
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT_1);

		// verify hard limit stopped
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);

		// start hard limit from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);

		// start SigGen from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, getWaveformPath(), SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);

		// stop hard limit from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);

		// stop SigGen from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, getWaveformPath(), SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);

		// start both components
		ScaExplorerTestUtils.startResourceInExplorer(bot, getWaveformPath(), HARD_LIMIT_1);
		ScaExplorerTestUtils.startResourceInExplorer(bot, getWaveformPath(), SIGGEN_1);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);

		// stop chalkboard
		ScaExplorerTestUtils.stopResourceInExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);

		// start chalkboard
		ScaExplorerTestUtils.startResourceInExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STARTED);
	}
}
