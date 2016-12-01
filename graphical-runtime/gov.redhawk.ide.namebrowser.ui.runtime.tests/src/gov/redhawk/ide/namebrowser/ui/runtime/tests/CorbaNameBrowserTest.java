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
package gov.redhawk.ide.namebrowser.ui.runtime.tests;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class CorbaNameBrowserTest extends UIRuntimeTest {

	private static final String DOMAIN = "SWTBOT_SAD_TEST_" + (int) (1000.0 * Math.random());
	private static final String[] DOMAIN_WAVEFORM_PARENT_PATH = { DOMAIN, "Waveforms" };
	private static final String DEVICE_MANAGER = "DevMgr_localhost";

	@Test
	public void namespacedWaveformTest() {
		final String waveformName = "a.b.c.d.waveform";

		// Launch the domain
		ScaExplorerTestUtils.launchDomain(bot, DOMAIN, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);

		// Launch the namespaced waveform
		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, waveformName);
		String fullName = getWaveformFullName(waveformName);

		// Open the namebrowser and check for the waveform
		bot.menu("Window").menu("Show View").menu("CORBA Name Browser").click();
		SWTBotView view = bot.viewById("gov.redhawk.ui.views.namebrowserview");
		view.bot().buttonWithTooltip("Connect to the specified host").click();
		String[] path = { "127.0.0.1", DOMAIN, fullName + "_1" };
		SWTBotTreeItem node = StandardTestActions.waitForTreeItemToAppear(bot, view.bot().tree(), Arrays.asList(path));
		Assert.assertTrue("Waveform children not displaying", node.getItems().length == 2);
	}

	private String getWaveformFullName(String waveformName) {
		return ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveformName);
	}
}
