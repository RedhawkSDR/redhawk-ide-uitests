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

import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportProperty;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportType;
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
		details(portDesc, "in <provides>");
	}

	@Test
	public void usesPortDetails() {
		PortDescription portDesc = prepareUsesPort();
		details(portDesc, "out <uses>");
	}

	private void details(PortDescription portDesc, String inout) {
		SWTBot propViewBot = ViewUtils.selectPropertiesTab(bot, "Port Details");
		String summaryText = propViewBot.text().getText();
		Assert.assertTrue(summaryText.contains("Direction: " + inout));
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
		advanced(new TransportTypeAndProps(TransportType.SHMIPC, new TransportProperty("hostname", null)));
	}

	@Test
	public void usesPortAdvanced() {
		prepareUsesPortAdvanced();
		advanced(new TransportTypeAndProps(TransportType.SHMIPC, new TransportProperty("hostname", null)));
	}

	protected void advanced(TransportTypeAndProps... transports) {
		SWTBot propViewBot = ViewUtils.selectPropertiesTab(bot, "Advanced");
		SWTBotTree tree = propViewBot.tree();

		SWTBotTreeItem supportedTransports = tree.getTreeItem("Supported Transports");
		String transportsString = supportedTransports.cell(1);
		for (TransportTypeAndProps transport : transports) {
			Assert.assertTrue("Looking for " + transport.getTransportType().getText(), transportsString.contains(transport.getTransportType().getText()));
		}

		supportedTransports.click(1);
		new SWTBot(tree.widget).button("...").click();
		SWTBotShell shell = bot.shell("Transport Details");

		try {
			Assert.assertEquals(transports.length, shell.bot().comboBoxWithLabel("Transport:").itemCount());

			// For multiple transports, repeat looking at  them to test a bug where the props disappeared
			int repeats = (transports.length == 1) ? 1 : 2;
			for (int i = 0; i < repeats; i++) {
				for (TransportTypeAndProps transport : transports) {
					shell.bot().comboBoxWithLabel("Transport:").setSelection(transport.getTransportType().getText());
					Assert.assertEquals("Incorrect number of props for transport " + transport.getTransportType().getText(), transport.getProperties().size(),
						shell.bot().tree().rowCount());
					for (TransportProperty prop : transport.getProperties()) {
						SWTBotTreeItem treeItem = shell.bot().tree().getTreeItem(prop.getPropName());
						if (prop.getPropValue() != null) {
							Assert.assertEquals(prop.getPropValue(), treeItem.cell(1));
						}
					}
				}
			}
		} finally {
			shell.bot().button("Close").click();
			bot.waitUntil(Conditions.shellCloses(shell));
		}
	}
}
