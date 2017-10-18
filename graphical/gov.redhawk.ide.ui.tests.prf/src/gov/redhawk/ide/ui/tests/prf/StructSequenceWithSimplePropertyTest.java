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
import org.junit.Test;

public class StructSequenceWithSimplePropertyTest extends SimplePropertyTest {

	@Override
	public void before() throws Exception {
		super.before();

		editorBot.sleep(600);
		editorBot.tree().expandNode("ID").select("Struct");
		editorBot.textWithLabel("ID*:").setText("Struct");
		editorBot.sleep(600);
		editorBot.tree().expandNode("ID", "Struct").select("Simple");
		editorBot.textWithLabel("ID*:").setText("Simple");
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
	public void testStructID() throws CoreException {
		selectStruct();
		super.testID();
	}

	@Test
	public void testSimpleID() throws CoreException {
		selectSimple();
		super.testID();
	}

	@Test
	public void testStructName() {
		selectStruct();
		super.testName();
	}

	@Test
	public void testSimpleName() {
		selectSimple();
		super.testName();
	}

	@Test
	@Override
	public void testValue() throws CoreException {
		selectSimple();
		testValue("Default Value:");
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
