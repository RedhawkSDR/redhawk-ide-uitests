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

import mil.jpeojtrs.sca.prf.Simple;

public class SimplePropertyTest extends AbstractPropertyTest<Simple> {

	@Override
	protected void createType() {
		editorBot.button("Add Simple").click();
	}
	
	@Override
	protected Simple getModelObject() throws IOException {
		return getModelFromXml().getSimple().get(0);
	}

	@Test
	public void testID() throws CoreException {
		// ID should already be set
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());

		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
	}

	@Test
	public void testName() {
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		assertFormValid();
		Assert.assertEquals("Name1", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testKind() throws IOException {
		testKind(true, false);
	}

	@Test
	public void testValueString() throws CoreException, IOException {
		testValueString("Value:");
	}

	@Test
	public void testValueBoolean() throws CoreException {
		testValueBoolean("Value:");
	}

	@Test
	public void testValueChar() throws CoreException {
		testValueChar("Value:");
	}

	@Test
	public void testValueDouble() throws CoreException {
		testValueDouble("Value:");
	}

	@Test
	public void testValueFloat() throws CoreException {
		testValueFloat("Value:");
	}

	@Test
	public void testValueLong() throws CoreException {
		testValueLong("Value:");
	}

	@Test
	public void testValueLongLong() throws CoreException {
		testValueLongLong("Value:");
	}

	@Test
	public void testValueShorts() throws CoreException {
		testValueShorts("Value:");
	}

	@Test
	public void testValueObjRef() throws CoreException {
		testValueObjRef("Value:");
	}

	@Test
	public void testValueUTCTime() throws CoreException {
		testValueUTCTime("Value:");
	}

	protected void testValueString(String valueLabel) throws IOException {
		editorBot.textWithLabel(valueLabel).setText("stringValue");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("\"\"");
		Assert.assertEquals("Double-quotes did not convert to empty string", "", getModelObject().getValue());
		assertFormValid();

		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();
	}

	protected void testValueBoolean(String valueLabel) {
		setType("boolean", "complex");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("");
		editorBot.textWithLabel(valueLabel).setText("true");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();
	}

	protected void testValueChar(String valueLabel) {
		setType("char", "");
		editorBot.textWithLabel(valueLabel).setText("1");
		editorBot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();
	}

	protected void testValueDouble(String valueLabel) {
		setType("double (64-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();
	}

	protected void testValueFloat(String valueLabel) {
		setType("float (32-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();
	}

	protected void testValueLong(String valueLabel) {
		setType("long (32-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();

		setType("ulong (32-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();
	}

	protected void testValueLongLong(String valueLabel) {
		setType("longlong (64-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();

		setType("ulonglong (64-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();
	}

	protected void testValueShorts(String valueLabel) {
		setType("short (16-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();

		setType("ushort (16-bit)", "real");
		editorBot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		editorBot.comboBox(1).setSelection("");
		assertFormValid();
	}

	protected void testValueObjRef(String valueLabel) {
		setType("objref", "complex");
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormInvalid();
	}

	protected void testValueUTCTime(String valueLabel) {
		setType("utctime", "real");
		editorBot.textWithLabel(valueLabel).setText("abc");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("2017:01:02::03:04:05");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("123");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("2017:01:02::03:04:05.123456");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("abc");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("now");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

		setType("utctime", "complex");
		assertFormInvalid();
	}

	private void setType(String type, String complex) {
		editorBot.comboBox().setSelection(type);
		editorBot.comboBox(1).setSelection(complex);
	}
}
