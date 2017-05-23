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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;

public class OctaveWizardTest extends AbstractCreationWizard2Test {

	@Override
	protected String getProjectType() {
		return "REDHAWK Octave Project";
	}

	@Test
	@Override
	public void testNonDefaultLocation() throws IOException {
		final String projectName = "OctaveProject_LocationTest";

		getWizardBot().textWithLabel("&Project name:").setText(projectName);
		getWizardBot().checkBox("Use default location").click();

		getWizardBot().textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File createdFolder = folder.newFolder(projectName);
		getWizardBot().textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());
		getWizardBot().button("Next >").click();

		// Skip the Implementation page
		getWizardBot().button("Next >").click();

		// Input the mfile
		URL bundleUrl = FileLocator.find(FrameworkUtil.getBundle(OctaveWizardTest.class), new Path("/testFiles/octave_test_file.m"), null);
		URL fileUrl = FileLocator.toFileURL(bundleUrl);
		getWizardBot().textWithLabel("Primary M-file:").setText(fileUrl.getPath());
		getWizardBot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(getWizardShell()));

		// Check project location
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());
	}

	@Test
	public void testArgWithDefaultValue() throws IOException {
		final String projectName = "OctaveProject_DefaultValueTest";
		final String propId = "myConstant";
		final String propValue = "5";

		// Set project name
		getWizardBot().textWithLabel("&Project name:").setText(projectName);
		getWizardBot().button("Next >").click();

		// Skip the Implementation page
		getWizardBot().button("Next >").click();

		// Input the mfile
		URL bundleUrl = FileLocator.find(FrameworkUtil.getBundle(OctaveWizardTest.class), new Path("/testFiles/octave_test_file.m"), null);
		URL fileUrl = FileLocator.toFileURL(bundleUrl);
		getWizardBot().textWithLabel("Primary M-file:").setText(fileUrl.getPath());
		getWizardBot().button("Next >").click();

		// Check that the arg with a default value is treated as a property
		SWTBotTableItem propItem = getWizardBot().table().getTableItem("myConstant");
		Assert.assertEquals("Property kind mapping was incorrect", "Property (Simple)", propItem.getText(1));
		getWizardBot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(getWizardShell()));

		// Check property existence and default value
		SWTBotEditor editor = bot.editorByTitle(projectName);
		editor.bot().cTabItem("Properties").activate();
		SWTBotTree tree = editor.bot().tree();
		tree.select(propId);
		Assert.assertEquals("Default value was not set correctly", propValue, editor.bot().textWithLabel("Value:").getText());
	}
}
