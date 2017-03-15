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
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DevManagerSandboxSyncTest extends AbstractDeviceManagerSandboxTest {

	private RHBotGefEditor editor;

	/**
	 * IDE-1119
	 * Adds, then removes a device via dev manager chalkboard diagram.
	 * Verify its no longer present in REDHAWK Explorer Sandbox or Diagram
	 */
	@Test
	public void addRemoveDeviceInDeviceManagerDiagram() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
		bot.sleep(500);
		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(DEVICE_STUB));

		// wait until device not present in REDHAWK Explorer Chalkboard & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		Assert.assertNull(editor.getEditPart(DEVICE_STUB));
	}

	/**
	 * IDE-1119
	 * Adds, then terminates a device via dev manager chalkboard diagram.
	 * Verify it's no longer present in REDHAWK Explorer Sandbox or Diagram
	 */
	@Test
	public void addTerminateDeviceInChalkboardDiagram() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(DEVICE_STUB));

		// wait until device not present in REDHAWK Explorer Chalkboard & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		Assert.assertNull(editor.getEditPart(DEVICE_STUB));
	}
	
	/**
	 * IDE-1037
	 * Adds, then terminates a service via dev manager chalkboard diagram.
	 * Verify it's no longer present in REDHAWK Explorer Sandbox or Diagram
	 */
	@Test
	public void addTerminateServiceInChalkboardDiagram() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		DiagramTestUtils.addFromPaletteToDiagram(editor, SERVICE_STUB, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SERVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, SERVICE_STUB_1);
		bot.sleep(500);
		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(SERVICE_STUB));

		// wait until service not present in REDHAWK Explorer Chalkboard & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, SERVICE_STUB_1);
		Assert.assertNull(editor.getEditPart(SERVICE_STUB));
	}

	/**
	 * IDE-1119
	 * Adds, then removes a device connection via dev manager chalkboard diagram.
	 * Verify its no longer present in REDHAWK Explorer Chalkboard
	 */
	@Test
	public void addRemoveDeviceConnectionInChalkboardDiagram() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Add a device stub to diagram from palette, which will connect to itself for this test
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// Draw connection
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, DEVICE_STUB_DOUBLE_OUT_PORT);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_1, DEVICE_STUB_DOUBLE_IN_PORT);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1, "dataDouble_out",
			"connection_1");

		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1, "dataDouble_out",
			"connection_1");
	}

	/**
	 * IDE-1119
	 * Adds device, starts/stops them from Chalkboard Diagram and verifies
	 * device in REDHAWK Explorer Dev Manager Chalkboard reflect changes
	 * 
	 */
	@Test
	public void startStopDevicesFromChalkboardDiagram() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Add two devices to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);

		// wait for device to show up in the Redhawk explorer view
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, GPP_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// GPP starts when launched
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_1, ComponentState.STARTED);
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1);

		// verify GPP stopped
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
	 * Adds then terminate a device via the REDHAWK Explorer
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addTerminateDeviceInExplorerView() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch device from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify DeviceStub was added to the diagram and explorer view
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// delete device from REDHAWK Explorer dev manager chalkboard
		ScaExplorerTestUtils.terminate(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// verify DeviceStub device not present in Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, DEVICE_STUB_1);

	}

	/**
	 * IDE-1119
	 * Adds, then removes a device via terminating the REDHAWK Explorer Sandbox Device Manager
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addDevice_TerminateDeviceManager() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch device from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify DeviceStub was added to the diagram and explorer view
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// terminate device manager
		ScaExplorerTestUtils.terminate(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER);

		// verify DeviceStub not present in Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, DEVICE_STUB_1);
	}

	/**
	 * IDE-1119
	 * Adds, then removes a device via calling shutdown on the REDHAWK Explorer Sandbox Device Manager
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addDevice_ShutdownDeviceManager() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch device from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify DeviceStub was added to the diagram and explorer view
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);

		// terminate device manager
		ScaExplorerTestUtils.shutdown(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER);

		// verify DeviceStub not present in Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, DEVICE_STUB_1);
	}

	/**
	 * IDE-1037
	 * Adds then terminate a service via the REDHAWK Explorer
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addTerminateServiceInExplorerView() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch service from TargetSDR
		ScaExplorerTestUtils.launchServiceFromTargetSDR(bot, SERVICE_STUB, "python");

		// verify ServiceStub was added to the diagram and explorer view
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SERVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, SERVICE_STUB_1);

		// delete service from REDHAWK Explorer dev manager chalkboard
		ScaExplorerTestUtils.terminate(bot, SANDBOX_DEVMGR_PATH, SERVICE_STUB_1);

		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, SERVICE_STUB_1);
	}

	/**
	 * IDE-1880
	 * Adds, then removes a service via terminating the REDHAWK Explorer Sandbox Device Manager
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addService_TerminateDeviceManager() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch service from TargetSDR
		ScaExplorerTestUtils.launchServiceFromTargetSDR(bot, SERVICE_STUB, "python");

		// verify ServiceStub was added to the diagram and explorer view
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SERVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, SERVICE_STUB_1);

		// terminate device manager
		ScaExplorerTestUtils.terminate(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER);

		// verify ServiceStub not present in Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, SERVICE_STUB_1);
	}

	/**
	 * IDE-1881
	 * Adds, then removes a service via calling shutdown on the REDHAWK Explorer Sandbox Device Manager
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addService_ShutdownDeviceManager() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch service from TargetSDR
		ScaExplorerTestUtils.launchServiceFromTargetSDR(bot, SERVICE_STUB, "python");

		// verify ServiceStub was added to the diagram and explorer view
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SERVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, SERVICE_STUB_1);

		// terminate device manager
		ScaExplorerTestUtils.shutdown(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER);

		// verify ServiceStub not present in Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, SERVICE_STUB_1);
	}
	
	/**
	 * IDE-1119
	 * Adds, then removes device connections via REDHAWK Explorer view.
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addRemoveDeviceConnectionInScaExplorer() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch two devices from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify devices were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB_2);

		// create connection between devices via REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, DEVICE_STUB_1);
		ScaExplorerTestUtils.connectPortsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, "connection_1", DEVICE_STUB_1, "dataDouble_out", DEVICE_STUB_2,
			"dataDouble_in");

		// verify connection exists in diagram
		DiagramTestUtils.waitUntilConnectionDisplaysInDiagram(bot, editor, DEVICE_STUB_2);

		// disconnect connection_1 via REDHAWK Explorer
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, "connection_1", DEVICE_STUB_1, "dataDouble_out");

		// verify connection does NOT exist in diagram
		DiagramTestUtils.waitUntilConnectionDisappearsInDiagram(bot, editor, DEVICE_STUB_2);
	}

	/**
	 * IDE-1119
	 * Adds devices, starts/stops them from REDHAWK Explorer Dev Manager Chalkboard and verifies
	 * devices in diagram reflect appropriate color changes
	 */
	@Test
	public void startStopDevicesFromRedhawkExplorer() {
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);

		// Launch two devices from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, GPP, "cpp");
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify devices were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, GPP);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB);

		// GPP starts when launched
		DiagramTestUtils.waitForComponentState(bot, editor, GPP_1, ComponentState.STARTED);
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1);

		// verify device stub stopped
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_STUB, ComponentState.STOPPED);

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
