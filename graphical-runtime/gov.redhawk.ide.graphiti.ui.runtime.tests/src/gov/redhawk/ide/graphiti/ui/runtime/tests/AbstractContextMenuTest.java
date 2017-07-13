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

import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.logging.ui.LogLevels;

public abstract class AbstractContextMenuTest extends UIRuntimeTest {

	/**
	 * The test component should be capable of standard runtime operations (start, stop, logging, etc). It should also
	 * have one BULKIO output port that produces data.
	 */
	protected abstract ComponentDescription getTestComponent();

	private ComponentDescription testComponent = getTestComponent();

	/**
	 * Perform all necessary setup to get the appropriate diagram open.
	 */
	protected abstract RHBotGefEditor launchDiagram();

	/**
	 * Ensure the "standard" runtime context menu items are present on a resource and appear to do something.
	 * IDE-661 Start/stop
	 * IDE-665 Monitor ports
	 * IDE-1009, IDE-1325 Set log level
	 * IDE-1010 Tail log
	 * IDE-1423 Show Properties
	 * IDE-1648 Monitor ports
	 */
	@Test
	public void standardResourceRuntimeContextMenu() {
		RHBotGefEditor editor = launchDiagram();
		String componentName = testComponent.getShortName(1);

		// Start
		DiagramTestUtils.startComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STARTED);

