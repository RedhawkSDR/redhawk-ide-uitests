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
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.Test;

public class SimplePropertyTest extends AbstractPropertyTest {

	@Override
	protected void createType() {
		bot.button("Add Simple").click();
	}

	@Test
	public void testKind() throws IOException {
		testKind(true);
	}

	@Test
	public void testValue() throws CoreException {
		testValue("Value:");
	}

	protected void testValue(String valueLabel) throws CoreException {
		SWTBot editorBot = editor.bot();
		editorBot.textWithLabel(valueLabel).setText("stringValue");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("\"\"");
		assertFormValid();

		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

		setType("boolean", "complex");
		assertFormInvalid();
		bot.comboBox(1).setSelection("");
		bot.textWithLabel(valueLabel).setText("true");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

		setType("char", "");
		bot.textWithLabel(valueLabel).setText("1");
		bot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

		setType("double (64-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("float (32-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("longlong (64-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("long (32-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("short (16-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("ulong (32-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("ulonglong (64-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("ushort (16-bit)", "real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();

		setType("objref", "complex");
		bot.textWithLabel(valueLabel).setText("1");
		assertFormInvalid();
	}

	private void setType(String type, String complex) {
		bot.comboBox().setSelection(type);
		bot.comboBox(1).setSelection(complex);
	}
}
