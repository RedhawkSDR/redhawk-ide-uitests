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
package gov.redhawk.ide.ui.tests.runtime.application.metrics;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MetricsTest extends UITest {

	private static final String DOMAIN_NAME = MetricsTest.class.getSimpleName();
	private static final String DEVICE_MANAGER_WITH_GPP = "DevMgr_localhost";
	private static final String GPP_LOCALHOST = "GPP_localhost";

	private static final String[] GPP_PARENT_PATH = { DOMAIN_NAME, "Device Managers", DEVICE_MANAGER_WITH_GPP };
	private static final String[] WAVEFORM_PARENT_PATH = { DOMAIN_NAME, "Waveforms" };

	/**
	 * IDE-2014 Display metrics in properties view
	 */
	@Test
	public void test() {
		final String WAVEFORM = "ExampleWaveform06";

		ScaExplorerTestUtils.launchDomainViaWizard(bot, DOMAIN_NAME, DEVICE_MANAGER_WITH_GPP);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, GPP_PARENT_PATH, GPP_LOCALHOST);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN_NAME, WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM);
		ScaExplorerTestUtils.startResourceInExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM);

		SWTBotTree tree = ViewUtils.selectPropertiesTab(bot, "Metrics");
		bot.waitUntil(Conditions.treeHasRows(tree, 3));
		Assert.assertEquals("application utilization", tree.cell(0, 0));
		Assert.assertEquals("SigGen_1", tree.cell(1, 0));
		Assert.assertEquals("HardLimit_1", tree.cell(2, 0));
		for (int i = 1; i <= 6; i++) {
			Assert.assertTrue("Cell (1, " + i + ") does not have text", !tree.cell(1, i).isEmpty());
			Assert.assertTrue("Cell (2, " + i + ") does not have text", !tree.cell(2, i).isEmpty());
		}
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.deleteDomainInstance(bot, DOMAIN_NAME);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

}
