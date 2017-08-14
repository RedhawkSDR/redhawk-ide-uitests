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

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DeviceTabTest extends AbstractGraphitiTest {

	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String PROJECT_NAME = "TestNode";

	private static final String GPP = "GPP";
	private static final String SERVICE = "ServiceNoPorts";
	private static final String PORT_SUP_SERVICE = "PortSupplierService";

	private RHBotGefEditor editor;
	private SWTBot editorBot;

	@Override
	@Before
	public void before() throws Exception {
		super.before();

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, PROJECT_NAME, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(PROJECT_NAME);
		editorBot = editor.bot();
	}

	/**
	 * Test form details section for devices in the device tab
	 */
	@Test
	public void deviceDetailsSection() {
		final SWTBotTreeItem treeItem = addElement(GPP);
		testUsageName(treeItem, GPP);

		// TODO: Test parent field
		SWTBotCombo parentCombo = bot.comboBoxWithLabel("Parent:");

		// TODO: Test properties tree
		SWTBotTree tree = bot.treeInGroup("Properties");
	}

	/**
	 * IDE-1502 - Test form details section for services in the device tab
	 */
	@Test
	public void serviceDetailsSection() {
		final SWTBotTreeItem treeItem = addElement(SERVICE);
		testUsageName(treeItem, SERVICE);

		try {
			bot.comboBoxWithLabel("Parent:");
		} catch (WidgetNotFoundException e) {
			// PASS - Services sections should not have a parent view
		}

		try {
			bot.treeInGroup("Properties");
		} catch (WidgetNotFoundException e) {
			// PASS - Services sections that do not inherit PortSupplier should not have a properties tree
		}
	}

	/**
	 * IDE-1502 - Test form details section for services in the device tab that inherit PortSupplier
	 */
	@Test
	public void portSupplierServiceDetailsSection() {
		final SWTBotTreeItem treeItem = addElement(PORT_SUP_SERVICE);
		testUsageName(treeItem, PORT_SUP_SERVICE);

		try {
			bot.comboBoxWithLabel("Parent:");
		} catch (WidgetNotFoundException e) {
			// PASS - Services sections should not have a parent view
		}

		// TODO: Test properties tree
		SWTBotTree tree = bot.treeInGroup("Properties");
	}

	/**
	 * Support adding devices in the node editor
	 */
	@Test
	public void addDeviceToExistingNode() {
		addDialogTest(GPP, 0);
	}

	/**
	 * IDE-1503 Support adding services in the node editor
	 */
	@Test
	public void addServiceToExistingNode() {
		addDialogTest(SERVICE, 1);
	}

	private void addDialogTest(String elementName, int treeIndex) {
		editorBot.cTabItem("Devices / Services").activate();
		editorBot.button("Add...").click();
		bot.waitUntil(Conditions.shellIsActive("Add Devices / Services Wizard"));
		SWTBotShell shell = bot.shell("Add Devices / Services Wizard");
		shell.bot().tree(treeIndex).getTreeItem(elementName).click();
		shell.bot().button("Finish").click();

		SWTBotTree tree = editorBot.tree(0);
		Assert.assertNotNull(elementName + " was not added to the node ", tree.getTreeItem(elementName + "_1"));
	}

	private SWTBotTreeItem addElement(String elementName) {
		// Add the element to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, elementName, 0, 0);

		// Confirm element is present in the devices table
		editorBot.cTabItem("Devices / Services").activate();
		SWTBotTree tree = editorBot.tree(0);
		return tree.getTreeItem(elementName + "_1");
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
}
