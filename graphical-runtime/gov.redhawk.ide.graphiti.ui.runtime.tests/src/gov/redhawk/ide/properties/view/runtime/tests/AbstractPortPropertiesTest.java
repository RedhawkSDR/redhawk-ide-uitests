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
package gov.redhawk.ide.properties.view.runtime.tests;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;

/**
 * IDE-1050 Properties view for selected port in diagram
 * IDE-1172 Display description of port
 * IDE-1520 Add IDL tree to port details for explorer view
 * IDE-2150 IDL tree missing for DCD diagrams
 */
public abstract class AbstractPortPropertiesTest extends UIRuntimeTest {

	@BeforeClass
	public static void disableAutoShowConsole() {
		ConsoleUtils.disableAutoShowConsole();
	}

	/**
	 * Method should take necessary steps to:
	 * - launch the test object,
	 * - select the provides port within the explorer view / diagram of the editor
	 */
	protected abstract PortDescription prepareProvidesPort();

	/**
	 * Method should take necessary steps to:
	 * - launch the test object,
	 * - select the uses port within the explorer view / diagram of the editor
	 */
	protected abstract PortDescription prepareUsesPort();

	/**
	 * Method should take necessary steps to:
	 * - launch the test object,
	 * - select the provides port within the explorer view / diagram of the editor
	 */
	protected abstract void prepareProvidesPortAdvanced();

	/**
	 * Method should take necessary steps to:
	 * - launch the test object,
	 * - select the uses port within the explorer view / diagram of the editor
	 */
	protected abstract void prepareUsesPortAdvanced();

	@Test
	public void providesPortDetails() {
		PortDescription portDesc = prepareProvidesPort();

		SWTBot propViewBot = ViewUtils.selectPropertiesTab(bot, "Port Details");
		String summaryText = propViewBot.text().getText();
		Assert.assertTrue(summaryText.contains("Direction: in <provides>"));
		Assert.assertTrue(summaryText.contains(portDesc.getType()));
		Assert.assertTrue(summaryText.contains("Type: data"));

		String descriptionText = propViewBot.text(1).getText();
		Assert.assertEquals(portDesc.getDescription(), descriptionText);

		// IDL tree
		String idlType = propViewBot.label(1).getText();
		Assert.assertEquals(portDesc.getType(), idlType);
		String className = propViewBot.tree().getAllItems()[0].getText();
		Assert.assertEquals(portDesc.getType().split("/")[1].split(":")[0], className);
	}

	@Test
	public void usesPortDetails() {
		PortDescription portDesc = prepareUsesPort();

		SWTBot propViewBot = ViewUtils.selectPropertiesTab(bot, "Port Details");
		String summaryText = propViewBot.text().getText();
		Assert.assertTrue(summaryText.contains("Direction: out <uses>"));
		Assert.assertTrue(summaryText.contains(portDesc.getType()));
		Assert.assertTrue(summaryText.contains("Type: data"));

		String descriptionText = propViewBot.text(1).getText();
		Assert.assertEquals(portDesc.getDescription(), descriptionText);

		// IDL tree
		String idlType = propViewBot.label(1).getText();
		Assert.assertEquals(portDesc.getType(), idlType);
		String className = propViewBot.tree().getAllItems()[0].getText();
		Assert.assertEquals(portDesc.getType().split("/")[1].split(":")[0], className);
	}

	@Test
	public void providesPortAdvanced() {
		prepareProvidesPortAdvanced();
		advanced();
	}

	@Test
	public void usesPortAdvanced() {
		prepareUsesPortAdvanced();
		advanced();
	}

	private void advanced() {
		SWTBot propViewBot = ViewUtils.selectPropertiesTab(bot, "Advanced");
		SWTBotTree tree = propViewBot.tree();

		SWTBotTreeItem supportedTransports = null;
		for (SWTBotTreeItem treeItem : tree.getAllItems()) {
			if ("Supported Transports".equals(treeItem.cell(0))) {
				supportedTransports = treeItem;
				break;
			}
		}
		Assert.assertNotNull(supportedTransports);
		Assert.assertEquals("shmipc", supportedTransports.cell(1));
		supportedTransports.click(1);
		new SWTBot(tree.widget).button("...").click();

		SWTBotShell shell = bot.shell("Transport Details");
		Assert.assertEquals("shmipc", shell.bot().comboBoxWithLabel("Transport:").getText());
		Assert.assertEquals("hostname", shell.bot().tree().cell(0, 0));
		shell.bot().button("Close").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}
}
