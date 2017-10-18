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
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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

	protected static final String ID_FIELD = "ID*:";
	protected static final String NAME_FIELD = "Name:";

	@Before
	public void before() throws Exception {
		super.before();

		createType();
		editorBot.textWithLabel(NAME_FIELD).setText("ID");
		editorBot.sleep(600);
	}

	protected abstract void createType();

	@Test
	public void testCreate() throws CoreException {
		assertFormValid();
	}

	@Test
	public void testUniqueID() {
		assertFormValid();
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel(ID_FIELD).setText("ID");
		assertFormInvalid();
		editorBot.textWithLabel(ID_FIELD).setText("ID2");
		assertFormValid();
	}

	@Test
	public void testUnits() {
		editorBot.textWithLabel("Units:").setText("m");
		editorBot.textWithLabel("Units:").setText("");
		assertFormValid();
	}

	@Test
	public void testRange() {
		editorBot.comboBox().setSelection("boolean");
		assertFormValid();

		editorBot.checkBox("Enable").click();
		assertFormInvalid();

		editorBot.textWithLabel("Min:").setText("true");
		editorBot.textWithLabel("Max:").setText("true");
		assertFormValid();

		editorBot.textWithLabel("Min:").setText("asopina");
		assertFormInvalid();

		editorBot.comboBox().setSelection("double (64-bit)");
		editorBot.comboBox(1).setSelection("complex");
		editorBot.textWithLabel("Max:").setText("20");
		editorBot.textWithLabel("Min:").setText("-1.1");
		assertFormValid();

		editorBot.textWithLabel("Max:").setText("20+i10sad");
		assertFormInvalid();

		editorBot.textWithLabel("Max:").setText("20");
		assertFormValid();

		editorBot.textWithLabel("Min:").setText("-1.1+ja");
		assertFormInvalid();

		editorBot.textWithLabel("Min:").setText("-1.1");
		assertFormValid();

		editorBot.textWithLabel("Max:").setText("10+j10.5");
		assertFormValid();

		editorBot.textWithLabel("Min:").setText("-1.1+j1");
		assertFormValid();

		editorBot.textWithLabel("Min:").setText("bad");
		editorBot.textWithLabel("Max:").setText("bad");
		assertFormInvalid();

		editorBot.checkBox("Enable").click();
		assertFormValid();
	}

	protected void testKind(boolean supportsExec, boolean supportsMessage) throws IOException {
		assertFormValid();

		SWTBotCombo kindCombo = editorBot.comboBoxWithLabel("Kind:");
		kindCombo.setSelection("property (default)");
		assertFormValid();
		assertKind(getModelFromXml().getProperty("ID"), PropertyConfigurationType.PROPERTY);

		if (supportsExec) {
			SWTBotCheckBox checkBox = editorBot.checkBox("Pass on command line");
			Assert.assertTrue(checkBox.isEnabled());
			checkBox.click();
			Assert.assertTrue(checkBox.isChecked());
			AbstractProperty prop = getModelFromXml().getProperty("ID");
			if (prop instanceof Simple) {
				Assert.assertTrue(((Simple) prop).getCommandline());
				Assert.assertTrue(((Simple) prop).isCommandLine());
			}

			kindCombo.setSelection("allocation");
			Assert.assertFalse(checkBox.isEnabled());
			Assert.assertFalse(checkBox.isChecked());
			prop = getModelFromXml().getProperty("ID");
			assertKind(prop, PropertyConfigurationType.ALLOCATION);
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

		if (supportsMessage) {
			kindCombo.setSelection("message");
			assertFormValid();
			assertKind(getModelFromXml().getProperty("ID"), PropertyConfigurationType.MESSAGE);
		}
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
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("Enumeration Wizard");
		shell.bot().textWithLabel("Label:").setText("lab");
		shell.bot().textWithLabel("Value:").setText("asf");
		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();

		SWTBotTable enumTable = editorBot.tableWithLabel("Enumerations:");
		SWTBotTableItem item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("asf", item.getText(1));

		item.select();
		editorBot.button("Edit").click();
		shell = bot.shell("Enumeration Wizard");
		Assert.assertEquals("lab", shell.bot().textWithLabel("Label:").getText());
		Assert.assertEquals("asf", shell.bot().textWithLabel("Value:").getText());
		shell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("asf", item.getText(1));

		editorBot.button("Edit").click();
		shell = bot.shell("Enumeration Wizard");
		shell.bot().textWithLabel("Value:").setText("abc");
		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("abc", item.getText(1));

		item = enumTable.getTableItem(0);
		item.select();
		editorBot.button("Remove", 1).click();
		assertFormValid();
		Assert.assertEquals(0, enumTable.rowCount());
	}

	@Test
	public void testDescription() {
		editorBot.textWithLabel("Description:").setText("This is a test");
		assertFormValid();
	}

	@Test
	public void testMode() {
		editorBot.comboBoxWithLabel("Mode:").setSelection("writeonly");
		assertFormValid();
		editorBot.comboBoxWithLabel("Mode:").setSelection("readonly");
		assertFormValid();
		editorBot.comboBoxWithLabel("Mode:").setSelection("readwrite");
		assertFormValid();
	}
}
