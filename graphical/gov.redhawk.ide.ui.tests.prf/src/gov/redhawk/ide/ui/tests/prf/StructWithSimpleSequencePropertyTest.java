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

import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.SimpleSequence;

public class StructWithSimpleSequencePropertyTest extends SimpleSequencePropertyTest {

	@Override
	protected void createType() {
		editorBot.button("Add Struct").click();
	}

	@Override
	public void before() throws Exception {
		super.before();

		editorBot.tree().expandNode("ID").select("Simple");
		editorBot.button("Remove").click();

		editorBot.tree().select("ID").contextMenu("New").menu("Simple Sequence").click();
		selectSimpleSequence();
		editorBot.textWithLabel(NAME_FIELD).setText("Simple Sequence");
		editorBot.sleep(600);

		selectStruct();
	}

	protected void selectStruct() {
		editorBot.tree().select("ID");
	}

	protected void selectSimpleSequence() {
		editorBot.tree().getTreeItem("ID").select("Simple Sequence");
	}

	@Test
	public void testID() throws CoreException {
		// ID should already be set
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());

		// Set ID to empty - error, child loses prefix
		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		selectSimpleSequence();
		Assert.assertEquals("Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to a DCE - ok, child still has no prefix
		editorBot.textWithLabel(ID_FIELD).setText("DCE:12345678-9abc-def0-1234-56789abcdef0");
		assertFormValid();
		selectSimpleSequence();
		Assert.assertEquals("Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set ID to something valid - ok, child has prefix
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
		selectSimpleSequence();
		Assert.assertEquals("hello::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
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
		selectSimpleSequence();
		Assert.assertEquals("ID::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
		selectStruct();

		// Set name to valid - IDs change
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		editorBot.sleep(600);
		Assert.assertEquals("Name1", editorBot.textWithLabel(ID_FIELD).getText());
		editorBot.tree().getTreeItem("Name1").select("Simple Sequence");
		Assert.assertEquals("Name1::Simple Sequence", editorBot.textWithLabel(ID_FIELD).getText());
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
	public void testSimpleSequenceDescription() {
		selectSimpleSequence();
		super.testDescription();
	}

	@Override
	public void testUniqueID() {
		selectSimpleSequence();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormInvalid();
		editorBot.textWithLabel("ID*:").setText("SID");
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
	public void testAddSecondSimpleSequence() {
		editorBot.tree().getTreeItem("ID").contextMenu("New").menu("Simple Sequence").click();
		assertFormInvalid();
		editorBot.textWithLabel("ID*:").setText("SID2");
		assertFormValid();
	}

	@Override
	public void testValueAutoUpdate() throws IOException {
		selectSimpleSequence();
		super.testValueAutoUpdate();
	}

	@Override
	protected SimpleSequence getSimpleSequence(Properties properties) {
		return properties.getStruct().get(0).getSimpleSequence().get(0);
	}
}
