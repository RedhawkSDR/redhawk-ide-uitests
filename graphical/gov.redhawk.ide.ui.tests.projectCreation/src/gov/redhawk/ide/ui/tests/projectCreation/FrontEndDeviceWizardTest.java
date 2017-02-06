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
package gov.redhawk.ide.ui.tests.projectCreation;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.ui.tests.projectCreation.util.FEICodegenInfo;
import gov.redhawk.ide.ui.tests.projectCreation.util.ICodegenInfo;
import gov.redhawk.ide.ui.tests.projectCreation.util.FEICodegenInfo.TunerType;

public class FrontEndDeviceWizardTest extends ComponentWizardTest {

	@Override
	protected String getProjectType() {
		return "REDHAWK Front End Device Project";
	}

	@Test
	public void receiver1() {
		FEICodegenInfo feiInfo = new FEICodegenInfo();
		feiInfo.setType(TunerType.RECEIVE);
		feiInfo.setInputDigital(false);
		feiInfo.setAnalogInputPorts(2);
		feiInfo.setOutputDigital(true);
		feiInfo.setOutputPortType("dataLong");
		feiInfo.setMultiOut(true);
		testProjectCreation("FEIReceiver1", "C++", "C++ Code Generator", feiInfo);

		SWTBotEditor editor = bot.editorByTitle("FEIReceiver1");
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Ports").activate();
		Assert.assertEquals(4, editorBot.tree().rowCount());
		assertContainsPort(editorBot, true, "RFInfo_in", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, true, "RFInfo_in_2", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, true, "DigitalTuner_in", "IDL:FRONTEND/DigitalTuner:1.0");
		assertContainsPort(editorBot, false, "dataLong_out", "IDL:BULKIO/dataLong:1.0");
	}

	@Test
	public void receiver2() {
		FEICodegenInfo feiInfo = new FEICodegenInfo();
		feiInfo.setIngestsGPS(true);
		feiInfo.setType(TunerType.RECEIVE);
		feiInfo.setInputDigital(false);
		feiInfo.setOutputDigital(false);
		testProjectCreation("FEIReceiver2", "Python", "Python Code Generator", feiInfo);

		SWTBotEditor editor = bot.editorByTitle("FEIReceiver2");
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Ports").activate();
		Assert.assertEquals(4, editorBot.tree().rowCount());
		assertContainsPort(editorBot, true, "GPS_in", "IDL:FRONTEND/GPS:1.0");
		assertContainsPort(editorBot, true, "RFInfo_in", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, true, "AnalogTuner_in", "IDL:FRONTEND/AnalogTuner:1.0");
		assertContainsPort(editorBot, false, "RFInfo_out", "IDL:FRONTEND/RFInfo:1.0");
	}

	@Test
	public void receiver3() {
		FEICodegenInfo feiInfo = new FEICodegenInfo();
		feiInfo.setOutputsGPS(true);
		feiInfo.setType(TunerType.RECEIVE);
		feiInfo.setInputDigital(true);
		feiInfo.setInputPortType("dataFloat");
		feiInfo.setOutputDigital(true);
		feiInfo.setOutputPortType("dataShort");
		feiInfo.setMultiOut(false);
		testProjectCreation("FEIReceiver3", "C++", "C++ Code Generator", feiInfo);

		SWTBotEditor editor = bot.editorByTitle("FEIReceiver3");
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Ports").activate();
		Assert.assertEquals(4, editorBot.tree().rowCount());
		assertContainsPort(editorBot, false, "GPS_out", "IDL:FRONTEND/GPS:1.0");
		assertContainsPort(editorBot, true, "DigitalTuner_in", "IDL:FRONTEND/DigitalTuner:1.0");
		assertContainsPort(editorBot, true, "dataFloat_in", "IDL:BULKIO/dataFloat:1.0");
		assertContainsPort(editorBot, false, "dataShort_out", "IDL:BULKIO/dataShort:1.0");
	}

