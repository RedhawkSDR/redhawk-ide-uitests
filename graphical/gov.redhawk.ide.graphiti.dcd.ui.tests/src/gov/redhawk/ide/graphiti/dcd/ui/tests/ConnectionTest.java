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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
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
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.ConnectionUtils;
import gov.redhawk.ide.swtbot.diagram.ConnectionUtils.ConnectionState;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils.PortState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHTestBotCanvas;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class ConnectionTest extends AbstractGraphitiTest {

	private RHBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";

	private static final String GPP = "GPP";
	private static final String GPP_1 = "GPP";

	private static final String DEVICE_STUB = "DeviceStub";
	private static final String DEVICE_STUB_1 = "DeviceStub_1";
	private static final String DEVICE_STUB_2 = "DeviceStub_2";
	private static final String DEVICE_STUB_OUT = "dataFloat_out";
	private static final String DEVICE_STUB_IN = "dataFloat_in";

	private static final String SERVICE_STUB = "ServiceStub";
	private static final String SERVICE_STUB_1 = "ServiceStub";

	/**
	 * IDE-985 Users should be able to create connections between devices/services in the Graphiti diagram
	 * IDE-687 - Users need to be able to delete connections
	 * IDE-1523 Creating connections in design diagram should be undoable/redoable
	 */
	@Test
	public void connectFeatureTest() {
		projectName = "Connection-Test";
		final String undoTooltip = "Undo connection creation (Ctrl+Z)";
		final String redoTooltip = "Redo connection creation (Shift+Ctrl+Z)";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Add to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 200);

		// Get component edit parts and container shapes
		SWTBotGefEditPart sourceEditPart = editor.getEditPart(DEVICE_STUB_1);
		ContainerShape sourceShape = (ContainerShape) sourceEditPart.part().getModel();
		SWTBotGefEditPart targetEditPart = editor.getEditPart(DEVICE_STUB_2);
		ContainerShape targetShape = (ContainerShape) targetEditPart.part().getModel();

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, DEVICE_STUB_OUT);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_2, DEVICE_STUB_IN);

		// Confirm that no connections currently exist
		Diagram diagram = DUtil.findDiagram(sourceShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertFalse(editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// Attempt to make an illegal connection and confirm that it was not actually made
		SWTBotGefEditPart illegalTarget = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_2);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, illegalTarget);
		Assert.assertTrue("Illegal connection should not have been drawn", diagram.getConnections().isEmpty());

		// Draw the connection and save
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.save(editor);

		// Test to make sure connection was made correctly
		Assert.assertFalse("Connection should exist", diagram.getConnections().isEmpty());

		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetShape).get(0);

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));

		// IDE-1582 - Check that connection properties are available in the properties view
		SWTBotGefConnectionEditPart connEditPart = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart).get(0);
		DcdConnectInterface connInterface = (DcdConnectInterface) ((FreeFormConnection) connEditPart.part().getModel()).getLink().getBusinessObjects().get(0);
		String connectionId = connInterface.getId();
		connEditPart.select();
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem treeItem = propTable.getTreeItem("Id");
		Assert.assertEquals(connectionId, treeItem.cell(1));

		// Check dcd.xml new for connection
		editor.setFocus();
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The dcd.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// IDE-1523 Creating connections in design diagram should be undoable/redoable
		bot.toolbarButtonWithTooltip(undoTooltip).click();
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertFalse("The dcd.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		bot.toolbarButtonWithTooltip(redoTooltip).click();
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The dcd.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
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
	 * IDE-1132
	 * Test that connection decorators are drawn for incompatible connections
	 */
	@Test
	public void incompatibleConnectionTest() {
		projectName = "incompat_connect_nodes";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);

		// Add devices and a service to diagram from palette
		editor = gefBot.rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SERVICE_STUB, 300, 300);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_1, DEVICE_STUB_IN);

		// Draw incompatible connection between ports and confirm error decoration
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not added", connections.size() == 1);
		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.ERROR);

		connections.get(0).select();
		editor.clickContextMenu("Delete");
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not removed", connections.size() == 0);

		// Draw incompatible connection to component supported interface (DEVICE) and confirm error decoration
		SWTBotGefEditPart lollipopEditPart = DiagramTestUtils.getComponentSupportedInterface(editor, DEVICE_STUB_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, lollipopEditPart);
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not added", connections.size() == 1);
		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.ERROR);

		connections.get(0).select();
		editor.clickContextMenu("Delete");
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not removed", connections.size() == 0);

		// Test incompatible connection to component supported interface (SERVICE) and confirm error decoration
		lollipopEditPart = DiagramTestUtils.getComponentSupportedInterface(editor, SERVICE_STUB_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, lollipopEditPart);
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not added", connections.size() == 1);
		ConnectionUtils.assertConnectionStyling(connections.get(0), ConnectionState.ERROR);
	}

	/**
	 * IDE-1388
	 * Test the compatible port highlight behavior
	 */
	@Test
	public void highlightTest() {
		projectName = "HighlightTestNode";
		String deviceStubUses = "dataFloat_out";
		String deviceStubProvides = "dataFloat_in";

		NodeUtils.createNewNodeProject(bot, projectName, DOMAIN_NAME);

		// Add DeviceStub to the diagram from the palette
		editor = gefBot.rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 10, 10);
		SWTBotGefEditPart usesPort = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB, deviceStubUses).children().get(0);
		usesPort.select();

		// Mouse down on target port (direct mouse event)
		RHTestBotCanvas canvas = editor.getDragViewer().getCanvas();
		Point point = DiagramTestUtils.getDiagramRelativeCenter(usesPort);
		canvas.mouseDown(point.x(), point.y());

		// Check the corresponding provides port for color change - (it's actually the anchor that changes color)
		SWTBotGefEditPart deviceStubEditPart = editor.getEditPart(DEVICE_STUB);
		List<SWTBotGefEditPart> providesPorts = PortUtils.getProvidesPortContainerBots(deviceStubEditPart);

		for (SWTBotGefEditPart providesPort : providesPorts) {
			ContainerShape shape = (ContainerShape) providesPort.part().getModel();
			ProvidesPortStub portStub = (ProvidesPortStub) Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape);
			if (deviceStubProvides.equals(portStub.getName())) {
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
