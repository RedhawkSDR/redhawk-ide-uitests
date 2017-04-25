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

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractCreationWizardTest extends UITest {

	private static final String EXISTING_FILES_WARNING = " The specified location already exists and contains files. They will become part of this project.";

	private SWTBotShell wizardShell;
	private SWTBot wizardBot;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@BeforeClass
	public static void setupPyDev() throws Exception {
		StandardTestActions.configurePyDev();
	}

	protected SWTBotShell getWizardShell() {
		return wizardShell;
	}

	protected SWTBot getWizardBot() {
		return wizardBot;
	}

	@Test
	public void uuid() {
		wizardBot.textWithLabel("&Project name:").setText("WizardTest02");
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());

		wizardBot.radio("Provide an ID").click();
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.text(" Enter a DCE UUID");

		wizardBot.textWithLabel("DCE UUID:").setText("187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.text(" DCE UUID must start with 'DCE:'");

		wizardBot.textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.text(" DCE UUID must start with 'DCE:'");

		wizardBot.textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595dz");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.text(" Enter a valid UUID");

		wizardBot.textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.text(" DCE UUID must start with 'DCE:'");

		wizardBot.radio("Generate an ID").click();
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());

		wizardBot.radio("Provide an ID").click();
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.text(" DCE UUID must start with 'DCE:'");

		wizardBot.textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());

		wizardShell.close();
	}

	@Before
	@Override
	public void before() throws Exception {
		super.before();

		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");

		// On el7, SWTBot tests seem to reach a point where the new project wizard is no longer activated when it opens
		if (!wizardShell.isActive()) {
			wizardShell.setFocus();
		}

		wizardBot = wizardShell.bot();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", getProjectType()));
		treeItem.select();
		wizardBot.button("Next >").click();
	}

	protected abstract String getProjectType();

	/**
	 * Tests using a non-default location for the created project
	 * IDE-1782 Warn when new project location contains files
	 * @throws IOException
	 */
	@Test
	public void nonDefaultLocation() throws IOException {
		bot.textWithLabel("&Project name:").setText("ProjectName");
		bot.checkBox("Use default location").click();

		bot.textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File dirWithFiles = folder.newFolder("LocationWithFiles");
		File.createTempFile("nonDefaultLocation", ".tmp", dirWithFiles);
		bot.textWithLabel("&Location:").setText(dirWithFiles.getAbsolutePath());
		for (int i = 0;; i++) {
			String textBoxContents = bot.text(i).getText();
			if (EXISTING_FILES_WARNING.equals(textBoxContents)) {
				break;
			}
		}

		File createdFolder = folder.newFolder("ProjectName");
		bot.textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());

		nonDefaultLocation_extraSteps();

		bot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(wizardShell));
		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("ProjectName");
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());
	}

	/**
	 * This method is called by the {@link #nonDefaultLocation()} test if there are additional steps after filling
	 * out the first page of the wizard before "Finish" can be clicked.
	 */
	protected void nonDefaultLocation_extraSteps() {
	}

	protected String getBaseFilename(String projectName) {
		String[] segments = projectName.split("\\.");
		return segments[segments.length - 1];
	}

}
