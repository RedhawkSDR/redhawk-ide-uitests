/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.scd;

import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

/**
 * IDE-980. Ensures that if a service implements an interface that derives from CF/PortSupplier then the button to add a
 * port in the component editor will be enabled.
 */
public abstract class AbstractServiceAddPortTest extends UITest {

	static final String PORTS_TAB_NAME = "Ports";

	protected abstract SWTBotEditor openEditor(String projectName);

	/**
	 * Tests that the add button is enabled for a service project with the CF/PortSupplier IDL.
	 */
	@Test
	public void testAddForPortSupplier() {
		String projectName = "testAddForPortSupplier";
		ServiceUtils.createServiceProject(bot, projectName, "IDL:CF/PortSupplier:1.0", "C++");
		bot.editorByTitle(projectName).close();

		SWTBotEditor editor = openEditor(projectName);

		SWTBot editorBot = editor.bot();
		editorBot.cTabItem(PORTS_TAB_NAME).activate();
		Assert.assertTrue("Add port button should be enabled", editorBot.button("Add").isEnabled());
	}

	/**
	 * Tests that the add button is enabled for a service project with an IDL type that inherits from CF/PortSupplier.
	 */
	@Test
	public void testAddForPortSupplierChild() {
		String projectName = "testAddForPortSupplierChild";
		ServiceUtils.createServiceProject(bot, projectName, "IDL:CF/Device:1.0", "Python");
		bot.editorByTitle(projectName).close();

		SWTBotEditor editor = openEditor(projectName);

		SWTBot editorBot = editor.bot();
		editorBot.cTabItem(PORTS_TAB_NAME).activate();
		Assert.assertTrue("Add port button should be enabled", editorBot.button("Add").isEnabled());
	}

	/**
	 * Tests that the add button is disabled for a service project with an IDL type that does not inherit from
	 * CF/PortSupplier.
	 * IDE-1272
	 */
	@Test
	public void testAddForNonPortSupplier() {
		String projectName = "testAddForNonPortSupplier";
		ServiceUtils.createServiceProject(bot, projectName, "IDL:CF/LifeCycle:1.0", "Java");
		bot.editorByTitle(projectName).close();

		SWTBotEditor editor = openEditor(projectName);

		SWTBot editorBot = editor.bot();
		try {
			editorBot.cTabItem(PORTS_TAB_NAME);
		} catch (WidgetNotFoundException e) {
			// PASS - the port tab should not be displayed
			return;
		}

		Assert.fail("Port tab should not be visible for Service projects that don't inherit from CF/PortSupplier");
	}

}
