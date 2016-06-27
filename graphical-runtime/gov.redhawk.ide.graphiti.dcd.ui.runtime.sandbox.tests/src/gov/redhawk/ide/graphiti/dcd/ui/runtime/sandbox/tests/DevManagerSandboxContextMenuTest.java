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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLocalContextMenuTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * IDE-1506 Devices were launching with id 'null'
 */
public class DevManagerSandboxContextMenuTest extends AbstractLocalContextMenuTest {

	private static final String[] SANDBOX_PATH = { "Sandbox" };
	private static final String DEVICE_MANAGER = "Device Manager";
	private static final String[] DEVICE_MANAGER_PATH = { "Sandbox", DEVICE_MANAGER };

	private static final String DEVICE_STUB = "DeviceStub";
	private static final String DEVICE_STUB_OUT = "dataFloat_out";

	@Override
	protected ComponentDescription getTestComponent() {
		return new ComponentDescription(DEVICE_STUB, null, new String[] { DEVICE_STUB_OUT });
	}

	@Override
	protected ComponentDescription getLocalTestComponent() {
		return getTestComponent();
	}

	@Override
	protected RHBotGefEditor launchDiagram() {
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(bot);

		DiagramTestUtils.addFromPaletteToDiagram(editor, getTestComponent().getFullName(), 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DEVICE_MANAGER_PATH, getTestComponent().getShortName(1));

		return editor;
	}

	@After
	public void after() {
		ScaExplorerTestUtils.terminateFromScaExplorer(bot, SANDBOX_PATH, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilSandboxDeviceManagerEmpty(bot, SANDBOX_PATH, DEVICE_MANAGER);
		ConsoleUtils.removeTerminatedLaunches(bot);
		bot.closeAllEditors();
	}

	@Override
	protected boolean supportsTailLog() {
		return false;
	}
}
