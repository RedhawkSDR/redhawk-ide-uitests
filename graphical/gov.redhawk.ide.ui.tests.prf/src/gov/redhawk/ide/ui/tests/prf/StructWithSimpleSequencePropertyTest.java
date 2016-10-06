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
import org.junit.Test;

public class StructWithSimpleSequencePropertyTest extends SimpleSequencePropertyTest {

	@Override
	protected void createType() {
		bot.button("Add Struct").click();
	}

	@Override
	public void before() throws Exception {
		super.before();
		editor.bot().sleep(600);

		editor.bot().tree().expandNode("ID").select("Simple");
		bot.button("Remove").click();

		editor.bot().tree().select("ID").contextMenu("New").menu("Simple Sequence").click();
		selectSimpleSequence();
		editor.bot().textWithLabel("ID*:").setText("Simple Sequence");
		editor.bot().sleep(600);

		selectStruct();
	}

	protected void selectStruct() {
		editor.bot().tree().select("ID");
	}

	protected void selectSimpleSequence() {
		editor.bot().tree().getTreeItem("ID").select("Simple Sequence");
	}

	@Test
	public void testSimpleSequenceName() {
		selectSimpleSequence();
		super.testName();
	}

	@Test
	public void testSimpleSequenceID() throws CoreException {
		selectSimpleSequence();
		super.testID();
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
	public void testSimpleSequenceDescription() {
		selectSimpleSequence();
		super.testDescription();
	}

	@Override
	public void testUniqueID() {
		selectSimpleSequence();
		editor.bot().textWithLabel("ID*:").setText("ID");
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("SID");
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
		editor.bot().tree().getTreeItem("ID").contextMenu("New").menu("Simple Sequence").click();
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("SID2");
		assertFormValid();
	}

	@Override
	public void testAction() {
		// No Action element available for structs or simple sequences within structs
	}

}
