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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.dcd.ui.ext.DeviceShape;
import gov.redhawk.core.graphiti.dcd.ui.ext.ServiceShape;
import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class NodeComponentTest extends AbstractGraphitiTest {

	private RHBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String GPP_1 = "GPP_1";
	private static final String SERVICE_STUB = "ServiceStub";

	/**
	 * IDE-988
	 * Create the pictogram shape in the node diagram that represents device/service business objects.
	 * This includes ContainerShape, usage name, ID, port shapes and labels, and component supported interface.
	 */
	@Test
	public void checkNodePictogramElements() {
		projectName = "PictogramShapesNode";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Add to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SERVICE_STUB, 300, 200);

		// Confirm created object is as expected
		assertGPP(editor.getEditPart(GPP));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(GPP));
		assertServiceStub(editor.getEditPart(SERVICE_STUB));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(SERVICE_STUB));

		// Add to diagram from Target SDR
		DiagramTestUtils.dragDeviceFromTargetSDRToDiagram(gefBot, editor, GPP);
		editor.drag(editor.getEditPart(GPP), 10, 50);
		assertGPP(editor.getEditPart(GPP));

		DiagramTestUtils.dragServiceFromTargetSDRToDiagram(gefBot, editor, SERVICE_STUB);
		editor.select(editor.getEditPart(SERVICE_STUB));
		assertServiceStub(editor.getEditPart(SERVICE_STUB));
	}

	/**
	 * Test that a device is added/removed from the DCD without saving
	 * 
	 * IDE-1504 - Check that the componentfile element is removed once empty
	 */
	@Test
	public void checkDeviceInDcd() throws IOException {
		projectName = "DeviceTestNode";

		NodeUtils.createNewNodeProject(bot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);

		// Add the device and check that it was added to the dcd.xml
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		DeviceConfiguration dcd = getDeviceConfiguration(editor);
		DcdComponentPlacement placement = dcd.getPartitioning().getComponentPlacement().get(0);
		DcdComponentInstantiation ci = placement.getComponentInstantiation().get(0);
		Assert.assertNotNull("Device Instantiation not found", ci);
		Assert.assertFalse("ComponentFile element not created", dcd.getComponentFiles().getComponentFile().isEmpty());

		// Remove the device and check that it was removed from the dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(GPP_1));
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		dcd = getDeviceConfiguration(editor);
		Assert.assertTrue("Device was not removed", dcd.getPartitioning().getComponentPlacement().isEmpty());
		Assert.assertNull("ComponentFile element was not removed", dcd.getComponentFiles());
	}

	/**
	 * IDE-1131
	 * Name-spaced devices should have their component file id set to basename_UUID, not the fully qualified name
	 * 
	 * IDE-1506
	 * Update how IDs are generated for new devices / services
	 */
	@Test
	public void checkNameSpacedDeviceInDcd() {
		projectName = "NameSpacedDeviceTest";
		String deviceName = "name.space.device";
		String deviceBaseName = "device";

		NodeUtils.createNewNodeProject(bot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);

		DiagramTestUtils.addFromPaletteToDiagram(editor, deviceName, 0, 0);
		MenuUtils.save(editor);

		// Build expected xml string for device
		RHContainerShape deviceShape = (RHContainerShape) editor.getEditPart(deviceName).part().getModel();
		final String componentFileString = "(?s).*<componentfile id=\"" + deviceBaseName + ".*";
		final String deviceXmlString = DiagramTestUtils.regexStringForDevice(deviceShape);

		// IDE-1506 - check to make sure componentInstantiationId follows pattern of NodeName:DeviceName
		DcdComponentInstantiation ci = (DcdComponentInstantiation) DUtil.getBusinessObject(deviceShape);
		Assert.assertTrue("Component instantiation ID does not follow expected pattern (NodeName:DeviceName)",
			ci.getId().startsWith(projectName + ":" + deviceBaseName + "_"));

		// Check dcd.xml for string
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The componentfile should only include the NodeName:DeviceName", editorText.matches(componentFileString));
		Assert.assertTrue("The dcd.xml should include " + deviceName + "'s device configuration", editorText.matches(deviceXmlString));
	}

	/**
	 * Use context menu to delete a device
	 */
	@Test
	public void checkDeviceContextMenuDelete() {
		projectName = "Context-Delete";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		SWTBotGefEditPart gefEditPart = editor.getEditPart(GPP);
		DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
		Assert.assertNull(editor.getEditPart(GPP));
	}

	/**
	 * The delete context menu should not appear when ports are selected
	 */
	@Test
	public void doNotDeletePortsTest() {
		projectName = "No-Delete-Port-Test";
		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, GPP);

		List<SWTBotGefEditPart> anchors = new ArrayList<SWTBotGefEditPart>();
		anchors.add(DiagramTestUtils.getDiagramPortAnchor(uses));

		for (SWTBotGefEditPart anchor : anchors) {
			try {
				anchor.select();
				editor.clickContextMenu("Delete");
				Assert.fail();
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(e.getMessage(), "Delete", e.getMessage());
			}
		}
	}

	/**
	 * Private helper method for {@link #checkNodePictogramElements()} Asserts the given SWTBotGefEditPart is a GPP
	 * device and is drawn correctly
	 * @param gefEditPart
	 */
	private static void assertGPP(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti device shape
		DeviceShape deviceShape = (DeviceShape) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a DcdComponentInstantiation
		Object bo = DUtil.getBusinessObject(deviceShape);
		Assert.assertTrue("business object should be of type DcdComponentInstantiation", bo instanceof DcdComponentInstantiation);
		DcdComponentInstantiation ci = (DcdComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", GPP, deviceShape.getOuterText().getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), deviceShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", deviceShape.getLollipop());

		// GPP only has the two ports
		Assert.assertTrue(deviceShape.getUsesPortStubs().size() == 2 && deviceShape.getProvidesPortStubs().size() == 0);

		// Port is of type propEvent
		Assert.assertEquals("propEvent", deviceShape.getUsesPortStubs().get(0).getUses().getName());
	}

	/**
	 * Private helper method for {@link #checkNodePictogramElements()} Asserts the given SWTBotGefEditPart is a
	 * ServiceStub service and is drawn correctly
	 * @param gefEditPart
	 */
	private static void assertServiceStub(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti service shape
		ServiceShape serviceShape = (ServiceShape) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a DcdComponentInstantiation
		Object bo = DUtil.getBusinessObject(serviceShape);
		Assert.assertTrue("business object should be of type DcdComponentInstantiation", bo instanceof DcdComponentInstantiation);
		DcdComponentInstantiation ci = (DcdComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", SERVICE_STUB, serviceShape.getOuterText().getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), serviceShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", serviceShape.getLollipop());

		// SERVICE_STUB should not have a port
		Assert.assertTrue(serviceShape.getUsesPortStubs().size() == 0 && serviceShape.getProvidesPortStubs().size() == 0);
	}

	private DeviceConfiguration getDeviceConfiguration(RHBotGefEditor editor) throws IOException {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		String editorText = editor.toTextEditor().getText();
		Resource resource = resourceSet.createResource(org.eclipse.emf.common.util.URI.createURI("mem://temp.dcd.xml"), DcdPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		return DeviceConfiguration.Util.getDeviceConfiguration(resource);
	}
}
