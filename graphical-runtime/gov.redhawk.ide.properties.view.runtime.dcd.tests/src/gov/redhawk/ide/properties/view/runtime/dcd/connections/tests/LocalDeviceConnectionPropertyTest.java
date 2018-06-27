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
package gov.redhawk.ide.properties.view.runtime.dcd.connections.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractConnectionPropertiesTest;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportType;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.condition.TreeItemHasRows;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class LocalDeviceConnectionPropertyTest extends AbstractConnectionPropertiesTest {

	private static final String USRP = "rh.USRP_UHD";
	private static final String USRP_IMPL = "cpp";
	protected static final String USRP_1 = "rh.USRP_UHD_1";
	private static final String USRP_2 = "rh.USRP_UHD_2";
	protected static final String USES_PORT = "dataShort_out";
	private static final String PROVIDES_PORT = "dataShortTX_in";

	@After
	public void afterTest() {
		ScaExplorerTestUtils.shutdown(bot, new String[] { "Sandbox" }, "Device Manager");
	}

	@Override
	protected void prepareConnection() {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, USRP, USRP_IMPL);
		String[] parent1 = new String[] { "Sandbox", "Device Manager", USRP_1 };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parent1, USES_PORT);

		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, USRP, USRP_IMPL);
		String[] parent2 = new String[] { "Sandbox", "Device Manager", USRP_2 };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parent2, PROVIDES_PORT);

		// Connect the ports, select the resultant connection
		SWTBotTreeItem treeItem1 = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, parent1, USES_PORT);
		SWTBotTreeItem treeItem2 = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, parent2, PROVIDES_PORT);
		ViewUtils.getExplorerView(bot).bot().tree().select(treeItem1, treeItem2).contextMenu().menu("Connect").click();
		bot.waitUntil(new TreeItemHasRows(treeItem1));
		treeItem1.getItems()[0].select();
	}

	@Override
	protected TransportTypeAndProps getConnectionDetails() {
		return new TransportTypeAndProps(TransportType.SHMIPC);
	}
}
