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
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class WaveformTest extends UIRuntimeTest {
	
	SWTBot viewBot;
	
	@Override
	public void before() throws Exception {
		super.before();
		
		SWTBotView explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		viewBot = explorerView.bot();
	}
	
	/**
	 * IDE-1222 Launching (default) a Waveform with overloaded property values in Sandbox.  
	 */
	@Test
	public void launchSandboxWaveformDefault() {
		SWTBotTreeItem waveformTreeItem = WaveformUtils.launchLocalWaveform(bot, "SigGenToHardLimitWF");
		String waveformNameInSandbox = waveformTreeItem.getText();
		String[] nodeParentPath = { "Sandbox", waveformNameInSandbox };
		waveformTreeItem.expand(); // must expand otherwise will not see sub-items (e.g. Components) underneath this TreeItem)
		
		// check first Component's (SigGen) properties
		SWTBotTreeItem compTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, nodeParentPath, "SigGen_1");
		compTreeItem.select();
		
		MenuUtils.showPropertiesView(bot);
		SWTBotTree propertiesTree = bot.viewByTitle("Properties").bot().tree();
		
		SWTBotTreeItem[] allItems = propertiesTree.getAllItems();
		for (SWTBotTreeItem item : allItems) {
			String cellValue = item.cell(1);
			if ("frequency".equals(item.getText())) {
				Assert.assertEquals("SAD overridden value for frequency", "400.0 Hz", cellValue);
			} else if ("magnitude".equals(item.getText())) {
				Assert.assertEquals("SAD overridden value for magnitude", "120.0", cellValue);
			} else if ("stream_id".equals(item.getText())) {
				Assert.assertEquals("SAD overridden value for stream_id", "SigGenStreamFromWF", cellValue);
			}
		}
		
		// check second Component's (HardLimit) properties
		compTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, nodeParentPath, "HardLimit_1");
		compTreeItem.select();
		
		MenuUtils.showPropertiesView(bot);
		propertiesTree = bot.viewByTitle("Properties").bot().tree();
		
		SWTBotTreeItem limitsTreeItem = propertiesTree.expandNode("limits");
		for (SWTBotTreeItem item : limitsTreeItem.getItems()) {
			String cellValue = item.cell(1);
			if ("lower_limit".equals(item.getText())) {
				Assert.assertEquals("SAD overridden value for lower_limit", "-30.0", cellValue);
			} else if ("upper_limit".equals(item.getText())) {
				Assert.assertEquals("SAD overridden value for upper_limit", "90.0", cellValue);
			}
		}

		// Release waveform
		waveformTreeItem.contextMenu("Release").click();		
	}	
}
