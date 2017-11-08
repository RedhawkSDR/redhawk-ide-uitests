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
	public void testKind() throws IOException {
		testKind(true, false);
	}

	@Test
	public void testValue() throws CoreException, IOException {
		testValue("Value:");
	}

	protected void testValue(String valueLabel) throws CoreException, IOException {
		editorBot.textWithLabel(valueLabel).setText("stringValue");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("\"\"");
		assertFormValid();
		Assert.assertEquals("Double-quotes did not convert to empty string", "", getModelObject().getValue());

		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

		setType("boolean", "complex");
		assertFormInvalid();
		editorBot.comboBox(1).setSelection("");
		editorBot.textWithLabel(valueLabel).setText("true");
		assertFormValid();
		editorBot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

		setType("char", "");
		editorBot.textWithLabel(valueLabel).setText("1");
		editorBot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

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

		setType("objref", "complex");
		editorBot.textWithLabel(valueLabel).setText("1");
		assertFormInvalid();
	}

	private void setType(String type, String complex) {
		editorBot.comboBox().setSelection(type);
		editorBot.comboBox(1).setSelection(complex);
	}
}
