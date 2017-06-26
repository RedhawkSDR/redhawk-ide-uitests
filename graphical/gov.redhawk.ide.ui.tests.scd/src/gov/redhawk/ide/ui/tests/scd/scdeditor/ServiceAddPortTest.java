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
package gov.redhawk.ide.ui.tests.scd.scdeditor;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.ui.tests.scd.AbstractServiceAddPortTest;

public class ServiceAddPortTest extends AbstractServiceAddPortTest {

	@Override
	protected SWTBotEditor openEditor(String projectName) {
		ProjectExplorerUtils.openProjectInEditor(bot, projectName, projectName + ".scd.xml");
		return bot.editorByTitle(projectName + ".scd.xml");
	}

}
