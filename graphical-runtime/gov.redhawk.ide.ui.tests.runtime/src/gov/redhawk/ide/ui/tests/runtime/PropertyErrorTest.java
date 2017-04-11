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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * IDE-1581
 * Tests error scenarios with setting/changing properties.
 */
public class PropertyErrorTest extends UIRuntimeTest {

	private static final String COMP = "errorComponent";
	private static final String COMP_INST = "python";
	private static final String COMP_1 = "errorComponent_1";

	private static final String PROP_ENABLE = "enableErrors";
	private static final String PROP_INVALID = "invalidConfigProp";
	private static final String PROP_PARTIAL = "partialConfigProp";

	@Test
	public void changePropertyError() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, COMP, COMP_INST);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMP_1);
		ConsoleUtils.disableAutoShowConsole(bot);
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMP_1).select();

		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem treeItem = propTree.getTreeItem(PROP_ENABLE);
		StandardTestActions.selectComboListFromCell(bot, treeItem, 1, "true");

		final boolean oldAutomatedMode = ErrorDialog.AUTOMATED_MODE;
		try {
			// Must be switched out of automated mode to allow error dialogs to pop
			ErrorDialog.AUTOMATED_MODE = false;

			treeItem = propTree.getTreeItem(PROP_INVALID);
			StandardTestActions.writeToCell(bot, treeItem, 1, "configure attempt");
			checkShellAndClose("CF.PropertySetPackage.InvalidConfiguration: Error from configure method. Properties: " + PROP_INVALID);

			treeItem = propTree.getTreeItem(PROP_PARTIAL);
			StandardTestActions.writeToCell(bot, treeItem, 1, "configure attempt");
			checkShellAndClose("CF.PropertySetPackage.PartialConfiguration. Properties: " + PROP_PARTIAL);
		} finally {
			ErrorDialog.AUTOMATED_MODE = oldAutomatedMode;
		}
	}

	private void checkShellAndClose(String message) {
		SWTBotShell shell = bot.shell("Problem Occurred");
		String text = shell.bot().label(2).getText();
		Assert.assertEquals(message, text);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMP_1);
		super.after();
	}
}
