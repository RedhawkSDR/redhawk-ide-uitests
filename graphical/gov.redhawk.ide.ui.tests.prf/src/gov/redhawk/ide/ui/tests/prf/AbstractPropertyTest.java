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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructSequence;

public abstract class AbstractPropertyTest extends AbstractPropertyTabTest {

	@Before
	public void before() throws Exception {
		super.before();

		createType();
		editor.bot().textWithLabel("ID*:").setText("ID");
	}

	protected abstract void createType();

	@Test
	public void testCreate() throws CoreException {
		assertFormValid();
	}

	@Test
	public void testID() throws CoreException {
		editor.bot().textWithLabel("ID*:").setText("");
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("hello");
		assertFormValid();
	}

	@Test
	public void testUniqueID() {
		assertFormValid();
		editor.bot().button("Add Simple").click();
		editor.bot().textWithLabel("ID*:").setText("ID");
		assertFormInvalid();
		editor.bot().textWithLabel("ID*:").setText("ID2");
		assertFormValid();
	}

	@Test
	public void testUnits() {
		editor.bot().textWithLabel("Units:").setText("m");
		editor.bot().textWithLabel("Units:").setText("");
		assertFormValid();
	}

	@Test
	public void testAction() {
		editor.bot().comboBoxWithLabel("Action:").setSelection("eq");
		assertFormValid();

		editor.bot().comboBoxWithLabel("Action:").setSelection("ge");
		assertFormValid();

		editor.bot().comboBoxWithLabel("Action:").setSelection("gt");
		assertFormValid();

		editor.bot().comboBoxWithLabel("Action:").setSelection("le");
		assertFormValid();

		editor.bot().comboBoxWithLabel("Action:").setSelection("lt");
		assertFormValid();

		editor.bot().comboBoxWithLabel("Action:").setSelection("ne");
		assertFormValid();
	}

	@Test
	public void testRange() {
		editor.bot().comboBox().setSelection("boolean");
		assertFormValid();

		editor.bot().checkBox("Enable").click();
		assertFormInvalid();

		editor.bot().textWithLabel("Min:").setText("true");
		editor.bot().textWithLabel("Max:").setText("true");
		assertFormValid();

		editor.bot().textWithLabel("Min:").setText("asopina");
		assertFormInvalid();

		editor.bot().comboBox().setSelection("double (64-bit)");
		editor.bot().comboBox(1).setSelection("complex");
		editor.bot().textWithLabel("Max:").setText("20");
		editor.bot().textWithLabel("Min:").setText("-1.1");
		assertFormValid();

		editor.bot().textWithLabel("Max:").setText("20+i10sad");
		assertFormInvalid();

		editor.bot().textWithLabel("Max:").setText("20");
		assertFormValid();

		editor.bot().textWithLabel("Min:").setText("-1.1+ja");
		assertFormInvalid();

		editor.bot().textWithLabel("Min:").setText("-1.1");
		assertFormValid();

		editor.bot().textWithLabel("Max:").setText("10+j10.5");
		assertFormValid();

		editor.bot().textWithLabel("Min:").setText("-1.1+j1");
		assertFormValid();

		editor.bot().textWithLabel("Min:").setText("bad");
		editor.bot().textWithLabel("Max:").setText("bad");
		assertFormInvalid();

		editor.bot().checkBox("Enable").click();
		assertFormValid();
	}

