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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class LaunchLocalWaveformTest extends UIRuntimeTest {

	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = { "Sandbox" };

	private RHSWTGefBot gefBot;

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHSWTGefBot();
	}

	/**
	 * IDE-1705 - Waveforms need to support namespaces of depths greater than one (e.g. a.b.c.d.waveform)
	 */
	@Test
	public void launchNamespacedWaveform() {
		launchWaveform("a.b.c.d.waveform");
	}

	/**
	 * IDE-1747 - Make sure no errors are thrown when launching a local waveform with an Event Channel connection
	 */
	@Test
	public void launchMessageEventWaveform() {
		launchWaveform("messageEventWaveform");
	}

	private void launchWaveform(String waveformName) {
		// Launch Local Waveform From Target SDR
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, waveformName);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, waveformName);
		ConsoleUtils.disableAutoShowConsole(gefBot);

		// Open the error log and check that it is empty
		ViewUtils.checkErrorLogIsEmpty(gefBot);

		ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, waveformName);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, waveformName);
	}
}
