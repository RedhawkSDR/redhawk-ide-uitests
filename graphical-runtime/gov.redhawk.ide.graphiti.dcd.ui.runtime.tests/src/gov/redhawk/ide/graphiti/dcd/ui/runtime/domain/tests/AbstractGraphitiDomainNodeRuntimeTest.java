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

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractGraphitiDomainNodeRuntimeTest extends UIRuntimeTest {

	protected static final String DEVICE_MANAGER = "DevMgr_localhost";
	protected static final String DEVICE_MANAGER_W_BULKIO = "DevMgr_with_bulkio";
	protected static final String NAMESPACE_DEVICE_MANAGER = "name.space.node";

	protected static final String GPP_1 = "GPP_1";
	protected static final String GPP_LOCALHOST = "GPP_localhost";
	protected static final String DEVICE_STUB = "DeviceStub";
	protected static final String NAME_SPACE_DEVICE_1 = "device_1";

	private final String DOMAIN = "SWTBOT_TEST_" + (int) (1000.0 * Math.random());
	protected String[] DEV_MGR_PATH = null;

	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE VisibilityModifier

	@Before
	public void beforeTest() throws Exception {
		super.before();
		gefBot = new SWTGefBot();
	}

	protected void launchDomainAndDevMgr(String deviceManager) {
		String[] parentPath = new String[] { DOMAIN, "Device Managers" };
		ScaExplorerTestUtils.launchDomain(bot, DOMAIN, deviceManager);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, parentPath, deviceManager, DiagramType.GRAPHITI_NODE_EXPLORER);
		DEV_MGR_PATH = Arrays.copyOf(parentPath, parentPath.length + 1);
		DEV_MGR_PATH[parentPath.length] = deviceManager;
	}

	@After
	public void afterTest() {
		ScaExplorerTestUtils.deleteDomainInstance(bot, DOMAIN);
		ConsoleUtils.terminateAllProcesses(gefBot);
	}
}
