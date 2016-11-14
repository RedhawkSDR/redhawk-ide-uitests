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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils.PortState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * Test external ports functionality in the SAD design diagram.
 */
public class ExternalPortsTest extends AbstractGraphitiTest {

	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String HARD_LIMIT_2 = "HardLimit_2";
	private static final String HARD_LIMIT_PROVIDES = "dataFloat_in";
	private static final String HARD_LIMIT_USES = "dataFloat_out";

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
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 250, 0);
		MenuUtils.save(editor);

		addPortViaWizard(editor, HARD_LIMIT_1, HARD_LIMIT_USES, "");
		addPortViaWizard(editor, HARD_LIMIT_2, HARD_LIMIT_USES, HARD_LIMIT_USES + "_1");

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		SWTBotGefEditPart hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, true, HARD_LIMIT_1 + ":uses");

		SWTBotGefEditPart hardLimit2UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_2);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, true, HARD_LIMIT_2 + ":uses");

		// remove both ports via Overview tab
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		int numPorts = 2;
		while (numPorts > 0) {
			bot.table(0).select(0);
			bot.button("Remove").click();
			numPorts--;
			Assert.assertEquals("External port not removed", numPorts, bot.table(0).rowCount());
		}

		// Confirm that no external ports exist in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, false, HARD_LIMIT_1 + ":uses");
		hardLimit2UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_2);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, false, HARD_LIMIT_2 + ":uses");
	}

	private void addPortViaWizard(RHBotGefEditor editor, String componentName, String portName, String externalName) {
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		bot.button("Add").click();
		SWTBotShell addExternalPortShell = bot.shell("Add external Port");
		final SWTBot wizardBot = addExternalPortShell.bot();
		addExternalPortShell.activate();

		// Select the component
		wizardBot.table(0).select(componentName);

		// Select the port
		wizardBot.table(1).select(portName);

		// Confirm port was added
		wizardBot.button("Finish").click();
		assertOverviewTableDetails(componentName, portName, externalName);
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

		/**
		 * NOTE - We are checking uses and provides ports independently to make it easier to validate the external ports
		 * section in the overview tab
		 **/
		// ##### Check USES ports ##### //
		// Mark uses ports as external
		SWTBotGefEditPart hardLimit1UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		SWTBotGefEditPart hardLimit2UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_2);
		markPortExternal(editor, hardLimit1UsesEditPart, true);
		markPortExternal(editor, hardLimit2UsesEditPart, true);

		// Check that the overview tab shows the external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		assertOverviewTableDetails(HARD_LIMIT_1, HARD_LIMIT_USES, "");
		assertOverviewTableDetails(HARD_LIMIT_2, HARD_LIMIT_USES, HARD_LIMIT_USES + "_1");

		// Mark uses ports as non-external // TODO: May need to refresh usesEditParts
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		markPortExternal(editor, hardLimit1UsesEditPart, false);
		markPortExternal(editor, hardLimit2UsesEditPart, false);

		// Check that ports were removed from overview tab
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("External ports were not removed correctly", 0, bot.table(0).rowCount());

		// ##### Check PROVIDES ports ##### //
		// Mark provides ports as external
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		SWTBotGefEditPart hardLimit1ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		SWTBotGefEditPart hardLimit2ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_2);
		markPortExternal(editor, hardLimit1ProvidesEditPart, true);
		markPortExternal(editor, hardLimit2ProvidesEditPart, true);

		DiagramTestUtils.openTabInEditor(editor, "Overview");
		assertOverviewTableDetails(HARD_LIMIT_1, HARD_LIMIT_PROVIDES, "");
		assertOverviewTableDetails(HARD_LIMIT_2, HARD_LIMIT_PROVIDES, HARD_LIMIT_PROVIDES + "_1");

		// Mark uses ports as non-external // TODO: May need to refresh usesEditParts
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		markPortExternal(editor, hardLimit1ProvidesEditPart, false);
		markPortExternal(editor, hardLimit2ProvidesEditPart, false);

		// Check that ports were removed from overview tab
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("External ports were not removed correctly", 0, bot.table(0).rowCount());
	}

	private void markPortExternal(RHBotGefEditor editor, SWTBotGefEditPart portEditPart, boolean markExternal) {
		SWTBotGefEditPart portAnchor = DiagramTestUtils.getDiagramPortAnchor(portEditPart);
		portAnchor.select();

		boolean isExternal = true;
		if (markExternal) {
			editor.clickContextMenu("Mark External Port");
		} else {
			editor.clickContextMenu("Mark Non-External Port");
			isExternal = false;
		}

		DiagramTestUtils.assertExternalPort(portEditPart, isExternal, portEditPart.toString());
	}

	/**
	 * IDE-1510
	 * Deleting a component from the SAD diagram doesn't delete its external properties or external ports
	 * @throws IOException
	 */
	@Test
	public void deleteComponentWithExternalPorts() throws IOException {
		String waveformName = "DeleteWithExternalPort";
		final String HARDLIMIT = "rh.HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		MenuUtils.save(editor);

		SWTBotGefEditPart hardLimit1UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		SWTBotGefEditPart hardLimit1ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);

		SWTBotGefEditPart hardLimit1UsesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit1UsesEditPart);
		SWTBotGefEditPart hardLimit1ProvidesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit1ProvidesEditPart);

		SWTBotGefEditPart[] portEditParts = { hardLimit1UsesEditPart, hardLimit1ProvidesEditPart };
		SWTBotGefEditPart[] portAnchors = { hardLimit1UsesAnchor, hardLimit1ProvidesAnchor };

		// Mark each port as external, and check all ports after each change
		for (int i = 0; i < portEditParts.length; i++) {
			portAnchors[i].select();
			editor.clickContextMenu("Mark External Port");
		}

		// Check that model includes external ports
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		Assert.assertTrue("External ports were not updated in .sad.xml", sad.getExternalPorts().getPort().size() == 2);

		// Delete component
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));

		// Check that external ports were removed from model
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		resourceSet = ScaResourceFactoryUtil.createResourceSet();
		resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		Assert.assertNull("External ports were not removed from .sad.xml", sad.getExternalPorts());
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

	private void assertOverviewTableDetails(String componentName, String portName, String externalName) {
		SWTBotTableItem tableItem = bot.table(0).getTableItem(componentName);
		Assert.assertEquals("Incorrect component name displayed", componentName, tableItem.getText(0));
		Assert.assertEquals("Incorrect port name displayed", portName, tableItem.getText(1));
		Assert.assertEquals("Incorrect external port name displayed", externalName, tableItem.getText(2));
	}
}
