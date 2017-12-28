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
package gov.redhawk.ide.properties.view.runtime.dcd.ports.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractPortPropertiesTest;
import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class LocalDevicePortPropertyTest extends AbstractPortPropertiesTest {

	protected static final String DEVICE_STUB = "DeviceStub";
	protected static final String DEVICE_STUB_1 = "DeviceStub_1";
	protected static final String PROVIDES_PORT = "dataDouble_in";
	protected static final String USES_PORT = "dataFloat_out";
	protected static final PortDescription PROVIDES_DESC = new PortDescription("IDL:BULKIO/dataDouble:1.0", "Input port 1 description");
	protected static final PortDescription USES_DESC = new PortDescription("IDL:BULKIO/dataFloat:1.0", "Output port 1 description");

	@After
	public void afterTest() {
		ScaExplorerTestUtils.shutdown(bot, new String[] { "Sandbox" }, "Device Manager");
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Device Manager", DEVICE_STUB_1 },
			PROVIDES_PORT);
		treeItem.select();
		return PROVIDES_DESC;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Device Manager", DEVICE_STUB_1 },
			USES_PORT);
		treeItem.select();
		return USES_DESC;
	}

}
