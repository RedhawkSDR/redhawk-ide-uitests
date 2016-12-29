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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class SaveChalkboardTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	/**
	 * IDE-684
	 * Save the Graphiti chalkboard as a waveform
	 */
	@Test
	public void saveChalkboardAsWaveform() {
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add components to diagram and wait to start
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN_1, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);

		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		// Save waveform
		final String WAVEFORM_NAME = "ChalkboardAsWaveform";
		bot.menu("File").menu("Save As...").click();
		SWTBotShell saveShell = bot.shell("Save Chalkboard");
		saveShell.setFocus();
		saveShell.bot().textWithLabel("Project name:").setText(WAVEFORM_NAME);
		saveShell.bot().button("Finish").click();

		// Validate new waveform
		bot.waitUntil(Conditions.shellCloses(saveShell));

		// Wait for new project to appear
		bot.waitUntil(new DefaultCondition() {

			@Override
			public String getFailureMessage() {
				return "New Waveform does not appear in the project explorer view";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem waveformNode = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer").bot().tree().getTreeItem(WAVEFORM_NAME);
				return waveformNode != null;
			}

		});

		// Get new editor (opens automatically), and check parts
		editor = gefBot.rhGefEditor(WAVEFORM_NAME);
		editor.setFocus();
		Assert.assertNotNull(SIGGEN_1 + " component was not found", editor.getEditPart(SIGGEN_1));
		Assert.assertNotNull(HARD_LIMIT_1 + " component was not found", editor.getEditPart(HARD_LIMIT_1));

		usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1);
		providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertFalse("Source connections should not be empty for this test", sourceConnections.isEmpty());

		ContainerShape hardLimitContainerShape = (ContainerShape) editor.getEditPart(HARD_LIMIT_1).part().getModel();
		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(hardLimitContainerShape).get(0);

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));
	}

	/**
	 * IDE-963
	 * Testing key binding of Ctrl-S to Save As
	 * @throws AWTException
	 */
	@Test
	public void saveAsHotkeyTest() throws AWTException {
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIGGEN);

		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_S);
		robot.keyRelease(KeyEvent.VK_S);
		robot.keyRelease(KeyEvent.VK_CONTROL);

		SWTBotShell saveShell = bot.shell("Save Chalkboard");
		Assert.assertNotNull("Save As dialog not found", saveShell);
		saveShell.setFocus();
		saveShell.bot().button("Cancel").click();
	}
}
