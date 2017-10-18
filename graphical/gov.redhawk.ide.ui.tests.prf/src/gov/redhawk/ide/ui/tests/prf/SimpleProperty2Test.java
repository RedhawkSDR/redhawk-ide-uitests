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
package gov.redhawk.ide.ui.tests.prf;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;

/**
 * Additional tests for editing simples, but which are only applicable to top-level simples.
 */
public class SimpleProperty2Test extends AbstractPropertyTabTest {

	@Test
	public void testAction() {
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		editorBot.comboBoxWithLabel("Action:").setSelection("eq");
		assertFormValid();

		editorBot.comboBoxWithLabel("Action:").setSelection("ge");
		assertFormValid();

		editorBot.comboBoxWithLabel("Action:").setSelection("gt");
		assertFormValid();

		editorBot.comboBoxWithLabel("Action:").setSelection("le");
		assertFormValid();

		editorBot.comboBoxWithLabel("Action:").setSelection("lt");
		assertFormValid();

		editorBot.comboBoxWithLabel("Action:").setSelection("ne");
		assertFormValid();
	}

	/**
	 * IDE-1548 - do not allow selection of command-line attribute for non-property type properties immediately after
	 * opening the editor.
	 * @throws CoreException
	 */
	@Test
	public void testCommandLine() throws CoreException {
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		SWTBotCombo kindCombo = editorBot.comboBoxWithLabel("Kind:");
		SWTBotCheckBox cmdLineBox = editorBot.checkBox("Pass on command line");
		kindCombo.setSelection("allocation");
		Assert.assertFalse(cmdLineBox.isEnabled());
		bot.sleep(200);
		editor.saveAndClose();

		ProjectExplorerUtils.openProjectInEditor(bot, "PropTest_Comp", "PropTest_Comp.spd.xml");
		editor = bot.editorByTitle("PropTest_Comp");
		editorBot = editor.bot();

		kindCombo = editorBot.comboBoxWithLabel("Kind:");
		cmdLineBox = editorBot.checkBox("Pass on command line");
		Assert.assertEquals(kindCombo.getText(), "allocation");
		Assert.assertFalse(cmdLineBox.isEnabled());
		kindCombo.setSelection("property (default)");
		Assert.assertTrue(cmdLineBox.isEnabled());
	}
}
