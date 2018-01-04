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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;

/**
 * IDE-1050 Properties view for selected port in diagram
 * IDE-1172 Display description of port
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
	}
}