	@Test
	public void transmitter1() {
		FEICodegenInfo feiInfo = new FEICodegenInfo();
		feiInfo.setType(TunerType.TRANSMIT);
		feiInfo.setDigitalInputPorts(2);
		feiInfo.setInputPortType("dataUlong");
		testProjectCreation("FEITransmitter1", "C++", "C++ Code Generator", feiInfo);

		SWTBotEditor editor = bot.editorByTitle("FEITransmitter1");
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Ports").activate();
		Assert.assertEquals(5, editorBot.tree().rowCount());
		assertContainsPort(editorBot, true, "DigitalTuner_in", "IDL:FRONTEND/DigitalTuner:1.0");
		assertContainsPort(editorBot, false, "RFInfoTX_out", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, false, "RFInfoTX_out_2", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, true, "dataUlongTX_in", "IDL:BULKIO/dataUlong:1.0");
		assertContainsPort(editorBot, true, "dataUlongTX_in_2", "IDL:BULKIO/dataUlong:1.0");
	}

	@Test
	public void transmitter2() {
		FEICodegenInfo feiInfo = new FEICodegenInfo();
		feiInfo.setType(TunerType.TRANSMIT);
		testProjectCreation("FEITransmitter2", "Python", "Python Code Generator", feiInfo);

		SWTBotEditor editor = bot.editorByTitle("FEITransmitter2");
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Ports").activate();
		Assert.assertEquals(3, editorBot.tree().rowCount());
		assertContainsPort(editorBot, true, "DigitalTuner_in", "IDL:FRONTEND/DigitalTuner:1.0");
		assertContainsPort(editorBot, false, "RFInfoTX_out", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, true, "dataDoubleTX_in", "IDL:BULKIO/dataDouble:1.0");
	}

	@Test
	public void receiveAndTransmit() {
		FEICodegenInfo feiInfo = new FEICodegenInfo();
		feiInfo.setType(TunerType.BOTH);
		feiInfo.setInputDigital(false);
		feiInfo.setOutputDigital(true);
		testProjectCreation("FEI_RxAndTx", "C++", "C++ Code Generator", feiInfo);

		SWTBotEditor editor = bot.editorByTitle("FEI_RxAndTx");
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Ports").activate();
		Assert.assertEquals(5, editorBot.tree().rowCount());
		assertContainsPort(editorBot, true, "RFInfo_in", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, true, "DigitalTuner_in", "IDL:FRONTEND/DigitalTuner:1.0");
		assertContainsPort(editorBot, false, "dataDouble_out", "IDL:BULKIO/dataDouble:1.0");
		assertContainsPort(editorBot, false, "RFInfoTX_out", "IDL:FRONTEND/RFInfo:1.0");
		assertContainsPort(editorBot, true, "dataDoubleTX_in", "IDL:BULKIO/dataDouble:1.0");
	}

	protected void setupCodeGeneration(ICodegenInfo iCodegenInfo) {
		getWizardBot().button("Next >").click();
		if (!(iCodegenInfo instanceof FEICodegenInfo)) {
			// Leave defaults for the Setup Code Generation page and FEI pages
			getWizardBot().button("Next >").click();
			getWizardBot().button("Next >").click();
			return;
		}
		FEICodegenInfo codegenInfo = (FEICodegenInfo) iCodegenInfo;

		// FEI device type
		SWTBotCheckBox checkBox = bot.checkBox("This device will ingest GPS data");
		if (codegenInfo.isIngestsGPS() != checkBox.isChecked()) {
			checkBox.click();
		}
		checkBox = bot.checkBox("This device will output GPS data");
		if (codegenInfo.isOutputsGPS() != checkBox.isChecked()) {
			checkBox.click();
		}
		switch (codegenInfo.getType()) {
		case RECEIVE:
			bot.radio("Receive only (default)").click();
			break;
		case TRANSMIT:
			bot.radio("Transmit only").click();
			break;
		case BOTH:
			bot.radio("Both receive and transmit").click();
			break;
		default:
			Assert.fail();
			break;
		}
		getWizardBot().button("Next >").click();

		// FEI device options
		switch (codegenInfo.getType()) {
		case RECEIVE:
			receivePage(codegenInfo);
			break;
		case TRANSMIT:
			transmitPage(codegenInfo);
			break;
		case BOTH:
			receivePage(codegenInfo);
			transmitPage(codegenInfo);
			break;
		default:
			Assert.fail();
		}
		getWizardBot().button("Next >").click();

		// FEI properties
		// TODO
	}

