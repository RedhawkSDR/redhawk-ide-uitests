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
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.debug.core.DebugException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class PlottingTest extends UIRuntimeTest {

	private static final String DOMAIN_NAME = "PlottingTest";
	private static final String DEV_MGR_NAME = "DevMgr_localhost";
	private static final String GPP = "GPP_localhost";

	private static final String WAVEFORM = "ExampleWaveform05";
	private static final String SIG_GEN_1 = "SigGen_1";

	@Before
	public void before() throws Exception {
		super.before();
		ScaExplorerTestUtils.launchDomainViaWizard(bot, DOMAIN_NAME, DEV_MGR_NAME);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN_NAME);
		final String[] GPP_PARENT_PATH = new String[] { DOMAIN_NAME, "Device Managers", DEV_MGR_NAME };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, GPP_PARENT_PATH, GPP);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN_NAME, WAVEFORM);
		SWTBotTreeItem sigGenTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { DOMAIN_NAME, "Waveforms", WAVEFORM },
			SIG_GEN_1);

		sigGenTreeItem.select();
		sigGenTreeItem.contextMenu("Start").click();
	}

	@Test
	public void plotPort() {
		final SWTBotTreeItem outPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot,
			new String[] { DOMAIN_NAME, "Waveforms", WAVEFORM, SIG_GEN_1 }, "dataFloat_out");
		outPort.select();
		outPort.expand();
		Assert.assertEquals("Port connections", 0, outPort.getItems().length);
		outPort.contextMenu("Plot Port Data").click();

		SWTBotView plotView = bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
		Assert.assertEquals("dataFloat_out", plotView.getReference().getTitle());

		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return outPort.getItems().length == 1;
			}

			@Override
			public String getFailureMessage() {
				return "Port connection did not appear";
			}
		});
	}

	@After
	public void cleanup() throws DebugException {
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
	}
}
