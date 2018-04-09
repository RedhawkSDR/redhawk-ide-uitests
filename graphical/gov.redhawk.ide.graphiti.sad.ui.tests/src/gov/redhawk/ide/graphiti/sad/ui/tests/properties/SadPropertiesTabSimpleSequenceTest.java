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

public class SadPropertiesTabSimpleSequenceTest extends UITest {

	private static final String ALLPROPS = "AllPropertyTypesComponent";
	private static final String ALLPROPS_2 = "AllPropertyTypesComponent_2";

	private SWTBot editorBot;

	@Before
	public void before() throws Exception {
		super.before();

		WaveformUtils.createNewWaveform(bot, "SadPropertiesTabStructSeqTest", ALLPROPS);
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor("SadPropertiesTabStructSeqTest");
		DiagramTestUtils.addFromPaletteToDiagram(editor, ALLPROPS, 150, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, ALLPROPS_2);

		editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
	}


	/**
	 * IDE-2170 Tests settings the external property ID
	 */
	@Test
	public void setExternal() {
		List<String> path = Arrays.asList(ALLPROPS_2, "simpleSeqString");
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(SadAbstractPropertiesTabTest.COLUMN_NAME);
		StandardTestActions.writeToCell(editorBot, treeItem, SadAbstractPropertiesTabTest.COLUMN_EXT_ID, "ext");
		Assert.assertEquals("External property ID is incorrect", "ext", treeItem.cell(SadAbstractPropertiesTabTest.COLUMN_EXT_ID));
	}
}