	protected void reverseFromCodeGeneration() {
		getWizardBot().button("< Back").click();
		getWizardBot().button("< Back").click();
		getWizardBot().button("< Back").click();
	}

	@Override
	protected void nonDefaultLocation_setupCodeGeneration() {
		setupCodeGeneration(null);
	}

	@Override
	protected void nonDefaultLocation_assertOutputDir() {
		// PASS
	}

	private void receivePage(FEICodegenInfo codegenInfo) {
		if (codegenInfo.isInputDigital()) {
			getWizardBot().radio("Digital Input").click();
			if (codegenInfo.getInputPortType() != null) {
				SWTBotCombo combo = getWizardBot().comboBoxWithLabelInGroup("Digital Input Type:", "Receiver Properties");
				assertDataCharDeprecated(combo);
				combo.setSelection(codegenInfo.getInputPortType());
			}
		} else {
			getWizardBot().radio("Analog Input (default)").click();
			if (codegenInfo.getAnalogInputPorts() != -1) {
				getWizardBot().spinnerWithLabel("Number of Analog input ports:").setSelection(codegenInfo.getAnalogInputPorts());
			}
		}

		if (codegenInfo.isOutputDigital()) {
			// wizardBot.radio("Digital Output (default)").click();
			if (codegenInfo.getOutputPortType() != null) {
				SWTBotCombo combo = getWizardBot().comboBoxWithLabel("Digital Output Type:");
				assertDataCharDeprecated(combo);
				combo.setSelection(codegenInfo.getOutputPortType());
			}
			SWTBotCheckBox checkBox = bot.checkBox("Multi-out");
			if (codegenInfo.isMultiOut() != checkBox.isChecked()) {
				checkBox.click();
			}
		} else {
			getWizardBot().radio("Analog Output").click();
		}
	}

	private void transmitPage(FEICodegenInfo codegenInfo) {
		if (codegenInfo.getDigitalInputPorts() != -1) {
			getWizardBot().spinnerWithLabel("Number of Digital input ports:").setSelection(codegenInfo.getDigitalInputPorts());
		}
		if (codegenInfo.getInputPortType() != null) {
			SWTBotCombo combo = bot.comboBoxWithLabelInGroup("Digital Input Type:", "Transmitter Properties");
			assertDataCharDeprecated(combo);
			combo.setSelection(codegenInfo.getInputPortType());
		}
	}

	/**
	 * IDE-1425
	 * @param combo
	 */
	private void assertDataCharDeprecated(SWTBotCombo combo) {
		if (combo.getText().contains("dataChar")) {
			Assert.fail("Default was dataChar");
		}
		for (String item : combo.items()) {
			if ("dataChar (deprecated)".equals(item)) {
				return;
			}
		}
		Assert.fail("dataChar isn't marked deprecated");
	}

	private void assertContainsPort(SWTBot bot, boolean provides, String portName, String portInterface) {
		SWTBotTreeItem[] items = bot.tree().getAllItems();
		int index = -1;
		for (int i = 0; i < items.length; i++) {
			if (portName.equals(bot.tree().cell(i, 0))) {
				index = i;
			}
		}
		if (index == -1) {
			String msg = String.format("Couldn't find port ", portName);
			Assert.fail(msg);
		}

		bot.tree().select(index);
		if (provides) {
			String msg = String.format("Expected port %s to be a provides port", portName);
			Assert.assertEquals(msg, "in <provides>", bot.comboBoxWithLabel("Direction:").getText());
		} else {
			String msg = String.format("Expected port %s to be a uses port", portName);
			Assert.assertEquals(msg, "out <uses>", bot.comboBoxWithLabel("Direction:").getText());
		}
		String msg = String.format("Expected port %s to have interface %s", portName, portInterface);
		Assert.assertEquals(msg, portInterface, bot.textWithLabel("Interface:").getText());
	}
}
