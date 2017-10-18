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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;

/**
 * Additional tests for editing simple sequences, but which are only applicable to top-level simple sequences.
 */
public class SimpleSequenceProperty2Test extends AbstractPropertyTabTest {

	/**
	 * IDE-1484 - ensure remove button is enabled immediately upon editor opening
	 */
	@Test
	public void testRemoveButton() {
		editorBot.button("Add Sequence").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		setType("long (32-bit)", "");
		addValue(editorBot, "1");
		addValue(editorBot, "2");
		editor.saveAndClose();

		ProjectExplorerUtils.openProjectInEditor(bot, "PropTest_Comp", "PropTest_Comp.spd.xml");
		editor = bot.editorByTitle("PropTest_Comp");
		SWTBotButton removeButton = editor.bot().button("Remove", 1);
		bot.table().select(0);
		Assert.assertTrue(removeButton.isEnabled());
		removeButton.click();
		Assert.assertEquals("Value was not removed", 1, bot.table().rowCount());
	}

	private void setType(String type, String complex) {
		editorBot.comboBoxWithLabel("Type*:").setSelection(type);
		editorBot.comboBoxWithLabel("Type*:", 1).setSelection(complex);
	}

	private void addValue(SWTBot bot, String text) {
		final int startingRows = bot.table().rowCount();
		bot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText(text);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				int endingRows = bot.table().rowCount();
				return (startingRows + 1) == endingRows;
			}

			@Override
			public String getFailureMessage() {
				return "Value was not added to table";
			}
		});
	}
}
