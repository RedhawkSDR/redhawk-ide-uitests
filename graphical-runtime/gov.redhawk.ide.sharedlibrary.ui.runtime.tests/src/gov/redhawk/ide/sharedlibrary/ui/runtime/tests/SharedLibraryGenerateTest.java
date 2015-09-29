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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;

/**
 * Shared Library projects should display many fewer options in the editor pages
 * since they do not have things such as a .prf.xml, .scd.xml, ports, and cannot be launched
 */
public class SharedLibraryGenerateTest extends UIRuntimeTest {

	SWTBotEditor editor;

	@BeforeClass
	public static void beforeClassSetup() {
		// PyDev needs to be configured before running New REDHAWK Shared Library (formerly Softpackage) Project Wizards
		StandardTestActions.configurePyDev(new SWTWorkbenchBot());
	}

	// TODO: Do we need to include Octave project type for this test?
	/**
	 * IDE-1117
	 * Softpackage (shared library) code generation test
	 * Currently only checks case where code generation is initiated from the editor toolbar button
	 */
	@Test
	public void softpackageGenerationTest() {
		final String projectName = "SharedLibraryTest";
		final String projectType = "C++ Library";

		SharedLibraryUtils.createSharedLibraryProject(bot, projectName, projectType);
		editor = bot.editorByTitle(projectName);

		StandardTestActions.generateProject(bot, editor);

		// Every possible path starts from the same root, in this case the projectName
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		Assert.assertTrue(project.getFolder("cpp").exists());
		Assert.assertTrue(project.getFolder("cpp/include").exists());
		Assert.assertTrue(project.getFolder("cpp/src").exists());
	}
}
