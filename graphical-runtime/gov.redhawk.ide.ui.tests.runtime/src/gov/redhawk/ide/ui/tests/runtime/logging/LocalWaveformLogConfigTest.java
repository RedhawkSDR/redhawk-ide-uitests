/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.ui.tests.runtime.logging;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Logging tests against a component belonging to a local waveform in the sandbox.
 */
public class LocalWaveformLogConfigTest extends AbstractLogConfigTest {

	private static final String TEST_WAVEFORM = "SigGenToHardLimitWF";
	private static final String[] WAVEFORM_PARENT_PATH = { "Sandbox" };
	private static final String SIGGEN_1 = "SigGen_1";

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		// Launch a waveform
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(bot, TEST_WAVEFORM);
		String[] parentPath = new String[] { "Sandbox", TEST_WAVEFORM };
		return ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, SIGGEN_1);
	}

	@Override
	protected String getLoggingResourceName() {
		return SIGGEN_1;
	}

	@Override
	protected boolean canTailLog() {
		return false;
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	@Override
	protected String getConsoleTitle() {
		return SIGGEN_1;
	}
}
