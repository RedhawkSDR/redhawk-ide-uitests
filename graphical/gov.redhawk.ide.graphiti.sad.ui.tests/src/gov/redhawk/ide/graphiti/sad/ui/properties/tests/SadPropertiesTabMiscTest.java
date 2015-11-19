/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.sad.ui.properties.tests;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
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

public class SadPropertiesTabMiscTest extends UITest {

	private static final String SIG_GEN = "rh.SigGen";
	protected static final String SIG_GEN_1 = "SigGen_1";
	private static final String DATA_CONVERTER = "rh.DataConverter";

	private static final String SIG_GEN_DOUBLE = "frequency";

	private static final int COLUMN_VALUE = 3;

	private SWTBot editorBot;

	@Before
	public void before() throws Exception {
		super.before();

		WaveformUtils.createNewWaveform(bot, "SadAbstractPropertiesTabTest", SIG_GEN);
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor("SadAbstractPropertiesTabTest");
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 150, 0);

		editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
	}

	/**
	 * IDE-1075 Ensure the editor doesn't collapse everything between edits
	 */
	@Test
	public void noCollapseBetweenEdits() {
		// Write a value in a cell
		List<String> path = Arrays.asList(SIG_GEN_1, SIG_GEN_DOUBLE);
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		StandardTestActions.writeToCell(editorBot, treeItem, COLUMN_VALUE, "1.23");
		Assert.assertEquals("1.23", treeItem.cell(COLUMN_VALUE));
		
		// Ensure things didn't collapse
		Assert.assertTrue(editorBot.tree().getTreeItem(SIG_GEN_1).isExpanded());
	}
}
