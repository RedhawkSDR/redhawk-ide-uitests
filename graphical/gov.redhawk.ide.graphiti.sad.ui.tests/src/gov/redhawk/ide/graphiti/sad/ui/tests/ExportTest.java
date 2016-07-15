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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ExportTest extends AbstractGraphitiTest {

	/**
	 * IDE-574 - Ensure warning method displays when installing and empty waveform
	 */
	@Test
	public void exportEmptyProjectTest() {
		// Create empty waveform project
		final String waveformName = "emptyWaveform";
		WaveformUtils.createNewWaveform(bot, waveformName, null);

		// Attempt to export and make sure warning dialog pops
		StandardTestActions.exportProject(waveformName, bot);
		bot.waitUntil(Conditions.shellIsActive("Invalid Model"));
		SWTBotShell shell = bot.shell("Invalid Model");
		shell.bot().button("No").click();

		// Make sure waveform did not export
		bot.waitUntil(Conditions.shellCloses(shell));

		try {
			bot.waitUntil(new DefaultCondition() {
				@Override
				public String getFailureMessage() {
					return waveformName + " did not load into REDHAWK Explorer";
				}

				@Override
				public boolean test() throws Exception {
					ScaExplorerTestUtils.getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, new String[] { "Target SDR", "Waveforms" }, waveformName);
					return true;
				}
			});

			Assert.fail("Waveform should not have been exported");
		} catch (TimeoutException e) {
			// PASS - Expected since the waveform should not have exported
		}

		// Try to export again
		StandardTestActions.exportProject(waveformName, bot);
		shell = bot.shell("Invalid Model");
		shell.bot().button("Yes").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		// Make sure waveform loaded into the Target SDR
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Target SDR", "Waveforms" }, waveformName);
	}
}
