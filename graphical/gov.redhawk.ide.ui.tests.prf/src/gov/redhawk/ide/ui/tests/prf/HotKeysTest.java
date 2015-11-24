/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.ui.tests.prf;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.ui.xml.editors.tests.HotKeysAbstractTest;

public class HotKeysTest extends HotKeysAbstractTest {

	@Override
	protected void importProjectAndOpen() throws CoreException {
		StandardTestActions.importProject(FrameworkUtil.getBundle(HotKeysTest.class), new Path("workspace/PropTest_Comp"), null);
		ProjectExplorerUtils.openProjectInEditor(bot, "PropTest_Comp", "PropTest_Comp.spd.xml");
		editor = bot.editorByTitle("PropTest_Comp");
		editor.bot().cTabItem("PropTest_Comp.prf.xml").activate();
	}
}
