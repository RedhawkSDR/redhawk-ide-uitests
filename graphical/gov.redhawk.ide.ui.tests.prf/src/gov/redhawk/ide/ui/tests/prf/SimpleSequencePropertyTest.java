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
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;
import org.junit.Test;

public class SimpleSequencePropertyTest extends AbstractPropertyTest {

	@Test
	public void testValues() throws CoreException {
		SWTBotTable valuesViewer = editor.bot().tableWithLabel("Values:");
		// Start with type selected as string
		addValue(bot, "true");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "a");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "true");
		assertFormValid();
		addValue(bot, "true");
		assertFormValid();
		clearValues();

		setType("char", "");
		addValue(bot, "1");
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "abc");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1");
		assertFormValid();
		addValue(bot, "1");
		assertFormValid();
		clearValues();

		setType("double (64-bit)", "");
		bot.button("Add...").click();
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		bot.button("OK").click();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "al");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-1.1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		setType("float (32-bit)", "complex");
		bot.button("Add...").click();
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1+jjak");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1+j10.1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		setType("long (32-bit)", "");
		addValue(bot, "-11");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1.1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		setType("longlong (64-bit)", "");
		addValue(bot, "-11");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1.1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		setType("short (16-bit)", "complex");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-11-j2");
		bot.button("OK").click();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1+100iada");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-11-j2");
		assertFormValid();
		addValue(bot, "-11-j2");
		assertFormValid();
		clearValues();

		setType("ulong (32-bit)", "");
		addValue(bot, "11");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		setType("ulonglong (64-bit)", "");
		addValue(bot, "11");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		setType("ushort (16-bit)", "complex");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11+j2");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1+j1ada");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11+j2");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11+j2");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		setType("objref", "");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.button("Cancel").click();
		assertFormValid();
		clearValues();

		setType("string", "");
		addValue(bot, "abcd");
		assertFormValid();
		addValue(bot, "efg");
		assertFormValid();
		clearValues();
	}

	private void setType(String type, String complex) {
		bot.comboBoxWithLabel("Type*:").setSelection(type);
		bot.comboBoxWithLabel("Type*:", 1).setSelection(complex);
	}

	private void addValue(SWTWorkbenchBot bot, String text) {
		final int startingRows = bot.table().rowCount();
		bot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText(text);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				int endingRows = bot.table().rowCount();
				return (startingRows + 1) == endingRows;
			}

			@Override
			public String getFailureMessage() {
				return "Value was not added to table";
			}
		});
	}

	private void clearValues() {
		SWTBotTable valuesTable = bot.tableWithLabel("Values:");
		if (valuesTable.rowCount() > 0) {
			for (int i = 0; i <= valuesTable.rowCount(); i++) {
				valuesTable.select(0);
				SWTBotButton removeButton = bot.button("Remove", 1);
				if (removeButton.isEnabled()) {
					bot.button("Remove", 1).click();
				} else {
					break;
				}
			}
		}
	}

	@Override
	protected void createType() {
		bot.button("Add Sequence").click();
	}

	@Override
	public void testEnum() throws CoreException {
		// Override to do nothing since Simple Sequences do not have enums
	}

	@Test
	public void testKind() throws IOException {
		assertFormValid();

		testKind(false);
	}
}
