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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import java.util.Arrays;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ExternalPortsTest extends UIRuntimeTest {

	private static final String[] WAVEFORM_PARENT_PATH = { "Sandbox" };
	private static final String WAVEFORM = "ExtPortsWF";

	private static final String EXTERNAL_OUT = "dataFloat_sg_out";
	private static final String EXTERNAL_IN = "dataFloat_hl_in";

	private RHSWTGefBot gefBot;
	private String waveformFullName;

	@Before
	public void beforeTest() {
		gefBot = new RHSWTGefBot();

		// Launch Local Waveform From Target SDR
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, WAVEFORM_PARENT_PATH, WAVEFORM);

		// Open Local Waveform Diagram
		waveformFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, WAVEFORM);
	}

	@Test
	public void connectExternalPorts() {
		// Connect via the uses port
		createConnection(EXTERNAL_OUT, "Source", EXTERNAL_IN);

		// Connect via the provides port
		createConnection(EXTERNAL_IN, "Target", EXTERNAL_OUT);
	}

	private void createConnection(final String portOneName, final String portOneGroup, final String portTwoName) {
		// Open connect wizard
		SWTBotTreeItem waveformTreeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, waveformFullName);
		SWTBotTreeItem outPort = waveformTreeItem.expandNode("External Ports", portOneName);
		outPort.contextMenu("Connect").click();
		final SWTBotShell connectShell = bot.shell("Connect");
		final SWTBot connectBot = connectShell.bot();

		// Assert that external port is visible
		StandardTestActions.waitForTreeItemToAppear(connectBot, connectBot.treeInGroup(portOneGroup), Arrays.asList(portOneName));

		// Find and connect to the other external port
		final String portTwoGroup = portOneGroup.equals("Source") ? "Target" : "Source";
		final String[] parentPath = { "Sandbox", waveformFullName, "External Ports" };

		// Wait until the waveform fully displays and we can select the port
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return parentPath + " did not display entirely in Connect wizard";
			}

			@Override
			public boolean test() throws Exception {
				// We collapse/expand everything at each test. SWTBot's quick expansion can cause issues with the
				// tree view's display.
				SWTBotTree targetTree = connectBot.treeInGroup(portTwoGroup);
				targetTree.collapseNode(parentPath[0]);
				SWTBotTreeItem targetParentTreeItem = connectBot.treeInGroup(portTwoGroup).expandNode(parentPath);
				targetParentTreeItem.select(portTwoName);
				return true;
			}
		});
		String connectionName = "external_port_connection";
		connectBot.textWithLabel("Connection ID:").setText(connectionName);
		connectBot.button("Finish").click();

		// Make sure connection was made and clean up
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(gefBot, new String[] { "Sandbox" }, waveformFullName, "External Ports", EXTERNAL_OUT,
			connectionName);
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, WAVEFORM_PARENT_PATH, waveformFullName, connectionName, "External Ports", EXTERNAL_OUT);
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(gefBot, new String[] { "Sandbox" }, waveformFullName, "External Ports", EXTERNAL_OUT,
			connectionName);
	}

	@After
	public void afterTest() {
		// Release the waveform if it exists
		try {
			ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM);
		} catch (WidgetNotFoundException ex) {
			return;
		}
		ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, WAVEFORM);
	}
}
