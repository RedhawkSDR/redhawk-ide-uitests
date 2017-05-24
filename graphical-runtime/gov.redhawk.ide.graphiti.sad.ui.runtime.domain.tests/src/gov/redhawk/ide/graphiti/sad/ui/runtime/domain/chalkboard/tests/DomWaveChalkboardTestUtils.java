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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.chalkboard.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DomWaveChalkboardTestUtils {

	private static final String DEVICE_MANAGER_WITH_GPP = "DevMgr_localhost";
	private static final String GPP_LOCALHOST = "GPP_localhost";

	private DomWaveChalkboardTestUtils() {
	}

	protected static String generateDomainName() {
		return String.format("SWTBOT_%s_%d", DomWaveChalkboardTestUtils.class.getSimpleName(), (int) (1000.0 * Math.random()));
	}

	protected static RHBotGefEditor launchDomainAndWaveform(SWTWorkbenchBot bot, String domainName, String waveformName) {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, DEVICE_MANAGER_WITH_GPP);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		final String[] GPP_PARENT_PATH = new String[] { domainName, "Device Managers", DEVICE_MANAGER_WITH_GPP };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, GPP_PARENT_PATH, GPP_LOCALHOST);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, domainName, waveformName);
		final String[] WAVEFORM_PARENT_PATH = new String[] { domainName, "Waveforms" };
		SWTBotTreeItem waveformItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, WAVEFORM_PARENT_PATH, waveformName);
		waveformItem.collapse();
		final String waveFormFullName = waveformItem.getText();
		bot.editorByTitle(waveFormFullName).close();

		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, WAVEFORM_PARENT_PATH, waveFormFullName, DiagramType.GRAPHITI_CHALKBOARD);
		return new RHSWTGefBot().rhGefEditor(waveFormFullName);
	}

	protected static void cleanup(SWTWorkbenchBot bot, String domainName) {
		ScaExplorerTestUtils.deleteDomainInstance(bot, domainName);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
	}
}
