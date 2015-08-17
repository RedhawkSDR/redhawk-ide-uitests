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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

/**
 * Tests relating to opening a node from the target SDR (i.e. the design-time editor, which should be read-only).
 */
public class TargetSDRTest extends AbstractGraphitiTest {

	private static final String[] NODE_PARENT_PATH = new String[] { "Target SDR", "Nodes" };
	private static final String NODE_NAME = "DevMgr_with_bulkio";
	private static final String DEVICESTUB_1 = "DeviceStub_1";
	private static final String DEVICESTUB_PORT_FLOAT_OUT = "dataFloat_out";

	/**
	 * IDE-1324
	 * Checks the context menus available on a device in a node opened from the Target SDR.
	 */
	@Test
	public void contextMenus() {
		// Open waveform diagram from the Target SDR
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, NODE_PARENT_PATH, NODE_NAME, DiagramType.GRAPHITI_NODE_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(NODE_NAME);

		SWTBotGefEditPart device = editor.getEditPart(DEVICESTUB_1);
		Assert.assertNotNull(DEVICESTUB_1 + " component not found in diagram", device);
		device.select();

		// Check that runtime options are missing on the device
		String[] runtimeComponentOptions = { "Start", "Stop", "Show Console", "Log Level", "Release", "Terminate" };
		for (String menuText : runtimeComponentOptions) {
			ensureContextMenuNotPresent(editor, menuText);
		}

		// Check that design-time editor options are present, but disabled on the device.  We'll attempt to click the
		// menu, but then have to make sure nothing actually happened to know the menu was present, but disabled.
		editor.clickContextMenu("Delete");
		Assert.assertNotNull("Device should not have been deleted", editor.getEditPart(DEVICESTUB_1));

		SWTBotGefEditPart port = DiagramTestUtils.getDiagramUsesPort(editor, DEVICESTUB_1, DEVICESTUB_PORT_FLOAT_OUT);
		Assert.assertNotNull(DEVICESTUB_PORT_FLOAT_OUT + " port not found on component " + DEVICESTUB_1, port);
		port.select();

		// Check that runtime options are missing on the device's port
		String[] runtimePortOptions = { "Plot Port Data", "Data List", "Monitor Ports", "Display SRI", "Snapshot", "Play Port" };
		for (String menuText : runtimePortOptions) {
			ensureContextMenuNotPresent(editor, menuText);
		}
	}

	private void ensureContextMenuNotPresent(SWTBotGefEditor editor, String menuText) {
		try {
			editor.clickContextMenu(menuText);

			// The only way to get here is if the undesired context menu option appears
			Assert.fail("The menu '" + menuText + "' was present, but should not be");
		} catch (WidgetNotFoundException e) {
			Assert.assertEquals(e.getMessage(), menuText, e.getMessage());
		}
	}

}
