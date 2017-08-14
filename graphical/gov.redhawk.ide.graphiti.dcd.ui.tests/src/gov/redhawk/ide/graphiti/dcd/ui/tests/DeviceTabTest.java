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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DeviceTabTest extends AbstractGraphitiTest {

	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String GPP_1 = GPP + "_1";

	@Test
	public void deviceDetailsSection() {
		final String projectName = "TestNode";
		final String newName = "A_New_Name";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		RHBotGefEditor editor = gefBot.rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);

		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Devices").activate();

		// Confirm device is present in the devices table
		SWTBotTree tree = editorBot.tree(0);
		final SWTBotTreeItem treeItem = tree.getTreeItem(GPP_1);

		// Edit usage name and test that the table entry updates accordingly
		SWTBotText nameText = editorBot.textWithLabel("Name:");
		Assert.assertEquals("Usage name is incorrect in text field", GPP_1, nameText.getText());
		nameText.selectAll();
		nameText.typeText("A_New_Name");
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
