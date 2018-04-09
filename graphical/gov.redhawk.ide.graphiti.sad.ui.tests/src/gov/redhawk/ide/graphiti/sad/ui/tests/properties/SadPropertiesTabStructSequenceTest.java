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
package gov.redhawk.ide.graphiti.sad.ui.tests.properties;

import java.util.Arrays;
import java.util.List;

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
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public class SadPropertiesTabStructSequenceTest extends UITest {

	private static final String FILE_READER = "rh.FileReader";
	private static final String FILE_READER_1 = "FileReader_1";
	private static final String FILE_READER_2 = "FileReader_2";

	private SWTBot editorBot;

	@Before
	public void before() throws Exception {
		super.before();

		WaveformUtils.createNewWaveform(bot, "SadPropertiesTabStructSeqTest", FILE_READER);
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor("SadPropertiesTabStructSeqTest");
		DiagramTestUtils.addFromPaletteToDiagram(editor, FILE_READER, 150, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, FILE_READER_2);

		editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
	}

	@Test
	public void addStructValue() {
		// Get the tree item for a structSequence property
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), Arrays.asList(FILE_READER_1, "file_status"));

		// Activate the "SAD Value" column
		treeItem.click(SadAbstractPropertiesTabTest.COLUMN_VALUE);
		treeItem.click(SadAbstractPropertiesTabTest.COLUMN_VALUE);

		// Hit 'space' to bring up the edit property dialog
		Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
		keyboard.pressShortcut(Keystrokes.SPACE);

		// Add a new struct value
		editorBot.waitUntil(Conditions.shellIsActive("Edit Property Value"));
		SWTBotShell shell = editorBot.shell("Edit Property Value");
		shell.bot().buttonWithTooltip("Add").click();
		shell.bot().button("Finish").click();
		editorBot.waitUntil(Conditions.shellCloses(shell));

		String value = treeItem.cell(SadAbstractPropertiesTabTest.COLUMN_VALUE);
		Assert.assertEquals("Struct Value was not added", "[1]", value);
	}

	/**
	 * Tests settings the external property ID
	 */
	@Test
	public void setExternal() {
		List<String> path = Arrays.asList(FILE_READER_2, "default_sri_keywords");
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(SadAbstractPropertiesTabTest.COLUMN_NAME);
		StandardTestActions.writeToCell(editorBot, treeItem, SadAbstractPropertiesTabTest.COLUMN_EXT_ID, "ext");
		Assert.assertEquals("External property ID is incorrect", "ext", treeItem.cell(SadAbstractPropertiesTabTest.COLUMN_EXT_ID));
	}
}
