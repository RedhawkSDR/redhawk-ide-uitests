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

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.condition.WaitForCellValue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class StructSequenceWithSimpleSequencePropertyTest extends SimpleSequencePropertyTest {

	@Override
	public void before() throws Exception {
		super.before();
		editor.bot().sleep(600);

		SWTBotTree tree = editor.bot().tree();
		tree.expandNode("ID").select("Struct");
		editor.bot().textWithLabel("ID*:").setText("Struct");
		editor.bot().sleep(600);

		tree.expandNode("ID", "Struct").select("Simple");
		bot.button("Remove").click();

		tree.getTreeItem("ID").getNode("Struct").select().contextMenu("New").menu("Simple Sequence").click();
		tree.expandNode("ID", "Struct").select("Simple Sequence");
		editor.bot().textWithLabel("ID*:").setText("Simple Sequence");
		editor.bot().sleep(600);

		selectStructSequence();
	}

	@Override
	protected void createType() {
		editor.bot().button("Add StructSeq").click();
	}

	protected void selectStructSequence() {
		editor.bot().tree().select("ID");
	}

	protected void selectStruct() {
		editor.bot().tree().getTreeItem("ID").select("Struct");
	}

	protected void selectSimpleSequence() {
		editor.bot().tree().expandNode("ID", "Struct").select("Simple Sequence");
	}

	@Test
	public void testIDSimpleSequence() throws CoreException {
		selectSimpleSequence();
		super.testID();
	}

	@Test
	public void testNameSimpleSequence() {
		selectSimpleSequence();
		super.testName();
	}

	@Test
	@Override
	public void testValues() throws CoreException {
		selectSimpleSequence();
		super.testValues();
	}

	@Test
	@Override
	public void testUnits() {
		selectSimpleSequence();
		super.testUnits();
	}

	@Test
	@Override
	public void testRange() {
		selectSimpleSequence();
		super.testRange();
	}

	@Test
	public void testDescriptionSimpleSequence() {
		selectSimpleSequence();
		super.testDescription();
	}

	@Test
	public void testIDStruct() throws CoreException {
		selectStruct();
		super.testID();
	}

	@Test
	public void testNameStruct() {
		selectStruct();
		super.testName();
	}

	@Test
	public void testDescriptionStruct() {
		selectStruct();
		super.testDescription();
	}

	@Test
	public void testStructValue() {
		SWTBotTree structValueTable = editor.bot().treeWithLabel("StructValue:");
		editor.bot().button("Add...").click();
		SWTBotTreeItem item = structValueTable.getTreeItem("Struct[0]");
		item.expand();
		SWTBotTreeItem subItem = item.getNode("Simple Sequence");

		editor.bot().button("Add...").click();
		SWTBotTreeItem item1 = structValueTable.getTreeItem("Struct[1]");
		item1.expand();
		SWTBotTreeItem subItem1 = item1.getNode("Simple Sequence");
		subItem1.select();
		subItem1.click(2);
		new SWTBot(structValueTable.widget).button("...").click();

		SWTBotShell shell = bot.shell("Values");
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "hello");
		assertValuesDialogState(bot, new String[] { "hello" }, false, false, true);

		shell.bot().button("Add").click();
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 1, 0, "world");
		assertValuesDialogState(bot, new String[] { "hello", "world" }, true, false, true);

		shell.bot().table().select(0);
		assertValuesDialogState(bot, new String[] { "hello", "world" }, false, true, true);

		shell.bot().button("Down").click();
		assertValuesDialogState(bot, new String[] { "world", "hello" }, true, false, true);

		shell.bot().button("Up").click();
		assertValuesDialogState(bot, new String[] { "hello", "world" }, false, true, true);

		shell.bot().button("Remove").click();
		assertValuesDialogState(bot, new String[] { "world" }, false, false, false);

		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();

		item1.select();
		editor.bot().button("Remove", 1).click();

		selectSimpleSequence();
		editor.bot().comboBoxWithLabel("Type*:").setSelection("double (64-bit)");
		assertFormInvalid();
		selectStructSequence();

		structValueTable = editor.bot().treeWithLabel("StructValue:");
		item = structValueTable.getTreeItem("Struct[0]");
		item.expand();
		subItem = item.getNode("Simple Sequence");
		subItem.select();
		subItem.click(2);
		new SWTBot(structValueTable.widget).button("...").click();

		shell = bot.shell("Values");
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "1.1");
		bot.waitUntil(new WaitForCellValue(shell.bot().table(), 0, 0, "1.1"));
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "abc");
		bot.waitUntil(new WaitForCellValue(shell.bot().table(), 0, 0, "1.1"));

		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
	}

	private void assertValuesDialogState(SWTBot bot, String[] text, boolean upButtonEnabled, boolean downButtonEnabled, boolean removeButtonEnabled) {
		Assert.assertEquals("Row count", text.length, bot.table().rowCount());
		for (int i = 0; i < text.length; i++) {
			bot.waitUntil(new WaitForCellValue(bot.table(), i, 0, text[i]));
		}
		Assert.assertEquals("Up button enablement", upButtonEnabled, bot.button("Up").isEnabled());
		Assert.assertEquals("Down button enablement", downButtonEnabled, bot.button("Down").isEnabled());
		Assert.assertEquals("Remove button enablement", removeButtonEnabled, bot.button("Remove").isEnabled());
	}

	@Test
	public void testKind() {
		testKind(false);
	}

	@Override
	public void testAction() {
		// Disable Action element for Struct sequences
	}

}
