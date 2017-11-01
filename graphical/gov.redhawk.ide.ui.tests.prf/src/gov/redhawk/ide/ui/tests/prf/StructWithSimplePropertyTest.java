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

import mil.jpeojtrs.sca.prf.Simple;

public class StructWithSimplePropertyTest extends SimplePropertyTest {

	@Override
	protected void createType() {
		editorBot.button("Add Struct").click();
	}
	
	@Override
	protected Simple getModelObject() throws IOException {
		return getModelFromXml().getStruct().get(0).getSimple().get(0);
	}

	@Override
	public void before() throws Exception {
		super.before();
		editorBot.sleep(600);

		editorBot.tree().expandNode("ID").select("Simple");
		editorBot.textWithLabel("ID*:").setText("Simple");
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
	public void testSimpleID() throws CoreException {
		selectSimple();
		super.testID();
	}

	@Test
	public void testSimpleName() {
		selectSimple();
		super.testName();
	}

	@Test
	@Override
	public void testValue() throws CoreException, IOException {
		selectSimple();
		super.testValue();
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
	public void testAddSecondSimple() {
		editorBot.tree().getTreeItem("ID").select().contextMenu("New").menu("Simple").click();
		assertFormInvalid();
		editorBot.textWithLabel("ID*:").setText("SID2");
		assertFormValid();
	}
}
