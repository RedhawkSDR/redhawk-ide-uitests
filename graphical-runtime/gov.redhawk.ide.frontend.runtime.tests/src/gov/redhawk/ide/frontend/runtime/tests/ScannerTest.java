/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.frontend.runtime.tests;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.syncExec;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import CF.DataType;
import CF.DevicePackage.InsufficientCapacity;
import CF.DevicePackage.InvalidCapacity;
import CF.DevicePackage.InvalidState;
import CF.PortSupplierPackage.UnknownPort;
import FRONTEND.BadParameterException;
import FRONTEND.DigitalScanningTuner;
import FRONTEND.DigitalScanningTunerHelper;
import FRONTEND.FrontendException;
import FRONTEND.NotSupportedException;
import FRONTEND.ScanningTunerPackage.OutputControlMode;
import FRONTEND.ScanningTunerPackage.ScanMode;
import FRONTEND.ScanningTunerPackage.ScanStatus;
import gov.redhawk.frontend.util.TunerProperties.ScannerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.ScannerAllocationProperty;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperty;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaSimpleProperty;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.Struct;

public class ScannerTest extends UIRuntimeTest {

	private static final String SANDBOX = "Sandbox";
	private static final String DEV_MGR = "Device Manager";
	private static final String DEV = "test.scanner";
	private static final String DEV_1 = DEV + "_1";
	private static final String FEI_CONTAINER = "FrontEnd Tuners";
	private static final String ALLOCATION_ID = "abc";

