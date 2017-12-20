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

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MessagePortEventTest extends UIRuntimeTest {

	/**
	 * IDE-1007 - Test listening to messages being sent from a components port
	 */
	@Test
	public void listenToMsgPort() {
		final String componentName = "MessagePortTestComp";
		final String implId = "python";
		final String portName = "message_out";

		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, componentName, implId);
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, componentName + "_1");
		SWTBotTreeItem usesPort = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", componentName + "_1" },
			portName);

		component.contextMenu("Start").click();
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, componentName + "_1");
		usesPort.contextMenu("Listen to Message Events").click();

		SWTBotView eventView = bot.viewByTitle(portName);
		SWTBotTree tree = eventView.bot().tree();

		// Wait to make sure a couple of messages come in
		bot.waitWhile(Conditions.treeHasRows(tree, 2));
	}
}
