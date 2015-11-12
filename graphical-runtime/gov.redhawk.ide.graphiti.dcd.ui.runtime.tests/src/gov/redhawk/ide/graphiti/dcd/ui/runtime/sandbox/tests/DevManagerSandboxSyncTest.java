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
		editor = openNodeChalkboardDiagram(gefBot);

		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
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
		editor = openNodeChalkboardDiagram(gefBot);

		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(DEVICE_STUB));

		// wait until device not present in REDHAWK Explorer Chalkboard & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		Assert.assertNull(editor.getEditPart(DEVICE_STUB));
	}

	/**
	 * IDE-1119
	 * Adds, then removes a device connection via dev manager chalkboard diagram.
	 * Verify its no longer present in REDHAWK Explorer Chalkboard
	 */
	@Test
	public void addRemoveDeviceConnectionInChalkboardDiagram() {
		editor = openNodeChalkboardDiagram(gefBot);

		// Add a device stub to diagram from palette, which will connect to itself for this test
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

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
		editor = openNodeChalkboardDiagram(gefBot);

		// Add two devices to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);

		// wait for device to show up in REDHAWK Explorer Dev Manager Chalkboard
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// GPP starts when launched
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1);

		// verify GPP stopped
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);

		// start GPP
		DiagramTestUtils.startComponentFromDiagram(editor, GPP_1);

		// verify GPP started but DeviceStub did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, GPP_1);
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// start DeviceStub
		DiagramTestUtils.startComponentFromDiagram(editor, DEVICE_STUB_1);

		// verify DeviceStub started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, DEVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// stop GPP
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1);

		// verify GPP stopped, DeviceStub started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, GPP_1);
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// stop DeviceStub
		DiagramTestUtils.stopComponentFromDiagram(editor, DEVICE_STUB_1);

		// verify DeviceStub stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, DEVICE_STUB_1);
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// start both devices
		DiagramTestUtils.startComponentFromDiagram(editor, GPP_1);
		DiagramTestUtils.startComponentFromDiagram(editor, DEVICE_STUB_1);

		// verify both started
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

	}

	/**
	 * IDE-11199
	 * Adds, then removes a device via REDHAWK Explorer Dev Manager Chalkboard.
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addRemoveDeviceInScaExplorer() {
		editor = openNodeChalkboardDiagram(gefBot);

		// Launch device from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify DeviceStub was added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB);

		// delete device from REDHAWK Explorer dev manager chalkboard
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		ScaExplorerTestUtils.terminateDeviceInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// verify DeviceStub device not present in Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, DEVICE_STUB);

		// Launch device from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify DeviceStub was added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB);

		// terminate device manager
		ScaExplorerTestUtils.terminateFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER);

		// verify DeviceStub not present in Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, DEVICE_STUB);
	}

	/**
	 * IDE-1119
	 * Adds, then removes device connections via REDHAWK Explorer view.
	 * Verify its no longer present in Diagram
	 */
	@Test
	public void addRemoveDeviceConnectionInScaExplorer() {
		editor = openNodeChalkboardDiagram(gefBot);

		// Launch two devices from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify devices were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB_2);

		// create connection between devices via REDHAWK Explorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		ScaExplorerTestUtils.connectComponentPortsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, "connection_1", DEVICE_STUB_1, "dataDouble_out",
			DEVICE_STUB_2, "dataDouble_in");

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
	public void startStopDevicesFromScaExplorer() {
		editor = openNodeChalkboardDiagram(gefBot);

		// Launch two devices from TargetSDR
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, GPP, "DCE:7facfff8-afd1-4ccc-a0bf-7f249ae6addb");
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_STUB, "python");

		// verify devices were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, GPP);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_STUB);
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP_1); // GPP starts when launched

		// verify hard limit stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, DEVICE_STUB);

		// start hard limit from REDHAWK explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// verify device stub started but GPP did not
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, DEVICE_STUB);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, GPP);

		// start GPP from REDHAWK explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);

		// verify GPP started in explorer and diagram
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, GPP);

		// stop device stub from REDHAWK explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);

		// verify device stub stopped, GPP started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, DEVICE_STUB);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, GPP);

		// stop GPP from REDHAWK explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);

		// verify GPP stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, GPP);

		// start both devices
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DEVICE_STUB_1);
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, GPP_1);

		// verify both started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, DEVICE_STUB);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, GPP);
	}

}
