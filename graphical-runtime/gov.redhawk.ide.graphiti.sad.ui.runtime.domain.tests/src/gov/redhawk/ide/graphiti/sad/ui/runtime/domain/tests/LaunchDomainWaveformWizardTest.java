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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.condition.WaitForModalContext;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class LaunchDomainWaveformWizardTest extends UIRuntimeTest {

	protected static final String DEVICE_MANAGER = "DevMgr_localhost";
	private final String DOMAIN = "SWTBOT_SAD_TEST_" + (int) (1000.0 * Math.random()); // SUPPRESS CHECKSTYLE INLINE
	private final String[] DOMAIN_WAVEFORM_PARENT_PATH = { DOMAIN, "Waveforms" }; // SUPPRESS CHECKSTYLE INLINE

	@Test
	public void launchWaveformWithWildcard() {
		final String waveformNameWildcard = "HardLimit";
		final String waveformNameFull = "SigGenToHardLimitWF";

		ScaExplorerTestUtils.launchDomainViaWizard(bot, DOMAIN, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);

		// Launch the domain
		SWTBotTreeItem domainTreeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { DOMAIN }, null);
		domainTreeItem.contextMenu("Launch Waveform...").click();

		// Open the wizard dialog and wait for the waveform list to load
		SWTBotShell wizardShell = bot.shell("Launch Waveform");
		SWTBot wizardBot = wizardShell.bot();
		wizardBot.waitWhile(Conditions.treeHasRows(wizardBot.tree(), 1));
		wizardBot.waitUntil(new WaitForModalContext());
		wizardBot.sleep(ScaExplorerTestUtils.WIZARD_POST_MODAL_PROGRESS_DELAY);

		// Enter the wildcard string
		wizardBot.text().typeText(waveformNameWildcard);
		bot.sleep(200);
		SWTBotTreeItem[] visibleItems = wizardBot.tree().getAllItems();
		Assert.assertEquals("Unexpected number of visible waveforms", 2, visibleItems.length);
		for (SWTBotTreeItem item : visibleItems) {
			Assert.assertTrue(item.getText().matches(".*" + waveformNameWildcard + ".*"));
		}

		// Launch waveform and confirm it is added to the domain
		wizardBot.tree().select(waveformNameFull);
		wizardBot.button("Finish").click();
		bot.waitUntil(new WaitForEditorCondition(), WaitForEditorCondition.DEFAULT_WAIT_FOR_EDITOR_TIME);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveformNameFull);
	}
}
