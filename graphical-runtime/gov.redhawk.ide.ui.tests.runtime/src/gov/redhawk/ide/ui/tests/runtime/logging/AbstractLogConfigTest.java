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
package gov.redhawk.ide.ui.tests.runtime.logging;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;

public abstract class AbstractLogConfigTest extends UIRuntimeTest {

	/**
	 * Launch the resource whose logging configuration we want to edit.
	 * @return The SWTBotTreeItem for the resource in the REDHAWK Explorer
	 */
	protected abstract SWTBotTreeItem launchLoggingResource();

	/**
	 * @return The runtime name of the launched logging resource
	 */
	protected abstract String getLoggingResourceName();

	/**
	 * @return The title of the logging resource's console
	 */
	protected abstract String getConsoleTitle();

	/**
	 * @return If tailing the resource's log is allowed
	 */
	protected abstract boolean canTailLog();

	/**
	 * IDE-1009 Test setting the log level
	 */
	@Test
	public void logLevel() {
		// Launch the test resource, open the log level dialog
		SWTBotTreeItem resourceTreeItem = launchLoggingResource();
		ConsoleUtils.disableAutoShowConsole();
		resourceTreeItem.contextMenu().menu("Logging", "Log Level").click();

		// Change log level to TRACE
		SWTBotShell shell = bot.shell("Set Debug Level");
		shell.setFocus();
		SWTBot dialogBot = shell.bot();
		Assert.assertNotEquals("Should not start at TRACE logging level", "TRACE", dialogBot.comboBox().selection());
		dialogBot.comboBox().setSelection("TRACE");
		dialogBot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		// Wait for log level to get changed
		try {
			SWTBotShell progressShell = bot.shell("Progress Information");
			bot.waitUntil(Conditions.shellCloses(progressShell));
		} catch (WidgetNotFoundException e) {
			// The dialog may be too quick - it's okay if we don't see it
		}

		// Open the log level dialog again, ensure we're at TRACE, change to INFO
		resourceTreeItem.contextMenu().menu("Logging", "Log Level").click();
		shell = bot.shell("Set Debug Level");
		shell.setFocus();
		dialogBot = shell.bot();
		Assert.assertEquals("TRACE", dialogBot.comboBox().selection());
		dialogBot.comboBox().setSelection("INFO");
		dialogBot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	/**
	 * IDE-1011 Test editing a running resource's logging configuration
	 */
	@Test
	public void logConfigEditorTest() {
		// Launch the test resource, open the log config editor
		SWTBotTreeItem resourceTreeItem = launchLoggingResource();
		String consoleTitle = getConsoleTitle();
		ConsoleUtils.disableAutoShowConsole();
		resourceTreeItem.contextMenu().menu("Logging", "Edit Log Config").click();

		// Handle warning dialog
		SWTBotShell warningShell = null;
		try {
			warningShell = bot.shell("Opening Error Log Config");
		} catch (WidgetNotFoundException e) {
			// PASS
		} finally {
			if (warningShell != null) {
				warningShell.bot().button("Yes").click();
				bot.waitUntil(Conditions.shellCloses(warningShell));
			}
		}

		// Make sure the editor comes up (check for tab title)
		SWTBotEditor editor = bot.editorByTitle("Edit Log Config");

		// Refresh the resource, then check the console view and make sure that no TRACE messages show up (5 seconds)
		resourceTreeItem.contextMenu().menu("Refresh").click();
		try {
			bot.waitUntil(new TraceInConsoleCondition(consoleTitle));
			Assert.fail("TRACE messages found in console view.  Logging should not be set to TRACE as default");
		} catch (TimeoutException e) {
			// PASS - We don't expect TRACE messages at this point
		}

		// Make an edit to the editor (change log level to Trace) and save
		editor.setFocus();
		String text = editor.toTextEditor().getText();
		text = text.replace("INFO", "TRACE");
		editor.toTextEditor().setText(text);
		editor.save();

		// Refresh the resource. Wait for trace messages to show up.
		resourceTreeItem.contextMenu().menu("Refresh").click();
		bot.waitUntil(new TraceInConsoleCondition(consoleTitle));

		// Close editor, re-open
		editor.close();
		resourceTreeItem.contextMenu().menu("Logging", "Edit Log Config").click();
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
		bot.sleep(250);
		SWTBotView consoleView = ConsoleUtils.showConsole(bot, consoleTitle);
		consoleView.toolbarButton("Clear Console").click();

		// Refresh the resource. Make sure the console view no longer pushes Trace messages (10 seconds)
		resourceTreeItem.contextMenu().menu("Refresh").click();
		try {
			bot.waitUntil(new TraceInConsoleCondition(consoleTitle));
			Assert.fail("TRACE messages found in console view.  Logging should not be set to TRACE as default");
		} catch (TimeoutException e) {
			// PASS - We don't expect TRACE messages to at this point
		}

		editor.close();
	}

	/**
	 * IDE-1010 View log ('tail') of a running resource.
	 */
	@Test
	public void tailLog() {
		// Launch the test resource, open the log level dialog if applicable
		SWTBotTreeItem resourceTreeItem = launchLoggingResource();
		ConsoleUtils.disableAutoShowConsole();
		SWTBotMenu menu = resourceTreeItem.contextMenu().menu("Logging", "Tail Log");
		if (canTailLog()) {
			menu.click();
		} else {
			boolean enabled = menu.isEnabled();
			menu.hide();
			Assert.assertFalse(enabled);
			return;
		}

		// Configure for TRACE at root level
		SWTBotShell shell = bot.shell("View log for " + getLoggingResourceName());
		shell.bot().comboBox().setSelection("TRACE");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		// Check the console
		ConsoleUtils.waitForConsole(bot, "Log events on channel");
		ConsoleUtils.stopLogging(bot, "Log events on channel");
	}

	private class TraceInConsoleCondition extends DefaultCondition {

		private String consoleTitle;

		public TraceInConsoleCondition(String consoleTitle) {
			this.consoleTitle = consoleTitle;
		}

		@Override
		public void init(SWTBot bot) {
			super.init((SWTWorkbenchBot) bot);
		}

		@Override
		public boolean test() throws Exception {
			return ConsoleUtils.checkConsoleContents((SWTWorkbenchBot) bot, consoleTitle, "TRACE");
		}

		@Override
		public String getFailureMessage() {
			return "'TRACE' was not found in the text of the console";
		}
	}
}
