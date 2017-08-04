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
package gov.redhawk.ide.frontend.runtime.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Tests functionality in the allocation wizard itself
 */
public class AllocWizard extends UIRuntimeTest {

	private static final String SANDBOX = "Sandbox";
	private static final String DEV_MGR = "Device Manager";
	private static final String FEI_DEVICE = "rh.FmRdsSimulator";
	private static final String FEI_DEVICE_IMPL = "cpp";
	private static final String FEI_DEVICE_1 = "rh.FmRdsSimulator_1";
	private static final long VALIDATION_DELAY = 500;

	@Before
	public void before() throws Exception {
		super.before();
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, FEI_DEVICE, FEI_DEVICE_IMPL);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR }, FEI_DEVICE_1);
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.terminate(bot, new String[] { SANDBOX }, DEV_MGR);
		bot.waitUntil(new WaitForLaunchTermination(false));
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	/**
	 * Test field validation in the FEI allocation wizard
	 * IDE-789 BW tolerance can be > 100% in FEI allocation wizard
	 */
	@Test
	public void fieldValidation() {
		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR }, FEI_DEVICE_1);

		SWTBotShell shell = bot.shell("Allocate Tuner");
		SWTBot wizardBot = shell.bot();

		// Specify CF + BW + SR (wizard should be valid)
		SWTBotText cfText = wizardBot.textWithLabel("Center Frequency (MHz)");
		cfText.setText("100");
		SWTBotText bwText = wizardBot.textWithLabel("Bandwidth (MHz)");
		bwText.setText("20");
		SWTBotText srText = wizardBot.textWithLabel("Sample Rate (Msps)");
		srText.setText("20");
		wizardBot.sleep(VALIDATION_DELAY);
		Assert.assertTrue("Finish should be enabled", wizardBot.button("Finish").isEnabled());

		// Specify      BW + SR (no-go)
		cfText.setText("");
		wizardBot.sleep(VALIDATION_DELAY);
		Assert.assertFalse("Finish should be disabled", wizardBot.button("Finish").isEnabled());
		cfText.setText("100");

		// Specify CF +      SR (no-go)
		bwText.setText("");
		wizardBot.sleep(VALIDATION_DELAY);
		Assert.assertFalse("Finish should be disabled", wizardBot.button("Finish").isEnabled());
		bwText.setText("20");

		// Specify CF + BW      (no-go)
		srText.setText("");
		wizardBot.sleep(VALIDATION_DELAY);
		Assert.assertFalse("Finish should be disabled", wizardBot.button("Finish").isEnabled());
		srText.setText("20");

		// BW tolerance = 100% (ok)
		SWTBotText bwTolText = wizardBot.textWithLabel("Bandwidth Tolerance (%)");
		bwTolText.setText("100");
		wizardBot.sleep(VALIDATION_DELAY);
		Assert.assertTrue("Finish should be enabled", wizardBot.button("Finish").isEnabled());

		// BW tolerance = 200% (ok)
		bwTolText.setText("200");
		wizardBot.sleep(VALIDATION_DELAY);
		Assert.assertTrue("Finish should be enabled", wizardBot.button("Finish").isEnabled());

		// BW tolerance = -1% (no-go)
		bwTolText.setText("-1");
		wizardBot.sleep(VALIDATION_DELAY);
		Assert.assertFalse("Finish should be disabled", wizardBot.button("Finish").isEnabled());

		wizardBot.button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

}
