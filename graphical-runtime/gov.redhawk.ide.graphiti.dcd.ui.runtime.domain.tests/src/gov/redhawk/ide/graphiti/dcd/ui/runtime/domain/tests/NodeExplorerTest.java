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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import static org.junit.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.impl.ScaDeviceManagerImpl;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;

public class NodeExplorerTest extends AbstractGraphitiDomainNodeRuntimeTest {

	private static final String EDITOR_NAME = "gov.redhawk.core.graphiti.dcd.ui.editor.GraphitiDeviceManagerExplorerEditor";

	private SWTBotGefEditor editor;

	/**
	 * Test the most basic functionality / presence of the device manager explorer diagram.
	 * IDE-998 Opens Graphiti Node Explorer diagram.
	 * IDE-1001 Hide grid on runtime diagram.
	 * IDE-1089 Editor title, missing XML tab
	 * IDE-1194 Check the type of editor that opens as well as its input
	 * IDE-1668 Correct diagram titles and tooltips
	 */
	@Test
	public void deviceManagerExplorerTest() {
		launchDomainAndDevMgr(DEVICE_MANAGER);
		SWTBotEditor nodeEditor = gefBot.editorByTitle(DEVICE_MANAGER);
		editor = gefBot.gefEditor(DEVICE_MANAGER);
		editor.setFocus();

		// IDE-1194
		IEditorPart editorPart = editor.getReference().getEditor(false);
		Assert.assertEquals("Device manager explorer editor class is incorrect", EDITOR_NAME, editorPart.getClass().getName());
		IEditorInput editorInput = editorPart.getEditorInput();
		Assert.assertEquals("Device manager explorer editor's input object is incorrect", ScaFileStoreEditorInput.class, editorInput.getClass());
		Assert.assertEquals("Device manager explorer editor's input SCA object is incorrect", ScaDeviceManagerImpl.class,
			((ScaFileStoreEditorInput) editorInput).getScaObject().getClass());

		// IDE-1668
		Assert.assertEquals("Incorrect title", DEVICE_MANAGER, editorPart.getTitle());
		Assert.assertEquals("Incorrect tooltip", devMgrPath[0] + " - " + DEVICE_MANAGER_SDR_PATH, editorPart.getTitleToolTip());

		// IDE-1089 test
		nodeEditor.bot().cTabItem("DeviceManager.dcd.xml").activate();
		nodeEditor.bot().cTabItem("Diagram").activate();

		// IDE-1347 - Check that properties are visible in the properties view when the diagram is selected
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		editor.click(0, 0);
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties").tree();
		SWTBotTreeItem treeItem = propTable.getTreeItem("DOMAIN_NAME");
		Assert.assertEquals(treeItem.cell(1), getDomainName());

		// check that device is removed from editor when released in the Sca Explorer
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, devMgrPath, GPP_LOCALHOST);

		// IDE-1001 check that grid is hidden on runtime diagram
		Diagram diagram = DiagramTestUtils.getDiagram(editor);
		Assert.assertNotNull("Found in Diagram (model object) on editor", diagram);
		int gridUnit = diagram.getGridUnit();
		assertEquals("Grid is hidden on runtime Node/DeviceMgr diagram", -1, gridUnit); // -1 means it is hidden

		// Confirm that .dcd.xml is visible
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
	}

	/**
	 * IDE-1563 & IDE-1564 DCD runtime explorers should display connections,
	 * User should not be allowed to edit connections via the diagram
	 * Connections added via the ScaExplorer should appear
	 * @throws AWTException
	 */
	@Test
	public void nodeExplorerConnectionTest() throws AWTException {
		final String connectionDeviceManager = "NodeWithConnections";
		final String dOne = "DeviceStub_1";
		final String dTwo = "DeviceStub_2";
		final String doubleInPort = "dataDouble_in";
		final String doubleOutPort = "dataDouble_out";
		final String connectionId = "connection_1";

		// Launch the node to be used for this test
		launchDomainAndDevMgr(connectionDeviceManager);

		// Get reference to the connection
		editor = gefBot.gefEditor(connectionDeviceManager);
		editor.setFocus();
		SWTBotGefEditPart dOneUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, dOne, doubleOutPort);
		SWTBotGefConnectionEditPart dOneConn = DiagramTestUtils.getSourceConnectionsFromPort(editor, dOneUsesEditPart).get(0);

		// Check that connection properties are visible
		dOneConn.select();
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Advanced").tree();
		SWTBotTreeItem treeItem = propTable.getTreeItem("Id");
		Assert.assertEquals(connectionId, treeItem.cell(1));

		// Test that connection cannot be deleted.
		// Have to try and delete with the hot-key since the context menu for "delete" should not exist
		editor.setFocus();
		dOneConn.select();
		dOneConn.click();
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_DELETE);
		robot.keyRelease(KeyEvent.VK_DELETE);

		dOneConn = DiagramTestUtils.getSourceConnectionsFromPort(editor, dOneUsesEditPart).get(0);
		Assert.assertEquals("Expected connection ID was not found", connectionId, getConnectionId(dOneConn));

		// Attempt to add a connection - Assumes the DeviceStub_2 has no outgoing connections
		SWTBotGefEditPart dTwoUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, dTwo, doubleOutPort);
		SWTBotGefEditPart dOneProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, dOne, doubleInPort);
		Assert.assertEquals(dTwo + " is not expected to have any source connections", 0,
			DiagramTestUtils.getSourceConnectionsFromPort(editor, dTwoUsesEditPart).size());

		DiagramTestUtils.drawConnectionBetweenPorts(editor, dTwoUsesEditPart, dOneProvidesEditPart);
		dTwoUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, dTwo, doubleOutPort);
		Assert.assertEquals(dTwo + " is not expected to have any source connections", 0,
			DiagramTestUtils.getSourceConnectionsFromPort(editor, dTwoUsesEditPart).size());

		devMgrPath[0] = devMgrPath[0] + " CONNECTED";
		ScaExplorerTestUtils.connectPortsInScaExplorer(bot, devMgrPath, "newConnection", dTwo, doubleOutPort, dOne, doubleInPort);

		dTwoUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, dTwo, doubleOutPort);
		Assert.assertEquals("The connection for " + dTwo + " was not created", 1,
			DiagramTestUtils.getSourceConnectionsFromPort(editor, dTwoUsesEditPart).size());
	}

	private String getConnectionId(SWTBotGefConnectionEditPart connection) {
		DcdConnectInterface modelObj = (DcdConnectInterface) ((FreeFormConnection) connection.part().getModel()).getLink().getBusinessObjects().get(0);
		return modelObj.getId();
	}
}
