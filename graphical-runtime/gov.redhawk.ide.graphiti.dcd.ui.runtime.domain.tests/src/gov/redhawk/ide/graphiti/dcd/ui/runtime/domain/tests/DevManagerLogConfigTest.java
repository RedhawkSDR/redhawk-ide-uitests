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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLogConfigTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DevManagerLogConfigTest extends AbstractLogConfigTest {

	private static final String DEVICE_MANAGER = "DevMgr_localhost";
	private static final String GPP_LOCALHOST = "GPP_localhost";
	private final String domain = "SWTBOT_TEST_" + (int) (1000.0 * Math.random());
	private String[] devMgrParentPath = null;
	private String[] devMgrPath = null;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		devMgrParentPath = new String[] { domain, "Device Managers" };
		ScaExplorerTestUtils.launchDomain(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, devMgrParentPath, DEVICE_MANAGER);
		devMgrPath = Arrays.copyOf(devMgrParentPath, devMgrParentPath.length + 1);
		devMgrPath[devMgrParentPath.length] = DEVICE_MANAGER;
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, devMgrPath, GPP_LOCALHOST + " STARTED");
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(gefBot, devMgrPath, GPP_LOCALHOST + " STARTED");
	}

	@Override
	protected SWTBotView showConsole() {
		return ConsoleUtils.showConsole(gefBot, DEVICE_MANAGER);
	}

	@Override
	protected SWTBotGefEditPart openResourceDiagram() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, devMgrParentPath, DEVICE_MANAGER, DiagramType.GRAPHITI_NODE_EXPLORER);
		return gefBot.gefEditor(DEVICE_MANAGER).getEditPart(GPP_LOCALHOST);
	}

	@Override
	protected SWTBotGefEditor getDiagramEditor() {
		return gefBot.gefEditor(DEVICE_MANAGER);
	}

	@After
	public void afterTest() {
		ScaExplorerTestUtils.deleteDomainInstance(bot, domain);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
	}

}
