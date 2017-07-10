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
package gov.redhawk.ide.ui.tests.spd;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.finder.RHBot;

/**
 * IDE-1397 - Test invalid and deprecated code generators are not included as options in the SPD Implementations tab
 */
public class AvailableCodegenTest extends UITest {

	@Test
	public void cppProjectCodegens() throws CoreException {
		List<String> expectedCodegens = Arrays.asList("C++ Code Generator", "Octave Shared Library Code Generator", "C++ Octave Code Generator",
			"C++ Shared Library Code Generator", "Manual Generator");
		checkCodegenerators("CppComTest", expectedCodegens);
	}

	@Test
	public void javaProjectCodegens() throws CoreException {
		List<String> expectedCodegens = Arrays.asList("Java Code Generator", "Manual Generator");
		checkCodegenerators("JavaComTest", expectedCodegens);
	}

	@Test
	public void pythonProjectCodegens() throws CoreException {
		List<String> expectedCodegens = Arrays.asList("Python Code Generator", "Manual Generator");
		checkCodegenerators("PythonComTest", expectedCodegens);
	}

	private void checkCodegenerators(String projectName, List<String> expectedCodegens) throws CoreException {
		StandardTestActions.importProject(SpdUiTestsActivator.getInstance().getBundle(), new Path("/workspace/" + projectName), null);

		// Ensure SPD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem(projectName).select();
		view.bot().tree().getTreeItem(projectName).expand();
		view.bot().tree().getTreeItem(projectName).getNode(projectName + ".spd.xml").doubleClick();

		SWTBotEditor editor = bot.editorByTitle(projectName);
		editor.setFocus();
		RHBot editorBot = new RHBot(editor.bot());
		editorBot.cTabItem("Implementations").activate();

		// Check visible code generators
		SWTBotCombo codegenCombo = editorBot.comboBoxWithLabel("Generator:");
		List<String> comboItems = Arrays.asList(codegenCombo.items());

		// Test that each combo selection is an acceptable code generator type
		for (String item : comboItems) {
			Assert.assertTrue("Unexpected codegenerator found: " + item, expectedCodegens.contains(item));
		}

		// Test that every expected code generator type is available as a selection in the combo box
		for (String codegen : expectedCodegens) {
			Assert.assertTrue("Expected codegenerator is missing from combo: " + codegen, comboItems.contains(codegen));
		}
	}
}
