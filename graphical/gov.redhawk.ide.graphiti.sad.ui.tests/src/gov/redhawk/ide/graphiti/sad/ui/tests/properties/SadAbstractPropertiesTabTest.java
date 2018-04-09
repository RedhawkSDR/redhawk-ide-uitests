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

import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public abstract class SadAbstractPropertiesTabTest extends UITest {

	protected static final int COLUMN_NAME = 0;
	protected static final int COLUMN_EXT_ID = 1;
	protected static final int COLUMN_VALUE = 3;

	private SWTBot editorBot;

	protected SWTBot getEditorBot() {
		return editorBot;
	}

	@Before
	public void before() throws Exception {
		super.before();

		WaveformUtils.createNewWaveform(bot, "SadAbstractPropertiesTabTest", null);
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor("SadAbstractPropertiesTabTest");
		addComponents(editor);

		editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
	}

	/**
	 * Add the test components from the palette. Diagram is open when this code is invoked.
	 * @param editor
	 */
	protected abstract void addComponents(RHBotGefEditor editor);

	/**
	 * Should test that the component instances are present in the properties tree.
	 */
	@Test
	public abstract void componentsPresent();

	protected abstract List<String> getBooleanPath();

	@Test
	public void setBoolean() {
		List<String> path = getBooleanPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		setPropertyEnum(treeItem, "true");
		setPropertyEnum(treeItem, "false");
		setPropertyEnum(treeItem, "");
	}

	protected abstract List<String> getCharPath();

	@Test
	public void setChar() {
		List<String> path = getCharPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		setPropertyValue(treeItem, "a", "a");
		setPropertyValue(treeItem, "bb", "a");
	}

	protected abstract List<String> getDoublePath();

	@Test
	public void setDouble() {
		List<String> path = getDoublePath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		setPropertyValue(treeItem, "abc", "");
		setPropertyValue(treeItem, "123", "123");
		setPropertyValue(treeItem, "4.56", "4.56");
		setPropertyValue(treeItem, "-7.8e9", "-7.8e9");
		setPropertyValue(treeItem, "-321", "-321");
	}

	protected abstract List<String> getEnumPath();

	protected abstract List<String> getEnumValues();

	/**
	 * IDE-1082
	 */
	@Test
	public void setEnum() {
		List<String> path = getEnumPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		Assert.assertTrue(getEnumValues().size() > 0);
		for (String value : getEnumValues()) {
			setPropertyEnum(treeItem, value);
		}
		setPropertyEnum(treeItem, "");
	}

	protected abstract List<String> getFloatPath();

	@Test
	public void setFloat() {
		List<String> path = getFloatPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		setPropertyValue(treeItem, "abc", "");
		setPropertyValue(treeItem, "123", "123");
		setPropertyValue(treeItem, "4.56", "4.56");
		setPropertyValue(treeItem, "-7.8e9", "-7.8e9");
		setPropertyValue(treeItem, "-321", "-321");
	}

	protected abstract List<String> getLongPath();

	@Test
	public void setLong() {
		List<String> path = getLongPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		setPropertyValue(treeItem, "abc", "");
		setPropertyValue(treeItem, "123", "123");
		setPropertyValue(treeItem, "4.56", "123");
		setPropertyValue(treeItem, "-7.8e9", "123");
		setPropertyValue(treeItem, "-321", "-321");
	}

	protected abstract List<String> getLongLongPath();

	@Test
	public void setLongLong() {
		List<String> path = getLongLongPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		setPropertyValue(treeItem, "abc", "");
		setPropertyValue(treeItem, "123", "123");
		setPropertyValue(treeItem, "4.56", "123");
		setPropertyValue(treeItem, "-7.8e9", "123");
		setPropertyValue(treeItem, "-321", "-321");
	}

	// TODO: objref
	// TODO: octet
	// TODO: short
	// TODO: octet

	protected abstract List<String> getStringPath();

	@Test
	public void setString() {
		List<String> path = getStringPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		setPropertyValue(treeItem, "abc", "abc");
		setPropertyValue(treeItem, "123", "123");
	}

	// TODO: ulong
	// TODO: ulonglong
	// TODO: ushort

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

	protected abstract List<String> getExternalPath();

	/**
	 * Tests settings the external property ID
	 */
	@Test
	public void setExternal() {
		List<String> path = getExternalPath();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		treeItem.click(COLUMN_NAME);
		StandardTestActions.writeToCell(editorBot, treeItem, COLUMN_EXT_ID, "ext");
		Assert.assertEquals("External property ID is incorrect", "ext", treeItem.cell(COLUMN_EXT_ID));
	}
}
