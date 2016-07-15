/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Attempts to launch several waveforms / components with XML issues in the sandbox and ensures an error dialog is
 * shown.
 */
public class ValidateXMLBeforeLaunchTest extends UIRuntimeTest {

	private Boolean oldAutomatedMode = null;

	@Before
	public void before() throws Exception {
		super.before();

		// Allow error dialogs to pop
		oldAutomatedMode = ErrorDialog.AUTOMATED_MODE;
		ErrorDialog.AUTOMATED_MODE = false;
	}

	@After
	public void after() throws CoreException {
		if (oldAutomatedMode != null) {
			// Switch error dialogs back to previous setting
			ErrorDialog.AUTOMATED_MODE = oldAutomatedMode;
			oldAutomatedMode = null;
		}
		super.after();
	}

	/**
	 * Launching a SAD with XML errors in the sandbox should present an error dialog.
	 * IDE-1445 Validate all XML before launching in sandbox
	 */
	@Test
	public void badWaveformLaunch() {
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(bot, "sadWithComponentsWithErrors");
		SWTBotShell shell = bot.shell("Problem Occurred");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.launchWaveformFromTargetSDR(bot, "sadWithErrors");
		shell = bot.shell("Problem Occurred");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	/**
	 * Launching an SPD with XML errors in the sandbox should present an error dialog.
	 * IDE-1445 Validate all XML before launching in sandbox
	 */
	@Test
	public void badComponentLaunch() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, "SpdMissingPrfAndScd", "cpp");
		SWTBotShell shell = bot.shell("Problem Occurred");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, "SpdWithErrors", "cpp");
		shell = bot.shell("Problem Occurred");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, "SpdWithPrfAndScdErrors", "cpp");
		shell = bot.shell("Problem Occurred");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

}
