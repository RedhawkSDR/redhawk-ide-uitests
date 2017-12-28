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
package gov.redhawk.ide.properties.view.runtime.sad.ports.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractPortPropertiesTest;
import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class LocalComponentPortPropertyTest extends AbstractPortPropertiesTest {

	protected static final String HARD_LIMIT = "rh.HardLimit";
	protected static final String HARD_LIMIT_1 = "HardLimit_1";
	protected static final String PROVIDES_PORT = "dataFloat_in";
	protected static final String USES_PORT = "dataFloat_out";
	protected static final PortDescription PROVIDES_DESC = new PortDescription("IDL:BULKIO/dataFloat:1.0",
		"Float input port for data before hard limit is applied. ");
	protected static final PortDescription USES_DESC = new PortDescription("IDL:BULKIO/dataFloat:1.0",
		"Float output port for data after hard limit is applied. ");

	@After
	public void afterTest() {
		ScaExplorerTestUtils.shutdown(bot, new String[] { "Sandbox" }, "Device Manager");
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", HARD_LIMIT_1 },
			PROVIDES_PORT);
		treeItem.select();
		return PROVIDES_DESC;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", HARD_LIMIT_1 },
			USES_PORT);
		treeItem.select();
		return USES_DESC;
	}

}
