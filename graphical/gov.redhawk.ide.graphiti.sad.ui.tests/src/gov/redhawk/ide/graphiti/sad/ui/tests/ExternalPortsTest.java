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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils.PortState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

/**
 * Test external ports functionality in the SAD design diagram.
 */
public class ExternalPortsTest extends AbstractGraphitiTest {

	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String HARD_LIMIT_2 = "HardLimit_2";

	/**
	 * IDE-965
	 * Change external ports in the overview tab, ensure the diagram reflects the changes
	 */
	@Test
	public void addRemoveExternalPortsViaOverviewTest() {
		String waveformName = "AddRemove_ExternalPort_Overview";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);
		MenuUtils.save(editor);

		// add port via Overview tab
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		bot.button("Add").click();
		SWTBotShell addExternalPortShell = bot.shell("Add external Port");
		final SWTBot wizardBot = addExternalPortShell.bot();
		addExternalPortShell.activate();
		wizardBot.table(1).select(1);
		wizardBot.button("Finish").click();
		Assert.assertEquals("External ports not added", 1, bot.table(0).rowCount());

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		// assert port set to external in diagram
		SWTBotGefEditPart hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, true, HARD_LIMIT_1 + ":uses");

		// remove port via Overview tab
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		bot.table(0).select(0);
		bot.button("Remove").click();
		Assert.assertEquals("External ports not removed", 0, bot.table(0).rowCount());

		// Confirm that no external ports exist in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, false, HARD_LIMIT_1 + ":uses");

	}

	/**
	 * IDE-978
	 * Change external ports in the diagram, ensure the overview tab reflects the changes too.
	 */
	@Test
	public void addRemoveExternalPortsInDiagram() {
		String waveformName = "AddRemove_ExternalPort_Diagram";
		final String HARDLIMIT = "rh.HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 200);
		MenuUtils.save(editor);

		// Make sure all 4 port anchors can be found
		SWTBotGefEditPart hardLimit1UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		SWTBotGefEditPart hardLimit2UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_2);
		SWTBotGefEditPart hardLimit1ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		SWTBotGefEditPart hardLimit2ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_2);

		SWTBotGefEditPart hardLimit1UsesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit1UsesEditPart);
		SWTBotGefEditPart hardLimit2UsesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit2UsesEditPart);
		SWTBotGefEditPart hardLimit1ProvidesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit1ProvidesEditPart);
		SWTBotGefEditPart hardLimit2ProvidesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit2ProvidesEditPart);

		SWTBotGefEditPart[] portEditParts = { hardLimit1UsesEditPart, hardLimit2UsesEditPart, hardLimit1ProvidesEditPart, hardLimit2ProvidesEditPart };
		SWTBotGefEditPart[] portAnchors = { hardLimit1UsesAnchor, hardLimit2UsesAnchor, hardLimit1ProvidesAnchor, hardLimit2ProvidesAnchor };

		// Mark each port as external, and check all ports after each change
		for (int i = 0; i < portEditParts.length; i++) {
			// Check all port styles
			portAnchors[i].select();
			editor.clickContextMenu("Mark External Port");
			for (int j = 0; j < portEditParts.length; j++) {
				boolean isExternal = (j <= i);
				String name = "port at index " + j + " on iteration " + i;
				DiagramTestUtils.assertExternalPort(portEditParts[j], isExternal, name);
			}

			// IDE-978 Check that the overview tab shows the external ports
			DiagramTestUtils.openTabInEditor(editor, "Overview");
			Assert.assertEquals("External port count incorrect", i + 1, bot.table(0).rowCount());

			DiagramTestUtils.openTabInEditor(editor, "Diagram");
		}

		// Un-mark each port as external, and check all ports after each change
		for (int i = 0; i < portEditParts.length; i++) {
			// Check all port styles
			portAnchors[i].select();
			editor.clickContextMenu("Mark Non-External Port");
			for (int j = 0; j < portEditParts.length; j++) {
				boolean isExternal = (j > i);
				DiagramTestUtils.assertExternalPort(portEditParts[j], isExternal, "port at index " + j);
			}

			// IDE-978 Check that the overview tab shows the external ports
			DiagramTestUtils.openTabInEditor(editor, "Overview");
			Assert.assertEquals("External port count incorrect", portEditParts.length - i - 1, bot.table(0).rowCount());

			DiagramTestUtils.openTabInEditor(editor, "Diagram");
		}
	}

	/**
	 * IDE-1329
	 * Port connection highlighting takes precedence over external port highlighting, so we have to ensure that
	 * external port coloring comes back after a selection.
	 */
	@Test
	public void checkExternalPortsAfterSelection() {
		String waveformName = "AddRemove_ExternalPort_Diagram";
		final String HARDLIMIT = "rh.HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 200);

		// Mark external port
		SWTBotGefEditPart hardLimit1UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		SWTBotGefEditPart hardLimit1UsesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit1UsesEditPart);
		hardLimit1UsesAnchor.select();
		editor.clickContextMenu("Mark External Port");

		// Get coordinates for a provides port we can click on as if we're going to connect it
		SWTBotGefEditPart hardLimit2ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_2);
		SWTBotGefEditPart hardLimit2ProvidesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit2ProvidesEditPart);
		Point point = DiagramTestUtils.getDiagramRelativeCenter(hardLimit2ProvidesAnchor);

		// Before mouse-down
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, true, HARD_LIMIT_1 + ":uses");
		PortUtils.assertPortStyling(hardLimit1UsesEditPart, PortState.EXTERNAL_PORT);

		// Mouse-down
		editor.getDragViewer().getCanvas().mouseDown(point.x, point.y);
		PortUtils.assertPortStyling(hardLimit1UsesEditPart, PortState.HIGHLIGHT_FOR_CONNECTION);

		// Mouse-up
		editor.getDragViewer().getCanvas().mouseUp(point.x, point.y);
		PortUtils.assertPortStyling(hardLimit1UsesEditPart, PortState.EXTERNAL_PORT);
	}
}
