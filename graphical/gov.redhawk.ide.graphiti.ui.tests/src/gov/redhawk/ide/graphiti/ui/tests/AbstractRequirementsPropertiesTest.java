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
package gov.redhawk.ide.graphiti.ui.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import mil.jpeojtrs.sca.partitioning.Requirements;
import mil.jpeojtrs.sca.partitioning.Requires;

public abstract class AbstractRequirementsPropertiesTest extends AbstractGraphitiTest {

	/**
	 * Create the test project and populate with a testable resource
	 */
	protected abstract void createProject();

	/**
	 * Open the test project from the Target SDR
	 */
	protected abstract void openTargetSdrProject();

	/**
	 * @return either SadComponentInstantition.getDeviceRequires or DcdComponentInstantiation.getDeployerRequires
	 */
	protected abstract Requirements getRequirements();

	// Test editing component instantiation requirements via requirements property tab
	@Test
	public void addRequirementsViaView() {
		createProject();

		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		ViewUtils.selectPropertiesTab(bot, "Requirements");
		SWTBotTree requirementsTree = propView.bot().tree(0);
		SWTBotButton addButton = propView.bot().buttonWithTooltip("Add Requires");
		SWTBotButton removeButton = propView.bot().buttonWithTooltip("Remove Requires");

		// Check initial state
		Assert.assertEquals("View should not display any 'requires' elements", 0, requirementsTree.getAllItems().length);
		Assert.assertNull("Component should not have a 'requirements' element", getRequirements());

		// Add two requires elements via view buttons and check tree & model for changes
		addButton.click();
		addButton.click();
		Assert.assertEquals("'Requires' elements not correctly added to view", 2, requirementsTree.getAllItems().length);
		Assert.assertNotNull("Component should have a 'requirements' element", getRequirements());
		Assert.assertEquals("'Requires' elements not correctly added to model", 2, getRequirements().getRequires().size());

		// Edit first requires element and make check tree & model for changes
		final String newId = "NewID";
		final String newValue = "NewValue";
		SWTBotTreeItem treeItem = requirementsTree.getAllItems()[0];
		StandardTestActions.writeToCell(bot, treeItem, 0, newId);
		treeItem = requirementsTree.getAllItems()[0];
		StandardTestActions.writeToCell(bot, treeItem, 1, newValue);
		treeItem = requirementsTree.getAllItems()[0];

		Assert.assertEquals("Requires ID did not update", newId, treeItem.cell(0));
		Assert.assertEquals("Requires Value did not update", newValue, treeItem.cell(1));

		Requires req = getRequirements().getRequires().get(0);
		Assert.assertEquals("Model ID did not update", newId, req.getId());
		Assert.assertEquals("Model value did not update", newValue, req.getValue());

		// Remove both requires elements via view buttons and check tree & model for changes
		removeButton.click();
		Assert.assertEquals("'Requires' elements not correctly removed from view", 1, requirementsTree.getAllItems().length);
		Assert.assertEquals("'Requires' elements not correctly removed from model", 1, getRequirements().getRequires().size());
		removeButton.click();
		Assert.assertEquals("View should not display any 'requires' elements", 0, requirementsTree.getAllItems().length);
		Assert.assertNull("Component should not have a 'requirements' element", getRequirements());
	}

	// Test viewing requirements from an existing project
	@Test
	public void checkRequirements() {
		openTargetSdrProject();

		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		ViewUtils.selectPropertiesTab(bot, "Requirements");
		SWTBotTree requirementsTree = propView.bot().tree(0);
		Assert.assertEquals("'Requires' elements not correctly shown to view", 2, requirementsTree.getAllItems().length);
		for (int i = 0; i < requirementsTree.getAllItems().length; i++) {
			SWTBotTreeItem item = requirementsTree.getAllItems()[i];
			Assert.assertEquals("Requires ID is incorrect", "ReqId_" + (i + 1), item.cell(0));
			Assert.assertEquals("Requires value is incorrect", "ReqValue_" + (i + 1), item.cell(1));
		}
	}
}
