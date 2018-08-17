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

import org.eclipse.debug.core.DebugException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class AdvancedRuntimeProperties extends UIRuntimeTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private static final String SIG_GEN_IMPL = "python";
	private static final String SIG_GEN_PATH = "components/rh/SigGen/SigGen.spd.xml";

	/**
	 * IDE-1448 Test that the profile name for a component launched in the sandbox is correct
	 */
	@Test
	public void profileName() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, SIG_GEN_IMPL);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SIG_GEN_1).select();

		SWTBotTree tree = ViewUtils.selectPropertiesTab(bot, "Advanced").tree();
		String profileName = null;
		for (SWTBotTreeItem treeItem : tree.getAllItems()) {
			if ("Profile".equals(treeItem.cell(0))) {
				profileName = treeItem.cell(1);
				break;
			}
		}
		Assert.assertEquals(IdeSdrPreferences.getTargetSdrDomPath().append(SIG_GEN_PATH).toString(), profileName);
	}

	@After
	public void cleanup() throws DebugException {
		StandardTestActions.cleanUpLaunches();
	}
}
