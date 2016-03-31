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

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class ChalkboardTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	/**
	 * IDE-884 Create the chalkboard waveform diagram. Add components to diagram from palette and TargetSDR.
	 * IDE-658 Open chalkboard with components already launched in the Sandbox.
	 * IDE-1187 Add namespaced component to chalkboard
	 */
	@Test
	public void checkChalkboardComponents() {
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT));

		// IDE-984 Make sure device cannot be added from Target SDR
		DiagramTestUtils.dragDeviceFromTargetSDRToDiagram(gefBot, editor, "GPP");
		Assert.assertNull("Unexpected device found in diagram", editor.getEditPart("GPP"));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragComponentFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// Open the chalkboard with components already launched
		editor.close();
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));

		// Add namespaced component to the chalkboard
		String nameSpaceComp = "name.space.comp";
		DiagramTestUtils.addFromPaletteToDiagram(editor, nameSpaceComp, 200, 300);
		Assert.assertNotNull(editor.getEditPart(nameSpaceComp));
	}

	@Test
	public void monitorPortStyleTest() {
		final String[] comps = { "SigGen", "DataConverter" };
		final String[] dataConGreenPorts = { "dataChar", "dataOctet", "dataShort", "dataUshort", "dataDouble" };

		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		for (String comp : comps) {
			DiagramTestUtils.addFromPaletteToDiagram(editor, "rh." + comp, 0, 0);
			DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, comp + "_1");
			DiagramTestUtils.waitForComponentState(bot, editor, comp + "_1", ComponentState.STOPPED);
		}

		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1, "dataFloat_out");
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, "DataConverter_1", "dataFloat");
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN_1);
		SWTBotTreeItem chalkboard = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
		chalkboard.contextMenu("Monitor Ports").click();

		synchronized (bot) {
			try {
				bot.wait(20000);
				// Give the port enough time to turn red
			} catch (InterruptedException e) {
				// PASS
			}
		}

		// The following port should be red, the data will have backed up as the DataConverter component has not started
		SWTBotGefEditPart redPort = DiagramTestUtils.getDiagramProvidesPort(editor, "DataConverter_1", "dataFloat");
		ContainerShape portModel = (ContainerShape) redPort.part().getModel();
		IDecorator[] decorators = DiagramTestUtils.getPictogramElementDecorators(editor, portModel.getChildren().get(0));
		checkDecoratorColor(new RGB(255, 0, 0), decorators);

		// All other ports should be green, as there is no data flowing through them
		for (String portName : dataConGreenPorts) {
			SWTBotGefEditPart port = DiagramTestUtils.getDiagramProvidesPort(editor, "DataConverter_1", portName);
			portModel = (ContainerShape) port.part().getModel();
			decorators = DiagramTestUtils.getPictogramElementDecorators(editor, portModel.getChildren().get(0));
			checkDecoratorColor(new RGB(0, 255, 0), decorators);
		}
	}

	private void checkDecoratorColor(final RGB rgb, IDecorator[] decorators) {
		for (IDecorator decorator : decorators) {
			if (decorator instanceof ColorDecorator) {
				IColorConstant bgc = ((ColorDecorator) decorator).getBackgroundColor();
				final RGB decoratorRgb = new RGB(bgc.getRed(), bgc.getGreen(), bgc.getBlue());
				Assert.assertEquals("Port did not match expected color", rgb, decoratorRgb);
			}
		}
	}

	/**
	 * IDE-1398 Chalkboard 'start' only starts the first component launched
	 */
	@Test
	public void multiStartTest() {
		final String[] comps = { "SigGen", "HardLimit", "DataConverter" };

		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		for (String comp : comps) {
			DiagramTestUtils.addFromPaletteToDiagram(editor, "rh." + comp, 0, 0);
			DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, comp + "_1");
			DiagramTestUtils.waitForComponentState(bot, editor, comp + "_1", ComponentState.STOPPED);
		}

		SWTBotTreeItem chalkboard = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
		chalkboard.contextMenu("Start").click();

		for (String comp : comps) {
			DiagramTestUtils.waitForComponentState(bot, editor, comp + "_1", ComponentState.STARTED);
		}

		chalkboard.contextMenu("Stop").click();

		for (String comp : comps) {
			DiagramTestUtils.waitForComponentState(bot, editor, comp + "_1", ComponentState.STOPPED);
		}
	}

	/**
	 * IDE-928 Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 * IDE-124 Check to make sure UsesDevice tool does not appear in the Palette when in the Graphiti sandbox
	 */
	@Test
	public void checkNotInSandbox() {

		// Check for Find Bys
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		String[] findByList = { FindByUtils.FIND_BY_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, FindByUtils.FIND_BY_EVENT_CHANNEL,
			FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };

		for (String findByType : findByList) {
			try {
				DiagramTestUtils.addFromPaletteToDiagram(editor, findByType, 0, 0);
				Assert.fail(); // The only way to get here is if the FindBy type appears in the Palette
			} catch (WidgetNotFoundException e) {
				Assert.assertTrue(e.getMessage(), e.getMessage().matches(".*" + findByType + ".*"));
			}
		}

		// Check for Uses Devices
		String usesDevice = "Use FrontEnd Tuner Device";
		try {
			DiagramTestUtils.addFromPaletteToDiagram(editor, usesDevice, 0, 0);
			Assert.fail(); // The only way to get here is if the FindBy type appears in the Palette
		} catch (WidgetNotFoundException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().matches(".*" + usesDevice + ".*"));
		}
	}

	/**
	 * IDE-953
	 * Verifies that when the user drags a component to the diagram of a particular implementation
	 * that it in fact the correct implementation was added.
	 */
	@Test
	public void checkCorrectImplementationAddedToDiagram() {
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add two components to diagram from palette
		final String sourceComponent = SIGGEN + " (python)";
		final String targetComponent = HARD_LIMIT + " (java)";
		DiagramTestUtils.addFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// verify sigGen is python
		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN_1);
		// get graphiti shape
		ComponentShape sigGenComponentShape = (ComponentShape) sigGenEditPart.part().getModel();
		// Grab the associated business object and confirm it is a SadComponentInstantiation
		SadComponentInstantiation sigGenSadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(sigGenComponentShape);
		Assert.assertEquals("SigGen implementation was not python", "python", sigGenSadComponentInstantiation.getImplID());

		// verify hardLimit is java
		SWTBotGefEditPart hardLimitEditPart = editor.getEditPart(HARD_LIMIT_1);
		// get graphiti shape
		ComponentShape hardLimitComponentShape = (ComponentShape) hardLimitEditPart.part().getModel();
		// Grab the associated business object and confirm it is a SadComponentInstantiation
		SadComponentInstantiation hardLimitSadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(hardLimitComponentShape);
		Assert.assertEquals("HardLimit implementation was not java", "java", hardLimitSadComponentInstantiation.getImplID());
	}

	/**
	 * Private helper method for {@link #checkComponentPictogramElements()} and
	 * {@link #checkComponentPictogramElementsWithAssemblyController()}.
	 * Asserts the given SWTBotGefEditPart is a HardLimit component and assembly controller
	 * @param gefEditPart
	 */
	private static void assertHardLimit(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti component shape
		ComponentShape componentShape = (ComponentShape) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue("business object should be of type SadComponentInstantiation", bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", HARD_LIMIT, ComponentUtils.getOuterText(componentShape).getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), ComponentUtils.getInnerText(componentShape).getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", ComponentUtils.getLollipop(componentShape));
		Assert.assertNull("start order shape/text should be null", ComponentUtils.getStartOrderText(componentShape));

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);

		// Both ports are of type dataFloat
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataFloat");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataFloat");
	}
}
