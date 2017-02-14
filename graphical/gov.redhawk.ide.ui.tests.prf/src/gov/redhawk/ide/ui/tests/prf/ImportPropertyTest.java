/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.ui.tests.prf;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;

public class ImportPropertyTest extends AbstractPropertyTabTest {

	@Test
	public void testAddPropertyFromBrowse() {
		editorBot.button("Browse...").click();
		SWTBot dialogBot = bot.shell("Browse Properties").bot();

		dialogBot.text().setText("frequency");
		List<String> path = Arrays.asList("Target SDR", "Components", "rh.SigGen", "frequency");
		StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), path).select();
		dialogBot.button("Finish").click();

		assertFormValid();
		editor.bot().tree().getTreeItem("frequency").select();
		Assert.assertEquals("double (64-bit)", bot.comboBoxWithLabel("Type*:").getText());
	}

	@Test
	public void testBug1295_BrowseWizardValidation() {
		editorBot.button("Browse...").click();
		SWTBot dialogBot = bot.shell("Browse Properties").bot();

		List<String> path = Arrays.asList("Target SDR", "Components", "rh.SigGen", "frequency");
		for (int i = 1; i < path.size(); i++) {
			SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), path.subList(0, i));
			treeItem.select();
			Assert.assertFalse("Finish button should not be enabled.", dialogBot.button("Finish").isEnabled());
		}

		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), path);
		treeItem.select();
		Assert.assertTrue("Finish button should be enabled.", dialogBot.button("Finish").isEnabled());

		dialogBot.button("Cancel").click();
		assertFormValid();
		Assert.assertFalse("Properties should be empty.", editor.bot().tree().hasItems());
	}

}
