/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class WaveformReleaseTest extends UIRuntimeTest {

	/**
	 * IDE-913 - Ensure waveform release action completes successfully
	 */
	@Test
	public void releaseWaveformTest() {
		final String WAVEFORM = "ExampleWaveform01";
		final String COMPONENT = "rh.SigGen";
		final String COMPONENT_1 = "SigGen_1";
		final String COMPONENT_IMPL = "python";
		final String COMPONENT_PORT = "dataFloat_out";

		SWTBotTreeItem waveformTreeItem = WaveformUtils.launchLocalWaveform(bot, WAVEFORM);
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, COMPONENT, COMPONENT_IMPL);
		SWTBotTreeItem componentTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMPONENT_1);

		// Release waveform
		waveformTreeItem.contextMenu("Release").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox" }, WAVEFORM);

		// Make sure waveform is not present in connection wizard
		componentTreeItem.expand().getNode(COMPONENT_PORT).contextMenu("Connect").click();
		SWTBotShell connectWizard = bot.shell("Connect");
		connectWizard.setFocus();
		SWTBotTreeItem targetTree = bot.treeInGroup("Target").getTreeItem("Sandbox");
		targetTree.expand();
		for (SWTBotTreeItem item : targetTree.getItems()) {
			if (item.getText().matches(WAVEFORM + ".*")) {
				throw new AssertionError("Waveform " + WAVEFORM + " was not released");
			}
		}
		connectWizard.close();
	}
}
