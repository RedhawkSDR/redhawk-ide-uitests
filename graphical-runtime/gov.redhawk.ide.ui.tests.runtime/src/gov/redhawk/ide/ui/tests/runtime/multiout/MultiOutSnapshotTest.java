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

import java.util.UUID;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MultiOutSnapshotTest extends AbstractMultiOutPortTest {

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
		return "Snapshot";
	}

	@Override
	protected void testActionResults(int allocationIndex) {

		// Create a job change listener to make sure we actually get a snapshot
		JobListener jobListener = new JobListener();
		Job.getJobManager().addJobChangeListener(jobListener);

		// Handle "Start Resource" dialog
		bot.waitUntil(Conditions.shellIsActive("Start Resource"));
		SWTBotShell startShell = bot.shell("Start Resource");
		startShell.bot().button("No").click();
		bot.waitUntil(Conditions.shellCloses(startShell));

		// Check that the combo field is set to the allocation ID
		bot.waitUntil(Conditions.shellIsActive("Snapshot"));
		SWTBotShell snapshotShell = bot.shell("Snapshot");
		SWTBotCombo idCombo = snapshotShell.bot().comboBoxWithLabel("Connection ID:");
		idCombo.setSelection(getAllocationId(allocationIndex));
		Assert.assertEquals("Connection ID combo value does not equal tuner allocation ID", getAllocationId(allocationIndex), idCombo.getText());
		
		// Make sure confirm overwrite is not selected
		SWTBotCheckBox overwriteButton = snapshotShell.bot().checkBox("Confirm overwrite");
		if (overwriteButton.isChecked()) {
			overwriteButton.click();
		}

		// Generate a tmp file path and take the snapshot
		String fileName = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID();
		snapshotShell.bot().textWithLabel("File Name:").setText(fileName);
		snapshotShell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(snapshotShell));

		// Make sure the snapshot job completed
		try {
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					return jobListener.snapshotJobCompleted;
				}

				@Override
				public String getFailureMessage() {
					return "Snapshot Job never completed";
				}
			});
		} finally {
			Job.getJobManager().removeJobChangeListener(jobListener);
		}
	}

	private class JobListener extends JobChangeAdapter {
		volatile boolean snapshotJobCompleted = false; // SUPPRESS CHECKSTYLE expose variable

		@Override
		public void done(IJobChangeEvent event) {
			if (event.getJob().getName().matches(".*Snapshot of Device Manager.*")) {
				snapshotJobCompleted = true;
			}
		}
	}

}
