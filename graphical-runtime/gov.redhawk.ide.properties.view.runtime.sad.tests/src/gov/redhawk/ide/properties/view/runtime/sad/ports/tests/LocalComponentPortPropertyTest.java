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

import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class LocalComponentPortPropertyTest extends AbstractComponentPortPropertiesTest {

	protected static final String HARD_LIMIT = "rh.HardLimit";
	protected static final String HARD_LIMIT_1 = "HardLimit_1";
	protected static final String HARD_LIMIT_PROVIDES = "dataFloat_in";
	protected static final String HARD_LIMIT_USES = "dataFloat_out";
	private static final PortDescription HARD_LIMIT_PROVIDES_DESC = new PortDescription("IDL:BULKIO/dataFloat:1.0",
		"Float input port for data before hard limit is applied. ");
	private static final PortDescription HARD_LIMIT_USES_DESC = new PortDescription("IDL:BULKIO/dataFloat:1.0",
		"Float output port for data after hard limit is applied. ");

	private static final String NEGOTIATOR = "Negotiator";
	private static final String NEGOTIATOR_IMPL = "python";
	protected static final String NEGOTIATOR_1 = "Negotiator_1";
	protected static final String NEGOTIATOR_PROVIDES = "negotiable_in";
	protected static final String NEGOTIATOR_USES = "negotiable_out";

	@After
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", HARD_LIMIT_1 },
			HARD_LIMIT_PROVIDES);
		treeItem.select();
		return HARD_LIMIT_PROVIDES_DESC;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", HARD_LIMIT_1 },
			HARD_LIMIT_USES);
		treeItem.select();
		return HARD_LIMIT_USES_DESC;
	}

	@Override
	protected void prepareProvidesPortAdvanced() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "cpp");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", HARD_LIMIT_1 },
			HARD_LIMIT_PROVIDES);
		treeItem.select();
	}

	@Override
	protected void prepareUsesPortAdvanced() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "cpp");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", HARD_LIMIT_1 },
			HARD_LIMIT_USES);
		treeItem.select();
	}

	@Override
	protected void prepareNegotiatorComponentProvides() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, NEGOTIATOR, NEGOTIATOR_IMPL);
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", NEGOTIATOR_1 },
			NEGOTIATOR_PROVIDES);
		treeItem.select();
	}

	@Override
	protected void prepareNegotiatorComponentUses() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, NEGOTIATOR, NEGOTIATOR_IMPL);
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", NEGOTIATOR_1 },
			NEGOTIATOR_USES);
		treeItem.select();
	}
}
