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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

public class DevManagerSandboxTest extends AbstractDeviceManagerSandboxTest {

	private RHBotGefEditor editor;

	private static final String NSDEV = "name.space.device";
	private static final String NSDEV_1 = "device_1";

	/**
	 * Add devices to the sandbox node diagram from palette
	 */
	@Test
	public void launchDevice() {
		editor = openNodeChalkboardDiagram(gefBot);

		// Add device to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		assertGPP(editor.getEditPart(GPP));

		// Open the chalkboard with components already launched
		editor.close();
		editor = openNodeChalkboardDiagram(gefBot);
		Assert.assertNotNull(editor.getEditPart(GPP));
	}

	/**
	 * IDE-1187, IDE-1446 - Ensure namespaced devices will launch in sandbox
	 */
	@Test
	public void launchNamespacedDevice() {
		editor = openNodeChalkboardDiagram(gefBot);

		// Add namespaced component to the chalkboard
		DiagramTestUtils.addFromPaletteToDiagram(editor, NSDEV, 200, 300);
		Assert.assertNotNull(editor.getEditPart(NSDEV_1));

		// Ensure it doesn't have any problems
		ScaDevice< ? > device = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager().getDevice(NSDEV_1);
		Assert.assertNotNull(device);
		Assert.assertTrue(device.getStatus().isOK());
		Assert.assertNotNull(device.getProfileObj());
	}

	/**
	 * Private helper method for {@link #checkNodePictogramElements()} Asserts the given SWTBotGefEditPart is a GPP
	 * device and is drawn correctly
	 * @param gefEditPart
	 */
	private static void assertGPP(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti device shape
		DeviceShapeImpl deviceShape = (DeviceShapeImpl) gefEditPart.part().getModel();

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
}
