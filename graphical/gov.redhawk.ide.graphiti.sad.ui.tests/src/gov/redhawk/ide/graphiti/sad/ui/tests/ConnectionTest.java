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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.ConnectionUtils;
import gov.redhawk.ide.swtbot.diagram.ConnectionUtils.ConnectionState;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils.PortState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHTestBotCanvas;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;

public class ConnectionTest extends AbstractGraphitiTest {

	private String waveformName;
	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String DATA_CONVERTER = "rh.DataConverter";
	private static final String DATA_CONVERTER_1 = "DataConverter_1";

	/**
	 * IDE-731
	 * Users should be able to create connections between components in the Graphiti diagram
	 */
	@Test
	public void connectFeatureTest() {
		waveformName = "IDE-731-Test";
		final String undoTooltip = "Undo connection creation (Ctrl+Z)";
		final String redoTooltip = "Redo connection creation (Shift+Ctrl+Z)";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);

		// Add components to diagram from palette
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		// Get component edit parts and container shapes
		SWTBotGefEditPart sourceComponentEditPart = editor.getEditPart(SIG_GEN_1);
		ContainerShape sourceContainerShape = (ContainerShape) sourceComponentEditPart.part().getModel();
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(HARD_LIMIT_1);
		ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);

		// Confirm that no connections currently exist
		Diagram diagram = DUtil.findDiagram(sourceContainerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertFalse(editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// (IDE-657) Attempt to make an illegal connection and confirm that it was not actually made
		SWTBotGefEditPart illegalTarget = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, illegalTarget);
		Assert.assertTrue("Illegal connection should not have been drawn", diagram.getConnections().isEmpty());

		// Draw the connection and save
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.save(editor);

		// Test to make sure connection was made correctly
		Assert.assertFalse("Connection should exist", diagram.getConnections().isEmpty());

		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).get(0);

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));

		Assert.assertTrue("Only arrowhead decorator should be present", connection.getConnectionDecorators().size() == 1);
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			Assert.assertTrue("Only arrowhead decorator should be present", decorator.getGraphicsAlgorithm() instanceof Polyline);
		}

		// IDE-1582 - Check that connection properties are available in the properties view
		SWTBotGefConnectionEditPart connEditPart = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart).get(0);
		SadConnectInterface connInterface = (SadConnectInterface) ((FreeFormConnection) connEditPart.part().getModel()).getLink().getBusinessObjects().get(0);
		String connectionId = connInterface.getId();
		connEditPart.select();
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem treeItem = propTable.getTreeItem("Id");
		Assert.assertEquals(connectionId, treeItem.cell(1));

		// Check sad.xml new for connection
		editor.setFocus();
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// IDE-1523 Creating connections in design diagram should be undoable/redoable
		bot.toolbarButtonWithTooltip(undoTooltip).click();
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertFalse("The sad.xml should not include a connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		bot.toolbarButtonWithTooltip(redoTooltip).click();
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// Delete connection (IDE-687 - Users need to be able to delete connections)
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertFalse("Source connections should not be empty for this test", sourceConnections.isEmpty());
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}
		MenuUtils.save(editor);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Source connections should be empty, all connections were deleted", sourceConnections.isEmpty());
		Assert.assertTrue("All connections should have been deleted", diagram.getConnections().isEmpty());
	}

	/**
	 * IDE-679 && IDE-657
	 * The creation of a redundant connection results in a yellow warning icon, an error message
	 * ("Redundant connection"), and a dotted red line for the connection path.
	 * When the redundant connection(s) are deleted the error decorators should be removed.
	 */
	@Test
	public void redundantConnectionTest() {
		waveformName = "IDE-679-Test";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);

		// Add components to diagram from palette
		gefBot.waitUntil(new WaitForEditorCondition());
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);

		// Create the first connection, which should be normal
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.NORMAL);

		// Draw redundant connection and check for warning indicators
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.WARNING);

		// Delete one of the connections
		DiagramTestUtils.deleteFromDiagram(editor, connections.get(0));

		// Confirm that warning indicators do not exist for the remaining connection
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.NORMAL);
	}

	/**
	 * IDE-657
	 * Test that connection decorators are drawn for incompatible connections
	 */
	@Test
	public void incompatibleConnectionTest() {
		waveformName = "IDE-657-Test";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);

		// Add components to diagram from palette
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 300, 0);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER_1, "dataOctet");

		// Draw incompatible connection and confirm error decorator exists
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertEquals("Connection was not added", 1, connections.size());

		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.ERROR);
		connections.get(0).select();
		editor.clickContextMenu("Delete");
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertEquals("Connection was not removed", 0, connections.size());

		// Test incompatible connection to component supported interface
		SWTBotGefEditPart lollipopEditPart = DiagramTestUtils.getComponentSupportedInterface(editor, DATA_CONVERTER_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, lollipopEditPart);
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertEquals("Connection was not added", 1, connections.size());
		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.ERROR);
	}

	/**
	 * IDE-1058
	 * Test the compatible port highlight behavior
	 */
	@Test
	public void highlightTest() {
		waveformName = "HighlightTestWF";
		String sigGenPort = "dataFloat_out";
		String dataConPort = "dataFloat";

		WaveformUtils.createNewWaveform(gefBot, waveformName, null);

		// Add components to diagram from palette
		// We need the RHTestBotEditor so we can get the canvas and do direct mouse events
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 300, 0);
		SWTBotGefEditPart usesPort = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1, sigGenPort).children().get(0);
		usesPort.select();

		// Mouse down on target port (direct mouse event)
		RHTestBotCanvas canvas = editor.getDragViewer().getCanvas();
		Point point = DiagramTestUtils.getDiagramRelativeCenter(usesPort);
		canvas.mouseDown(point.x(), point.y());

		// Check data converter ports for color change - (it's actually the anchor that changes color)
		SWTBotGefEditPart dataConEditPart = editor.getEditPart(DATA_CONVERTER_1);
		List<SWTBotGefEditPart> providesPorts = PortUtils.getProvidesPortContainerBots(dataConEditPart);

		for (SWTBotGefEditPart providesPort : providesPorts) {
			ContainerShape shape = (ContainerShape) providesPort.part().getModel();
			ProvidesPortStub portStub = (ProvidesPortStub) Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape);
			if (dataConPort.equals(portStub.getName())) {
				PortUtils.assertPortStyling(providesPort, PortState.HIGHLIGHT_FOR_CONNECTION);
			} else {
				PortUtils.assertPortStyling(providesPort, PortState.NORMAL_PROVIDES);
			}
		}

		// Mouse up on target port
		canvas.mouseUp(point.x(), point.y());

		// Confirm ports return to default color
		for (SWTBotGefEditPart providesPort : providesPorts) {
			PortUtils.assertPortStyling(providesPort, PortState.NORMAL_PROVIDES);
		}
	}
}
