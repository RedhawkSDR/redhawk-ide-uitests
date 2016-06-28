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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformExplorerEditor;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils.PortState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.diagram.RHTestBotCanvas;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.impl.ScaWaveformImpl;

public class WaveformExplorerTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	@Override
	protected String getWaveformName() {
		return "ExampleWaveform06";
	}

	/**
	 * IDE-969 Opens Graphiti diagram using the Waveform Explorer editor.
	 * This editor is "look but don't touch". All design functionality should be disabled.
	 * Runtime functionality (start/stop, plot, etc) should still work.
	 * IDE-1001 Hide grid on runtime diagram.
	 * IDE-1120 - Ensure check that class hierarchy and input type are as expected
	 */
	@Test
	public void waveformExplorerTest() {
		final String[] waveformPath = ScaExplorerTestUtils.joinPaths(DOMAIN_WAVEFORM_PARENT_PATH, new String[] { getWaveFormFullName() });

		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());

		// IDE-1120
		Assert.assertEquals("Editor class should be GraphitiWaveformExplorerEditor", GraphitiWaveformExplorerEditor.class,
			editor.getReference().getPart(false).getClass());
		GraphitiWaveformExplorerEditor editorPart = (GraphitiWaveformExplorerEditor) editor.getReference().getPart(false);
		Assert.assertEquals("Explorer editors in a domain should have ScaWaveform as their input", ScaWaveformImpl.class, editorPart.getWaveform().getClass());

		editor.setFocus();

		// check for components
		SWTBotGefEditPart hardLimit = editor.getEditPart(HARD_LIMIT);
		Assert.assertNotNull(HARD_LIMIT + " component not found in diagram", hardLimit);

		// check that start/plot/stop works
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformPath, HARD_LIMIT_1);

		editor.setFocus();
		DiagramTestUtils.plotPortDataOnComponentPort(editor, HARD_LIMIT, null);
		SWTBotView plotView = ViewUtils.getPlotView(bot);
		plotView.close();

		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, waveformPath, HARD_LIMIT_1);

		// check that DnD does not work - From Target SDR
		DiagramTestUtils.dragComponentFromTargetSDRToDiagram(gefBot, editor, DATA_CONVERTER);
		editor.setFocus();
		SWTBotGefEditPart dataReader = editor.getEditPart(DATA_CONVERTER);
		Assert.assertNull(DATA_CONVERTER + " component should not have be drawn in diagram", dataReader);

		// IDE-1001 check that grid is hidden on runtime diagram
		Diagram diagram = DiagramTestUtils.getDiagram(editor);
		Assert.assertNotNull("Found in Diagram (model object) on editor", diagram);
		int gridUnit = diagram.getGridUnit();
		assertEquals("Grid is hidden on diagram", -1, gridUnit); // -1 means it is hidden
	}

	/**
	 * IDE-1136 & IDE-1524 Target ports should not highlight when a user clicks a port in the Waveform Explorer
	 */
	@Test
	public void waveformExplorerPortStyleTest() {
		final String sigGenPort = "dataFloat_out";

		RHSWTGefBot rhGefBot = new RHSWTGefBot();
		RHBotGefEditor editor = rhGefBot.rhGefEditor(getWaveFormFullName());

		SWTBotGefEditPart usesPort = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1, sigGenPort).children().get(0);
		usesPort.select();

		// Mouse down on target port (direct mouse event)
		RHTestBotCanvas canvas = editor.getDragViewer().getCanvas();
		Point point = DiagramTestUtils.getDiagramRelativeCenter(usesPort);
		canvas.mouseDown(point.x(), point.y());

		// Check data converter ports for color change - (it's actually the anchor that changes color)
		SWTBotGefEditPart hardLimitEditPart = editor.getEditPart(HARD_LIMIT_1);
		List<SWTBotGefEditPart> providesPorts = PortUtils.getProvidesPortContainerBots(hardLimitEditPart);

		for (SWTBotGefEditPart providesPort : providesPorts) {
			PortUtils.assertPortStyling(providesPort, PortState.NORMAL_PROVIDES);
		}

		// Mouse up on target port
		canvas.mouseUp(point.x(), point.y());

		// Confirm ports return to default color
		for (SWTBotGefEditPart providesPort : providesPorts) {
			PortUtils.assertPortStyling(providesPort, PortState.NORMAL_PROVIDES);
		}
	}
}
