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
package gov.redhawk.ide.ui.tests.runtime.events;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MessagePortEventTest extends UIRuntimeTest {

	private static final String COMPONENT_NAME = "MessagePortTestComp";
	private static final String IMPL_ID = "python";
	private static final String PORT_NAME = "message_out";

	@Before
	public void before() throws Exception {
		super.before();
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, COMPONENT_NAME, IMPL_ID);
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMPONENT_NAME + "_1");
		component.select();
		component.contextMenu("Start").click();
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMPONENT_NAME + "_1");
	}

	@After
	public void after() throws CoreException {
		bot.viewByTitle(PORT_NAME).close();
		super.after();
	}

	/**
	 * IDE-1007 - Test listening to messages being sent from a component's port
	 */
	@Test
	public void standardListen() {
		SWTBotTreeItem usesPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", COMPONENT_NAME + "_1" },
			PORT_NAME);
		usesPort.select();
		usesPort.contextMenu("Listen to Message Events").click();

		SWTBotView eventView = bot.viewByTitle(PORT_NAME);
		SWTBotTree tree = eventView.bot().tree();

		// Wait to make sure a messages comes in
		bot.waitWhile(Conditions.treeHasRows(tree, 0));
	}

	/**
	 * IDE-2194 - Test listening to messages being sent from a component's port using a specific connection ID
	 */
	@Test
	public void advancedListen() {
		SWTBotTreeItem usesPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", COMPONENT_NAME + "_1" },
			PORT_NAME);
		usesPort.select();
		usesPort.contextMenu("Listen to Message Events...").click();

		SWTBotShell dialogShell = bot.shell("Specify Connection ID");
		dialogShell.bot().text().typeText("abc");
		dialogShell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(dialogShell));

		SWTBotView eventView = bot.viewByTitle(PORT_NAME);
		SWTBotTree tree = eventView.bot().tree();

		// Wait to make sure a messages comes in
		bot.waitWhile(Conditions.treeHasRows(tree, 0));

		// Ensure the conneciton ID is correct
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", COMPONENT_NAME + "_1", PORT_NAME }, "abc");
	}
}
