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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MessagePortEventTest extends UIRuntimeTest {

	private static final String PORT_NAME = "message_out";

	@After
	public void after() throws CoreException {
		bot.viewByTitle(PORT_NAME).close();
		super.after();
	}

	/**
	 * IDE-1007 - Test listening to messages being sent from a components port
	 */
	@Test
	public void listenToMsgPort() {
		final String componentName = "MessagePortTestComp";
		final String implId = "python";

		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, componentName, implId);
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, componentName + "_1");
		SWTBotTreeItem usesPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", componentName + "_1" },
			PORT_NAME);

		component.contextMenu("Start").click();
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, componentName + "_1");
		usesPort.contextMenu("Listen to Message Events").click();

		SWTBotView eventView = bot.viewByTitle(PORT_NAME);
		SWTBotTree tree = eventView.bot().tree();

		// Wait to make sure a messages comes in
		bot.waitWhile(Conditions.treeHasRows(tree, 0));
	}
}
