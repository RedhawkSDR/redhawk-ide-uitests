/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Test;

public class DevManagerDomainSyncTest extends AbstractGraphitiDomainNodeRuntimeTest {

	/**
	 * IDE-1119
	 * Test starting and stopping devices from the node explorer diagram. Verify state in node explorer diagram and
	 * explorer view.
	 */
	@Test
	public void startStopDevicesFromChalkboardDiagram() {
		launchDomainAndDevMgr(DEVICE_MANAGER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEVICE_MANAGER);

		// Stop GPP
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_LOCALHOST);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_LOCALHOST, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, devMgrPath, GPP_LOCALHOST);

		// Start GPP
		DiagramTestUtils.startComponentFromDiagram(editor, GPP_LOCALHOST);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_LOCALHOST, ComponentState.STARTED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, devMgrPath, GPP_LOCALHOST);

		// Stop GPP
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_LOCALHOST);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_LOCALHOST, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, devMgrPath, GPP_LOCALHOST);
	}

	/**
	 * IDE-1119
	 * Test starting and stopping devices from the explorer view. Verify state in node explorer diagram and explorer
	 * view.
	 */
	@Test
	public void startStopDevicesFromScaExplorer() {
		launchDomainAndDevMgr(DEVICE_MANAGER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEVICE_MANAGER);

		// Stop GPP
		ScaExplorerTestUtils.stopResourceInExplorer(bot, devMgrPath, GPP_LOCALHOST);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, devMgrPath, GPP_LOCALHOST);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_LOCALHOST, ComponentState.STOPPED);

		// Start GPP
		ScaExplorerTestUtils.startResourceInExplorer(bot, devMgrPath, GPP_LOCALHOST);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, devMgrPath, GPP_LOCALHOST);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_LOCALHOST, ComponentState.STARTED);

		// Stop GPP
		ScaExplorerTestUtils.stopResourceInExplorer(bot, devMgrPath, GPP_LOCALHOST);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, devMgrPath, GPP_LOCALHOST);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_LOCALHOST, ComponentState.STOPPED);
	}
}
