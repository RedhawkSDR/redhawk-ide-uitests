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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.junit.After;
import org.junit.Before;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * 
 */
public abstract class AbstractDeviceManagerSandboxTest extends UIRuntimeTest {

	static final String[] SANDBOX_PATH = { "Sandbox" };
	static final String DEVICE_MANAGER = "Device Manager";
	static final String[] SANDBOX_DEVMGR_PATH = new String[] { "Sandbox", "Device Manager" };

	// Common Test Component Names
	static final String GPP = "GPP";
	static final String GPP_1 = "GPP_1";

	static final String DEVICE_STUB = "DeviceStub";
	static final String DEVICE_STUB_1 = "DeviceStub_1";
	static final String DEVICE_STUB_2 = "DeviceStub_2";
	static final String DEVICE_STUB_DOUBLE_IN_PORT = "dataDouble_in";
	static final String DEVICE_STUB_DOUBLE_OUT_PORT = "dataDouble_out";

	static final String SERVICE_STUB = "ServiceStub";
	static final String SERVICE_STUB_1 = "ServiceStub_1";
	static final String SERVICE_STUB_2 = "ServiceStub_2";

	protected RHSWTGefBot gefBot;  // SUPPRESS CHECKSTYLE INLINE - package field

	@Override
	@Before
	public void before() throws Exception {
		super.before();
		gefBot = new RHSWTGefBot();
	}

	@After
	public void afterTest() {
		ScaExplorerTestUtils.terminate(bot, SANDBOX_PATH, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilSandboxDeviceManagerEmpty(bot);
		ConsoleUtils.removeTerminatedLaunches(bot);
		bot.closeAllEditors();
	}
}
