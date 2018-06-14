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

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportProperty;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;

public abstract class AbstractConnectionPropertiesTest extends UIRuntimeTest {

	/**
	 * This method should launch anything necessary and select the connection.
	 */
	protected abstract void prepareConnection();

	/**
	 * @return What type of transport is expected for the given connection
	 */
	protected abstract TransportTypeAndProps getConnectionDetails();

	/**
	 * Tests the advanced properties of a connection.
	 */
	@Test
	public void connectionAdvanced() {
		prepareConnection();
		TransportTypeAndProps transportDetails = getConnectionDetails();

		common(transportDetails);
	}

	protected void common(TransportTypeAndProps transportDetails) {
		// Check values of the various advanced properties
		SWTBot propViewBot = ViewUtils.selectPropertiesTab(bot, "Advanced");
		String alive = propViewBot.tree().getTreeItem("Alive").cell(1);
		Assert.assertEquals("true", alive);
		String id = propViewBot.tree().getTreeItem("Id").cell(1);
		Assert.assertTrue(id != null && !id.isEmpty());
		String transportType = propViewBot.tree().getTreeItem("Transport Type").cell(1);
		Assert.assertEquals(transportDetails.getTransportType().getText(), transportType);

		// Check viewing the transport info
		propViewBot.tree().getTreeItem("Transport Info").click(0);
		propViewBot.button("...").click();
		SWTBotShell shell = bot.shell("Transport Details");
		try {
			String transport = shell.bot().textWithLabel("Transport:").getText();
			Assert.assertEquals(transportDetails.getTransportType().getText(), transport);
			Assert.assertEquals(transportDetails.getProperties().size(), shell.bot().tree().rowCount());
			for (TransportProperty prop : transportDetails.getProperties()) {
				SWTBotTreeItem treeItem = shell.bot().tree().getTreeItem(prop.getPropName());
				if (prop.getPropValue() != null) {
					Assert.assertEquals(prop.getPropValue(), treeItem.cell(1));
				}
			}
		} finally {
			shell.bot().button("Close").click();
			bot.waitUntil(Conditions.shellCloses(shell));
		}
	}

}
