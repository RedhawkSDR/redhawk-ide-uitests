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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class PortPropertiesTest extends AbstractGraphitiChalkboardTest {

	/**
	 * IDE-1520 - Show IDL tree in port details for explorer view
	 */
	@Test
	public void checkPortProperties() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);
		
		SWTBotTreeItem providesPort = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] {"Sandbox", CHALKBOARD, HARD_LIMIT_1 }, "dataFloat_in");
		SWTBotTreeItem usesPort = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] {"Sandbox", CHALKBOARD, HARD_LIMIT_1 }, "dataFloat_out");
		
		final String inDesc = "Float input port for data before hard limit is applied. ";
		final String outDesc = "Float output port for data after hard limit is applied. ";
		
		checkPortDetails(providesPort, inDesc);
		checkPortDetails(usesPort, outDesc);
	}

	private void checkPortDetails(SWTBotTreeItem port, String expectedDesc) {
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propView.show();
		port.select();
		ViewUtils.selectPropertiesTab(bot, "Port Details");
		String description = propView.bot().textWithLabel("Description:").getText(); // IDE-1172
		Assert.assertEquals("provides Port description", expectedDesc, description);
		SWTBotTree tree = gefBot.viewByTitle("Properties").bot().tree();
		tree.expandNode("dataFloat");
		Assert.assertTrue("Properties view tree should have multiple nodes", tree.visibleRowCount() > 1);
		
	}
}
