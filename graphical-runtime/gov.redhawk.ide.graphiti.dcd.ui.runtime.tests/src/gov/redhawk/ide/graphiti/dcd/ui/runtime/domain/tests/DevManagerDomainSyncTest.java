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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Test;

public class DevManagerDomainSyncTest extends AbstractGraphitiDomainNodeRuntimeTest {

	private SWTBotGefEditor editor;

	/**
	 * IDE-1119
	 * Adds device, starts/stops them from Chalkboard Diagram and verifies
	 * device in ScaExplorer Dev Manager Chalkboard reflect changes
	 * 
	 */
	@Test
	public void startStopDevicesFromChalkboardDiagram() {
		String[] gppParentPath = { DOMAIN + " CONNECTED", "Device Managers", getNodeFullName()}; 
		editor = gefBot.gefEditor(getNodeFullName());
		
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP); // GPP starts when launched

		// verify GPP stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, GPP);
		
		// Get device full name, need to cut of "STARTED" if it exists
		final String GPP_FULL_NAME = getDeviceFullName(ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, gppParentPath, GPP)); // Needed
		String tempStr = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, gppParentPath, GPP);
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);

		// start GPP
		DiagramTestUtils.startComponentFromDiagram(editor, GPP);

		// verify GPP started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, GPP);
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);

		// stop GPP
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP);

		// verify GPP stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, GPP);
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);
	}

	/**
	 * IDE-1119
	 * Adds devices, starts/stops them from ScaExplorer Dev Manager Chalkboard and verifies
	 * devices in diagram reflect appropriate color changes
	 */
	@Test
	public void startStopDevicesFromScaExplorer() {
		String[] gppParentPath = { DOMAIN + " CONNECTED", "Device Managers", getNodeFullName()}; 
		editor = gefBot.gefEditor(getNodeFullName());
		
		DiagramTestUtils.stopComponentFromDiagram(editor, GPP); // GPP starts when launched
		String GPP_FULL_NAME = getDeviceFullName(ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, gppParentPath, GPP));
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);
		
		// start GPP from sca explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);

		// verify GPP started
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, GPP);

		// stop GPP from sca explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);

		// verify GPP stopped
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, GPP_FULL_NAME);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, GPP);
	}

	
	private String getDeviceFullName(String deviceName) {
		int startedLoc = deviceName.indexOf(" STARTED");
		if (startedLoc == -1) {
			return deviceName;
		}
		deviceName = deviceName.substring(0, startedLoc);
		return deviceName;
	}
}
