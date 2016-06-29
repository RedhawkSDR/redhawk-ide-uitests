/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.runtime.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.condition.WaitForCellValue;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public abstract class AbstractSyncText extends UIRuntimeTest {

	/**
	 * Launch the waveform/node. Open and return the appropriate diagram.
	 */
	protected abstract RHBotGefEditor launchDiagram();

	/**
	 * Get the parent path to the waveform/node.
	 */
	protected abstract String[] getWaveformOrNodeParent();

	/**
	 * Get the name of the waveform/node.
	 */
	protected abstract String getWaveformOrNodeName();

	protected abstract ComponentDescription resourceA();

	protected abstract ComponentDescription resourceB();

	protected abstract String resourceA_doubleProperty();

	protected abstract double resourceA_doubleProperty_startingValue();

	protected abstract String propertiesTabName();

	/**
	 * True if the parent resource supports starting/stopping (i.e. just waveforms, not nodes)
	 */
	protected abstract boolean supportsParentResourceStartStop();

	/**
	 * IDE-672 Starts/stops resources in the diagram and verifies the explorer view reflects the changes
	 */
	@Test
	public void startStopResourcesFromDiagram() {
		SWTBotGefEditor editor = launchDiagram();
		List<String> parentPath = new ArrayList<String>();
		Collections.addAll(parentPath, getWaveformOrNodeParent());
		parentPath.add(getWaveformOrNodeName());
		final String[] PARENT_PATH = parentPath.toArray(new String[parentPath.size()]);

		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, PARENT_PATH, resourceA().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, PARENT_PATH, resourceB().getShortName(1));

		DiagramTestUtils.startComponentFromDiagram(editor, resourceA().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, PARENT_PATH, resourceA().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, PARENT_PATH, resourceB().getShortName(1));

		DiagramTestUtils.startComponentFromDiagram(editor, resourceB().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, PARENT_PATH, resourceA().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, PARENT_PATH, resourceB().getShortName(1));

		DiagramTestUtils.stopComponentFromDiagram(editor, resourceA().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, PARENT_PATH, resourceA().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, PARENT_PATH, resourceB().getShortName(1));

		DiagramTestUtils.stopComponentFromDiagram(editor, resourceB().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, PARENT_PATH, resourceA().getShortName(1));
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, PARENT_PATH, resourceB().getShortName(1));
	}

	/**
	 * IDE-672 Starts/stops resources in the explorer view and verified the diagram reflects the changes
	 */
	@Test
	public void startStopResourcesFromExplorerView() {
		SWTBotGefEditor editor = launchDiagram();
		List<String> parentPath = new ArrayList<String>();
		Collections.addAll(parentPath, getWaveformOrNodeParent());
		parentPath.add(getWaveformOrNodeName());
		final String[] PARENT_PATH = parentPath.toArray(new String[parentPath.size()]);

		DiagramTestUtils.waitForComponentState(bot, editor, resourceA().getShortName(1), ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, resourceB().getShortName(1), ComponentState.STOPPED);

		ScaExplorerTestUtils.startResourceInExplorer(bot, PARENT_PATH, resourceA().getShortName(1));
		DiagramTestUtils.waitForComponentState(bot, editor, resourceA().getShortName(1), ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, resourceB().getShortName(1), ComponentState.STOPPED);

		ScaExplorerTestUtils.startResourceInExplorer(bot, PARENT_PATH, resourceB().getShortName(1));
		DiagramTestUtils.waitForComponentState(bot, editor, resourceA().getShortName(1), ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, resourceB().getShortName(1), ComponentState.STARTED);

		ScaExplorerTestUtils.stopResourceInExplorer(bot, PARENT_PATH, resourceA().getShortName(1));
		DiagramTestUtils.waitForComponentState(bot, editor, resourceA().getShortName(1), ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, resourceB().getShortName(1), ComponentState.STARTED);

		ScaExplorerTestUtils.stopResourceInExplorer(bot, PARENT_PATH, resourceB().getShortName(1));
		DiagramTestUtils.waitForComponentState(bot, editor, resourceA().getShortName(1), ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, resourceB().getShortName(1), ComponentState.STOPPED);

		if (supportsParentResourceStartStop()) {
			ScaExplorerTestUtils.startResourceInExplorer(bot, getWaveformOrNodeParent(), getWaveformOrNodeName());
			DiagramTestUtils.waitForComponentState(bot, editor, resourceA().getShortName(1), ComponentState.STARTED);
			DiagramTestUtils.waitForComponentState(bot, editor, resourceB().getShortName(1), ComponentState.STARTED);

			ScaExplorerTestUtils.stopResourceInExplorer(bot, getWaveformOrNodeParent(), getWaveformOrNodeName());
			DiagramTestUtils.waitForComponentState(bot, editor, resourceA().getShortName(1), ComponentState.STOPPED);
			DiagramTestUtils.waitForComponentState(bot, editor, resourceB().getShortName(1), ComponentState.STOPPED);
		}
	}

	/**
	 * IDE-1205 Make sure properties match whether component is selected in diagram or REDHAWK Explorer.
	 */
	@Test
	public void changePropertiesInExplorerView() {
		SWTBotGefEditor editor = launchDiagram();
		List<String> parentPath = new ArrayList<String>();
		Collections.addAll(parentPath, getWaveformOrNodeParent());
		parentPath.add(getWaveformOrNodeName());
		final String[] PARENT_PATH = parentPath.toArray(new String[parentPath.size()]);

		// Open the properties view
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();

		// Select resource in REDHAWK explorer tree
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, PARENT_PATH, resourceA().getShortName(1)).select().click();

		// Verify value; change it to double that value
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem magItemExplorer = propTree.getTreeItem(resourceA_doubleProperty());
		Assert.assertEquals(magItemExplorer.cell(1), Double.toString(resourceA_doubleProperty_startingValue()));
		magItemExplorer.select().click(1);
		final String newValue = Double.toString(resourceA_doubleProperty_startingValue() * 2);
		StandardTestActions.writeToCell(bot, magItemExplorer, 1, newValue);

		// Select resource in the diagram
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(resourceA().getShortName(1));
		componentPart.select();

		// Wait for the property to update (currently domain objects opened with the chalkboard editor have a delay)
		propTree = ViewUtils.selectPropertiesTab(bot, propertiesTabName());
		final SWTBotTreeItem magItemDiagram = propTree.getTreeItem(resourceA_doubleProperty());
		bot.waitUntil(new WaitForCellValue(magItemDiagram, 1, newValue), 15000);
	}

	/**
	 * IDE-1205 Make sure properties match whether component is selected in diagram or REDHAWK Explorer.
	 */
	@Test
	public void changePropertiesInDiagram() {
		SWTBotGefEditor editor = launchDiagram();
		List<String> parentPath = new ArrayList<String>();
		Collections.addAll(parentPath, getWaveformOrNodeParent());
		parentPath.add(getWaveformOrNodeName());
		final String[] PARENT_PATH = parentPath.toArray(new String[parentPath.size()]);

		// Open properties view
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();

		// Select resource in the diagram
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(resourceA().getShortName(1));
		componentPart.select();

		// Verify value; change it to double that value
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, propertiesTabName());
		SWTBotTreeItem magItemDiagram = propTree.getTreeItem(resourceA_doubleProperty());
		Assert.assertEquals(magItemDiagram.cell(1), Double.toString(resourceA_doubleProperty_startingValue()));
		magItemDiagram.select().click(1);
		final String newValue = Double.toString(resourceA_doubleProperty_startingValue() * 2);
		StandardTestActions.writeToCell(bot, magItemDiagram, 1, newValue);

		// Select resource in REDHAWK explorer tree
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, PARENT_PATH, resourceA().getShortName(1)).select().click();

		// Wait for the property to update (currently domain objects opened with the chalkboard editor have a delay)
		propTree = ViewUtils.selectPropertiesTab(bot, "Properties");
		final SWTBotTreeItem magItemExplorer = propTree.getTreeItem(resourceA_doubleProperty());
		bot.waitUntil(new WaitForCellValue(magItemExplorer, 1, newValue), 15000);
	}
}
