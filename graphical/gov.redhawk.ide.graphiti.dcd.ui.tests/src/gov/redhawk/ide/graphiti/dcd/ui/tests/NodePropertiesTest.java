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

import java.io.IOException;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

public class NodePropertiesTest extends AbstractGraphitiTest {
	private static final String DOMAIN_NAME = "REDHAWK_DEV";

	@Test
	public void nodeProperties() throws IOException {
		String projectName = "PropertiesNode";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		RHBotGefEditor editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Validate existing properties
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem treeItem = propTable.getTreeItem("Id");
		Assert.assertTrue(treeItem.cell(1).matches("DCE.*"));
		treeItem = propTable.getTreeItem("Name");
		Assert.assertEquals(projectName, treeItem.cell(1));

		// Modify a property and check that the model object updates
		final String descriptionText = "A new Node description";
		treeItem = propTable.getTreeItem("Description");
		StandardTestActions.writeToCell(bot, treeItem, 1, descriptionText);
		DeviceConfiguration dcd = NodeUtils.getDeviceConfiguration(editor);
		Assert.assertEquals(descriptionText, dcd.getDescription());
	}
}