		// Test Log Levels (IDE-1009, IDE-1325)
		DiagramTestUtils.changeLogLevelFromDiagram(editor, componentName, LogLevels.TRACE);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, componentName, LogLevels.TRACE);

		DiagramTestUtils.changeLogLevelFromDiagram(editor, componentName, LogLevels.FATAL);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, componentName, LogLevels.FATAL);

		// Test edit log (IDE-1011)
		editor.select(componentName);
		editor.clickContextMenu("Edit Log Config");
		bot.waitUntil(Conditions.shellIsActive("Opening Error Log Config"));
		SWTBotShell warningShell = bot.shell("Opening Error Log Config");
		warningShell.bot().button("Yes").click();
		SWTBotEditor logCfgEditor = bot.editorByTitle("Edit Log Config");
		logCfgEditor.close();
		editor.setFocus();

		// Test tail log (IDE-1010)
		if (supportsTailLog()) {
			DiagramTestUtils.tailLog(editor, componentName, "", LogLevels.DEBUG);
			ConsoleUtils.waitForConsole(bot, "Log events on channel");
			ConsoleUtils.stopLogging(bot, "Log events on channel");
		}

		// Test monitor ports context menu
		DiagramTestUtils.displayPortMonitorView(editor, componentName);
		ViewUtils.waitUntilPortMonitorViewPopulates(bot, componentName);
		SWTBotView monitorView = ViewUtils.getPortMonitorView(bot);
		monitorView.close();

		// Show Properties
		DiagramTestUtils.showProperties(editor, componentName);
		SWTBotView view = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		Assert.assertTrue(view.isActive());

		// Stop
		DiagramTestUtils.stopComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STOPPED);
	}

	/**
	 * Ensure the "standard" runtime context menu items are present on a port and appear to do something.
	 * IDE-662 Plotting
	 * IDE-663 Play port
	 * IDE-664 Display SRI
	 * IDE-665 Monitor port
	 * IDE-666 Snapshot
	 * IDE-667 Data list
	 * IDE-1189 Connect port
	 * IDE-1423 Show properties
	 * IDE-1648 Monitor ports
	 */
	@Test
	public void standardPortRuntimeContextMenu() {
		RHBotGefEditor editor = launchDiagram();
		bot.sleep(1000);
		String componentName = testComponent.getShortName(1);
		String portName = testComponent.getOutPort(0);

		// Connect port
		DiagramTestUtils.showConnectionWizardForUsesPort(editor, componentName, portName);
		SWTBotShell shell = bot.shell("Connect");
		SWTBotTreeItem[] items = shell.bot().tree().getAllItems();
		Assert.assertEquals(1, items.length);
		Assert.assertEquals(portName, items[0].getText());
		shell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		// Show properties from port
		DiagramTestUtils.showProperties(editor, componentName, portName);
		SWTBotView view = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		Assert.assertTrue(view.isActive());

		// Start so we'll have data flowing
		DiagramTestUtils.startComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STARTED);

		// Test plot context menu
		editor.setFocus();
		DiagramTestUtils.plotPortDataOnComponentPort(editor, componentName, portName);
		SWTBotView plotView = ViewUtils.getPlotView(bot);
		plotView.close();

		// Test data list context menu
		DiagramTestUtils.displayDataListViewOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilDataListViewDisplays(bot);
		ViewUtils.startAquireOnDataListView(bot);
		ViewUtils.waitUntilDataListViewPopulates(bot);
		SWTBotView dataListView = ViewUtils.getDataListView(bot);
		dataListView.close();

		// Test monitor ports context menu
		DiagramTestUtils.displayPortMonitorViewOnUsesPort(editor, componentName, portName);
		ViewUtils.waitUntilPortMonitorViewPopulates(bot, componentName);
		SWTBotView monitorView = ViewUtils.getPortMonitorView(bot);
		monitorView.close();

		// Test SRI view context menu
		DiagramTestUtils.displaySRIDataOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilSRIViewPopulates(bot);
		SWTBotView sriView = ViewUtils.getSRIView(bot);
		Assert.assertEquals("Expected streamID property in row 0", "streamID: ", sriView.bot().tree().cell(0, "Property: "));
		String streamID = sriView.bot().tree().cell(0, "Value: ");
		Assert.assertNotNull(streamID);
		Assert.assertTrue(!streamID.isEmpty());
		sriView.close();

		// Test audio/play port context menu
		DiagramTestUtils.playPortDataOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilAudioViewPopulates(bot);
		SWTBotView audioView = ViewUtils.getAudioView(bot);
		String item = audioView.bot().list().getItems()[0];
		Assert.assertTrue(testComponent.getShortName() + " not found in Audio Port Playback", item.matches(componentName + ".*"));
		audioView.close();

		// Test snapshot context menu
		DiagramTestUtils.displaySnapshotDialogOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilSnapshotDialogDisplays(bot);
		SWTBotShell snapshotDialog = ViewUtils.getSnapshotDialog(bot);
		Assert.assertNotNull(snapshotDialog);
		snapshotDialog.close();
	}

	/**
	 * Tests that undo does not work after certain context menu operations.
	 * IDE-1038 Start and stop should not be undoable
	 * IDE-1065 Shouldn't be able to undo from initial diagram load, start/stop, various state transitions
	 */
	@Test
	public void noUndoForStartStopAndTransitions() {
		RHBotGefEditor editor = launchDiagram();
		String componentName = testComponent.getShortName(1);

		Assert.assertFalse("Undo menu is enabled", MenuUtils.isUndoDisabled(bot));

		// Perform start -> stop -> start, so we don't have to worry about initial state
		DiagramTestUtils.startComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STARTED);
		Assert.assertFalse("Editor dirty after starting a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after starting a resource", MenuUtils.isUndoDisabled(bot));

		DiagramTestUtils.stopComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STOPPED);
		Assert.assertFalse("Editor dirty after stopping a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after stopping a resource", MenuUtils.isUndoDisabled(bot));

		DiagramTestUtils.startComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STARTED);
		Assert.assertFalse("Editor dirty after starting a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after starting a resource", MenuUtils.isUndoDisabled(bot));
	}

	/**
	 * Tests that certain context menu options are <b>not</b> present in the context menu.
	 * Depends on sub-class implementing {@link #getAbsentContextMenuOptions()}.
	 */
	@Test
	public void absentContextMenuOptions() {
		RHBotGefEditor editor = launchDiagram();

		SWTBotGefEditPart editPart = editor.getEditPart(testComponent.getShortName(1));
		editPart.select();
		List<String> removedContextOptions = getAbsentContextMenuOptions();
		for (String contextOption : removedContextOptions) {
			try {
				editor.clickContextMenu(contextOption);

				// The only way to get here is if the undesired context menu option appears
				Assert.fail("Context menu item was present, but should not be: " + contextOption);
			} catch (WidgetNotFoundException e) {
				// Expected - an error because the context menu item is not present
				Assert.assertEquals(contextOption, e.getMessage());
			}
		}
	}

	/**
	 * IDE-961 No "Delete" option for runtime
	 * IDE-1196 "Show Console" should not be present for domain objects
	 * @return
	 */
	protected List<String> getAbsentContextMenuOptions() {
		return Arrays.asList("Delete", "Release", "Terminate", "Show Console");
	}

	/**
	 * If the test supports tailing the log (used by {@link #standardRuntimeContextMenuOptions()} only).
	 * IDE-1010 Tail log
	 * @return True if supported
	 */
	protected boolean supportsTailLog() {
		return true;
	}
}
