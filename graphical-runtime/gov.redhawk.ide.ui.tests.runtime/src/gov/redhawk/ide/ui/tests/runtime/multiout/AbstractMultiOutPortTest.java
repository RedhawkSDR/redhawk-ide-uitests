/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.runtime.multiout;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.frontend.TunerStatus;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public abstract class AbstractMultiOutPortTest extends UIRuntimeTest {
	protected static final String RX_DIGITIZER_SIM = "RX_Digitizer_Sim";
	protected static final String RX_DIGITIZER_SIM_1 = RX_DIGITIZER_SIM + "_1";
	protected static final String[] DEVICE_PARENT_PATH = { "Sandbox", "Device Manager" };
	protected static final String[] DEVICE_PATH = { "Sandbox", "Device Manager", RX_DIGITIZER_SIM_1 };

	private String allocationId;

	/** Returns a {@link String} for the desired context menu option */
	protected abstract String getContextMenu();

	/**
	 * Check that the expected behavior occurred (a view was opened and populated, etc.)
	 * @param allocationIndex - Used to dictate which Tuner ID will be used for the test. Use 0 for testing a single
	 * tuner, and 1 for testing multiple tuners.
	 */
	protected abstract void testActionResults(int allocationIndex);

	/**
	 * Used during cleanup to close the view that was opened for testing.
	 */
	protected abstract void closeView();

	@Before
	public void before() throws Exception {
		super.before();

		// Launch a FEI device with a multi-out port, a connectionTable property, and multiple tuners
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, RX_DIGITIZER_SIM, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
	}

	/**
	 * IDE-2042, 2043, 2049, 2067, 2068, 2069
	 * Test support for a variety of options using a multi-out port with one allocated tuners
	 */
	@Test
	public void mulitOutPortSingleTunerTest() {
		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("allocation1", "101.5");
		waitForTunerAllocation(0);

		// Click on the appropriate context menu
		getUsesPort().contextMenu(getContextMenu()).click();

		// Verify that the expected behavior occurred
		testActionResults(0);
	}

	/**
	 * IDE-2042, 2043, 2049, 2067, 2068, 2069
	 * Test support for a variety of options using a multi-out port with multiple allocated tuners
	 */
	@Test
	public void mulitOutPortMultiTunerTest() {
		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("firstAllocation", "101.5");
		waitForTunerAllocation(0);

		// Allocate the second tuner, important that it is alphabetically later than the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("secondAllocation", "88.5");
		waitForTunerAllocation(1);

		// Click on the appropriate context menu
		getUsesPort().contextMenu(getContextMenu()).click();

		// Complete the multi-out connection dialog
		SWTBotShell multiOutShell = bot.shell("Multi-out port connection wizard");
		multiOutShell.bot().tree().select(1);
		multiOutShell.bot().button("OK").click();

		// Verify that the expected behavior occurred
		testActionResults(1);
	}

	protected SWTBotTreeItem getUsesPort() {
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DEVICE_PATH, "dataShort_out");
	}

	private SWTBotTreeItem getFeiTunerContainer() {
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DEVICE_PATH, "FrontEnd Tuners");
	}

	/**
	 * Wait for a connection node to appear in the REDHAWK Explorer view
	 * @param allocationIndex - the index of the tuner allocation whose ID we want to match
	 * @return true if the connection ID matches the desired allocation ID
	 */
	protected void waitForConnection(int allocationIndex) {
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				String expectedConnectionId = getAllocationId(allocationIndex);
				for (SWTBotTreeItem connectionItem : getUsesPort().getItems()) {
					if (expectedConnectionId.equals(connectionItem.getText())) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "Connection node never appeared, or had the wrong ID";
			}
		}, 12000);
	}

	protected String getAllocationId(final int index) {
		final SWTBotTreeItem feiContainer = getFeiTunerContainer();
		Display.getDefault().syncExec(() -> {
			TunerStatus tuner = (TunerStatus) feiContainer.getItems()[index].widget.getData();
			setAllocationId(tuner.getAllocationID());
		});

		return this.allocationId;
	}

	private void setAllocationId(String id) {
		this.allocationId = id;
	}

	protected void completeAllocateWizard(String allocationId, String centerFreq) {
		SWTBotShell shell = bot.shell("Allocate Tuner");
		SWTBot wizardBot = shell.bot();
		wizardBot.textWithLabel("New Allocation ID").setText(allocationId);
		wizardBot.textWithLabel("Center Frequency (MHz)").setText(centerFreq);
		wizardBot.checkBox("Any Value", 0).click();
		wizardBot.checkBox("Any Value", 1).click();
		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	/**
	 * Get the {@link SWTBotTreeItem} at the specified index location
	 * @param index
	 * @return
	 */
	protected SWTBotTreeItem waitForTunerAllocation(final int index) {
		SWTBotTreeItem feiContainer = getFeiTunerContainer();
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				feiContainer.collapse();
				bot.sleep(200);
				feiContainer.expand();
				return feiContainer.getItems()[index].getText().startsWith("RX_DIGITIZER");
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not allocated";
			}
		}, 15000);

		return feiContainer.getItems()[index];
	}

	@After
	public void after() throws CoreException {
		// Close view
		closeView();

		// Release the device
		ScaExplorerTestUtils.shutdown(bot, new String[] { "Sandbox" }, "Device Manager");
		ScaExplorerTestUtils.waitUntilSandboxDeviceManagerEmpty(bot);

		super.after();
	}
}
