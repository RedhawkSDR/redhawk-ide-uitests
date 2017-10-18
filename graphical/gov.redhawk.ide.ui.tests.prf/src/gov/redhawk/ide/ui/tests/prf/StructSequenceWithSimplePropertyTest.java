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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class StructSequenceWithSimplePropertyTest extends SimplePropertyTest {

	@Override
	public void before() throws Exception {
		super.before();

		editorBot.tree().expandNode("ID").select("Struct");
		editorBot.textWithLabel(NAME_FIELD).setText("Struct");
		editorBot.sleep(600);
		editorBot.tree().expandNode("ID", "Struct").select("Simple");
		editorBot.textWithLabel(NAME_FIELD).setText("Simple");
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

	protected void selectSimple() {
		editorBot.tree().expandNode("ID", "Struct").select("Simple");
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
		selectSimple();
		Assert.assertEquals("Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStructSequence();

		// Set ID to a DCE - ok, children still have no prefix
		editorBot.textWithLabel(ID_FIELD).setText("DCE:12345678-9abc-def0-1234-56789abcdef0");
		assertFormValid();
		selectStruct();
		Assert.assertEquals("Struct", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimple();
		Assert.assertEquals("Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStructSequence();

		// Set ID to something valid - ok, children have prefix
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
		selectStruct();
		Assert.assertEquals("hello::Struct", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimple();
		Assert.assertEquals("hello::Simple", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testStructID() throws CoreException {
		selectStruct();

		// ID should already be set
		Assert.assertEquals("ID::Struct", editorBot.textWithLabel(ID_FIELD).getText());

		// Set ID to empty - error, child simple not affected
		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		selectSimple();
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to a DCE - ok, child simple not affected
		editorBot.textWithLabel(ID_FIELD).setText("DCE:12345678-9abc-def0-1234-56789abcdef0");
		assertFormValid();
		selectSimple();
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to something valid - ok, child simple not affected
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
		selectSimple();
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testSimpleID() throws CoreException {
		selectSimple();

		// ID should already be set
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());

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
		selectSimple();
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStructSequence();

		// Set name to valid - IDs change
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		editorBot.sleep(600);
		Assert.assertEquals("Name1", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("Name1").select("Struct");
		Assert.assertEquals("Name1::Struct", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("Name1").getNode("Struct").select("Simple");
		Assert.assertEquals("Name1::Simple", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testStructName() {
		// Set name to empty - no ID changes
		editorBot.textWithLabel(NAME_FIELD).setText("");
		editorBot.sleep(600);
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());
		selectSimple();
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set name to valid - my ID changes, child ID same
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		editorBot.sleep(600);
		Assert.assertEquals("ID::Name1", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("ID").getNode("Name1").select("Simple");
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testSimpleName() {
		selectSimple();

		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		assertFormValid();
		Assert.assertEquals("ID::Name1", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	@Override
	public void testValueString() throws CoreException {
		selectSimple();
		testValueString("Default Value:");
	}

	@Test
	@Override
	public void testValueBoolean() throws CoreException {
		selectSimple();
		testValueBoolean("Default Value:");
	}

	@Test
	@Override
	public void testValueChar() throws CoreException {
		selectSimple();
		testValueChar("Default Value:");
	}

	@Test
	@Override
	public void testValueDouble() throws CoreException {
		selectSimple();
		testValueDouble("Default Value:");
	}

	@Test
	@Override
	public void testValueFloat() throws CoreException {
		selectSimple();
		testValueFloat("Default Value:");
	}

	@Test
	@Override
	public void testValueLong() throws CoreException {
		selectSimple();
		testValueLong("Default Value:");
	}

	@Test
	@Override
	public void testValueLongLong() throws CoreException {
		selectSimple();
		testValueLongLong("Default Value:");
	}

	@Test
	@Override
	public void testValueShorts() throws CoreException {
		selectSimple();
		testValueShorts("Default Value:");
	}

	@Test
	@Override
	public void testValueObjRef() throws CoreException {
		selectSimple();
		testValueObjRef("Default Value:");
	}

	@Test
	@Override
	public void testValueUTCTime() throws CoreException {
		selectSimple();
		testValueUTCTime("Default Value:");
	}

	@Test
	@Override
	public void testUnits() {
		selectSimple();
		super.testUnits();
	}

	@Test
	@Override
	public void testEnum() throws CoreException {
		selectSimple();
		super.testEnum();
	}

	@Test
	@Override
	public void testRange() {
		selectSimple();
		super.testRange();
	}

	@Test
	public void testDescriptionSimple() {
		selectSimple();
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
		SWTBotTreeItem subItem = item.getNode("Simple");

		editorBot.button("Add...").click();
		SWTBotTreeItem item1 = structValueTable.getTreeItem("Struct[1]");
		item1.expand();
		SWTBotTreeItem subItem1 = item1.getNode("Simple");

		StandardTestActions.writeToCell(editorBot, subItem1, 2, "hello");
		assertFormValid();

		item1.select();
		editorBot.button("Remove", 1).click();

		selectSimple();
		editorBot.comboBoxWithLabel("Type*:").setSelection("double (64-bit)");
		assertFormInvalid();
		selectStructSequence();

		structValueTable = editorBot.treeWithLabel("StructValue:");
		item = structValueTable.getTreeItem("Struct[0]");
		item.expand();
		subItem = item.getNode("Simple");

		StandardTestActions.writeToCell(editorBot, subItem, 2, "1.1");
		assertFormValid();
	}

	/**
	 * IDE-1439
	 */
	@Test
	public void testKind() throws IOException {
		testKind(false, false);
	}
}
