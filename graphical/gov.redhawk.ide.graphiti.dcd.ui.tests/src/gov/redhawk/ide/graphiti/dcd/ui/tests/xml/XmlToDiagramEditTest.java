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
package gov.redhawk.ide.graphiti.dcd.ui.tests.xml;

import java.util.List;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

/**
 * Test class that deals with editing elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramEditTest extends AbstractGraphitiTest {

	private RHBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String DEVICE_STUB = "DeviceStub";
	private static final String DEVICE_STUB_1 = "DeviceStub_1";
	private static final String DEVICE_STUB_2 = "DeviceStub_2";

	/**
	 * IDE-994
	 * Test editing device properties in the dcd.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editDeviceInXmlTest() {
		projectName = "Edit_Device_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Add devices to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		MenuUtils.save(editor);

		// Edit content of dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace(DEVICE_STUB_1, DEVICE_STUB_2);
		editor.toTextEditor().setText(editorText);
		// TODO: Currently only typing in the XML editor seems to force an update. Why?
		editor.toTextEditor().pressShortcut(Keystrokes.SPACE, Keystrokes.BS);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		gefBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB_2) != null;
			}

			@Override
			public String getFailureMessage() {
				return "Usage Name did not update correctly. Expected [" + DEVICE_STUB_2 + "] Found [" + DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB).getUsageName()
					+ "]";
			}
		}, 10000, 1000);

		DcdComponentInstantiation deviceObj = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB_2);
		Assert.assertEquals("Component ID did not update correctly", projectName + ":" + DEVICE_STUB_2, deviceObj.getId());
		Assert.assertEquals("Usage Name did not update correctly", DEVICE_STUB_2, deviceObj.getUsageName());
	}

	/**
	 * IDE-994
	 * Test editing connection properties in the dcd.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editConnectionInXmlTest() {
		projectName = "Edit_Connection_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Add devices to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);
		MenuUtils.save(editor);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, "dataFloat_out");
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_2, "dataDouble_in");
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.save(editor);

		// Edit content of dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<providesidentifier>dataDouble_in</providesidentifier>", "<providesidentifier>dataFloat_in</providesidentifier>");
		editor.toTextEditor().setText(editorText);
		// TODO: Currently only typing in the XML editor seems to force an update. Why?
		editor.toTextEditor().pressShortcut(Keystrokes.SPACE, Keystrokes.BS);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// Check that connection data has changed
		usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, "dataFloat_out");
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertEquals("Wrong number of connections found", 1, sourceConnections.size());
		final Connection connection = (Connection) sourceConnections.get(0).part().getModel();

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		final SWTBotGefEditPart deviceStubProvidesPort = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_2, "dataFloat_in");
		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", DUtil.getBusinessObject((ContainerShape) deviceStubProvidesPort.part().getModel()),
			providesPort);
	}
}
