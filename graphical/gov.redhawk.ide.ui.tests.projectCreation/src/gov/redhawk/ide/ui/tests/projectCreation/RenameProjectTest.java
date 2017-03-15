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
package gov.redhawk.ide.ui.tests.projectCreation;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.UITest;
import mil.jpeojtrs.sca.spd.SpdPackage;

public class RenameProjectTest extends UITest {

	/**
	 * IDE-1803 - A project's ID should update when a rename operation is executed
	 */
	@Test
	public void renameProjectTest() {
		final String projectName = "renameTestProject";
		final String projectLanguage = "Python";

		ComponentUtils.createComponentProject(bot, projectName, projectLanguage);

		// Check value for existing ID
		SWTBotTreeItem node = ProjectExplorerUtils.waitUntilNodeAppears(bot, projectName);
		ProjectExplorerUtils.openProjectInEditor(bot, new String[] { projectName, projectName + SpdPackage.FILE_EXTENSION });

		String oldId = bot.textWithLabel("ID*:").getText();
		Assert.assertTrue("ID is not a DCE UUID", oldId.matches("DCE.*"));

		// Rename project
		node.contextMenu("Rename...").click();
		bot.waitUntil(Conditions.shellIsActive("Rename Resource"));
		SWTBotShell shell = bot.shell("Rename Resource");

		String newProjectName = "rh." + projectName;
		SWTBotText newNameText = shell.bot().textWithLabel("New name:");
		newNameText.setText(newProjectName);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		// Check that ID field updated
		node = ProjectExplorerUtils.waitUntilNodeAppears(bot, newProjectName);
		ProjectExplorerUtils.openProjectInEditor(bot, new String[] { newProjectName, projectName + SpdPackage.FILE_EXTENSION });
		String newId = bot.textWithLabel("ID*:").getText();
		Assert.assertFalse("ID did not update", newId.equals(oldId));
		Assert.assertTrue("ID is not a DCE UUID", newId.matches("DCE.*"));

	}
}
