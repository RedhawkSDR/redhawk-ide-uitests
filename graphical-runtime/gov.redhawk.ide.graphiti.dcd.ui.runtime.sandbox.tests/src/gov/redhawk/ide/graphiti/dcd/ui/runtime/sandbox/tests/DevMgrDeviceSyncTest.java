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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DevMgrDeviceSyncTest extends AbstractDevMgrSandboxSyncTest {

	@Override
	protected String getType() {
		return "device";
	}

	@Override
	protected String getResourceId() {
		return DEVICE_STUB;
	}

	@Override
	protected String getResourceLaunchId() {
		return DEVICE_STUB_1;
	}

	@Override
	protected String getSecondResourceLaunchId() {
		return DEVICE_STUB_2;
	}

	/**
	 * IDE-1119
	 * Adds, then releases a device via dev manager diagram.
	 * Verify it's no longer present in the explorer view or diagram.
	 */
	@Test
	public void addReleaseDeviceInDiagram() {
		// Launch device
		launchResourceInDiagram(DEVICE_STUB, DEVICE_STUB_1);

		// Release
		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(DEVICE_STUB));

		// wait until device not present in REDHAWK Explorer & Diagram
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
		Assert.assertNull(editor.getEditPart(DEVICE_STUB));
	}

	/**
	 * IDE-1119
	 * Adds, then removes a device connection via dev manager diagram.
	 * Verify it's no longer present in the explorer view or diagram.
	 */
	@Test
	public void addRemoveDeviceConnectionInDiagram() {
		// Add a device stub to diagram from palette, which will connect to itself for this test
		launchResourceInDiagram(DEVICE_STUB, DEVICE_STUB_1);

		// Draw connection
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, DEVICE_STUB_DOUBLE_OUT_PORT);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_1, DEVICE_STUB_DOUBLE_IN_PORT);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(bot, SANDBOX_PATH, DEVICE_MANAGER, DEVICE_STUB_1, "dataDouble_out", "connection_1");

		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(bot, SANDBOX_PATH, DEVICE_MANAGER, DEVICE_STUB_1, "dataDouble_out", "connection_1");
	}

	/**
	 * IDE-1119
	 * Adds, then removes device connections via REDHAWK Explorer view.
	 * Verify they're no longer present in the diagram.
	 */
	@Test
	public void addRemoveDeviceConnectionInExplorerView() {
		// Launch two devices from TargetSDR
		launchResourceInExplorer(bot, DEVICE_STUB, DEVICE_STUB_1);
		launchResourceInExplorer(bot, DEVICE_STUB, DEVICE_STUB_2);

		// create connection between devices via REDHAWK Explorer
		ScaExplorerTestUtils.connectPortsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, "connection_1", DEVICE_STUB_1, "dataDouble_out", DEVICE_STUB_2,
			"dataDouble_in");

		// verify connection exists in diagram
		DiagramTestUtils.waitUntilConnectionDisplaysInDiagram(bot, editor, DEVICE_STUB_2);

		// disconnect connection_1 via REDHAWK Explorer
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, SANDBOX_PATH, DEVICE_MANAGER, "connection_1", DEVICE_STUB_1, "dataDouble_out");

		// verify connection does NOT exist in diagram
		DiagramTestUtils.waitUntilConnectionDisappearsInDiagram(bot, editor, DEVICE_STUB_2);
	}

	/**
	 * IDE-1119
	 * Adds devices, starts/stops them in the diagram.
	 * Verifies the changes in the explorer view and diagram.
	 */
	@Test
	public void startStopDevicesInDiagram() {
		// Add two devices to diagram from palette
		// -- GPP
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_1, ComponentState.STARTED);
		// -- DeviceStub
		launchResourceInDiagram(DEVICE_STUB, DEVICE_STUB_1);

		// verify GPP stopped
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);

		// start GPP
		DiagramTestUtils.startComponentFromDiagram(editor, GPP_1);

		// verify GPP started but DeviceStub did not
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_1, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_STUB_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// start DeviceStub
		DiagramTestUtils.startComponentFromDiagram(editor, DEVICE_STUB_1);

		// verify DeviceStub started
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_STUB_1, ComponentState.STARTED);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// stop GPP
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1);

		// verify GPP stopped, DeviceStub started
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// stop DeviceStub
		DiagramTestUtils.stopComponentFromDiagram(editor, DEVICE_STUB_1);

		// verify DeviceStub stopped
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_STUB_1, ComponentState.STOPPED);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// start both devices
		DiagramTestUtils.startComponentFromDiagram(editor, GPP_1);
		DiagramTestUtils.startComponentFromDiagram(editor, DEVICE_STUB_1);

		// verify both started
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

	}

	/**
	 * IDE-1119
	 * Adds devices, starts/stops them from REDHAWK Explorer.
	 * Verifies the changes in the explorer view and diagram.
	 */
	@Test
	public void startStopDevicesInExplorerView() {
		// Launch two devices from TargetSDR
		// -- GPP
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, GPP, "cpp");
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, GPP);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_1, ComponentState.STARTED);
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1);
		// -- DeviceStub
		launchResourceInExplorer(bot, DEVICE_STUB, DEVICE_STUB_1);

		// start device stub REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// verify device stub started but GPP did not
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_STUB, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP, ComponentState.STOPPED);

		// start GPP from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);

		// verify GPP started in explorer and diagram
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP, ComponentState.STARTED);

		// stop device stub from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// verify device stub stopped, GPP started
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_STUB, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP, ComponentState.STARTED);

		// stop GPP from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);

		// verify GPP stopped
		DiagramTestUtils.waitForComponentState(bot, editor, GPP, ComponentState.STOPPED);

		// start both devices
		ScaExplorerTestUtils.startResourceInExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
		ScaExplorerTestUtils.startResourceInExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);

		// verify both started
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_STUB, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, GPP, ComponentState.STARTED);
	}
}
