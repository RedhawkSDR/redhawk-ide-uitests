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

import java.io.IOException;
import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import mil.jpeojtrs.sca.partitioning.FindByStub;

public abstract class AbstractFindByWithDialogTest extends AbstractFindByTest {

	/**
	 * IDE-1087 - FindBy Wizards should not allow duplicate Ports
	 */
	@Test
	public void redundantPortNameTest() {
		waveformName = "FindBy_Redundant_Port";

		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		editor = gefBot.rhGefEditor(waveformName);

		DiagramTestUtils.addFromPaletteToDiagram(editor, getFindByType(), 0, 150);
		FindByUtils.completeFindByWizard(gefBot, getFindByType(), getFindByName(), PROVIDES_PORTS, USES_PORTS);

		// Open FindBy edit wizard and try to add the same ports
		getEditor().getEditPart(getFindByName()).select();
		getEditor().clickContextMenu("Edit " + getFindByType());

		SWTBotList providesList = gefBot.listInGroup("Port(s) to use for connections", 0);
		SWTBotList usesList = gefBot.listInGroup("Port(s) to use for connections", 1);

		Assert.assertEquals("Incorrect number of provides ports found", PROVIDES_PORTS.length, providesList.getItems().length);
		Assert.assertEquals("Incorrect number of uses ports found", USES_PORTS.length, usesList.getItems().length);

		SWTBotText providesText = gefBot.textInGroup("Port(s) to use for connections", 0);
		for (String s : PROVIDES_PORTS) {
			providesText.setText(s);
			bot.button("Add Provides Port").click();
		}

		SWTBotText usesText = gefBot.textInGroup("Port(s) to use for connections", 1);
		for (String s : USES_PORTS) {
			usesText.setText(s);
			bot.button("Add Uses Port").click();
		}

		Assert.assertEquals("Redundant provides ports should not have been added", PROVIDES_PORTS.length, providesList.getItems().length);
		Assert.assertEquals("Redundant uses ports should not have been added", USES_PORTS.length, usesList.getItems().length);

		gefBot.button("Finish").click();

	}

	@Test
	public void feedbackLoopTest() throws IOException {
		waveformName = "FindBy_Edit_With_Port";

		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		editor = gefBot.rhGefEditor(waveformName);

		DiagramTestUtils.addFromPaletteToDiagram(editor, getFindByType(), 0, 150);
		FindByUtils.completeFindByWizard(gefBot, getFindByType(), getFindByName(), PROVIDES_PORTS, USES_PORTS);

		SWTBotGefEditPart findByUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), getFindByName());
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, getFindByName());

		DiagramTestUtils.drawConnectionBetweenPorts(editor, findByUsesPart, findByProvidesPart);

		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, findByUsesPart);
		Assert.assertEquals("Connection was not added", 1, sourceConnections.size());

		List<SWTBotGefConnectionEditPart> targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, findByProvidesPart);
		Assert.assertEquals("Connection was not added", 1, targetConnections.size());

		// Closing and opening the editor is required to check for certain edge cases
		MenuUtils.save(editor);
		editor.close();
		ProjectExplorerUtils.openProjectInEditor(bot, waveformName, waveformName + ".sad.xml");
		editor = gefBot.rhGefEditor(waveformName);

		getEditor().getEditPart(getFindByName()).select();
		getEditor().clickContextMenu("Edit " + getFindByType());
		final String newFindByName = "Edited" + getFindByName();
		gefBot.textWithLabel(getEditTextLabel()).setText(newFindByName);
		gefBot.button("Finish").click();

		// Check XML to see if connection details were updated
		checkXMLForUpdates(editor, newFindByName);

		// Make sure FindBy updated on the diagram
		SWTBotGefEditPart fbEditPart = editor.getEditPart(getFindByName());
		Assert.assertNull("FindBy Element name was not updated", fbEditPart);
		fbEditPart = editor.getEditPart(newFindByName);
		Assert.assertNotNull("FindBy Element not found", fbEditPart);

		// Delete the provides port
		editor.getEditPart(newFindByName).select();
		editor.clickContextMenu("Edit " + getFindByType());
		gefBot.listInGroup("Port(s) to use for connections", 0).select(PROVIDES_PORTS[0]);
		gefBot.button("Delete", 0).click();
		gefBot.button("Finish").click();

		// Make sure the connection disappears
		RHContainerShapeImpl findByShape = (RHContainerShapeImpl) fbEditPart.part().getModel();
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);
		Assert.assertTrue("Number of ports is incorrect", findByShape.getUsesPortStubs().size() == 1 && findByShape.getProvidesPortStubs().size() == 0);
		Assert.assertEquals("Uses port name is incorrect", USES_PORTS[0], findByShape.getUsesPortStubs().get(0).getName());
		Assert.assertEquals("Diagram uses and domain uses don't match", USES_PORTS[0], findByObject.getUses().get(0).getName());

		findByUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), newFindByName);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, findByUsesPart);
		Assert.assertEquals("Connection was not deleted", 0, sourceConnections.size());
	}
}
