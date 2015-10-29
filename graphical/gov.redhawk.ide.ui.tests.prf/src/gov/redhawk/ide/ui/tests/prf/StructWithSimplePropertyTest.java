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

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class StructWithSimplePropertyTest extends SimplePropertyTest {

	@Override
	protected void createType() {
		bot.button("Add Struct").click();
	}

	@Override
	public void before() throws Exception {
		super.before();
		editor.bot().sleep(600);

		editor.bot().tree().expandNode("ID").select("Simple");
		editor.bot().textWithLabel("ID*:").setText("Simple");
		editor.bot().sleep(600);

		selectStruct();
	}

	protected void selectStruct() {
		editor.bot().tree().select("ID");
	}

	protected void selectSimple() {
		editor.bot().tree().getTreeItem("ID").select("Simple");
	}

	@Test
	public void testSimpleName() {
		selectSimple();
		super.testName();
	}

	@Test
	public void testSimpleID() throws CoreException {
		selectSimple();
		super.testID();
	}

	@Test
	@Override
	public void testValue() throws CoreException {
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
		editor.bot().textWithLabel("ID*:").setText("ID");
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("SID");
		assertFormValid();

		super.testUniqueID();
	}

	@Test
	public void testKind() {
		testKind(false);
	}

	@Test
	public void testAddSecondSimple() {
		editor.bot().tree().getTreeItem("ID").select().contextMenu("New").menu("Simple").click();
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("SID2");
		assertFormValid();
	}

	@Override
	public void testAction() {
		// No Action element available for structs or simples within structs
	}

}
