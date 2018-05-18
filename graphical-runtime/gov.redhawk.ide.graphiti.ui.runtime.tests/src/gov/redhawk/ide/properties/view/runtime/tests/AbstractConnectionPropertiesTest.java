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
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;

public abstract class AbstractConnectionPropertiesTest extends UIRuntimeTest {

	protected enum TransportType {
		SHMIPC("shmipc"),
		CORBA("CORBA");

		private String text;

		TransportType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	};

	/**
	 * This method should launch anything necessary and select the connection
	 * @return What type of transport is expected for the given connection
	 */
	protected abstract TransportType prepareConnection();

	@Test
	public void connectionAdvanced() {
		TransportType connType = prepareConnection();
		SWTBot propViewBot = ViewUtils.selectPropertiesTab(bot, "Advanced");

		// Check values of the various advanced properties
		String alive = propViewBot.tree().getTreeItem("Alive").cell(1);
		Assert.assertEquals("true", alive);
		String id = propViewBot.tree().getTreeItem("Id").cell(1);
		Assert.assertTrue(id != null && !id.isEmpty());
		String transportType = propViewBot.tree().getTreeItem("Transport Type").cell(1);
		Assert.assertEquals(connType.getText(), transportType);

		// Check viewing the transport info
		propViewBot.tree().getTreeItem("Transport Info").click(0);
		propViewBot.button("...").click();
		SWTBotShell shell = bot.shell("Transport Details");
		String transport = shell.bot().textWithLabel("Transport:").getText();
		Assert.assertEquals(connType.getText(), transport);
		shell.bot().button("Close").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

}
