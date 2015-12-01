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
package gov.redhawk.ide.graphiti.sad.ui.tests.properties;

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

public abstract class SadAbstractPropertiesTabTest extends UITest {

	private static final String SIG_GEN = "rh.SigGen";
	protected static final String SIG_GEN_1 = "SigGen_1";
	private static final String DATA_CONVERTER = "rh.DataConverter";
	protected static final String DATA_CONVERTER_1 = "DataConverter_1";

	private static final int COLUMN_NAME = 0;
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

	@Test
	public void componentsPresent() {
		SWTBotTreeItem[] topLevelItems = editorBot.tree().getAllItems();
		Assert.assertEquals(2, topLevelItems.length);
		Assert.assertEquals(SIG_GEN_1, topLevelItems[0].cell(COLUMN_NAME));
		Assert.assertEquals(DATA_CONVERTER_1, topLevelItems[1].cell(COLUMN_NAME));
	}

	protected abstract List<String> getBooleanPath();

	@Test
	public void setBoolean() {
		List<String> path = getBooleanPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		setPropertyEnum(treeItem, "true");
		setPropertyEnum(treeItem, "false");
		setPropertyEnum(treeItem, "");
	}

	protected abstract List<String> getDoublePath();

	@Test
	public void setDouble() {
		List<String> path = getDoublePath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		setPropertyValue(treeItem, "abc", "");
		setPropertyValue(treeItem, "123", "123");
		setPropertyValue(treeItem, "4.56", "4.56");
		setPropertyValue(treeItem, "-7.8e9", "-7.8e9");
	}

	protected abstract List<String> getEnumPath();

	@Test
	public void setEnum() {
		List<String> path = getEnumPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		setPropertyEnum(treeItem, "Complex");
		setPropertyEnum(treeItem, "");
	}

	protected abstract List<String> getLongPath();

	@Test
	public void setLong() {
		List<String> path = getLongPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		setPropertyValue(treeItem, "abc", "");
		setPropertyValue(treeItem, "123", "123");
		setPropertyValue(treeItem, "4.56", "123");
		setPropertyValue(treeItem, "-7.8e9", "123");
	}

	protected abstract List<String> getStringPath();

	@Test
	public void setString() {
		List<String> path = getStringPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		setPropertyValue(treeItem, "abc", "abc");
		setPropertyValue(treeItem, "123", "123");
	}

	private void setPropertyValue(SWTBotTreeItem propertyItem, String typeText, String expectedCellValue) {
		StandardTestActions.writeToCell(editorBot, propertyItem, COLUMN_VALUE, typeText);
		String msg = String.format("Property '%s' set to wrong value", propertyItem.cell(COLUMN_NAME));
		Assert.assertEquals(msg, expectedCellValue, propertyItem.cell(COLUMN_VALUE));
	}

	private void setPropertyEnum(final SWTBotTreeItem propertyItem, String selectText) {
		StandardTestActions.selectXViewerListFromCell(bot, propertyItem, COLUMN_VALUE, selectText);
		String msg = String.format("Property '%s' set to wrong value", propertyItem.cell(COLUMN_NAME));
		Assert.assertEquals(msg, selectText, propertyItem.cell(COLUMN_VALUE));
	}
}
