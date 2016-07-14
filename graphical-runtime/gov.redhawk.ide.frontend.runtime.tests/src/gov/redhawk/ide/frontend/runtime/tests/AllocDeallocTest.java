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
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class AllocDeallocTest extends UIRuntimeTest {

	private static final String SANDBOX = "Sandbox";
	private static final String DEV_MGR = "Device Manager";
	private static final String FEI_DEVICE = "rh.FmRdsSimulator";
	private static final String FEI_DEVICE_IMPL = "cpp";
	private static final String FEI_DEVICE_1 = "rh.FmRdsSimulator_1";
	private static final String FEI_CONTAINER = "FrontEnd Tuners";
	private static final String CENTER_FREQ = "101";

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
	 * Allocate a tuner via the device, then deallocate all
	 * IDE-1357 Warn on deallocate all
	 */
	@Test
	public void allocate_deallocateAll_device() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1 },
			FEI_CONTAINER);
		waitForTunerDeallocation(feiContainer);

		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR }, FEI_DEVICE_1);
		completeAllocateWizard();
		waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.deallocateAll(bot, new String[] { SANDBOX, DEV_MGR }, FEI_DEVICE_1);
		acknowledgeWarning();
		waitForTunerDeallocation(feiContainer);
	}

	/**
	 * Allocate a tuner via the container, then deallocate all
	 * IDE-1357 Warn on deallocate all
	 */
	@Test
	public void allocate_deallocateAll_container() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1 },
			FEI_CONTAINER);
		waitForTunerDeallocation(feiContainer);

		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1 }, FEI_CONTAINER);
		completeAllocateWizard();
		waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.deallocateAll(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1 }, FEI_CONTAINER);
		acknowledgeWarning();
		waitForTunerDeallocation(feiContainer);
	}

	/**
	 * Allocate and deallocate via the unallocated tuner.
	 */
	@Test
	public void allocate_deallocate_tuner() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1 },
			FEI_CONTAINER);
		SWTBotTreeItem tuner = waitForTunerDeallocation(feiContainer);

		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER }, tuner.getText());
		completeAllocateWizard();
		tuner = waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.deallocate(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER }, tuner.getText());
		waitForTunerDeallocation(feiContainer);
	}

	/**
	 * Allocate and deallocate a listener on a tuner.
	 */
	@Test
	public void allocate_deallocate_listner() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1 },
			FEI_CONTAINER);
		SWTBotTreeItem tuner = waitForTunerDeallocation(feiContainer);

		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER }, tuner.getText());
		completeAllocateWizard();
		tuner = waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.addListener(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER }, tuner.getText());
		completeListenerWizard();
		SWTBotTreeItem listener = waitForListenerAllocation(tuner);

		ScaExplorerTestUtils.deallocate(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER, tuner.getText() }, listener.getText());
		waitForListenerDeallocation(tuner);
	}

	/**
	 * Allocate a listener, then deallocate the tuner.
	 */
	@Test
	public void allocateListener_deallocateTuner() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1 },
			FEI_CONTAINER);
		SWTBotTreeItem tuner = waitForTunerDeallocation(feiContainer);

		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER }, tuner.getText());
		completeAllocateWizard();
		tuner = waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.addListener(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER }, tuner.getText());
		completeListenerWizard();
		waitForListenerAllocation(tuner);

		ScaExplorerTestUtils.deallocate(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1, FEI_CONTAINER }, tuner.getText());
		acknowledgeWarning();
		waitForTunerDeallocation(feiContainer);
	}

	private SWTBotTreeItem waitForTunerAllocation(final SWTBotTreeItem feiContainer) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return feiContainer.getItems()[0].getText().startsWith("RX_DIGITIZER");
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not allocated";
			}
		}, 10000);
		return feiContainer.getItems()[0];
	}

	private SWTBotTreeItem waitForTunerDeallocation(final SWTBotTreeItem feiContainer) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return "Unallocated RX_DIGITIZER: 1 available".equals(feiContainer.getItems()[0].getText());
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not deallocated";
			}
		}, 15000);
		return feiContainer.getItems()[0];
	}

	private SWTBotTreeItem waitForListenerAllocation(final SWTBotTreeItem feiTuner) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return feiTuner.getItems().length > 0;
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not allocated";
			}
		}, 15000);
		return feiTuner.expand().getItems()[0];
	}

	private void waitForListenerDeallocation(final SWTBotTreeItem feiTuner) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return feiTuner.getItems().length == 0;
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not allocated";
			}
		}, 10000);
	}

	private void completeAllocateWizard() {
		SWTBotShell shell = bot.shell("");
		SWTBot wizardBot = shell.bot();
		wizardBot.textWithLabel("Center Frequency (MHz)").setText(CENTER_FREQ);
		wizardBot.checkBox("Any Value", 0).click();
		wizardBot.checkBox("Any Value", 1).click();
		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	private void completeListenerWizard() {
		SWTBotShell shell = bot.shell("");
		SWTBot wizardBot = shell.bot();
		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	private void acknowledgeWarning() {
		SWTBotShell shell = bot.shell("Deallocation Warning");
		shell.bot().button("Yes").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}
}
