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

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.ui.tests.projectCreation.util.ICodegenInfo;
import gov.redhawk.ide.ui.tests.projectCreation.util.StandardCodegenInfo;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * IDE-1219
 */
public class ComponentWizardTest extends AbstractCreationWizardTest {

	@Override
	protected String getProjectType() {
		return "REDHAWK Component Project";
	}

	@Test
	@Override
	public void testNonDefaultLocation() throws IOException {
		wizardBot.textWithLabel("&Project name:").setText("ProjectName");
		wizardBot.checkBox("Use default location").click();

		wizardBot.textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File createdFolder = folder.newFolder("ProjectName");
		wizardBot.textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());
		wizardBot.button("Next >").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection(0);
		wizardBot.button("Next >").click();

		testNonDefaultLocation_setupCodeGeneration();

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(wizardShell));
		SWTBotEditor editorBot = bot.editorByTitle("ProjectName");

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("ProjectName");
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());

		testNonDefaultLocation_assertOutputDir(editorBot);
	}

	protected void testNonDefaultLocation_setupCodeGeneration() {
		SWTBotCombo templateCombo = wizardBot.comboBoxWithLabel("Template:");
		for (int i = 0; i < templateCombo.itemCount(); i++) {
			wizardBot.comboBoxWithLabel("Template:").setSelection(i);
			if (wizardBot.button("Finish").isEnabled()) {
				break;
			}
		}
		wizardBot.textWithLabel("Output Directory:").setText("customOutput");
	}

	protected void testNonDefaultLocation_assertOutputDir(SWTBotEditor editorBot) {
		editorBot.bot().cTabItem("Implementations").activate();
		Assert.assertEquals("customOutput", editorBot.bot().textWithLabel("Output Dir:").getText());
	}

	protected void testProjectCreation(String name, String lang, String generator, ICodegenInfo iCodegenInfo) {
		wizardBot.textWithLabel("&Project name:").setText(name);
		wizardBot.button("Next >").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection(lang);
		if (generator != null) {
			wizardBot.comboBoxWithLabel("Code Generator:").setSelection(generator);
		}
		Assert.assertFalse(wizardBot.textWithLabel("ID:").getText().isEmpty());
		wizardBot.textWithLabel("ID:").setText("customImplID");
		Assert.assertFalse(wizardBot.textWithLabel("Description:").getText().isEmpty());
		wizardBot.textWithLabel("Description:").setText("custom description");
		wizardBot.button("Next >").click();

		setupCodeGeneration(iCodegenInfo);

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(wizardShell));

		// Ensure SPD file was created
		String baseFilename = getBaseFilename(name);
		ProjectExplorerUtils.waitUntilNodeAppears(bot, name, baseFilename + ".spd.xml");

		// Ensure SPD editor opened
		SWTBotEditor editorBot = bot.editorByTitle(name);
		Assert.assertEquals("gov.redhawk.ide.ui.editors.ComponentEditor", editorBot.getReference().getId());

		// Check overview tab contents
		Assert.assertEquals(name, editorBot.bot().textWithLabel("Name*:").getText());

		// Check implementation tab contents
		editorBot.bot().cTabItem("Implementations").activate();
		SWTBotTreeItem[] items = editorBot.bot().tree().getAllItems();
		Assert.assertEquals(1, editorBot.bot().tree().selectionCount());
		Assert.assertEquals(1, items.length);
		Assert.assertTrue(items[0].getText().matches("customImplID.*"));
		Assert.assertEquals("customImplID", editorBot.bot().textWithLabel("ID*:").getText());
		Assert.assertEquals(lang, editorBot.bot().textWithLabel("Prog. Lang:").getText());
		Assert.assertEquals("custom description", editorBot.bot().textWithLabel("Description:").getText());
	}

	@Test
	public void testPythonCreation() {
		testProjectCreation("ComponentWizardTest01", "Python", "Python Code Generator", new StandardCodegenInfo("Pull Port Data"));
	}

	@Test
	public void testCppCreation() {
		testProjectCreation("ComponentWizardTest01", "C++", "C++ Code Generator", new StandardCodegenInfo("Pull Port Data"));
	}

	@Test
	public void testJavaCreation() {
		testProjectCreation("ComponentWizardTest01", "Java", "Java Code Generator", new StandardCodegenInfo("Pull Port Data (Base/Derived)"));
	}

	@Test
	public void testBackNext() {
		wizardBot.textWithLabel("&Project name:").setText("ComponentWizardTest01");
		wizardBot.button("Next >").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Python Code Generator");
		wizardBot.button("Next >").click();
		setupCodeGeneration(null);
		bot.waitUntil(Conditions.widgetIsEnabled(wizardBot.button("Finish")));
		reverseFromCodeGeneration();
		wizardBot.button("< Back").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Java");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Java Code Generator");
		wizardBot.button("Next >").click();
		bot.waitUntil(Conditions.widgetIsEnabled(wizardBot.button("Finish")));
		wizardBot.button("< Back").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("C++");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("C++ Code Generator");
		wizardBot.button("Next >").click();
		bot.waitUntil(Conditions.widgetIsEnabled(wizardBot.button("Finish")));

		wizardShell.close();
	}

	@Test
	public void testContributedPropertiesUI() {
		wizardBot.textWithLabel("&Project name:").setText("WizardTest03");
		wizardBot.button("Next >").click();
		wizardBot.comboBox().setSelection("Java");
		wizardBot.button("Next >").click();
		Assert.assertFalse(wizardBot.textWithLabel("Package:").getText().isEmpty());
		wizardBot.textWithLabel("Package:").setText("customPackageName");

		wizardShell.close();
	}

	/**
	 * IDE-1111, IDE-1359
	 * Test creation of component with dots in project name
	 */
	@Test
	public void testNamespacedObjectCreation() {
		testProjectCreation("namespaced.project.IDE1111", "Python", null, null);
		Assert.assertEquals("code.entrypoint", "python/IDE1111.py", bot.activeEditor().bot().textWithLabel("Entry Point:").getText());
		verifyEditorTabPresent("IDE1111.spd.xml");
		verifyEditorTabPresent("IDE1111.prf.xml");
		verifyEditorTabPresent("IDE1111.scd.xml");
	}

	protected void setupCodeGeneration(ICodegenInfo iCodegenInfo) {
		StandardCodegenInfo codegenInfo = (StandardCodegenInfo) iCodegenInfo;
		if (codegenInfo != null && codegenInfo.getTemplate() != null) {
			wizardBot.comboBoxWithLabel("Template:").setSelection(codegenInfo.getTemplate());
		}
		Assert.assertFalse(wizardBot.textWithLabel("Output Directory:").getText().isEmpty());
	}
	
	protected void reverseFromCodeGeneration() {
	}

	protected void verifyEditorTabPresent(String tabName) {
		Assert.assertNotNull(tabName + " editor tab is missing", bot.activeEditor().bot().cTabItem(tabName));
	}

}
