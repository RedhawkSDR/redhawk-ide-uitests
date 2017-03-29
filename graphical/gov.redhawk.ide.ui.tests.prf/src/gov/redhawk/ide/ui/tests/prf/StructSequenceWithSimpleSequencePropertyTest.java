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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.condition.WaitForCellValue;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.SimpleSequence;

public class StructSequenceWithSimpleSequencePropertyTest extends SimpleSequencePropertyTest {

	@Override
	public void before() throws Exception {
		super.before();
		editorBot.sleep(600);

		SWTBotTree tree = editorBot.tree();
		tree.expandNode("ID").select("Struct");
		editorBot.textWithLabel("ID*:").setText("Struct");
		editorBot.sleep(600);

		tree.expandNode("ID", "Struct").select("Simple");
		editorBot.button("Remove").click();

		tree.getTreeItem("ID").getNode("Struct").select().contextMenu("New").menu("Simple Sequence").click();
		tree.expandNode("ID", "Struct").select("Simple Sequence");
		editorBot.textWithLabel("ID*:").setText("Simple Sequence");
		editorBot.sleep(600);

		selectStructSequence();
	}

	@Override
	protected void createType() {
		editorBot.button("Add StructSeq").click();
	}

	protected void selectStructSequence() {
		editorBot.tree().select("ID");
	}

	protected void selectStruct() {
		editorBot.tree().getTreeItem("ID").select("Struct");
	}

	protected void selectSimpleSequence() {
		editorBot.tree().expandNode("ID", "Struct").select("Simple Sequence");
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

	@Override
	public void testValueAutoUpdate() throws IOException {
		selectSimpleSequence();
		super.testValueAutoUpdate();
	}

	@Override
	protected SimpleSequence getSimpleSequence(Properties properties) {
		return properties.getStructSequence().get(0).getStruct().getSimpleSequence().get(0);
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
		SWTBotTree structValueTable = editorBot.treeWithLabel("StructValue:");
		editorBot.button("Add...").click();
		SWTBotTreeItem item = structValueTable.getTreeItem("Struct[0]");
		item.expand();
		SWTBotTreeItem subItem = item.getNode("Simple Sequence");

		editorBot.button("Add...").click();
		SWTBotTreeItem item1 = structValueTable.getTreeItem("Struct[1]");
		item1.expand();
		SWTBotTreeItem subItem1 = item1.getNode("Simple Sequence");
		subItem1.select();
		subItem1.click(2);
		new SWTBot(structValueTable.widget).button("...").click();

		SWTBotShell shell = bot.shell("Values");
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "hello", false);
		assertValuesDialogState(shell.bot(), new String[] { "hello" }, false, false, true);

		shell.bot().button("Add").click();
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 1, 0, "world", false);
		assertValuesDialogState(shell.bot(), new String[] { "hello", "world" }, true, false, true);

		shell.bot().table().select(0);
		assertValuesDialogState(shell.bot(), new String[] { "hello", "world" }, false, true, true);

		shell.bot().button("Down").click();
		assertValuesDialogState(shell.bot(), new String[] { "world", "hello" }, true, false, true);

		shell.bot().button("Up").click();
		assertValuesDialogState(shell.bot(), new String[] { "hello", "world" }, false, true, true);

		shell.bot().button("Remove").click();
		assertValuesDialogState(shell.bot(), new String[] { "world" }, false, false, false);

		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();

		item1.select();
		editorBot.button("Remove", 1).click();

		selectSimpleSequence();
		editorBot.comboBoxWithLabel("Type*:").setSelection("double (64-bit)");
		assertFormInvalid();
		selectStructSequence();

		structValueTable = editorBot.treeWithLabel("StructValue:");
		item = structValueTable.getTreeItem("Struct[0]");
		item.expand();
		subItem = item.getNode("Simple Sequence");
		subItem.select();
		subItem.click(2);
		new SWTBot(structValueTable.widget).button("...").click();

		shell = bot.shell("Values");
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "1.1", false);
		bot.waitUntil(new WaitForCellValue(shell.bot().table(), 0, 0, "1.1"));
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "abc", false);
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

	/**
	 * IDE-1439
	 */
	@Test
	public void testKind() throws IOException {
		testKind(false, false);
	}

	@Override
	public void testAction() {
		// Disable Action element for Struct sequences
	}

}
