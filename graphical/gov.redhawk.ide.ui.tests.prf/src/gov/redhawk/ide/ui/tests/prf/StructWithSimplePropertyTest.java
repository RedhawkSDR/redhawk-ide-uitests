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
import org.junit.Assert;
import org.junit.Test;

public class StructWithSimplePropertyTest extends SimplePropertyTest {

	@Override
	protected void createType() {
		editorBot.button("Add Struct").click();
	}

	@Override
	public void before() throws Exception {
		super.before();

		editorBot.tree().expandNode("ID").select("Simple");
		editorBot.textWithLabel(NAME_FIELD).setText("Simple");
		editorBot.sleep(600);

		selectStruct();
	}

	protected void selectStruct() {
		editorBot.tree().select("ID");
	}

	protected void selectSimple() {
		editorBot.tree().getTreeItem("ID").select("Simple");
	}

	@Test
	public void testID() throws CoreException {
		// ID should already be set
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());

		// Set ID to empty - error, child loses prefix
		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		selectSimple();
		Assert.assertEquals("Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to a DCE - ok, child still has no prefix
		editorBot.textWithLabel(ID_FIELD).setText("DCE:12345678-9abc-def0-1234-56789abcdef0");
		assertFormValid();
		selectSimple();
		Assert.assertEquals("Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to something valid - ok, child has prefix
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
		selectSimple();
		Assert.assertEquals("hello::Simple", editorBot.textWithLabel(ID_FIELD).getText());
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
		selectSimple();
		Assert.assertEquals("ID::Simple", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set name to valid - IDs change
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		editorBot.sleep(600);
		Assert.assertEquals("Name1", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("Name1").select("Simple");
		Assert.assertEquals("Name1::Simple", editorBot.textWithLabel(ID_FIELD).getText());
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
		super.testValueString();
	}

	@Test
	@Override
	public void testValueBoolean() throws CoreException {
		selectSimple();
		super.testValueBoolean();
	}

	@Test
	@Override
	public void testValueChar() throws CoreException {
		selectSimple();
		super.testValueChar();
	}

	@Test
	@Override
	public void testValueDouble() throws CoreException {
		selectSimple();
		super.testValueDouble();
	}

	@Test
	@Override
	public void testValueFloat() throws CoreException {
		selectSimple();
		super.testValueFloat();
	}

	@Test
	@Override
	public void testValueLong() throws CoreException {
		selectSimple();
		super.testValueLong();
	}

	@Test
	@Override
	public void testValueLongLong() throws CoreException {
		selectSimple();
		super.testValueLongLong();
	}

	@Test
	@Override
	public void testValueShorts() throws CoreException {
		selectSimple();
		super.testValueShorts();
	}

	@Test
	@Override
	public void testValueObjRef() throws CoreException {
		selectSimple();
		super.testValueObjRef();
	}

	@Test
	@Override
	public void testValueUTCTime() throws CoreException {
		selectSimple();
		super.testValueUTCTime();
	}

	@Test
	@Override
	public void testEnum() throws CoreException {
		selectSimple();
		super.testEnum();
	}

	@Test
	@Override
	public void testUnits() {
		selectSimple();
		super.testUnits();
	}

	@Test
	@Override
	public void testRange() {
		selectSimple();
		super.testRange();
	}

	@Test
	public void testSimpleDescription() {
		selectSimple();
		super.testDescription();
	}

	@Override
	public void testUniqueID() {
		selectSimple();
		editorBot.textWithLabel(ID_FIELD).setText("ID");
		assertFormInvalid();
		editorBot.textWithLabel(ID_FIELD).setText("SID");
		assertFormValid();

		super.testUniqueID();
	}

	/**
	 * IDE-1439
	 */
	@Test
	public void testKind() throws IOException {
		testKind(false, true);
	}

	@Test
	public void testAddSecondSimple() {
		editorBot.tree().getTreeItem("ID").select().contextMenu("New").menu("Simple").click();
		assertFormInvalid();
		editorBot.textWithLabel(ID_FIELD).setText("SID2");
		assertFormValid();
	}
}
