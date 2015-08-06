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
package gov.redhawk.ide.graphiti.sad.ui.tests;

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
 * Tests relating to opening a waveform from the target SDR (i.e. the design-time editor, which should be read-only).
 */
public class TargetSDRTest extends AbstractGraphitiTest {

	private static final String[] WAVEFORM_PARENT_PATH = new String[] { "Target SDR", "Waveforms" };
	private static final String WAVEFORM_NAME = "ExampleWaveform05";
	private static final String SIGGEN_1 = "SigGen_1";
	private static final String SIGGEN_PORT_FLOAT_OUT = "dataFloat_out";

	/**
	 * IDE-1323
	 * Checks the context menus available on a component in a waveform opened from the Target SDR.
	 */
	@Test
	public void contextMenus() {
		// Open waveform diagram from the Target SDR
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(WAVEFORM_NAME);

		SWTBotGefEditPart component = editor.getEditPart(SIGGEN_1);
		Assert.assertNotNull(SIGGEN_1 + " component not found in diagram", component);
		component.select();

		// Check that runtime options are missing on the component
		String[] runtimeComponentOptions = { "Start", "Stop", "Show Console", "Log Level", "Release", "Terminate" };
		for (String menuText : runtimeComponentOptions) {
			ensureContextMenuNotPresent(editor, menuText);
		}

		// Check that editor options are missing on the component
		String[] editorComponentOptions = { "Delete", "Set As Assembly Controller", "Move Start Order Earlier", "Move Start Order Later" };
		for (String menuText : editorComponentOptions) {
			ensureContextMenuNotPresent(editor, menuText);
		}

		SWTBotGefEditPart port = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1, SIGGEN_PORT_FLOAT_OUT);
		Assert.assertNotNull(SIGGEN_PORT_FLOAT_OUT + " port not found on component " + SIGGEN_1, port);
		port.select();

		// Check that runtime options are missing on the component's port
		String[] runtimePortOptions = { "Plot Port Data", "Data List", "Monitor Ports", "Display SRI", "Snapshot", "Play Port" };
		for (String menuText : runtimePortOptions) {
			ensureContextMenuNotPresent(editor, menuText);
		}

		// Check that editor options are missing on the component's port
		String[] editorPortOptions = { "Mark External Port" };
		for (String menuText : editorPortOptions) {
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
