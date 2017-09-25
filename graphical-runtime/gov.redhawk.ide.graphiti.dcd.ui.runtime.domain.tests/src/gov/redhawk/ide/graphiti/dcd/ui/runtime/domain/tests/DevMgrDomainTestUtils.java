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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DevMgrDomainTestUtils {

	private DevMgrDomainTestUtils() {
	}

	protected static String generateDomainName() {
		return "SWTBOT_TEST_" + (int) (1000.0 * Math.random());
	}

	/**
	 * Launches the specified domain and device managers. Waits for the specified resource in the device manager to be
	 * present, then opens node explorer diagram.
	 * @param bot
	 * @param domainName The name of the domain to launch
	 * @param devMgrName The name of the device manager to launch
	 * @param resourceName The device/service to wait for during launch
	 * @return The diagram that was just opened
	 */
	protected static RHBotGefEditor launchDomainAndDevMgr(SWTWorkbenchBot bot, String domainName, String devMgrName, String resourceName) {
		String[] devMgrsPath = new String[] { domainName, "Device Managers" };
		String[] devMgrPath = new String[] { domainName, "Device Managers", devMgrName };
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, devMgrName);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, devMgrPath, resourceName);
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, devMgrsPath, devMgrName, DiagramType.GRAPHITI_NODE_EXPLORER);
		return new RHSWTGefBot().rhGefEditor(devMgrName);
	}

	protected static void cleanup(SWTWorkbenchBot bot, String domainName) {
		ScaExplorerTestUtils.deleteDomainInstance(bot, domainName);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
	}

}
