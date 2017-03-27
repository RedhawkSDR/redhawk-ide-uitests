/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sharedlibrary.ui.runtime.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForBuild.BuildType;

/**
 * Shared Library projects should display many fewer options in the editor pages
 * since they do not have things such as a .prf.xml, .scd.xml, ports, and cannot be launched
 */
public class SharedLibraryGenerateTest extends UIRuntimeTest {

	private SWTBotEditor editor;

	@BeforeClass
	public static void beforeClassSetup() {
		// PyDev needs to be configured before running New REDHAWK Shared Library (formerly Softpackage) Project Wizards
		StandardTestActions.configurePyDev(new SWTWorkbenchBot());
	}

	// TODO: Do we need to include Octave project type for this test?
	/**
	 * IDE-1117 Re-codegen shared library projects
	 * IDE-1408 Ensure warning about localfile resolves after codegen
	 * IDE-1669 The IDE should add header files to the Makefile.am.ide for installation
	 *
	 * Softpackage (shared library) code generation test. Currently only checks case where code generation is
	 * initiated from the editor toolbar button.
	 * @throws CoreException
	 * @throws IOException
	 */
	@Test
	public void softpackageGenerationTest() throws IOException, CoreException {
		final String projectName = "SharedLibraryTest";
		final String projectType = "C++ Library";

		SharedLibraryUtils.createSharedLibraryProject(bot, projectName, projectType);
		editor = bot.editorByTitle(projectName);
		EditorUtils.assertEditorTabOkay(editor, EditorUtils.SPD_EDITOR_OVERVIEW_TAB_ID); // IDE-1408

		StandardTestActions.generateProject(bot, editor);

		// Every possible path starts from the same root, in this case the projectName
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		Assert.assertTrue(project.getFolder("cpp").exists());
		Assert.assertTrue(project.getFolder("cpp/include").exists());
		Assert.assertTrue(project.getFolder("cpp/src").exists());

		// IDE-1669 Test for shared library header installation in the Makefile.am.ide
		bot.waitUntil(new WaitForBuild(BuildType.CODEGEN), WaitForBuild.TIMEOUT);

		final String HEADER_INCLUDE_LINE = "redhawk_HEADERS_auto = include/" + projectName + ".h";
		IFile file = project.getFile("cpp/Makefile.am.ide");
		if (checkFileForLine(file, HEADER_INCLUDE_LINE)) {
			return;
		}
		Assert.fail("Header entry was not found");
	}

	private boolean checkFileForLine(IFile file, String lineInFile) throws IOException, CoreException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents(true)))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (lineInFile.equals(line)) {
					return true;
				}
			}
		}
		return false;
	}
}
