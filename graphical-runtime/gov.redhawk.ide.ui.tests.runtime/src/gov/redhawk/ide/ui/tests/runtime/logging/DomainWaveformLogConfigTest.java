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

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Logging tests against a component belonging to a waveform in a domain.
 */
public class DomainWaveformLogConfigTest extends AbstractLogConfigTest {

	private static final String DEVICE_MANAGER = "DevMgr_localhost";
	private static final String GPP_LOCALHOST = "GPP_localhost";

	private static final String TEST_WAVEFORM = "SigGenToHardLimitWF";
	private static final String SIGGEN_1 = "SigGen_1";

	private String domainName = null;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		domainName = "SWTBOT_TEST_" + (int) (1000.0 * Math.random());

		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		final String[] GPP_PARENT_PATH = new String[] { domainName, "Device Managers", DEVICE_MANAGER };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, GPP_PARENT_PATH, GPP_LOCALHOST);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, domainName, TEST_WAVEFORM);
		final String[] WAVEFORM_PARENT_PATH = new String[] { domainName, "Waveforms" };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		final String waveFormFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		new RHSWTGefBot().rhGefEditor(waveFormFullName);

		String[] sigGenParentPath = new String[] { domainName, "Waveforms", TEST_WAVEFORM };
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, sigGenParentPath, SIGGEN_1);
	}

	@After
	public void after() throws CoreException {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;

			ScaExplorerTestUtils.deleteDomainInstance(bot, localDomainName);
			NodeBooterLauncherUtil.getInstance().terminateAll();
			ConsoleUtils.removeTerminatedLaunches(bot);
		}
		super.after();
	}

	@Override
	protected String getConsoleTitle() {
		return DEVICE_MANAGER;
	}
}
