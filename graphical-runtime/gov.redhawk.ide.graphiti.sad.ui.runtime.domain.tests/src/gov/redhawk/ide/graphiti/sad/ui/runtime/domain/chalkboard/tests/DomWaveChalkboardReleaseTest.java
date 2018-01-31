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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.chalkboard.tests;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomWaveChalkboardReleaseTest extends UIRuntimeTest {

	private static final String WAVEFORM_NAME = "ExampleWaveform06";

	private String domainName;

	@Before
	public void before() throws Exception {
		super.before();
		domainName = DomWaveChalkboardTestUtils.generateDomainName();
		DomWaveChalkboardTestUtils.launchDomainAndWaveform(bot, domainName, WAVEFORM_NAME);
	}

	@After
	public void after() throws CoreException {
		showLocalReferences(false);

		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;
			DomWaveChalkboardTestUtils.cleanup(bot, localDomainName);
		}
		super.after();
	}

	/**
	 * Test to make sure the domain waveform and the hidden local copy (from opening with the chalkboard editor) are
	 * both correctly released.
	 */
	@Test
	public void releaseDomainWaveformTest() {
		// Expose the hidden waveform and make sure it is there
		showLocalReferences(true);

		// Waveform should be present under the sandbox
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox" }, WAVEFORM_NAME);

		// Release the domain waveform and make sure it is gone
		final String[] DOMAIN_WAVEFORM_PARENT_PATH = new String[] { domainName, "Waveforms" };
		SWTBotTreeItem waveformNode = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);
		waveformNode.select().contextMenu("Release").click();

		// Make sure the local copy is gone (including the possibility of one that say "Loading...")
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox" }, WAVEFORM_NAME);
		List<String> sandboxNodes = ScaExplorerTestUtils.showScaExplorerView(bot).tree().getTreeItem("Sandbox").getNodes();
		Assert.assertEquals("Too many nodes under the sandbox", 3, sandboxNodes.size());
	}

	private void showLocalReferences(boolean show) {
		SWTBotView view = bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
		view.setFocus();
		view.viewMenu().menu("Customize View...").click();

		SWTBotShell shell = bot.shell("Available Customizations");
		SWTBotTableItem hideLocalItem = shell.bot().table().getTableItem("Hide Local References");
		if (show) {
			hideLocalItem.uncheck();
		} else {
			hideLocalItem.check();
		}
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}
}
