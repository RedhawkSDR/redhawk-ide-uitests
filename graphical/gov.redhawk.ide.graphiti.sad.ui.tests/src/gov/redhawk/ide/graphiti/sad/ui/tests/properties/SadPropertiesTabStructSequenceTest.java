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
package gov.redhawk.ide.graphiti.sad.ui.tests.properties;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public class SadPropertiesTabStructSequenceTest extends UITest {

	private static final String FILE_READER = "rh.FileReader";
	private static final String FILE_READER_1 = "FileReader_1";

	private static final int VALUE_COLUMN = 3;

	private SWTBot editorBot;

	@Before
	public void before() throws Exception {
		super.before();

		WaveformUtils.createNewWaveform(bot, "SadPropertiesTabStructSeqTest", FILE_READER);
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor("SadPropertiesTabStructSeqTest");

		editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
	}

	@Test
	public void addStructValue() {
		// Get the tree item for a structSequence property
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), Arrays.asList(FILE_READER_1, "file_status"));

		// Activate the "SAD Value" column
		treeItem.click(VALUE_COLUMN);
		treeItem.click(VALUE_COLUMN);

		// Hit 'space' to bring up the edit property dialog
		Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
		keyboard.pressShortcut(Keystrokes.SPACE);

		// Add a new struct value
		editorBot.waitUntil(Conditions.shellIsActive("Edit Property Value"));
		SWTBotShell shell = editorBot.shell("Edit Property Value");
		shell.bot().buttonWithTooltip("Add").click();
		shell.bot().button("Finish").click();
		editorBot.waitUntil(Conditions.shellCloses(shell));

		String value = treeItem.cell(VALUE_COLUMN);
		Assert.assertEquals("Struct Value was not added", "[1]", value);

	}
}
