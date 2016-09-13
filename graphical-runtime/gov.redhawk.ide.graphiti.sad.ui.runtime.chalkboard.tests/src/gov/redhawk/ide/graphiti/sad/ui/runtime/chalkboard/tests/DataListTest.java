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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DataListTest extends UIRuntimeTest {
	private static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	private static final String SIGGEN = "rh.SigGen";
	private static final String SIGGEN_1 = "SigGen_1";
	private static final String SIGGEN_OUT = "dataShort_out";

	private RHSWTGefBot gefBot;
	private RHBotGefEditor editor;

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHSWTGefBot();
	}

	/**
	 * IDE-1322 - DataList does not display complex data streams correctly
	 */
	@Test
	public void dataListComplexAcquireTest() {
		String newFreq = "25";
		String newShape = "sawtooth";

		// TODO: CHECKSTYLE:OFF
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.waitForComponentState(gefBot, editor, SIGGEN_1, ComponentState.STOPPED);

		final SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, "Properties");
		editor.getEditPart(SIGGEN_1).click().select();
		SWTBotTreeItem freqTreeItem = propTree.getTreeItem("frequency");
		StandardTestActions.writeToCell(gefBot, freqTreeItem, 1, newFreq);

		Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
		int numTimesToTry = 10;
		for (int i = 0; i <= numTimesToTry; i++) {
			SWTBotTreeItem shapeTreeItem = propTree.getTreeItem("shape");
			shapeTreeItem.select();
			shapeTreeItem.click(1);
			if (newShape.equals(shapeTreeItem.cell(1))) {
				keyboard.pressShortcut(Keystrokes.CR);
				break;
			}
			if (i == numTimesToTry) {
				Assert.fail("SigGen Shape property could not be set to " + newShape);
			}
			keyboard.pressShortcut(Keystrokes.DOWN);
			keyboard.pressShortcut(Keystrokes.CR);
		}

		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN_1);
		DiagramTestUtils.displayDataListViewOnComponentPort(editor, SIGGEN_1, SIGGEN_OUT);

		SWTBotView dataListView = ViewUtils.getDataListView(gefBot);
		dataListView.show();
		SWTBot dataListBot = dataListView.bot();

		dataListBot.text(0).setText("10");
		dataListBot.comboBoxWithLabel("Number of Dimensions:").setSelection(1);
		dataListBot.buttonWithTooltip("Start Acquire").click();

		synchronized (bot) {
			try {
				bot.wait(2000);
			} catch (Exception e) {
				// PASS
			}
		}

		SWTBotTable table = dataListBot.table(0);

		String expectedResults[][] = { { "-00", "-99" }, { "-98", "-97" }, { "-96", "-95" }, { "-94", "-93" }, { "-92", "-91" } };
		for (int i = 0; i < expectedResults.length; i++) {
			for (int j = 0; j < expectedResults[i].length; j++) {
				// 'j' is incremented for the table to avoid the id field
				Assert.assertEquals("DataList did not display expected results", expectedResults[i][j], table.cell(i, j + 1));
			}
		}
	}

	@After
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
	}
}
