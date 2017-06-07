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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class PortPropertiesTest extends AbstractGraphitiTest {

	protected static final String HARD_LIMIT = "rh.HardLimit";

	/**
	 * Ensure that after selecting a port the IDL hierarchy for it is shown in the properties view.
	 */
	@Test
	public void checkPortProperties() {
		final String waveformName = "IDE-1050-test";
		final String onlyComponent = "rh.HardLimit";
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		final RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, onlyComponent, 0, 0);

		SWTBotGefEditPart providesPortEditPart = DiagramTestUtils.getDiagramPortAnchor(DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT));
		SWTBotGefEditPart usesPortEditPart = DiagramTestUtils.getDiagramPortAnchor(DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT));

		final String inDesc = "Float input port for data before hard limit is applied. ";
		final String outDesc = "Float output port for data after hard limit is applied. ";

		checkPortDetails(providesPortEditPart, editor, inDesc);
		checkPortDetails(usesPortEditPart, editor, outDesc);
	}

	private void checkPortDetails(SWTBotGefEditPart part, RHBotGefEditor editor, String expectedDesc) {
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propView.show();
		editor.select(part);
		editor.click(part);
		ViewUtils.selectPropertiesTab(bot, "Port Details");
		String description = propView.bot().textWithLabel("Description:").getText(); // IDE-1172
		Assert.assertEquals("provides Port description", expectedDesc, description);
		SWTBotTree tree = gefBot.viewByTitle("Properties").bot().tree();
		tree.expandNode("dataFloat");
		Assert.assertTrue("Properties view tree should have multiple nodes", tree.visibleRowCount() > 1);
	}
}
