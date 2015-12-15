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

import org.eclipse.graphiti.mm.pictograms.Anchor;
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

	public PortPropertiesTest() {
	}

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
		SWTBotGefEditPart part;
		SWTBotTree tree;
		String description;

		part = getAnchorPart(DiagramTestUtils.getDiagramProvidesPort(editor, onlyComponent));
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propView.show();
		editor.select(part);
		editor.click(part);
		ViewUtils.selectPropertiesTab(bot, "Port Details");
		description = propView.bot().textWithLabel("Description:").getText(); // IDE-1172
		Assert.assertEquals("provides Port description", "Float input port for data before hard limit is applied. ", description);
		tree = gefBot.viewByTitle("Properties").bot().tree();
		tree.expandNode("dataFloat");
		Assert.assertTrue("Properties view tree should have multiple nodes", tree.visibleRowCount() > 1);

		part = getAnchorPart(DiagramTestUtils.getDiagramUsesPort(editor, onlyComponent));
		propView.show();
		editor.select(part);
		editor.click(part);
		ViewUtils.selectPropertiesTab(bot, "Port Details");
		description = gefBot.viewByTitle("Properties").bot().textWithLabel("Description:").getText(); // IDE-1172
		Assert.assertEquals("uses Port description", "Float output port for data after hard limit is applied. ", description);
		tree = gefBot.viewByTitle("Properties").bot().tree();
		tree.expandNode("dataFloat");
		Assert.assertTrue("Properties view tree should have multiple nodes", tree.visibleRowCount() > 1);
	}


	private SWTBotGefEditPart getAnchorPart(SWTBotGefEditPart parent) {
		if (parent.part().getModel() instanceof Anchor) {
			return parent;
		}
		for (SWTBotGefEditPart part: parent.children()) {
			SWTBotGefEditPart partAnchor = getAnchorPart(part);
			if  (partAnchor != null) {
				return partAnchor;
			}
		}
		return null;
	}
}
