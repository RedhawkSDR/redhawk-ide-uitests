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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.sad.ui.ext.ComponentShape;
import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForBuild.BuildType;
import gov.redhawk.ide.swtbot.condition.WaitForTargetSdrRootLoad;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.PaletteUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;


public class ReinstallComponentTest extends AbstractGraphitiTest {

	/**
	 * IDE-1078
	 * Add or remove ports from a component that already exists in a design-time waveform diagram.  
	 * Install this component to the Target SDR.  The next time the waveform diagram is opened, it 
	 * should reflect the updated ports.  
	 */
	@Test
	public void reinstallComponentTest() {
		final String componentName = "ide1078_component";
		final String waveformName = "ide1078_waveform";
		
		// Create, generate and export the test component
		ComponentUtils.createComponentProject(bot, componentName, "Java");
		SWTBotEditor editor = bot.editorByTitle(componentName);
		StandardTestActions.generateProject(bot, editor);
		bot.waitUntil(new WaitForBuild(BuildType.CODEGEN), WaitForBuild.TIMEOUT);
		StandardTestActions.exportProject(componentName, bot);
		bot.waitUntil(new WaitForTargetSdrRootLoad(), WaitForTargetSdrRootLoad.TIMEOUT);
		editor.close();
		
		// Create a new waveform and add test component to it
		WaveformUtils.createNewWaveform(bot, waveformName, null);
		editor = bot.editorByTitle(waveformName);
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		final RHBotGefEditor rhEditor = new RHSWTGefBot().rhGefEditor(waveformName);
		
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return PaletteUtils.toolIsPresent(rhEditor, componentName);
			}

			@Override
			public String getFailureMessage() {
				return String.format("Palette did not refresh to display '%s' resource", componentName);
			}
		}, 10000);
		DiagramTestUtils.addFromPaletteToDiagram(rhEditor, componentName, 10, 10);
		DiagramTestUtils.waitForComponentState(bot, rhEditor, componentName, ComponentState.STOPPED);
		
		// Confirm that component displays no ports
		SWTBotGefEditPart gefEditPart = rhEditor.getEditPart(componentName + "_1");
		ComponentShape componentShape = (ComponentShape) gefEditPart.part().getModel();
		List<EObject> usesPortStubs = componentShape.getUsesPortsContainerShape().getLink().getBusinessObjects();
		List<EObject> providesPortStubs = componentShape.getProvidesPortsContainerShape().getLink().getBusinessObjects();
		Assert.assertTrue(usesPortStubs.size() == 0 && providesPortStubs.size() == 0);
		
		// Close waveform
		rhEditor.save();
		rhEditor.close();
		
		addPorts(componentName);
		
		// Re-export component
		StandardTestActions.exportProject(componentName, bot);
		bot.waitUntil(new WaitForTargetSdrRootLoad(), WaitForTargetSdrRootLoad.TIMEOUT);
		editor.close();
		
		// Reopen waveform
		ProjectExplorerUtils.openProjectInEditor(bot, new String[] {waveformName, waveformName + ".sad.xml"});
		editor = bot.editorByTitle(waveformName);
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		
		// Confirm that component now displays new ports
		gefEditPart = new RHSWTGefBot().rhGefEditor(waveformName).getEditPart(componentName + "_1");
		componentShape = (ComponentShape) gefEditPart.part().getModel();
		usesPortStubs = componentShape.getUsesPortsContainerShape().getLink().getBusinessObjects();
		providesPortStubs = componentShape.getProvidesPortsContainerShape().getLink().getBusinessObjects();
		Assert.assertTrue(usesPortStubs.size() == 1 && providesPortStubs.size() == 1);
		Assert.assertEquals(((UsesPortStub) usesPortStubs.get(0)).getUses().getName(), "dataFloat_out");
		Assert.assertEquals(((ProvidesPortStub) providesPortStubs.get(0)).getProvides().getName(), "dataFloat_in");
	}

	private void addPorts(String componentName) {
		// Edit component to include a new provides and uses port
		final String providesPort = "dataFloat_in";
		final String usesPort = "dataFloat_out";
		ProjectExplorerUtils.openProjectInEditor(bot, new String[] {componentName, componentName + ".spd.xml"});
		SWTBotEditor editor = bot.editorByTitle(componentName);
		DiagramTestUtils.openTabInEditor(editor, "Ports");
		bot.button("Add").click();
		bot.textWithLabel("Name*:").setText(providesPort);
		bot.sleep(500);
		bot.comboBoxWithLabel("Direction:").setSelection(1);
		bot.button("Add").click();
		bot.textWithLabel("Name*:").setText(usesPort);
		bot.sleep(500);
		bot.comboBoxWithLabel("Direction:").setSelection(0);
		editor.save();		
	}
}