	@Before
	public void before() throws Exception {
		super.before();
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEV, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR }, DEV_1);
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.terminate(bot, new String[] { SANDBOX }, DEV_MGR);
		bot.waitUntil(new WaitForLaunchTermination(false));
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	/**
	 * IDE-2178 Test allocation / deallocation of a scanner
	 */
	@Test
	public void allocate_deallocate() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, DEV_1 },
			FEI_CONTAINER);
		SWTBotTreeItem tuner = AbstractTunerTest.waitForTunerDeallocation(bot, feiContainer, "RX_SCANNER_DIGITIZER");

		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, DEV_1, FEI_CONTAINER }, tuner.getText());
		completeAllocateWizard();
		StandardTestActions.forceMainShellActive();
		tuner = AbstractTunerTest.waitForTunerAllocation(bot, feiContainer, "RX_SCANNER_DIGITIZER");

		ScaExplorerTestUtils.deallocate(bot, new String[] { SANDBOX, DEV_MGR, DEV_1, FEI_CONTAINER }, tuner.getText());
		AbstractTunerTest.waitForTunerDeallocation(bot, feiContainer, "RX_SCANNER_DIGITIZER");
	}

	/**
	 * IDE-1983 Perform a manual scan using a tuner
	 * @throws InsufficientCapacity
	 * @throws InvalidState
	 * @throws InvalidCapacity
	 * @throws InterruptedException
	 * @throws UnknownPort 
	 */
	@Test
	public void manualScan() throws InvalidCapacity, InvalidState, InsufficientCapacity, InterruptedException, UnknownPort {
		SWTBotTreeItem tuner = doAllocation();
		tuner.contextMenu().menu("Scan").click();

		String mode = "MANUAL_SCAN";
		String controlMode = "TIME_BASED";
		String controlValue = "1";
		int delay = 10;
		String centerFreq = "2";
		completeInitialScanWizardPage(mode, controlMode, controlValue, delay);

		SWTBotShell shell = bot.shell("Tuner Scan");
		SWTBot wizardBot = shell.bot();
		wizardBot.textWithLabel("Center Frequency (MHz)").setText(centerFreq);

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		assertScan(delay,
			ALLOCATION_ID + " = FRONTEND.ScanningTuner.ScanStrategy(scan_mode=" + mode
				+ ", scan_definition=FRONTEND.ScanningTuner.ScanModeDefinition(center_frequency = 2000000.0), control_mode=" + controlMode
				+ ", control_value=1.0)",
			ScanMode._MANUAL_SCAN, OutputControlMode._TIME_BASED, 1.0);

		tuner.contextMenu().menu("Scan").click();

		controlMode = "SAMPLE_BASED";
		controlValue = "3";
		delay = 4;
		centerFreq = "5";
		completeInitialScanWizardPage(mode, controlMode, controlValue, delay);

		shell = bot.shell("Tuner Scan");
		wizardBot = shell.bot();
		wizardBot.textWithLabel("Center Frequency (MHz)").setText(centerFreq);

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		assertScan(delay,
			ALLOCATION_ID + " = FRONTEND.ScanningTuner.ScanStrategy(scan_mode=" + mode
				+ ", scan_definition=FRONTEND.ScanningTuner.ScanModeDefinition(center_frequency = 5000000.0), control_mode=" + controlMode
				+ ", control_value=3.0)",
		ScanMode._MANUAL_SCAN, OutputControlMode._SAMPLE_BASED, 3.0);
	}

	/**
	 * IDE-1983 Perform a discrete scan using a tuner
	 * @throws InsufficientCapacity
	 * @throws InvalidState
	 * @throws InvalidCapacity
	 * @throws InterruptedException
	 * @throws UnknownPort 
	 */
	@Test
	public void discreteScan() throws InvalidCapacity, InvalidState, InsufficientCapacity, InterruptedException, UnknownPort {
		SWTBotTreeItem tuner = doAllocation();
		tuner.contextMenu().menu("Scan").click();

		String mode = "DISCRETE_SCAN";
		String controlMode = "SAMPLE_BASED";
		String controlValue = "3";
		int delay = 4;
		completeInitialScanWizardPage(mode, controlMode, controlValue, delay);

		SWTBotShell shell = bot.shell("Tuner Scan");
		SWTBot wizardBot = shell.bot();
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 1));
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 2));
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 0, "5", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 0, "6", false);

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		assertScan(delay,
			ALLOCATION_ID + " = FRONTEND.ScanningTuner.ScanStrategy(scan_mode=" + mode
				+ ", scan_definition=FRONTEND.ScanningTuner.ScanModeDefinition(discrete_freq_list = [5000000.0, 6000000.0]), control_mode=" + controlMode
				+ ", control_value=3.0)",
			ScanMode._DISCRETE_SCAN, OutputControlMode._SAMPLE_BASED, 3.0);

		tuner.contextMenu().menu("Scan").click();
		controlMode = "TIME_BASED";
		controlValue = "1";
		delay = 10;
		completeInitialScanWizardPage(mode, controlMode, controlValue, delay);

		shell = bot.shell("Tuner Scan");
		wizardBot = shell.bot();
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 1));
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 2));
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 0, "4", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 0, "5", false);

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		assertScan(delay,
			ALLOCATION_ID + " = FRONTEND.ScanningTuner.ScanStrategy(scan_mode=" + mode
				+ ", scan_definition=FRONTEND.ScanningTuner.ScanModeDefinition(discrete_freq_list = [4000000.0, 5000000.0]), control_mode=" + controlMode
				+ ", control_value=1.0)",
			ScanMode._DISCRETE_SCAN, OutputControlMode._TIME_BASED, 1.0);
	}

	/**
	 * IDE-1983 Perform a span scan using a tuner
	 * @throws InterruptedException
	 * @throws InsufficientCapacity
	 * @throws InvalidState
	 * @throws InvalidCapacity
	 * @throws UnknownPort 
	 */
	@Test
	public void spanScan() throws InvalidCapacity, InvalidState, InsufficientCapacity, InterruptedException, UnknownPort {
		SWTBotTreeItem tuner = doAllocation();
		tuner.contextMenu().menu("Scan").click();

		String mode = "SPAN_SCAN";
		String controlMode = "TIME_BASED";
		String controlValue = "7";
		int delay = 8;
		completeInitialScanWizardPage(mode, controlMode, controlValue, delay);

		SWTBotShell shell = bot.shell("Tuner Scan");
		SWTBot wizardBot = shell.bot();
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 1));
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 2));
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 0, "1", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 1, "2", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 2, "3", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 0, "4", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 1, "5", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 2, "6", false);

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		String span1 = "FRONTEND.ScanningTuner.ScanSpanRange(begin_frequency=1000000.0, end_frequency=2000000.0, step=3000000.0)";
		String span2 = "FRONTEND.ScanningTuner.ScanSpanRange(begin_frequency=4000000.0, end_frequency=5000000.0, step=6000000.0)";
		assertScan(delay,
			ALLOCATION_ID + " = FRONTEND.ScanningTuner.ScanStrategy(scan_mode=" + mode
				+ ", scan_definition=FRONTEND.ScanningTuner.ScanModeDefinition(freq_scan_list = [" + span1 + ", " + span2 + "]), control_mode=" + controlMode
				+ ", control_value=7.0)",
			ScanMode._SPAN_SCAN, OutputControlMode._TIME_BASED, 7.0);

		tuner.contextMenu().menu("Scan").click();
		controlMode = "SAMPLE_BASED";
		controlValue = "5";
		delay = 4;
		completeInitialScanWizardPage(mode, controlMode, controlValue, delay);

		shell = bot.shell("Tuner Scan");
		wizardBot = shell.bot();
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 1));
		wizardBot.button(0).click();
		wizardBot.waitUntilWidgetAppears(Conditions.tableHasRows(wizardBot.table(), 2));
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 0, "5", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 1, "6", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 0, 2, "7", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 0, "8", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 1, "9", false);
		StandardTestActions.writeToCell(wizardBot, wizardBot.table(), 1, 2, "10", false);

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		span1 = "FRONTEND.ScanningTuner.ScanSpanRange(begin_frequency=5000000.0, end_frequency=6000000.0, step=7000000.0)";
		span2 = "FRONTEND.ScanningTuner.ScanSpanRange(begin_frequency=8000000.0, end_frequency=9000000.0, step=10000000.0)";
		assertScan(delay,
			ALLOCATION_ID + " = FRONTEND.ScanningTuner.ScanStrategy(scan_mode=" + mode
				+ ", scan_definition=FRONTEND.ScanningTuner.ScanModeDefinition(freq_scan_list = [" + span1 + ", " + span2 + "]), control_mode=" + controlMode
				+ ", control_value=5.0)",
			ScanMode._SPAN_SCAN, OutputControlMode._SAMPLE_BASED, 5.0);
	}

	private void completeAllocateWizard() {
		SWTBotShell shell = bot.shell("Allocate Tuner");
		SWTBot wizardBot = shell.bot();
		wizardBot.textWithLabel("Center Frequency (MHz)").setText("1");
		wizardBot.checkBox("Any Value", 0).click();
		wizardBot.checkBox("Any Value", 1).click();
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Minimum Frequency (MHz)").setText("1");
		wizardBot.textWithLabel("Maximum Frequency (MHz)").setText("2");
		wizardBot.comboBoxWithLabel("Mode").setSelection("SPAN_SCAN");
		wizardBot.comboBoxWithLabel("Control Mode").setSelection("TIME_BASED");
		wizardBot.textWithLabel("Control Limit").setText("3");

		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	private void completeInitialScanWizardPage(String mode, String controlMode, String controlValue, int delay) {
		SWTBotShell shell = bot.shell("Tuner Scan");
		SWTBot wizardBot = shell.bot();
		wizardBot.comboBoxWithLabel("Mode").setSelection(mode);
		wizardBot.comboBoxWithLabel("Control Mode").setSelection(controlMode);
		wizardBot.textWithLabel("Control Value").setText(controlValue);
		wizardBot.spinnerWithLabel("Delay (s)").setSelection(delay);
		wizardBot.button("Next >").click();
	}

	private SWTBotTreeItem doAllocation() throws InvalidCapacity, InvalidState, InsufficientCapacity {
		final SWTBotTreeItem deviceTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR }, DEV_1);
		ScaDevice< ? > scaDevice = syncExec(deviceTreeItem.display, () -> {
			return (ScaDevice< ? >) deviceTreeItem.widget.getData();
		});

		Struct tunerStruct = TunerAllocationProperty.INSTANCE.createProperty();
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.ALLOCATION_ID.getId())).setValue(ALLOCATION_ID);
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.TUNER_TYPE.getId())).setValue("RX_SCANNER_DIGITIZER");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.CENTER_FREQUENCY.getId())).setValue("1.0");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.BANDWIDTH.getId())).setValue("0.0");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.SAMPLE_RATE.getId())).setValue("0.0");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.BANDWIDTH_TOLERANCE.getId())).setValue("20.0");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.SAMPLE_RATE_TOLERANCE.getId())).setValue("20.0");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.DEVICE_CONTROL.getId())).setValue("true");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.RF_FLOW_ID.getId())).setValue("");
		((Simple) tunerStruct.getProperty(TunerAllocationProperties.GROUP_ID.getId())).setValue("");

		Struct scannerStruct = ScannerAllocationProperty.INSTANCE.createProperty();
		((Simple) scannerStruct.getProperty(ScannerAllocationProperties.MIN_FREQ.getId())).setValue("1.0");
		((Simple) scannerStruct.getProperty(ScannerAllocationProperties.MAX_FREQ.getId())).setValue("2.0");
		((Simple) scannerStruct.getProperty(ScannerAllocationProperties.MODE.getId())).setValue("SPAN_SCAN");
		((Simple) scannerStruct.getProperty(ScannerAllocationProperties.CONTROL_MODE.getId())).setValue("TIME_BASED");
		((Simple) scannerStruct.getProperty(ScannerAllocationProperties.CONTROL_LIMIT.getId())).setValue("3.0");

		boolean result = scaDevice.allocateCapacity(new DataType[] { new DataType(TunerAllocationProperty.INSTANCE.getId(), tunerStruct.toAny()),
			new DataType(ScannerAllocationProperty.INSTANCE.getId(), scannerStruct.toAny()) });
		Assert.assertTrue("Failed to allocate", result);

		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, DEV_1 },
			FEI_CONTAINER);
		return AbstractTunerTest.waitForTunerAllocation(bot, feiContainer, "RX_SCANNER_DIGITIZER");
	}

	private void assertScan(int delay, String scanStrategy, int scanMode, int controlMode, double controlValue) throws InvalidCapacity, InvalidState, InsufficientCapacity, InterruptedException, UnknownPort {
		final SWTBotTreeItem deviceTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR }, DEV_1);
		ScaDevice< ? > scaDevice = syncExec(deviceTreeItem.display, () -> {
			return (ScaDevice< ? >) deviceTreeItem.widget.getData();
		});

		scaDevice.refresh(new NullProgressMonitor(), RefreshDepth.SELF);
		Double lastScanStartTimeSec = (Double) ((ScaSimpleProperty) scaDevice.getProperty("lastScanStartTimeSec")).getValue();
		String lastScanStrategy = (String) ((ScaSimpleProperty) scaDevice.getProperty("lastScanStrategy")).getValue();

		Assert.assertEquals(System.currentTimeMillis() / 1000 + delay, lastScanStartTimeSec, 5.0);
		Assert.assertEquals(scanStrategy, lastScanStrategy);
	
		EList<ScaPort< ? , ? >> ports = scaDevice.getPorts();
		DigitalScanningTuner tuner = null;
		for (ScaPort< ? , ? > p : ports) {
			if (p instanceof ScaProvidesPort) {
				ScaProvidesPort pp = (ScaProvidesPort) p;
				if (pp._is_a(DigitalScanningTunerHelper.id())) {
					tuner = DigitalScanningTunerHelper.narrow(pp.getObj());
					break;
				}
			}
		}

		if (tuner != null) {
			try {
				ScanStatus status = tuner.getScanStatus(ALLOCATION_ID);
				Assert.assertEquals(System.currentTimeMillis() / 1000 + delay, status.start_time.twsec, 5.0);
				Assert.assertEquals(scanMode, status.strategy.scan_mode.value());
				Assert.assertEquals(controlMode, status.strategy.control_mode.value());
				Assert.assertEquals(controlValue, status.strategy.control_value, 0.1);
				return;
			} catch (FrontendException e) {
			} catch (BadParameterException e) {
			} catch (NotSupportedException e) {
			}
		}
		Assert.assertTrue(false);
	}
}
