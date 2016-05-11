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
package gov.redhawk.ide.codegen.runtime.tests;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;

public class DeprecatedBuildersTest extends UIRuntimeTest {

	/**
	 * Tests that deprecated builders are removed from a project when we perform code generation.
	 */
	@Test
	public void deprecatedBuildersRemoved() throws CoreException {
		final String PROJECT_NAME = "deprecated_builders";
		final String BUILD_SH_BUILDER_ID = "gov.redhawk.ide.codegen.builders.TopLevelBuildScript";
		final String RPM_SPEC_BUILDER_ID = "gov.redhawk.ide.codegen.builders.TopLevelRPMSpec";

		ComponentUtils.createComponentProject(bot, PROJECT_NAME, "C++");

		// Add the deprecated builders
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		IProjectDescription description = project.getDescription();
		ICommand[] oldCommands = description.getBuildSpec();
		ICommand[] newCommands = new ICommand[oldCommands.length + 2];
		newCommands[0] = description.newCommand();
		newCommands[0].setBuilderName(BUILD_SH_BUILDER_ID);
		newCommands[1] = description.newCommand();
		newCommands[1].setBuilderName(RPM_SPEC_BUILDER_ID);
		System.arraycopy(oldCommands, 0, newCommands, 2, oldCommands.length);
		description.setBuildSpec(newCommands);
		project.setDescription(description, null);

		StandardTestActions.generateProject(bot, bot.editorByTitle(PROJECT_NAME));

		// Ensure the builders are no longer present
		for (ICommand command : project.getDescription().getBuildSpec()) {
			Assert.assertNotEquals("Builder was not removed", "gov.redhawk.ide.codegen.builders.TopLevelBuildScript", command.getBuilderName());
			Assert.assertNotEquals("Builder was not removed", "gov.redhawk.ide.codegen.builders.TopLevelRPMSpec", command.getBuilderName());
		}
	}

}
