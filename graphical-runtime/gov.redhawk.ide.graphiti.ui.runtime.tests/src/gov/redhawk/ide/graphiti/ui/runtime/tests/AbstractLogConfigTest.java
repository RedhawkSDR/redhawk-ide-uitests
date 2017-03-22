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
package gov.redhawk.ide.graphiti.ui.runtime.tests;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public abstract class AbstractLogConfigTest extends UIRuntimeTest {

	protected RHSWTGefBot gefBot; // SUPPRESS CHECKSTYLE INLINE
	private SWTBotTreeItem resourceTreeItem;

	/**
	 * Launch the resource whose logging configuration we want to edit.
	 * @return - The SWTBotTreeItem for the resource in the REDHAWK Explorer
	 */

	protected abstract SWTBotTreeItem launchLoggingResource();
	protected abstract SWTBotView showConsole();

	protected abstract SWTBotGefEditPart openResourceDiagram();

	protected abstract SWTBotGefEditor getDiagramEditor();

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHSWTGefBot();
	}

	/**
	 * IDE-1011 Test editing a running resource's logging configuration
	 */
	@Test
	public void logConfigEditorTest() {
		////////////////
		// TEST FROM REDHAWK EXPLORER

		// Launch the test resource
		resourceTreeItem = launchLoggingResource();

		// Open the log config editor via the explorer view
		resourceTreeItem.contextMenu().menu("Logging", "Edit Log Config").click();

		// Handle warning dialog
		bot.waitUntil(Conditions.shellIsActive("Opening Error Log Config"));
		SWTBotShell warningShell = bot.shell("Opening Error Log Config");
		warningShell.bot().button("Yes").click();

		// Make sure the editor comes up (check for tab title)
		SWTBotEditor editor = bot.editorByTitle("Edit Log Config");

		// Refresh the resource, then check the console view and make sure that no TRACE messages show up (5 seconds)
		resourceTreeItem.contextMenu("Refresh");
		final SWTBotView consoleView = showConsole();
		try {
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					return checkConsoleViewForString(consoleView, "TRACE");
				}

				@Override
				public String getFailureMessage() {
					return "Failure is expected, Logging should not be set to TRACE as default";
				}
			});
			Assert.fail("TRACE messages found in console view.  Logging should not be set to TRACE as default");
		} catch (TimeoutException e) {
			// PASS - We don't expect TRACE messages to be thrown at this point
		}

		// Make an edit to the editor (change log level to Trace) and save
		editor.setFocus();
		String text = editor.toTextEditor().getText();
		text = text.replace("INFO", "TRACE");
		editor.toTextEditor().setText(text);
		editor.save();

		// Refresh the resource. Focus console view and wait for trace messages to show up.
		resourceTreeItem.contextMenu("Refresh");
		consoleView.setFocus();
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return checkConsoleViewForString(consoleView, "TRACE");
			}

			@Override
			public String getFailureMessage() {
				return "Edits to the config log did not take effect.  Expected TRACE messages to be output to the console view";
			}
		});

		// Close editor
		editor.close();

		////////////////
		// TEST FROM GRAPHITI DIAGRAM
		SWTBotGefEditPart resourceEditPart = openResourceDiagram();
		resourceEditPart.select().click();
		getDiagramEditor().clickContextMenu("Edit Log Config");
		editor = bot.editorByTitle("Edit Log Config");
		editor.setFocus();

		// Confirm that the previous edit is still in effect
		String oldText = text;
		text = editor.toTextEditor().getText();
		Assert.assertTrue("Previous edits to the config log were not persisted", oldText.equals(text));

		// Change the log level back to default
		text = text.replace("TRACE", "INFO");
		editor.toTextEditor().setText(text);
		editor.save();

		// Clear the console view
		consoleView.setFocus();
		bot.sleep(250);
		consoleView.toolbarButton("Clear Console").click();

		// Refresh the resource. Make sure the console view no longer pushes Trace messages (10 seconds)
		resourceTreeItem.contextMenu("Refresh");
		try {
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					return checkConsoleViewForString(consoleView, "TRACE");
				}

				@Override
				public String getFailureMessage() {
					return "Failure is expected, Logging should not be set to TRACE as default";
				}
			});
			Assert.fail("TRACE messages found in console view.  Logging should not be set to TRACE as default");
		} catch (TimeoutException e) {
			// PASS - We don't expect TRACE messages to be thrown at this point
		}

		editor.close();
		getDiagramEditor().close();
	}

	/**
	 * Returns true if the targetString is part of the console views output
	 * @param consoleView
	 * @param targetString
	 * @return
	 */
	private boolean checkConsoleViewForString(SWTBotView consoleView, String targetString) {
		List<String> viewContents = consoleView.bot().styledText().getLines();
		boolean stringFound = false;
		for (String line : viewContents) {
			if (line.contains(targetString)) {
				stringFound = true;
				break;
			}
		}

		return stringFound;
	}
}
