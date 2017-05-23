/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.ui.tests.projectCreation;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;

public class SharedLibraryWizardTest extends AbstractCreationWizardTest {

	@Override
	protected String getProjectType() {
		return "REDHAWK Shared Library Project";
	}

	/**
	 * Test creating a C++ shared library project.
	 * IDE-1958 Ensure the "New Control Panel Project" button isn't present in the SPD editor
	 */
	@Test
	public void testCppCreation() {
		final String PROJECT_NAME = "SharedLibraryWizardTest01";

		getWizardBot().textWithLabel("&Project name:").setText(PROJECT_NAME);
		getWizardBot().comboBoxWithLabel("Type:").setSelection("C++ Library");
		getWizardBot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(getWizardShell()));

		// Ensure SPD file was created
		ProjectExplorerUtils.waitUntilNodeAppears(bot, PROJECT_NAME, PROJECT_NAME + ".spd.xml");

		// Ensure SPD editor opened
		SWTBotEditor editorBot = bot.editorByTitle(PROJECT_NAME);
		Assert.assertEquals("gov.redhawk.ide.ui.editors.ComponentEditor", editorBot.getReference().getId());

		// Check overview tab contents
		Assert.assertEquals(PROJECT_NAME, editorBot.bot().textWithLabel("Name*:").getText());
		try {
			editorBot.bot().toolbarButtonWithTooltip("New Control Panel Project");
			Assert.fail("Control panel button was present");
		} catch (WidgetNotFoundException e) {
			// PASS - We expect the button *not* to be present
		}

		// Check implementation tab contents
		editorBot.bot().cTabItem("Implementations").activate();
		SWTBotTreeItem[] items = editorBot.bot().tree().getAllItems();
		Assert.assertEquals(1, editorBot.bot().tree().selectionCount());
		Assert.assertEquals(1, items.length);
		Assert.assertEquals("C++", editorBot.bot().textWithLabel("Prog. Lang:").getText());
		try {
			editorBot.bot().toolbarButtonWithTooltip("New Control Panel Project");
			Assert.fail("Control panel button was present");
		} catch (WidgetNotFoundException e) {
			// PASS - We expect the button *not* to be present
		}
	}
}
