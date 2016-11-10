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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.forms.widgets.Section;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.ui.tests.projectCreation.util.ICodegenInfo;
import gov.redhawk.ide.ui.tests.projectCreation.util.StandardCodegenInfo;

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
		getWizardBot().textWithLabel("&Project name:").setText("ProjectName");
		getWizardBot().checkBox("Use default location").click();

		getWizardBot().textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File createdFolder = folder.newFolder("ProjectName");
		getWizardBot().textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());
		getWizardBot().button("Next >").click();

		getWizardBot().comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		getWizardBot().comboBoxWithLabel("Code Generator:").setSelection(0);
		getWizardBot().button("Next >").click();

		testNonDefaultLocation_setupCodeGeneration();

		getWizardBot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(getWizardShell()));
		SWTBotEditor editorBot = bot.editorByTitle("ProjectName");

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("ProjectName");
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());

		testNonDefaultLocation_assertOutputDir(editorBot);
	}

	protected void testNonDefaultLocation_setupCodeGeneration() {
		SWTBotCombo templateCombo = getWizardBot().comboBoxWithLabel("Template:");
		for (int i = 0; i < templateCombo.itemCount(); i++) {
			getWizardBot().comboBoxWithLabel("Template:").setSelection(i);
			if (getWizardBot().button("Finish").isEnabled()) {
				break;
			}
		}
		getWizardBot().textWithLabel("Output Directory:").setText("customOutput");
	}

	protected void testNonDefaultLocation_assertOutputDir(SWTBotEditor editorBot) {
		editorBot.bot().cTabItem("Implementations").activate();
		Assert.assertEquals("customOutput", editorBot.bot().textWithLabel("Output Dir:").getText());
	}

	protected void testProjectCreation(String name, String lang, String generator, ICodegenInfo iCodegenInfo) {
		getWizardBot().textWithLabel("&Project name:").setText(name);
		getWizardBot().button("Next >").click();

		getWizardBot().comboBoxWithLabel("Prog. Lang:").setSelection(lang);
		if (generator != null) {
			getWizardBot().comboBoxWithLabel("Code Generator:").setSelection(generator);
		}
		Assert.assertFalse(getWizardBot().textWithLabel("ID:").getText().isEmpty());
		getWizardBot().textWithLabel("ID:").setText("customImplID");
		Assert.assertFalse(getWizardBot().textWithLabel("Description:").getText().isEmpty());
		getWizardBot().textWithLabel("Description:").setText("custom description");
		getWizardBot().button("Next >").click();

		setupCodeGeneration(iCodegenInfo);

		getWizardBot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(getWizardShell()));

		// Ensure SPD file was created
		String baseFilename = getBaseFilename(name);
		ProjectExplorerUtils.waitUntilNodeAppears(bot, name, baseFilename + ".spd.xml");

		// Ensure SPD editor opened
		SWTBotEditor editor = bot.editorByTitle(name);
		Assert.assertEquals("gov.redhawk.ide.ui.editors.ComponentEditor", editor.getReference().getId());

		// Check overview tab contents
		Assert.assertEquals(name, editor.bot().textWithLabel("Name*:").getText());

		// Check implementation tab contents
		editor.bot().cTabItem("Implementations").activate();
		SWTBotTreeItem[] items = editor.bot().tree().getAllItems();
		Assert.assertEquals(1, editor.bot().tree().selectionCount());
		Assert.assertEquals(1, items.length);
		Assert.assertTrue(items[0].getText().matches("customImplID.*"));
		Assert.assertEquals("customImplID", editor.bot().textWithLabel("ID*:").getText());
		Assert.assertEquals(lang, editor.bot().textWithLabel("Prog. Lang:").getText());
		Assert.assertEquals("custom description", editor.bot().textWithLabel("Description:").getText());
	}

	@Test
	public void testPythonCreation() {
		testProjectCreation("ComponentWizardTest01", "Python", "Python Code Generator", new StandardCodegenInfo("Pull Port Data"));
	}

	@Test
	public void testCppCreation() {
		String projectName = "ComponentWizardTest01";
		testProjectCreation(projectName, "C++", "C++ Code Generator", new StandardCodegenInfo("Pull Port Data"));
		testSharedLibSettings(projectName);
	}

	// Check project localfile/entrypoint/code-type settings
	private void testSharedLibSettings(String projectName) {
		SWTBotEditor editor = bot.editorByTitle(projectName);
		editor.bot().cTabItem("Implementations").activate();
		final Section section = bot.widget(new BaseMatcher<Section>() {
			@Override
			public boolean matches(Object item) {
				if (item instanceof Section) {
					Section section = (Section) item;
					if ("Code".equals(section.getText())) {
						return true;
					}
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Of type Section");
			}
		});

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				section.setExpanded(true);
			}
		});

		Assert.assertTrue(editor.bot().textWithLabel("Entry Point:").getText().matches(".*\\.so.*"));
		Assert.assertTrue(editor.bot().textWithLabel("File*:").getText().matches(".*\\.so.*"));
		Assert.assertEquals("SharedLibrary", editor.bot().comboBoxWithLabel("Type:").getText());
	}

	@Test
	public void testJavaCreation() {
		testProjectCreation("ComponentWizardTest01", "Java", "Java Code Generator", new StandardCodegenInfo("Pull Port Data (Base/Derived)"));
	}

	@Test
	public void testBackNext() {
		getWizardBot().textWithLabel("&Project name:").setText("ComponentWizardTest01");
		getWizardBot().button("Next >").click();

		getWizardBot().comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		getWizardBot().comboBoxWithLabel("Code Generator:").setSelection("Python Code Generator");
		getWizardBot().button("Next >").click();
		setupCodeGeneration(null);
		bot.waitUntil(Conditions.widgetIsEnabled(getWizardBot().button("Finish")));
		reverseFromCodeGeneration();
		getWizardBot().button("< Back").click();

		getWizardBot().comboBoxWithLabel("Prog. Lang:").setSelection("Java");
		getWizardBot().comboBoxWithLabel("Code Generator:").setSelection("Java Code Generator");
		getWizardBot().button("Next >").click();
		setupCodeGeneration(null);
		bot.waitUntil(Conditions.widgetIsEnabled(getWizardBot().button("Finish")));
		reverseFromCodeGeneration();
		getWizardBot().button("< Back").click();

		getWizardBot().comboBoxWithLabel("Prog. Lang:").setSelection("C++");
		getWizardBot().comboBoxWithLabel("Code Generator:").setSelection("C++ Code Generator");
		getWizardBot().button("Next >").click();
		setupCodeGeneration(null);
		bot.waitUntil(Conditions.widgetIsEnabled(getWizardBot().button("Finish")));

		getWizardShell().close();
	}

	@Test
	public void testContributedPropertiesUI() {
		getWizardBot().textWithLabel("&Project name:").setText("WizardTest03");
		getWizardBot().button("Next >").click();
		getWizardBot().comboBox().setSelection("Java");
		getWizardBot().button("Next >").click();
		Assert.assertFalse(getWizardBot().textWithLabel("Package:").getText().isEmpty());
		getWizardBot().textWithLabel("Package:").setText("customPackageName");

		getWizardShell().close();
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
			getWizardBot().comboBoxWithLabel("Template:").setSelection(codegenInfo.getTemplate());
		}
		Assert.assertFalse(getWizardBot().textWithLabel("Output Directory:").getText().isEmpty());
	}
	
	protected void reverseFromCodeGeneration() {
	}

	protected void verifyEditorTabPresent(String tabName) {
		Assert.assertNotNull(tabName + " editor tab is missing", bot.activeEditor().bot().cTabItem(tabName));
	}

}
