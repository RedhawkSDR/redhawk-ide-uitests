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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class DeviceTabTest extends AbstractGraphitiTest {

	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String PROJECT_NAME = "TestNode";

	private static final String GPP = "GPP";

	private RHBotGefEditor editor;
	private SWTBot editorBot;
	private DeviceConfiguration dcd;

	@Override
	@Before
	public void before() throws Exception {
		super.before();

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, PROJECT_NAME, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(PROJECT_NAME);
		editorBot = editor.bot();

		editorBot.cTabItem("DeviceManager.dcd.xml").activate();
		dcd = getDeviceConfiguration(editor);
	}

	/**
	 * IDE-1504 - Make sure removing a device also removes the component file, if it is no longer needed
	 * @throws IOException
	 */
	@Test
	public void removeComponentFile() throws IOException {
		addElement(GPP, 0);
		addElement("DeviceStub", 0);

		// Assert that componentFile elements were created for each device
		String[] deviceNames = { GPP, "DeviceStub" };
		dcd = getDeviceConfiguration(editor);
		for (String deviceName : deviceNames) {
			boolean componentFileFound = false;
			for (ComponentFile file : dcd.getComponentFiles().getComponentFile()) {
				if (file.getId().matches(deviceName + ".*")) {
					componentFileFound = true;
					break;
				}
			}
			Assert.assertTrue("Component file for " + deviceName + " was not created", componentFileFound);
		}

		// Assert that the componentFile element for GPP was removed
		editorBot.tree(0).getTreeItem(GPP + "_1").select();
		editorBot.button("Remove").click();
		waitForTreeItemToBeRemoved(GPP + "_1");
		dcd = getDeviceConfiguration(editor);
		boolean componentFileFound = false;
		for (ComponentFile file : dcd.getComponentFiles().getComponentFile()) {
			if (file.getId().matches(GPP + ".*")) {
				componentFileFound = true;
			}
		}
		Assert.assertFalse("Component file for " + GPP + " was not removed", componentFileFound);
	}

	/**
	 * IDE-1505 - Test adding and removing a device via the Device tab
	 * @throws IOException
	 */
	@Test
	public void addRemoveDevice() throws IOException {

		// Test adding a device
		SWTBotTreeItem treeItem = addElement(GPP, 0);
		boolean deviceFound = false;
		dcd = getDeviceConfiguration(editor);
		String nodeName = dcd.getName();
		String deviceId = nodeName + ":" + GPP + "_1";
		for (DcdComponentPlacement placement : dcd.getPartitioning().getComponentPlacement()) {
			String ciId = placement.getComponentInstantiation().get(0).getId();
			if (deviceId.equals(ciId)) {
				deviceFound = true;
			}
		}
		Assert.assertTrue(GPP + "was not added to the SCA model", deviceFound);
		Assert.assertNotNull(dcd.getComponentFiles());

		// Make sure device also appears in the diagram
		editorBot.cTabItem("Diagram").activate();
		Assert.assertNotNull(GPP + " was not added to the diagram", editor.getEditPart(GPP + "_1"));

		// Test removing a device
		editorBot.cTabItem("Devices").activate();
		treeItem.select();
		editorBot.button("Remove").click();
		deviceFound = false;
		waitForTreeItemToBeRemoved(GPP + "_1");
		dcd = getDeviceConfiguration(editor);
		for (DcdComponentPlacement placement : dcd.getPartitioning().getComponentPlacement()) {
			String ciId = placement.getComponentInstantiation().get(0).getId();
			if (deviceId.equals(ciId)) {
				deviceFound = true;
			}
		}

		// Need to save no EMF error about elements with missing eResources occurs
		editor.save();
		Assert.assertFalse(GPP + "was not removed from the SCA model", deviceFound);
		Assert.assertNull(dcd.getComponentFiles());

		// Make sure device was also removed from the Diagram
		editorBot.cTabItem("Diagram").activate();
		Assert.assertNull(GPP + " was not removed from the diagram", editor.getEditPart(GPP + "_1"));
	}

	/**
	 * IDE-2056 - Test form details section for devices in the device tab
	 */
	@Test
	public void deviceDetailsSection() {
		final SWTBotTreeItem treeItem = addElement(GPP, 0);
		testUsageName(treeItem, GPP);

		// TODO: Test properties tree
		SWTBotTree tree = bot.treeInGroup("Properties");
	}

	/**
	 * IDE-2056, IDE-2065 - Test parent combo in devices tab
	 * @throws IOException
	 */
	@Test
	public void parentCombo() throws IOException {
		final String AGR_DEVICE = "AggregateDevice";

		// Add devices
		SWTBotTreeItem agrTreeItem = addElement(AGR_DEVICE, 0);
		SWTBotTreeItem gppTreeItem = addElement(GPP, 0);

		// Set the parent
		gppTreeItem.select();
		bot.comboBoxWithLabel("Parent:").setSelection(0);

		// Assert that the parent is set in the editor and in the SCA model
		agrTreeItem = editorBot.tree(0).getTreeItem(AGR_DEVICE + "_1");
		Assert.assertNotNull(agrTreeItem.expand().getNode(GPP + "_1"));
		dcd = getDeviceConfiguration(editor);
		for (DcdComponentPlacement placement : dcd.getPartitioning().getComponentPlacement()) {
			if (placement.getComponentInstantiation().get(0).getId().matches(".*" + GPP + ".*")) {
				Assert.assertNotNull(placement.getCompositePartOfDevice());
				Assert.assertTrue(placement.getCompositePartOfDevice().getRefID().matches(".*" + AGR_DEVICE + ".*"));
			}
		}

		// Unset the parent
		agrTreeItem.expand().getNode(GPP + "_1").select();
		bot.button("Unset").click();

		// Assert that the GPP device is no longer a composite part of the Aggregate Device
		agrTreeItem = editorBot.tree(0).getTreeItem(AGR_DEVICE + "_1");
		Assert.assertEquals(agrTreeItem.getItems().length, 0);
		dcd = getDeviceConfiguration(editor);
		for (DcdComponentPlacement placement : dcd.getPartitioning().getComponentPlacement()) {
			if (placement.getComponentInstantiation().get(0).getId().matches(".*" + GPP + ".*")) {
				Assert.assertNull(placement.getCompositePartOfDevice());
			}
		}

		// Reset the parent
		editorBot.tree(0).getTreeItem(GPP + "_1").select();
		bot.comboBoxWithLabel("Parent:").setSelection(0);

		// Run assertions
		agrTreeItem = editorBot.tree(0).getTreeItem(AGR_DEVICE + "_1");
		Assert.assertNotNull(agrTreeItem.expand().getNode(GPP + "_1"));
		dcd = getDeviceConfiguration(editor);
		for (DcdComponentPlacement placement : dcd.getPartitioning().getComponentPlacement()) {
			if (placement.getComponentInstantiation().get(0).getId().matches(".*" + GPP + ".*")) {
				Assert.assertNotNull(placement.getCompositePartOfDevice());
				Assert.assertTrue(placement.getCompositePartOfDevice().getRefID().matches(".*" + AGR_DEVICE + ".*"));
			}
		}

		// Delete parent
		agrTreeItem = editorBot.tree(0).getTreeItem(AGR_DEVICE + "_1").select();
		editorBot.button("Remove").click();

		// Assert that the GPP device is no longer a composite part of the Aggregate Device
		waitForTreeItemToBeRemoved(AGR_DEVICE + "_1");
		dcd = getDeviceConfiguration(editor);
		for (DcdComponentPlacement placement : dcd.getPartitioning().getComponentPlacement()) {
			if (placement.getComponentInstantiation().get(0).getId().matches(".*" + GPP + ".*")) {
				Assert.assertNull(placement.getCompositePartOfDevice());
			}
		}
	}

	// Edit usage name and test that the table entry updates accordingly
	private void testUsageName(final SWTBotTreeItem treeItem, final String elementName) {
		SWTBotText nameText = editorBot.textWithLabel("Name:");
		Assert.assertEquals("Usage name is incorrect in text field", elementName + "_1", nameText.getText());

		final String newName = "A_New_Name";
		nameText.selectAll();
		nameText.typeText(newName);
		Assert.assertEquals("Usage name is incorrect in text field", newName, nameText.getText());

		// Tree waits briefly before updating, so as not to update on every key stroke
		editorBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return newName.equals(treeItem.getText());
			}

			@Override
			public String getFailureMessage() {
				return "Usage name is incorrect in devices table";
			}
		});
	}

	private SWTBotTreeItem addElement(String elementName, int treeIndex) {
		// Confirm element is present in the devices table
		editorBot.cTabItem("Devices").activate();
		editorBot.button("Add...").click();
		bot.waitUntil(Conditions.shellIsActive("Add Devices Wizard"));
		SWTBotShell shell = bot.shell("Add Devices Wizard");
		shell.bot().tree(treeIndex).getTreeItem(elementName).click();
		shell.bot().button("Finish").click();

		SWTBotTree tree = editorBot.tree(0);
		return tree.getTreeItem(elementName + "_1");
	}

	private void waitForTreeItemToBeRemoved(final String elementId) {
		editorBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem treeItem;
				try {
					treeItem = editorBot.tree(0).getTreeItem(elementId);
				} catch (WidgetNotFoundException e) {
					return true;
				}
				if (treeItem == null) {
					return true;
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return elementId + " was not removed from 'All Devices' tree";
			}
		});
	}

	private DeviceConfiguration getDeviceConfiguration(RHBotGefEditor editor) throws IOException {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		String editorText = editor.toTextEditor().getText();
		Resource resource = resourceSet.createResource(org.eclipse.emf.common.util.URI.createURI("mem://temp.dcd.xml"), DcdPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		return DeviceConfiguration.Util.getDeviceConfiguration(resource);
	}
}
