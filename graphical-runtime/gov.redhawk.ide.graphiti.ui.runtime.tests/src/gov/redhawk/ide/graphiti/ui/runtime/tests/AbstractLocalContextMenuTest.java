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

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public abstract class AbstractLocalContextMenuTest extends AbstractContextMenuTest {

	/**
	 * Local runtime functionality will be checked (release, terminate, etc).
	 */
	protected abstract ComponentDescription getLocalTestComponent();

	private ComponentDescription localTestComponent = getLocalTestComponent();

	/**
	 * Tests using the context menu to release the resource.
	 * IDE-961 "Release" context menu
	 * IDE-971 Release shouldn't be undoable
	 */
	@Test
	public void release() {
		RHBotGefEditor editor = launchDiagram();

		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(localTestComponent.getShortName(1)));
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, localTestComponent.getShortName(1));
		Assert.assertFalse("Editor dirty after releasing a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after releasing a resource", MenuUtils.isUndoDisabled(bot));
	}

	/**
	 * Tests using the context menu to terminate the resource.
	 * IDE-961 "Terminate" context menu for runtime
	 * IDE-971 / IDE-1038 Terminate should not be undoable
	 * IDE-1327 Terminate must work for <b>components</b> which are <b>not</b> in the <b>Sandbox Chalkboard</b>
	 */
	@Test
	public void terminate() {
		RHBotGefEditor editor = launchDiagram();

		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(localTestComponent.getShortName(1)));
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, localTestComponent.getShortName(1));
		Assert.assertFalse("Editor dirty after terminating a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after terminating a resource", MenuUtils.isUndoDisabled(bot));
	}

	/**
	 * Tests using the context menu to show the console.
	 * IDE-960 "Show Console" context menu for runtime
	 * IDE-1498 Device usage name shown in console title for sandbox devices
	 */
	@Test
	public void showConsole() {
		RHBotGefEditor editor = launchDiagram();

		ConsoleUtils.disableAutoShowConsole(bot);
		DiagramTestUtils.showConsole(editor, localTestComponent.getShortName(1));
		bot.sleep(500);

		// Wait for the view, then ensure the first title is what we expect
		ViewUtils.getConsoleView(bot);
		String firstTitle = ConsoleUtils.getConsoleTitles(bot)[0];
		Assert.assertTrue("Console title doesn't contain the resource's usage name", firstTitle.contains(localTestComponent.getShortName(1)));
	}

	@Override
	protected List<String> getAbsentContextMenuOptions() {
		return Arrays.asList("Delete");
	}
}
