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

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.condition.WaitForCellValue;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class DomainWaveformRuntimeSyncTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	/**
	 * IDE-672
	 * Starts/stops them from Diagram and verifies
	 * components in REDHAWK Explorer reflect changes
	 * 
	 */
	@Test
	public void startStopComponentsFromDiagram() {
		// Open domain waveform with graphiti chalkboard editor
		bot.closeAllEditors();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// verify hard limit stopped
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);

		// start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit started but siggen did not
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN);

		// verify SigGen started but siggen did not
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit stopped, SigGen started
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIGGEN);

		// verify SigGen stopped
		ScaExplorerTestUtils.waitUntilNodeStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// start both components
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN);

		// verify both started
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilNodeStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);
	}

	/**
	 * IDE-672
	 * Starts/stops them from REDHAWK Explorer and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromScaExplorer() {
		// Open domain waveform with graphiti chalkboard editor
		bot.closeAllEditors();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// verify hard limit stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);

		// start hard limit from REDHAWK explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);

		// verify hardlimit started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN);

		// start SigGen from REDHAWK explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// verify SigGen started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN);

		// stop hard limit from REDHAWK explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);

		// verify hardlimit stopped, SigGen started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN);

		// stop SigGen from REDHAWK explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// verify SigGen stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN);

		// start both components
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// verify both started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN);

		// stop waveform
		ScaExplorerTestUtils.stopWaveformFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName());

		// verify both components stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN);

		// start waveform
		ScaExplorerTestUtils.startWaveformFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName());

		// verify both components started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN);
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
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties").bot().tree();
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

		// Wait for the property to update - there's a delay since we're going chalkboard -> explorer for a component in a domain
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
		editor.rootEditPart().click();		editor.click(SIGGEN);

		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Component Properties").bot().tree();
		SWTBotTreeItem magItemDiagram = propTable.getTreeItem("magnitude");
		Assert.assertEquals(magItemDiagram.cell(1), "100.0");
		magItemDiagram.select().click(1);
		propView.bot().text().setText("50");
		
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, waveformPath, SIGGEN_1).select().click();
		propTable = propView.bot().tree();
		final SWTBotTreeItem magItemExplorer = propTable.getTreeItem("magnitude");
		
		// Wait for the property to update - there's a delay since we're going chalkboard -> explorer for a component in a domain
		bot.waitUntil(new WaitForCellValue(magItemExplorer, 1, "50.0"), 15000);
	}
}
