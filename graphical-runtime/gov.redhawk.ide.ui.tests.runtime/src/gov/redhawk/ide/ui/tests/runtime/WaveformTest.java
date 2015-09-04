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
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class WaveformTest extends UIRuntimeTest {

	SWTBot scaExplorerViewBot;

	@Override
	public void before() throws Exception {
		super.before();

		SWTBotView explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		this.scaExplorerViewBot = explorerView.bot();
	}
	
	@Override
	public void after() throws Exception {
		this.scaExplorerViewBot = null;

		super.after();
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

		// cleanup
		releaseWaveform(waveformTreeItem);
	}

	/**
	 * IDE-1222 Launching a Waveform with overloaded property values, external ports and external properties
	 * in Sandbox with Advanced Wizard.
	 */
	@Test
	public void launchSandboxWaveformAdvanced() {
		SWTBotTreeItem waveformTreeItem = launchLocalWaveformAdvanced("SigGenToHardLimitExtPortsPropsWF");
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

		// cleanup
		releaseWaveform(waveformTreeItem);
	}

	private void releaseWaveform(SWTBotTreeItem waveformTreeItem) {
		try {
			waveformTreeItem.select();
			waveformTreeItem.contextMenu("Release").click();
		} catch (TimeoutException te) {
			waveformTreeItem.select();
			waveformTreeItem.pressShortcut(Keystrokes.DELETE);
		}
	}

	/**
	 * Launches the selected Waveform in the REDHAWK Explorer Sandbox using Advanced Wizard. This just clicks Finish on the
	 * Advanced Launch Waveform (in Sandbox) Wizard without specifying any user override property values.
	 * @returns the SWTBotTreeItem for the launched Waveform on the Sandbox
	 */
	SWTBotTreeItem launchLocalWaveformAdvanced(final String waveformName) {
		SWTBotTreeItem waveformNode = this.scaExplorerViewBot.tree().expandNode("Target SDR", "Waveforms", waveformName);
		waveformNode.contextMenu("Launch in Sandbox").menu("Advanced").click();
		bot.shell("Launch Waveform").bot().button("Finish").click();

		// Wait for the launched Waveform to appear in the Sandbox
		final SWTBotTreeItem sandbox = this.scaExplorerViewBot.tree().getTreeItem("Sandbox");
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				for (SWTBotTreeItem item : sandbox.getItems()) {
					if (item.getText().matches(waveformName + ".*")) {
						return true;
					}
				}
				return false;
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "Waveform: " + waveformName + " did not launch from Advanced Wizard";
			}
		});

		// Return the TreeItem for the running waveform from the Sandbox
		for (SWTBotTreeItem item : sandbox.getItems()) {
			if (item.getText().matches(waveformName + ".*")) {
				return item;
			}
		}

		return null;
	}

}
