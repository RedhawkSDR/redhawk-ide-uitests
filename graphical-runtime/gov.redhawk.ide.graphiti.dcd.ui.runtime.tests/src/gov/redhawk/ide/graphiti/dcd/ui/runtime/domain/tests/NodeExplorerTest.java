/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class NodeExplorerTest extends AbstractGraphitiDomainNodeRuntimeTest {

	private SWTBotGefEditor editor;

	/**
	 * IDE-998 Opens Graphiti Node Explorer diagram.
	 * This editor is "look but don't touch". All design functionality should be disabled.
	 * Runtime functionality (start/stop, plot, etc) should still work.
	 * IDE-1001 Hide grid on runtime diagram.
	 * IDE-1089 Editor title, missing XML tab
	 */
	@Test
	public void nodeExplorerTest() {
		launchDomainAndDevMgr(DEVICE_MANAGER);
		SWTBotEditor nodeEditor = gefBot.editorByTitle(DEVICE_MANAGER);
		editor = gefBot.gefEditor(DEVICE_MANAGER);
		editor.setFocus();

		// IDE-1089 test
		nodeEditor.bot().cTabItem("DeviceManager.dcd.xml").activate();
		nodeEditor.bot().cTabItem("Diagram").activate();

		// check that device is removed from editor when released in the Sca Explorer
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, DEV_MGR_PATH, GPP_LOCALHOST);

		// IDE-1001 check that grid is hidden on runtime diagram
		Diagram diagram = DiagramTestUtils.getDiagram(editor);
		Assert.assertNotNull("Found in Diagram (model object) on editor", diagram);
		int gridUnit = diagram.getGridUnit();
		assertEquals("Grid is hidden on runtime Node/DeviceMgr diagram", -1, gridUnit); // -1 means it is hidden

		// Confirm that .dcd.xml is visible
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
	}
}
