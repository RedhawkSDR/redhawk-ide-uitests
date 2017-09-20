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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Tests relating to allocating/deallocating tuners and listeners.
 */
public abstract class AbstractTunerTest extends UIRuntimeTest {

	protected static final String SANDBOX = "Sandbox";
	protected static final String DEV_MGR = "Device Manager";
	protected static final String FEI_CONTAINER = "FrontEnd Tuners";
	private static final String CENTER_FREQ = "101";

	protected abstract String getDeviceName();

	protected abstract String getDeviceImpl();

	/**
	 * Allocate a tuner via the device, then deallocate all
	 * IDE-1357 Warn on deallocate all
	 */
	@Test
	public void allocate_deallocateAll_device() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1" }, FEI_CONTAINER);
		waitForTunerDeallocation(feiContainer);

		SWTBotShell mainShell = bot.activeShell();
		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR }, getDeviceName() + "_1");
		completeAllocateWizard();
		mainShell.setFocus();
		waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.deallocateAll(bot, new String[] { SANDBOX, DEV_MGR }, getDeviceName() + "_1");
		acknowledgeWarning();
		waitForTunerDeallocation(feiContainer);
	}

	/**
	 * Allocate a tuner via the container, then deallocate all
	 * IDE-1357 Warn on deallocate all
	 */
	@Test
	public void allocate_deallocateAll_container() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1" }, FEI_CONTAINER);
		waitForTunerDeallocation(feiContainer);

		SWTBotShell mainShell = bot.activeShell();
		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1" }, FEI_CONTAINER);
		completeAllocateWizard();
		mainShell.setFocus();
		waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.deallocateAll(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1" }, FEI_CONTAINER);
		acknowledgeWarning();
		waitForTunerDeallocation(feiContainer);
	}

	/**
	 * Allocate and deallocate via the unallocated tuner.
	 */
	@Test
	public void allocate_deallocate_tuner() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1" }, FEI_CONTAINER);
		SWTBotTreeItem tuner = waitForTunerDeallocation(feiContainer);

		SWTBotShell mainShell = bot.activeShell();
		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER }, tuner.getText());
		completeAllocateWizard();
		mainShell.setFocus();
		tuner = waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.deallocate(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER }, tuner.getText());
		waitForTunerDeallocation(feiContainer);
	}

	/**
	 * Allocate and deallocate a listener on a tuner.
	 */
	@Test
	public void allocate_deallocate_listener() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1" }, FEI_CONTAINER);
		SWTBotTreeItem tuner = waitForTunerDeallocation(feiContainer);

		SWTBotShell mainShell = bot.activeShell();
		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER }, tuner.getText());
		completeAllocateWizard();
		mainShell.setFocus();
		tuner = waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.addListener(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER }, tuner.getText());
		completeListenerWizard();
		SWTBotTreeItem listener = waitForListenerAllocation(tuner);

		ScaExplorerTestUtils.deallocate(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER, tuner.getText() }, listener.getText());
		waitForListenerDeallocation(tuner);
	}

	/**
	 * Allocate a listener, then deallocate the tuner.
	 */
	@Test
	public void allocateListener_deallocateTuner() {
		final SWTBotTreeItem feiContainer = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1" }, FEI_CONTAINER);
		SWTBotTreeItem tuner = waitForTunerDeallocation(feiContainer);

		SWTBotShell mainShell = bot.activeShell();
		ScaExplorerTestUtils.allocate(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER }, tuner.getText());
		completeAllocateWizard();
		mainShell.setFocus();
		tuner = waitForTunerAllocation(feiContainer);

		ScaExplorerTestUtils.addListener(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER }, tuner.getText());
		completeListenerWizard();
		waitForListenerAllocation(tuner);

		ScaExplorerTestUtils.deallocate(bot, new String[] { SANDBOX, DEV_MGR, getDeviceName() + "_1", FEI_CONTAINER }, tuner.getText());
		acknowledgeWarning();
		waitForTunerDeallocation(feiContainer);
	}

	private SWTBotTreeItem waitForTunerAllocation(final SWTBotTreeItem feiContainer) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				feiContainer.collapse();
				bot.sleep(200);
				feiContainer.expand();
				return feiContainer.getItems()[0].getText().startsWith("RX_DIGITIZER");
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not allocated";
			}
		}, 15000);
		return feiContainer.getItems()[0];
	}

	private SWTBotTreeItem waitForTunerDeallocation(final SWTBotTreeItem feiContainer) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				feiContainer.collapse();
				bot.sleep(200);
				feiContainer.expand();
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
				feiTuner.collapse();
				bot.sleep(200);
				feiTuner.expand();
				return feiTuner.getItems().length > 0;
			}

			@Override
			public String getFailureMessage() {
				return "Tuner was not allocated";
			}
		}, 15000);
		return feiTuner.getItems()[0];
	}

	private void waitForListenerDeallocation(final SWTBotTreeItem feiTuner) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				feiTuner.collapse();
				bot.sleep(200);
				feiTuner.expand();
				return feiTuner.getItems().length == 0;
			}

			@Override
			public String getFailureMessage() {
				return "Listener was not deallocated";
			}
		}, 15000);
	}

	private void completeAllocateWizard() {
		SWTBotShell shell = bot.shell("Allocate Tuner");
		SWTBot wizardBot = shell.bot();
		wizardBot.textWithLabel("Center Frequency (MHz)").setText(CENTER_FREQ);
		wizardBot.checkBox("Any Value", 0).click();
		wizardBot.checkBox("Any Value", 1).click();
		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	private void completeListenerWizard() {
		SWTBotShell shell = bot.shell("Allocate Listener");
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
