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
import org.junit.Assert;
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

	/** Check that the expected behavior occurred (a view was opened and populated, etc.) */
	protected abstract void testActionResults();

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
		completeAllocateWizard("101.5");
		waitForTunerAllocation(0);

		// Click on the appropriate context menu
		getUsesPort().contextMenu(getContextMenu()).click();

		// Verify that the expected behavior occurred
		testActionResults();
	}

	/**
	 * IDE-2042, 2043, 2049, 2067, 2068, 2069
	 * Test support for a variety of options using a multi-out port with multiple allocated tuners
	 */
	@Test
	public void mulitOutPortSingleMultiTunerTest() {
		Assert.fail();
	}

	// TODO: Things to test
	/**
	 * Test Classes
	 * - Abstract multi-out test class
	 * -- Handles single allocation tests for
	 * --- Plotting ('Plot Port Data')
	 * --- Display SRI
	 * --- Snapshot
	 * --- Data List
	 * --- Create Direct Connection (CTRL multi-select)
	 * 
	 * --- Play Port
	 * 
	 * -- Methods
	 * --- Launch the device
	 * --- Get reference to the port
	 * --- Allocate a tuner
	 * --- <ABSTRACT> right click on the port and choose the appropriate action
	 * --- Wait for connection node to appear
	 * --- <ABSTRACT> confirm that the expected behavior occurred
	 * ---- Wait for view to appear
	 * ---- Check views details
	 * ---- Confirm data is flowing (if possible)
	 * --- Allocate a second tuner
	 * --- <ABSTRACT> right click on the port and choose the appropriate action
	 * --- Navigate the dialog, will always need to select the second connection ID in the table because in some cases
	 * the first will be in use
	 * --- Wait for connection node to appear
	 * --- <ABSTRACT> confirm that the expected behavior occurred
	 * ---- Wait for view to appear
	 * ---- Check views details
	 * ---- Confirm data is flowing (if possible)
	 * -- Cleanup
	 * --- Deallocate all tuners
	 * --- Release the device and confirm that it is removed from the explorer
	 * 
	 * 
	 * - Multi-out port dialog test class
	 * -- Make sure that the dialog can't be completed with the Text box empty and enabled
	 * -- Make sure the dialog displays 'IN USE' connection IDs
	 * -- Make sure the dialog can't be completed when selecting an 'IN USE' connection ID
	 * -- Make sure radio buttons enable/disable widgets as appropriate
	 * -- Make sure selecting an existing ID creates the correct connection node
	 * -- Make sure entering an ID manually creates the correct connection node
	 * -- Make sure canceling the dialog does NOT create a connection
	 * 
	 */

	protected SWTBotTreeItem getUsesPort() {
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DEVICE_PATH, "dataShort_out");
	}

	private SWTBotTreeItem getFeiTunerContainer() {
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DEVICE_PATH, "FrontEnd Tuners");
	}

	/** 
	 * Wait for a connection node to appear in the REDHAWK Explorer view 
	 * @param allocationIndex - the index of the tuner allocation whose ID we want to match
	 */
	protected void waitForConnection(int allocationIndex) {
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				// Returns true when a new connection node is created
				return getAllocationId(allocationIndex).equals(getUsesPort().getItems()[0].getText());
//				return (getUsesPort().getItems().length > 0);
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

	protected void completeAllocateWizard(String centerFreq) {
		SWTBotShell shell = bot.shell("Allocate Tuner");
		SWTBot wizardBot = shell.bot();
		wizardBot.textWithLabel("Center Frequency (MHz)").setText(centerFreq);
		wizardBot.checkBox("Any Value", 0).click();
		wizardBot.checkBox("Any Value", 1).click();
		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	/**
	 * Get the (@link {@link SWTBotTreeItem} at the specified index location
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

	private void waitForTunerDeallocation() {
		SWTBotTreeItem feiContainer = getFeiTunerContainer();
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				feiContainer.collapse();
				bot.sleep(200);
				feiContainer.expand();
				return "Unallocated RX_DIGITIZER: 2 available".equals(feiContainer.getItems()[0].getText());
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not deallocated";
			}
		}, 15000);
	}

	@After
	public void after() throws CoreException {
		// Deallocate all allocated tuners
		SWTBotTreeItem feiContainer = getFeiTunerContainer();
		SWTBotTreeItem[] items = feiContainer.getItems();
		// TODO: this may break for deallocating multiple tuners...
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText().startsWith("RX_DIGITIZER")) {
				items[i].contextMenu("Deallocate").click();
			}
		}
		waitForTunerDeallocation();

		// Release the device
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);

		super.after();
	}

}
