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

import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.SimpleSequence;

public class StructWithSimpleSequencePropertyTest extends SimpleSequencePropertyTest {

	@Override
	protected void createType() {
		editorBot.button("Add Struct").click();
	}
	
	@Override
	protected SimpleSequence getModelObject() throws IOException {
		return getModelFromXml().getStruct().get(0).getSimpleSequence().get(0);
	}

	@Override
	public void before() throws Exception {
		super.before();
		editorBot.sleep(600);

		editorBot.tree().expandNode("ID").select("Simple");
		editorBot.button("Remove").click();

		editorBot.tree().select("ID").contextMenu("New").menu("Simple Sequence").click();
		selectSimpleSequence();
		editorBot.textWithLabel("ID*:").setText("Simple Sequence");
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
	public void testSimpleSequenceID() throws CoreException {
		selectSimpleSequence();
		super.testID();
	}

	@Test
	public void testSimpleSequenceName() {
		selectSimpleSequence();
		super.testName();
	}

	@Test
	@Override
	public void testValues() throws CoreException, IOException {
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
