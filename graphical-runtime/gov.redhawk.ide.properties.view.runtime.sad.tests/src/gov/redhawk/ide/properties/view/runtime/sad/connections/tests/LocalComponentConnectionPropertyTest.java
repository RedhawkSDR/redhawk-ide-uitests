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
package gov.redhawk.ide.properties.view.runtime.sad.connections.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportType;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.condition.TreeItemHasRows;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class LocalComponentConnectionPropertyTest extends AbstractComponentConnectionPropertiesTest {

	private static final String SIG_GEN = "rh.SigGen";
	protected static final String SIG_GEN_1 = "SigGen_1";
	private static final String SIG_GEN_IMPL = "cpp";
	protected static final String SIG_GEN_USES = "dataFloat_out";

	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String HARD_LIMIT_IMPL = "python";
	private static final String HARD_LIMIT_PROVIDES = "dataFloat_in";

	private static final String NEGOTIATOR = "Negotiator";
	protected static final String NEGOTIATOR_1 = "Negotiator_1";
	private static final String NEGOTIATOR_2 = "Negotiator_2";
	private static final String NEGOTIATOR_IMPL = "python";
	protected static final String NEGOTIATOR_USES = "negotiable_out";
	private static final String NEGOTIATOR_PROVIDES = "negotiable_in";

	@After
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
	}

	@Override
	protected void prepareConnection() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, SIG_GEN_IMPL);
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, HARD_LIMIT_IMPL);

		// For some reason, we need to find both, then get them again or we get NPEs with SWTBot
		String[] parent1 = new String[] { "Sandbox", "Chalkboard", SIG_GEN_1 };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parent1, SIG_GEN_USES);
		String[] parent2 = new String[] { "Sandbox", "Chalkboard", HARD_LIMIT_1 };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parent2, HARD_LIMIT_PROVIDES);
		SWTBotTreeItem treeItem1 = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, parent1, SIG_GEN_USES);
		SWTBotTreeItem treeItem2 = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, parent2, HARD_LIMIT_PROVIDES);

		// Connect the ports, select the resultant connection
		ViewUtils.getExplorerView(bot).bot().tree().select(treeItem1, treeItem2).contextMenu().menu("Connect").click();
		bot.waitUntil(new TreeItemHasRows(treeItem1));
		treeItem1.getItems()[0].select();
	}

	@Override
	protected TransportTypeAndProps getConnectionDetails() {
		return new TransportTypeAndProps(TransportType.CORBA);
	}

	@Override
	protected void prepareNegotiatorComponentConnection() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, NEGOTIATOR, NEGOTIATOR_IMPL);
		String[] parent1 = new String[] { "Sandbox", "Chalkboard", NEGOTIATOR_1 };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parent1, NEGOTIATOR_USES);

		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, NEGOTIATOR, NEGOTIATOR_IMPL);
		String[] parent2 = new String[] { "Sandbox", "Chalkboard", NEGOTIATOR_2 };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parent2, NEGOTIATOR_PROVIDES);

		SWTBotTreeItem treeItem1 = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, parent1, NEGOTIATOR_USES);
		SWTBotTreeItem treeItem2 = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, parent2, NEGOTIATOR_PROVIDES);

		// Connect the ports, select the resultant connection
		ViewUtils.getExplorerView(bot).bot().tree().select(treeItem1, treeItem2).contextMenu().menu("Connect").click();
		bot.waitUntil(new TreeItemHasRows(treeItem1));
		treeItem1.getItems()[0].select();
	}
}
