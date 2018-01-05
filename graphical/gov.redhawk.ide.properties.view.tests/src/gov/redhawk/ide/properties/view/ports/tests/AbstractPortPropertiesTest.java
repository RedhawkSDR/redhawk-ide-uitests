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
package gov.redhawk.ide.properties.view.ports.tests;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.ViewUtils;

/**
 * IDE-1050 Properties view for selected port in diagram
 * IDE-1172 Display description of port
 * IDE-2150 IDL tree missing for DCD diagrams
 */
public abstract class AbstractPortPropertiesTest extends UITest {

	/**
	 * Method should take necessary steps to:
	 * - create a diagram, add the test object
	 * - select the provides port of the test object
	 */
	protected abstract PortDescription prepareProvidesPort();

	/**
	 * Method should take necessary steps to:
	 * - create a diagram, add the test object
	 * - select the uses port of the test object
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
}
