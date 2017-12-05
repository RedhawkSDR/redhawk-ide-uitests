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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;

public class MultiOutSnapshotTest extends AbstractMultiOutPortTest {

	@Override
	protected String getContextMenu() {
		return "Snapshot";
	}

	@Override
	protected void testActionResults() {

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
		Assert.assertEquals("Connection ID combo value does not equal tuner allocation ID", getAllocationId(0), idCombo.getText());

		// Generate a tmp file path and take the snapshot
		File file = null;
		try {
			file = File.createTempFile("file", "");
		} catch (IOException e) {
			Assert.fail("Could not write to tmp directory");
		}
		snapshotShell.bot().textWithLabel("File Name:").setText(file.getName());
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
