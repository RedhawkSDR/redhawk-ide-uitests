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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MultiOutPlottingTest extends AbstractMultiOutPortTest {

	@Override
	protected String getContextMenu() {
		return "Plot Port Data";
	}

	@Override
	protected void testActionResults(int allocationIndex) {
		waitForConnection(allocationIndex);
		SWTBotView plotView = bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
		Assert.assertEquals("dataShort_out", plotView.getReference().getTitle());
	}

	@Test
	public void advancedPlotWizardTest() {
		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("firstAllocation", "101.5");
		waitForTunerAllocation(0);

		testWithSingleTuner();

		// Launch a component and connect to it so that the first connection ID is 'IN USE'
		final String componentName = "rh.DataConverter";
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, componentName, "cpp");
		SWTBotTreeItem providesPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", "DataConverter_1" },
			"dataShort");
		SWTBot explorerViewBot = ScaExplorerTestUtils.showScaExplorerView(bot);
		explorerViewBot.tree().select(getUsesPort(), providesPort).contextMenu("Connect").click();

		// Allocate the second tuner, important that it is alphabetically later than the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("secondAllocation", "88.5");
		waitForTunerAllocation(1);

		testWithSecondTuner();
	}

	private void testWithSingleTuner() {
		SWTBot plotBot = initWizard();

		// Confirm that default connection ID matches the allocation ID
		SWTBotCombo idCombo = plotBot.comboBoxWithLabel("Connection ID:");
		Assert.assertEquals("Connection ID did not auto-populate correctly", getAllocationId(0), idCombo.getText());

		// Validate that emptying the connection ID results in an error
		idCombo.setText("");
		Assert.assertEquals("Error message did not appear", " Must enter valid connection ID", plotBot.text(5).getText());

		// Select the provided connection ID and confirm error message clears
		idCombo.setSelection(getAllocationId(0));
		Assert.assertTrue("Error message did not clear", plotBot.text(5).getText().startsWith("Provide the initial settings"));

		// Finish wizard and confirm that a connection with the correct ID was created, and that the view has opened
		plotBot.button("Finish").click();
		waitForConnection(0);
		SWTBotView plotView = bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
		Assert.assertEquals("dataShort_out", plotView.getReference().getTitle());

		// Close the view in preparation for the next part of the test
		plotView.close();
	}

	private void testWithSecondTuner() {
		SWTBot plotBot = initWizard();

		// Confirm that connection ID matches the new allocation ID (the first one is being used and not available)
		SWTBotCombo idCombo = plotBot.comboBoxWithLabel("Connection ID:");
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return idCombo.getText().equals(getAllocationId(1));
			}

			@Override
			public String getFailureMessage() {
				return "Connection ID did not auto-populate correctly";
			}
		});

		// Confirm that changing the selection to the other connection ID results in an error message
		idCombo.setSelection(getAllocationId(0));
		Assert.assertEquals("Error message did not appear", " Selected connection ID is already in use", plotBot.text(5).getText());

		// Confirm that reverting the connection ID results in the error disappearing
		idCombo.setSelection(getAllocationId(1));
		Assert.assertTrue("Error message did not clear", plotBot.text(5).getText().startsWith("Provide the initial settings"));

		// Finish and confirm that a connection with the correct ID was created, and that the view has opened
		plotBot.button("Finish").click();
		waitForConnection(1);
		SWTBotView plotView = bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
		Assert.assertEquals("dataShort_out", plotView.getReference().getTitle());

		// Close the view
		plotView.close();
	}

	private SWTBot initWizard() {
		// Open advanced plot wizard
		getUsesPort().contextMenu("Plot Port ...").click();

		// Get the shell's bot
		bot.waitUntil(Conditions.shellIsActive("Plot Port"));
		return bot.shell("Plot Port").bot();
	}
}
