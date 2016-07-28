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
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.core.resources.IMarker;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForSeverityMarkers;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

/**
 * Tests component, device, and shared library projects by adding a shared library dependency (libdsp)
 * and including a header from libdsp (RealFIRFilter.h)
 */
public class DynamicIncludesTest extends UIRuntimeTest {

	private static final String DSP = "rh.dsp";

	@Test
	public void componentWithSharedLibDep() {
		String headerToInclude = "RealFIRFilter.h";

		SWTBotEditor editor = null;

		String componentProjectName = "TestComponent";
		ComponentUtils.createComponentProject(bot, componentProjectName, "C++");
		editor = bot.editorByTitle(componentProjectName);
		setSpdDependency(editor.bot(), editor, DSP);
		generateProjectAndBuild(componentProjectName, headerToInclude);
	}

	@Test
	public void deviceWithSharedLibDep() {
		String headerToInclude = "RealFIRFilter.h";

		SWTBotEditor editor = null;

		String deviceProjectName = "TestDevice";
		DeviceUtils.createDeviceProject(bot, deviceProjectName, "C++");
		editor = bot.editorByTitle(deviceProjectName);
		setSpdDependency(editor.bot(), editor, DSP);
		generateProjectAndBuild(deviceProjectName, headerToInclude);
	}

	@Test
	public void sharedLibWithSharedLibDep() {
		String headerToInclude = "RealFIRFilter.h";

		SWTBotEditor editor = null;

		String sharedLibraryProjectName = "TestSharedLibrary";
		SharedLibraryUtils.createSharedLibraryProject(bot, sharedLibraryProjectName, "C++ Library");
		editor = bot.editorByTitle(sharedLibraryProjectName);
		setSpdDependency(editor.bot(), editor, DSP);
		generateProjectAndBuild(sharedLibraryProjectName, headerToInclude);
	}

	private void generateProjectAndBuild(String projectName, String headerToInclude) {
		// Generate
		SWTBotEditor spdEditor = bot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, spdEditor);

		// Default file editor should open
		SWTBotEditor textEditor = bot.editorByTitle(projectName + ".cpp");

		// Sometimes the comment block does not collapse fast enough and the include gets put in the wrong spot.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
		}
		textEditor.toTextEditor().insertText(3, 0, "#include \"" + headerToInclude + "\"");
		textEditor.save();

		// Rebuild the project with the new modifications
		StandardTestActions.buildAll();

		// Wait for the build to finish
		// One second sleep to make sure the build has started
		try {
			Thread.sleep(1000);
			textEditor.bot().waitUntil(new WaitForBuild(), 30000);
		} catch (TimeoutException e) {
			Assert.fail("Failed while waiting for the build to complete for: " + projectName);
		} catch (InterruptedException e) {
			Assert.fail("Failed while waiting for the build to complete for: " + projectName);
		}

		// Wait for any error markers to go away
		try {
			textEditor.bot().waitUntil(new WaitForSeverityMarkers(IMarker.SEVERITY_WARNING), 120000);
		} catch (TimeoutException e) {
			Assert.fail("Failed due severity markers being present on: " + projectName);
		}

		bot.closeAllEditors();
	}

	private void setSpdDependency(SWTBot swtBot, SWTBotEditor editor, String sharedLibraryName) {
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.IMPLEMENTATIONS);

		SWTBotButton addDepButton = swtBot.button("Add...", 3);
		addDepButton.click();

		SWTBotShell depWizardShell = bot.shell("Dependency Wizard");
		depWizardShell.activate();
		SWTBot depWizardBot = depWizardShell.bot();

		depWizardBot.comboBoxWithLabel("Kind:").setSelection("Shared Library (SoftPkg) Reference");
		depWizardBot.comboBoxWithLabel("Type:").setSelection("other");
		SWTBotTree depTree = depWizardBot.treeInGroup("Shared Library (SoftPkg) Reference");
		StandardTestActions.selectNamespacedTreeItem(depWizardBot, depTree, sharedLibraryName);
		depWizardBot.button("Finish").click();
		bot.saveAllEditors();
	}
}
