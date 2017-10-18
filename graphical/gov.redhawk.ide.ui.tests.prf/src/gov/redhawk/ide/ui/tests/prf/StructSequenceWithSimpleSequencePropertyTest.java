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

		SWTBotTree tree = editorBot.tree();
		tree.expandNode("ID").select("Struct");
		editorBot.textWithLabel(NAME_FIELD).setText("Struct");
		editorBot.sleep(600);

		tree.expandNode("ID", "Struct").select("Simple");
		editorBot.button("Remove").click();

		tree.getTreeItem("ID").getNode("Struct").select().contextMenu("New").menu("Simple Sequence").click();
		tree.expandNode("ID", "Struct").select("Simple Sequence");
		editorBot.textWithLabel(NAME_FIELD).setText("Simple Sequence");
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
	public void testID() throws CoreException {
		// ID should already be set
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());

		// Set ID to empty - error, children lose their prefix
		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		selectStruct();
		Assert.assertEquals("Struct", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimpleSequence();
		Assert.assertEquals("Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStructSequence();

		// Set ID to a DCE - ok, children still have no prefix
		editorBot.textWithLabel(ID_FIELD).setText("DCE:12345678-9abc-def0-1234-56789abcdef0");
		assertFormValid();
		selectStruct();
		Assert.assertEquals("Struct", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimpleSequence();
		Assert.assertEquals("Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStructSequence();

		// Set ID to something valid - ok, children have prefix
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
		selectStruct();
		Assert.assertEquals("hello::Struct", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimpleSequence();
		Assert.assertEquals("hello::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testStructID() throws CoreException {
		selectStruct();

		// ID should already be set
		Assert.assertEquals("ID::Struct", editorBot.textWithLabel(ID_FIELD).getText());

		// Set ID to empty - error, child simple not affected
		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		selectSimpleSequence();
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to a DCE - ok, child simple not affected
		editorBot.textWithLabel(ID_FIELD).setText("DCE:12345678-9abc-def0-1234-56789abcdef0");
		assertFormValid();
		selectSimpleSequence();
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to something valid - ok, child simple not affected
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
		selectSimpleSequence();
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testSimpleSequenceID() throws CoreException {
		selectSimpleSequence();

		// ID should already be set
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());

		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
	}

	@Test
	public void testName() {
		// Set name to empty - no ID changes
		editorBot.textWithLabel(NAME_FIELD).setText("");
		editorBot.sleep(600);
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();
		Assert.assertEquals("ID::Struct", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimpleSequence();
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStructSequence();

		// Set name to valid - IDs change
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		editorBot.sleep(600);
		Assert.assertEquals("Name1", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("Name1").select("Struct");
		Assert.assertEquals("Name1::Struct", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("Name1").getNode("Struct").select("Simple Sequence");
		Assert.assertEquals("Name1::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testStructName() {
		// Set name to empty - no ID changes
		editorBot.textWithLabel(NAME_FIELD).setText("");
		editorBot.sleep(600);
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimpleSequence();
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set name to valid - my ID changes, child ID same
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		editorBot.sleep(600);
		Assert.assertEquals("ID::Name1", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("ID").getNode("Name1").select("Simple Sequence");
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testSimpleSequenceName() {
		selectSimpleSequence();

		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		assertFormValid();
		Assert.assertEquals("ID::Name1", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	@Override
	public void testValuesChar() {
		selectSimpleSequence();
		super.testValuesChar();
	}

	@Test
	@Override
	public void testValuesDouble() {
		selectSimpleSequence();
		super.testValuesDouble();
	}

	@Test
	@Override
	public void testValuesFloat() {
		selectSimpleSequence();
		super.testValuesFloat();
	}

	@Test
	@Override
	public void testValuesLong() {
		selectSimpleSequence();
		super.testValuesLong();
	}

	@Test
	@Override
	public void testValuesLongLong() {
		selectSimpleSequence();
		super.testValuesLongLong();
	}

	@Test
	@Override
	public void testValuesShort() {
		selectSimpleSequence();
		super.testValuesShort();
	}

	@Test
	@Override
	public void testValuesObjRef() {
		selectSimpleSequence();
		super.testValuesObjRef();
	}

	@Test
	@Override
	public void testValuesString() {
		selectSimpleSequence();
		super.testValuesString();
	}

	@Test
	@Override
	public void testValuesUTCTime() {
		selectSimpleSequence();
		super.testValuesUTCTime();
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
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "hello", false, true);
		assertValuesDialogState(shell.bot(), new String[] { "hello" }, false, false, true);

		shell.bot().button("Add").click();
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 1, 0, "world", false, true);
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
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "1.1", false, true);
		bot.waitUntil(new WaitForCellValue(shell.bot().table(), 0, 0, "1.1"));
		StandardTestActions.writeToCell(shell.bot(), shell.bot().table(), 0, 0, "abc", false, true);
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
	public void testValueAutoUpdate() throws IOException {
		selectSimpleSequence();
		super.testValueAutoUpdate();
	}

	@Override
	protected SimpleSequence getSimpleSequence(Properties properties) {
		return properties.getStructSequence().get(0).getStruct().getSimpleSequence().get(0);
	}
}
