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

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.dcd.ui.ext.DeviceShape;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

public class DevManagerSandboxTest extends AbstractDeviceManagerSandboxTest {

	private RHBotGefEditor editor;

	private static final String NSDEV = "name.space.device";
	private static final String NSDEV_1 = "device_1";
	private static final String EDITOR_NAME = "gov.redhawk.ide.graphiti.dcd.internal.ui.editor.GraphitiDeviceManagerSandboxEditor";
	private static final String SANDBOX_DEV_MGR_EDITOR_TOOLIP = "Sandbox device manager";

	/**
	 * Test the most basic functionality / presence of the device manager sandbox diagram.
	 * IDE-1194 Check the type of editor that opens as well as its input
	 * IDE-1668 Correct diagram titles and tooltips
	 */
	@Test
	public void deviceManagerSandboxTest() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// IDE-1194
		IEditorPart editorPart = editor.getReference().getEditor(false);
		Assert.assertEquals("Device manager sandbox editor class is incorrect", EDITOR_NAME, editorPart.getClass().getName());
		IEditorInput editorInput = editorPart.getEditorInput();
		Assert.assertTrue("Device manager sandbox editor's input object is incorrect", editorInput instanceof URIEditorInput);

		// IDE-1668
		Assert.assertEquals("Incorrect title", DEVICE_MANAGER, editorPart.getTitle());
		Assert.assertEquals("Incorrect tooltip", SANDBOX_DEV_MGR_EDITOR_TOOLIP, editorPart.getTitleToolTip());
	}

	/**
	 * Add devices to the sandbox node diagram from palette
	 */
	@Test
	public void launchDevice() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Add device to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		assertGPP(editor.getEditPart(GPP));
		DiagramTestUtils.waitForComponentState(bot, editor, GPP, ComponentState.STARTED);

		// Open the chalkboard with components already launched
		editor.close();
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		Assert.assertNotNull(editor.getEditPart(GPP));
	}

	/**
	 * IDE-1187, IDE-1446, IDE-1450 - Ensure namespaced devices will launch in sandbox
	 */
	@Test
	public void launchNamespacedDevice() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Add namespaced component to the chalkboard
		DiagramTestUtils.addFromPaletteToDiagram(editor, NSDEV, 200, 300);
		Assert.assertNotNull(editor.getEditPart(NSDEV_1));
		DiagramTestUtils.waitForComponentState(bot, editor, NSDEV_1, ComponentState.STOPPED);

		// Ensure it doesn't have any problems
		ScaDevice< ? > device = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager().getAllDevices().get(0);
		Assert.assertNotNull(device);
		Assert.assertEquals(NSDEV_1, device.getLabel());
		Assert.assertTrue(device.getStatus().isOK());
		Assert.assertNotNull(device.getProfileObj());
	}

	/**
	 * IDE-1562 - shutdown Sandbox Device Manager
	 * When performing 'shutdown' on the local device manager, all contained devices should be released
	 */
	@Test
	public void shutdownSandboxDeviceManager() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Add device to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		assertGPP(editor.getEditPart(GPP));
		DiagramTestUtils.waitForComponentState(bot, editor, GPP, ComponentState.STARTED);

		SWTBotTreeItem devMgrTreeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox" }, "Device Manager");
		devMgrTreeItem.contextMenu("Shutdown").click();

		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, GPP_1);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, "GPP_1 STARTED");
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
		@SuppressWarnings("restriction")
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
