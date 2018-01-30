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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MultiOutDataListTest extends AbstractMultiOutPortTest {

	@Override
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

		// Verify that the expected behavior occurred
		testActionResults(1);
	}

	@Override
	protected String getContextMenu() {
		return "Data List";
	}

	@Override
	protected void testActionResults(int allocationIndex) {
		SWTBot dataListBot = ViewUtils.getDataListView(bot).bot();

		// Simply check that the table populates with values. If it does, then a connection was made with the correct
		// connection ID
		dataListBot.buttonWithTooltip("Start Acquire").click();

		// HACK to tell if we are testing with multiple tuners or not...
		if (allocationIndex == 1) {
			// Complete the multi-out connection dialog
			SWTBotShell multiOutShell = bot.shell("Multi-out port connection wizard");
			multiOutShell.bot().list().select(1);
			multiOutShell.bot().button("OK").click();
		}

		// Ignore the fact that the device isn't started, it is still pushing data
		SWTBotShell startedShell = bot.shell("Started?");
		startedShell.bot().button("Yes").click();
		bot.waitUntil(Conditions.shellCloses(startedShell));

		// Allow some time for data collection
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				SWTBotTable table = dataListBot.table(0);
				return table.rowCount() > 1;
			}

			@Override
			public String getFailureMessage() {
				return "No data was generated, connection ID may not be correct";
			}
		});
	}

	@Override
	protected void closeView() {
		ViewUtils.getDataListView(bot).close();
	}
}
