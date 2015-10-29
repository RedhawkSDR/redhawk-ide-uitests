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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class ImportPropertyTest extends AbstractPropertyTabTest {

	@Test
	public void testAddPropertyFromBrowse() {
		bot.button("Browse...").click();
		bot.text().setText("frequency");
		bot.tree().getTreeItem("Target SDR").select();
		bot.tree().getTreeItem("Target SDR").getNode("Components").getNode("rh.SigGen").getNode("frequency").select();
		bot.button("Finish").click();
		assertFormValid();
		editor.bot().tree().getTreeItem("frequency").select();
		Assert.assertEquals("double (64-bit)", bot.comboBoxWithLabel("Type*:").getText());
	}

	@Test
	public void testBug1295_BrowseWizardValidation() {
		bot.button("Browse...").click();
		SWTBot dialogBot = bot.shell("Browse Properties").bot();
		
		SWTBotTreeItem item = dialogBot.tree().getTreeItem("Target SDR").select();
		Assert.assertFalse("Finish button should not be enabled.", dialogBot.button("Finish").isEnabled());

		item = item.expand().getNode("Components").select();
		Assert.assertFalse("Finish button should not be enabled.", dialogBot.button("Finish").isEnabled());

		item = item.expand().getNode("rh.SigGen").select();
		Assert.assertFalse("Finish button should not be enabled.", dialogBot.button("Finish").isEnabled());

		item = item.expand().getNode("frequency").select();
		Assert.assertTrue("Finish button should be enabled.", dialogBot.button("Finish").isEnabled());

		dialogBot.button("Cancel").click();
		assertFormValid();
		Assert.assertFalse("Properties should be empty.", editor.bot().tree().hasItems());
	}

}
