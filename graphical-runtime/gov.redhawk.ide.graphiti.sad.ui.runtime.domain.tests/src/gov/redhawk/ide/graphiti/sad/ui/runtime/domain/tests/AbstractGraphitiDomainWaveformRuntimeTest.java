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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;

/**
 * Before: Starts a domain, device manager, launches a waveform, and opens the waveform's Graphiti runtime editor.
 * After: Releases the waveform if it's still running and ensures it shuts down.
 */
public abstract class AbstractGraphitiDomainWaveformRuntimeTest extends UIRuntimeTest {

	protected static final String DOMAIN_MANAGER_PROCESS = "Domain Manager";
	protected static final String DEVICE_MANAGER_PROCESS = "Device Manager";
	protected static final String DEVICE_MANAGER = "DevMgr_localhost";

	protected static final String SIGGEN = "rh.SigGen";
	protected static final String SIGGEN_1 = "SigGen_1";
	protected static final String HARD_LIMIT = "rh.HardLimit";
	protected static final String HARD_LIMIT_1 = "HardLimit_1";
	protected static final String DATA_CONVERTER = "rh.DataConverter";
	protected static final String DATA_CONVERTER_1 = "DataConverter_1";

	protected final String DOMAIN = "SWTBOT_SAD_TEST_" + (int) (1000.0 * Math.random()); // SUPPRESS CHECKSTYLE VisibilityModifier
	protected final String[] DOMAIN_WAVEFORM_PARENT_PATH = { DOMAIN, "Waveforms" }; // SUPPRESS CHECKSTYLE VisibilityModifier
	protected SWTGefBot gefBot = new RHSWTGefBot(); // SUPPRESS CHECKSTYLE VisibilityModifier
	private String waveFormFullName; // full name of waveform that is launched

	@Before
	public void beforeTest() throws Exception {
		ScaExplorerTestUtils.launchDomain(bot, DOMAIN, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, getWaveformName());
		bot.waitUntil(new WaitForEditorCondition(), WaitForEditorCondition.DEFAULT_WAIT_FOR_EDITOR_TIME);
		SWTBotTreeItem waveformItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveformName());
		waveformItem.collapse();
		waveFormFullName = waveformItem.getText();
	}

	protected abstract String getWaveformName();

	@After
	public void afterTest() {
		try {
			ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveformName());
			ScaExplorerTestUtils.releaseFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveformName());
			ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveformName());
		} catch (WidgetNotFoundException ex) {
			// PASS
		}

		ScaExplorerTestUtils.deleteDomainInstance(bot, DOMAIN);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
	}

	public String getWaveFormFullName() {
		return waveFormFullName;
	}

}
