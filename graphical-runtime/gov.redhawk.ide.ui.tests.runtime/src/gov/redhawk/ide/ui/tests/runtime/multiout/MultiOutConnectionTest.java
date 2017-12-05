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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MultiOutConnectionTest extends AbstractMultiOutPortTest {

	/**
	 * For this test, we will be content to confirm that the connection ID matches the tuner allocation ID
	 */
	@Override
	@Test
	public void mulitOutPortSingleTunerTest() {
		// Launch a component to connect to
		final String componentName = "rh.DataConverter";
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, componentName, "cpp");
		SWTBotTreeItem providesPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", "DataConverter_1" },
			"dataShort");

		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("allocation1", "101.5");
		waitForTunerAllocation(0);

		// Select the device uses port and the components provides port, and then click connect in the context menu
		SWTBot explorerViewBot = ScaExplorerTestUtils.showScaExplorerView(bot);
		explorerViewBot.tree().select(getUsesPort(), providesPort).contextMenu(getContextMenu()).click();

		// Verify that the expected behavior occurred
		testActionResults(0);
	}

	@Override
	@Test
	public void mulitOutPortMultiTunerTest() {
		// Launch a component to connect to
		final String componentName = "rh.DataConverter";
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, componentName, "cpp");
		SWTBotTreeItem providesPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", "DataConverter_1" },
			"dataShort");

		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("firstAllocation", "101.5");
		waitForTunerAllocation(0);

		// Allocate the second tuner, important that it is alphabetically later than the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("secondAllocation", "88.5");
		waitForTunerAllocation(1);

		// Select the device uses port and the components provides port, and then click connect in the context menu
		SWTBot explorerViewBot = ScaExplorerTestUtils.showScaExplorerView(bot);
		explorerViewBot.tree().select(getUsesPort(), providesPort).contextMenu(getContextMenu()).click();

		// Complete the multi-out connection dialog
		bot.waitUntil(Conditions.shellIsActive("Multi-out port connection wizard"));
		SWTBotShell multiOutShell = bot.shell("Multi-out port connection wizard");
		multiOutShell.bot().tree().select(1);
		multiOutShell.bot().button("OK").click();

		// Verify that the expected behavior occurred
		testActionResults(1);
	}

	@Test
	public void connectWizardTest() {
		// Need to test selecting a supplied connection ID, trying to select an IN-USE ID, and inputing an ID manually
		// (pick one that will receive data)
	}

	@Override
	protected String getContextMenu() {
		return "Connect";
	}

	@Override
	protected void testActionResults(int allocationIndex) {
		waitForConnection(allocationIndex);
	}

}
