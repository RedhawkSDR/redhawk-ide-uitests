/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotViewMenu;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class DomainWaveformReleaseTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	/**
	 * Test to make sure the domain waveform and the hidden local copy are both correctly released. 
	 * This test assumes a domain and a domain waveform are launched in the super beforeTest method
	 */
	@Test
	public void releaseDomainWaveformTest() {
		SWTBot viewBot = ScaExplorerTestUtils.showScaExplorerView(gefBot);

		// Expose the hidden waveform and make sure it is there
		SWTBotView view = bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
		List<SWTBotViewMenu> menus = view.menus();
		menus.get(0).click();
		SWTBot shellBot = bot.shell("Available Customizations").bot();
		SWTBotTable customizationTable = shellBot.table();
		SWTBotTableItem hideLocalItem = customizationTable.getTableItem("Hide Local References");
		hideLocalItem.uncheck();
		shellBot.button("OK").click();
		
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] {"Sandbox"}, getWaveFormFullName());
		
		// Release the domain waveform and make sure it is gone
		SWTBotTreeItem waveformNode = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName());
		waveformNode.select().contextMenu("Release").click();
		
		// Make sure the local copy is gone, as well as not "Loading...", maybe by checking the number of nodes under sandbox
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName());
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] {"Sandbox"}, getWaveFormFullName());
		List<String> sandboxNodes = viewBot.tree().getTreeItem("Sandbox").getNodes();
		
		// Expected Sandbox children - Chalkboard, File Manager, and Device Manager
		Assert.assertEquals("Sandbox has too many child nodes, waveform may not have disposed correctly", 3, sandboxNodes.size());
	}
}
