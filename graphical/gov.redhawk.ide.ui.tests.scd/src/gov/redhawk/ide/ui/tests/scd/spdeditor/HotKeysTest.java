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
package gov.redhawk.ide.ui.tests.scd.spdeditor;

import org.eclipse.core.runtime.CoreException;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.ui.xml.editors.tests.HotKeysAbstractTest;

public class HotKeysTest extends HotKeysAbstractTest {

	@Override
	protected void importProjectAndOpen() throws CoreException {
		ComponentUtils.createComponentProject(bot, "HotKeysTest_scd", "C++");
		editor = bot.editorByTitle("HotKeysTest_scd");
		editor.bot().cTabItem("HotKeysTest_scd.spd.xml").activate();
	}
}
