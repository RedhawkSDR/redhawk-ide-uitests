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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.debug.impl.LocalScaWaveformImpl;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformSandboxEditor;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.condition.WaitForCellValue;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DomainWaveformRuntimeSyncTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	@Override
	protected String getWaveformName() {
		return "ExampleWaveform06";
	}

	/**
	 * IDE-672
	 * Starts/stops them from Diagram and verifies components in REDHAWK Explorer reflect changes
	 * 
	 * IDE-1120 - Ensure check that class hierarchy and input type are as expected
	 * 
	 */
	@Test
	public void startStopComponentsFromDiagram() {
		final String[] waveformPath = ScaExplorerTestUtils.joinPaths(DOMAIN_WAVEFORM_PARENT_PATH, new String[] { getWaveFormFullName() });

		// Open domain waveform with graphiti chalkboard editor
		bot.closeAllEditors();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());

		// IDE-1120
		Assert.assertEquals("Editor class should be GraphitiWaveformSandboxEditor", GraphitiWaveformSandboxEditor.class,
			editor.getReference().getPart(false).getClass());
		GraphitiWaveformSandboxEditor editorPart = (GraphitiWaveformSandboxEditor) editor.getReference().getPart(false);
		Assert.assertEquals("Chalkboard editors in a domain should have LocalScaWaveform as their input", LocalScaWaveformImpl.class,
			editorPart.getWaveform().getClass());

		editor.setFocus();

		// verify hard limit stopped
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, waveformPath, HARD_LIMIT_1);

		// start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit started but siggen did not
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformPath, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, waveformPath, SIGGEN_1);

		// start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN);

		// verify SigGen started but siggen did not
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformPath, SIGGEN_1);

		// stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit stopped, SigGen started
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, waveformPath, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformPath, SIGGEN_1);

		// stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIGGEN);

		// verify SigGen stopped
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, waveformPath, SIGGEN_1);

		// start both components
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN);

		// verify both started
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformPath, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformPath, SIGGEN_1);
	}

	/**
	 * IDE-672
	 * Starts/stops them from REDHAWK Explorer and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromScaExplorer() {
		final String[] waveformPath = ScaExplorerTestUtils.joinPaths(DOMAIN_WAVEFORM_PARENT_PATH, new String[] { getWaveFormFullName() });

		// Open domain waveform with graphiti chalkboard editor
		bot.closeAllEditors();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// verify hard limit stopped
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STOPPED);

		// start hard limit from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, waveformPath, HARD_LIMIT_1);

		// verify hardlimit started but siggen did not
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STOPPED);

		// start SigGen from REDHAWK explorer
		ScaExplorerTestUtils.startResourceInExplorer(bot, waveformPath, SIGGEN_1);

		// verify SigGen started but siggen did not
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STARTED);

		// stop hard limit from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, waveformPath, HARD_LIMIT_1);

		// verify hardlimit stopped, SigGen started
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STARTED);

		// stop SigGen from REDHAWK explorer
		ScaExplorerTestUtils.stopResourceInExplorer(bot, waveformPath, SIGGEN_1);

		// verify SigGen stopped
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STOPPED);

		// start both components
		ScaExplorerTestUtils.startResourceInExplorer(bot, waveformPath, HARD_LIMIT_1);
		ScaExplorerTestUtils.startResourceInExplorer(bot, waveformPath, SIGGEN_1);

		// verify both started
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STARTED);

		// stop waveform
		ScaExplorerTestUtils.stopResourceInExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName());

		// verify both components stopped
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STOPPED);

		// start waveform
		ScaExplorerTestUtils.startResourceInExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName());

		// verify both components started
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STARTED);
	}

	/**
	 * IDE-1205 Make sure properties match whether component is selected in diagram or REDHAWK Explorer.
	 */
	@Test
	public void changePropertiesInScaExplorer() {
		bot.closeAllEditors();
		final String[] waveformPath = ScaExplorerTestUtils.joinPaths(DOMAIN_WAVEFORM_PARENT_PATH, new String[] { getWaveFormFullName() });
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propView.show();

		// Select component in REDHAWK explorer tree first
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, waveformPath, SIGGEN_1).select().click();

		// Note: when the component is selected via the explorer view, the tab name is "Properties" instead of
		// "Component Properties"
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem magItemExplorer = propTable.getTreeItem("magnitude");
		Assert.assertEquals(magItemExplorer.cell(1), "100.0");
		magItemExplorer.select().click(1);
		gefBot.viewByTitle("Properties").bot().text().setText("50");

		// Click in diagram outside of component first
		// Workaround for issue where diagram component does not populate
		// properties view if selected right after creation
		editor.rootEditPart().click();
		editor.click(SIGGEN);
		propTable = propView.bot().tree();
		final SWTBotTreeItem magItemDiagram = propTable.getTreeItem("magnitude");

		// Wait for the property to update - there's a delay since we're going chalkboard -> explorer for a component in
		// a domain
		bot.waitUntil(new WaitForCellValue(magItemDiagram, 1, "50.0"), 15000);
	}

	/**
	 * IDE-1205 Make sure properties match whether component is selected in diagram or REDHAWK Explorer.
	 */
	@Test
	public void changePropertiesInChalkboardDiagram() {
		// Close the explorer and open the chalkboard view
		bot.closeAllEditors();
		final String[] waveformPath = ScaExplorerTestUtils.joinPaths(DOMAIN_WAVEFORM_PARENT_PATH, new String[] { getWaveFormFullName() });
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propView.show();

		// TODO: Click in diagram outside of component first
		// Workaround for issue where diagram component does not populate
		// properties view if selected right after creation
		editor.rootEditPart().click();
		editor.click(SIGGEN);

		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Component Properties");
		SWTBotTreeItem magItemDiagram = propTable.getTreeItem("magnitude");
		Assert.assertEquals(magItemDiagram.cell(1), "100.0");
		magItemDiagram.select().click(1);
		propView.bot().text().setText("50");

		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, waveformPath, SIGGEN_1).select().click();
		propTable = propView.bot().tree();
		final SWTBotTreeItem magItemExplorer = propTable.getTreeItem("magnitude");

		// Wait for the property to update - there's a delay since we're going chalkboard -> explorer for a component in
		// a domain
		bot.waitUntil(new WaitForCellValue(magItemExplorer, 1, "50.0"), 15000);
	}
}