	protected void testKind(boolean supportsExec) throws IOException {
		assertFormValid();

		SWTBotCombo kindCombo = editor.bot().comboBoxWithLabel("Kind:");
		kindCombo.setSelection("property (default)");
		assertFormValid();
		assertKind(getModelFromXml().getProperty("ID"), PropertyConfigurationType.PROPERTY);

		if (supportsExec) {
			SWTBotCheckBox checkBox = editor.bot().checkBox("Pass on command line");
			Assert.assertTrue(checkBox.isEnabled());
			checkBox.click();
			Assert.assertTrue(checkBox.isChecked());
			AbstractProperty prop = getModelFromXml().getProperty("ID");
			if (prop instanceof Simple) {
				Assert.assertTrue(((Simple) prop).getCommandline());
				Assert.assertTrue(((Simple) prop).isCommandLine());
			}

			kindCombo.setSelection("message");
			Assert.assertFalse(checkBox.isEnabled());
			Assert.assertFalse(checkBox.isChecked());
			prop = getModelFromXml().getProperty("ID");
			assertKind(prop, PropertyConfigurationType.MESSAGE);
			if (prop instanceof Simple) {
				Boolean commandLine = ((Simple) prop).getCommandline();
				Assert.assertTrue(commandLine == null || commandLine == false);
				Assert.assertFalse(((Simple) prop).isCommandLine());
			}

			kindCombo.setSelection("property (default)");
			Assert.assertTrue(checkBox.isEnabled());
			Assert.assertFalse(checkBox.isChecked());
			prop = getModelFromXml().getProperty("ID");
			assertKind(prop, PropertyConfigurationType.PROPERTY);
			if (prop instanceof Simple) {
				Boolean commandLine = ((Simple) prop).getCommandline();
				Assert.assertTrue(commandLine == null || commandLine == false);
				Assert.assertFalse(((Simple) prop).isCommandLine());
			}
		}

		kindCombo.setSelection("allocation");
		assertFormValid();
		assertKind(getModelFromXml().getProperty("ID"), PropertyConfigurationType.ALLOCATION);

		kindCombo.setSelection("message");
		assertFormValid();
		assertKind(getModelFromXml().getProperty("ID"), PropertyConfigurationType.MESSAGE);
	}

	private void assertKind(AbstractProperty prop, PropertyConfigurationType type) {
		List<Kind> kinds;
		List<ConfigurationKind> configurationKinds;
		switch (prop.eClass().getClassifierID()) {
		case PrfPackage.SIMPLE:
			kinds = ((Simple) prop).getKind();
			Assert.assertNotNull(kinds);
			Assert.assertEquals(1, kinds.size());
			Assert.assertEquals(type, kinds.get(0).getType());
			break;
		case PrfPackage.SIMPLE_SEQUENCE:
			kinds = ((SimpleSequence) prop).getKind();
			Assert.assertNotNull(kinds);
			Assert.assertEquals(1, kinds.size());
			Assert.assertEquals(type, kinds.get(0).getType());
			break;
		case PrfPackage.STRUCT:
			configurationKinds = ((Struct) prop).getConfigurationKind();
			Assert.assertNotNull(configurationKinds);
			Assert.assertEquals(1, configurationKinds.size());
			Assert.assertEquals(type, configurationKinds.get(0).getType().getPropertyConfigurationType());
			break;
		case PrfPackage.STRUCT_SEQUENCE:
			configurationKinds = ((StructSequence) prop).getConfigurationKind();
			Assert.assertNotNull(configurationKinds);
			Assert.assertEquals(1, configurationKinds.size());
			Assert.assertEquals(type, configurationKinds.get(0).getType().getPropertyConfigurationType());
			break;
		default:
			break;
		}
	}

	@Test
	public void testEnum() throws CoreException {
		editor.bot().button("Add...").click();
		bot.textWithLabel("Label:").setText("lab");
		bot.textWithLabel("Value:").setText("asf");
		bot.button("Finish").click();
		assertFormValid();

		SWTBotTable enumTable = editor.bot().tableWithLabel("Enumerations:");
		SWTBotTableItem item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("asf", item.getText(1));

		item.select();
		editor.bot().button("Edit").click();
		Assert.assertEquals("lab", bot.textWithLabel("Label:").getText());
		Assert.assertEquals("asf", bot.textWithLabel("Value:").getText());
		bot.button("Cancel").click();
		item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("asf", item.getText(1));

		editor.bot().button("Edit").click();
		bot.textWithLabel("Value:").setText("abc");
		bot.button("Finish").click();
		item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("abc", item.getText(1));

		item = enumTable.getTableItem(0);
		item.select();
		editor.bot().button("Remove", 1).click();
		assertFormValid();
		Assert.assertEquals(0, enumTable.rowCount());
	}

	@Test
	public void testDescription() {
		editor.bot().textWithLabel("Description:").setText("This is a test");
		assertFormValid();
	}

	@Test
	public void testName() {
		editor.bot().textWithLabel("Name:").setText("Name1");
		assertFormValid();
	}

	@Test
	public void testMode() {
		editor.bot().comboBoxWithLabel("Mode:").setSelection("writeonly");
		assertFormValid();
		editor.bot().comboBoxWithLabel("Mode:").setSelection("readonly");
		assertFormValid();
		editor.bot().comboBoxWithLabel("Mode:").setSelection("readwrite");
		assertFormValid();
	}
}
