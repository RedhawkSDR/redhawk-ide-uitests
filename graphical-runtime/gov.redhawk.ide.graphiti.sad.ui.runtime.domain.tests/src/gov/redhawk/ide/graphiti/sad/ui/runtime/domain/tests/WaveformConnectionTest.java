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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;

public class WaveformConnectionTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	@Override
	protected String getWaveformName() {
		return "SigGenToHardLimitWF";
	}

	/**
	 * IDE-2073
	 * Display connection properties when selecting a connection in a runtime diagram
	 */
	@Test
	public void connectionPropertiesView() {

		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);

		// Select connection
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1, "dataFloat_out");
		SWTBotGefEditPart usesAnchor = DiagramTestUtils.getDiagramPortAnchor(usesEditPart);
		usesAnchor.sourceConnections().get(0).select();

		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Advanced");
		SWTBotTreeItem treeItem = propTable.getTreeItem("Id");
		Assert.assertEquals(treeItem.cell(1), "connection_1");
	}
}
