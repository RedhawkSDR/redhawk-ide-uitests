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

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Logging tests against a device belonging to the sandbox device manager.
 */
public class DevManagerSandboxLogConfigTest extends AbstractLogConfigTest {

	private static final String GPP = "GPP";
	private static final String GPP_1 = "GPP_1";
	private static final String[] SANDBOX_PATH = { "Sandbox" };
	private static final String DEVICE_MANAGER = "Device Manager";
	private static final String[] GPP_PARENT_PATH = { "Sandbox", "Device Manager" };

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, GPP, "cpp");
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, GPP_PARENT_PATH, GPP_1);
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, GPP_PARENT_PATH, GPP_1);
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.terminate(bot, SANDBOX_PATH, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilSandboxDeviceManagerEmpty(bot, SANDBOX_PATH, DEVICE_MANAGER);
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	@Override
	protected String getConsoleTitle() {
		return GPP;
	}
}
