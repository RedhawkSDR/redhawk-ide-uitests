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
package gov.redhawk.ide.properties.view.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.prf.Values;

public abstract class AbstractPropertiesViewDesignTest extends UITest {

	/**
	 * Used in the property edit tests. Must be a String numeral, to be valid with both all property types. Booleans are
	 * special cases.
	 */
	private static final String PREPENDER = "5";

	/** A Map of all properties found in the IDE's property view */
	protected Map<String, String> propertyMap = new HashMap<String, String>();

	SWTBotGefEditor editor;
	protected String propTabName;
	protected RHSWTGefBot gefBot;
	protected Keyboard keyboard = KeyboardFactory.getSWTKeyboard();

	/**
	 * Method should take necessary steps to:
	 * - launch the test object,
	 * - select editor,
	 * - set property tab name,
	 * - do initial object selection
	 */
	protected abstract void prepareObject();

	protected abstract void selectObject();

	protected abstract void setEditor();

	protected abstract ComponentProperties getModelPropertiesFromEditor() throws IOException;

	protected abstract void writeModelPropertiesToEditor(ComponentProperties componentProps) throws IOException;

	protected abstract void setPropTabName();

	@Before
	public void beforeTest() {
		gefBot = new RHSWTGefBot();
	}

	@After
	public void afterTest() {
		propertyMap.clear();
	}

	/**
	 * Wish we had an expand all button here...
	 * @param treeItems
	 * @param propTree
	 */
	protected void populatePropertyMap(SWTBotTreeItem[] treeItems) {

		for (SWTBotTreeItem item : treeItems) {
			propertyMap.put(item.cell(0), item.cell(1));
			item.expand();
			populatePropertyMap(item.getItems());
		}

	}

	// #################### TEST METHODS ############################### //
	/**
	 * Edit property values in the properties view and confirm that they update in the respective XML file.
	 * 
	 * IDE-1202 (for child classes that use a sad.xml)
	 * IDE-1139
	 */
	@Test
	public void editPropertyViewCheckXmlTest() throws IOException, InterruptedException {
		prepareObject();

		// Just using this to expand everything, we will populate later in the test
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, propTabName);
		populatePropertyMap(propTree.getAllItems());
		propertyMap.clear();

		for (SWTBotTreeItem treeItem : propTree.getAllItems()) {
			String propName = treeItem.cell(0);

			if (propName.matches("structSeq" + ".*")) {
				SWTBotTreeItem[] subItems = treeItem.getItems()[0].getItems();
				for (SWTBotTreeItem subItem : subItems) {
					propName = subItem.cell(0);
					if (propName.matches("structSeqStructSimpleSeq" + ".*")) {
						editSimpleSeqProperty(subItem);
					} else if (propName.matches("structSeqStructSimple" + ".*")) {
						editSimpleProperty(subItem);
					}
				}
			} else if (propName.matches("struct" + ".*")) {
				for (SWTBotTreeItem subItem : treeItem.getItems()) {
					propName = subItem.cell(0);
					if (propName.matches("structSimpleSeq" + ".*")) {
						editSimpleSeqProperty(subItem);
					} else if (propName.matches("structSimple" + ".*")) {
						editSimpleProperty(subItem);
					}
				}
			} else if (propName.matches("simpleSeq" + ".*")) {
				editSimpleSeqProperty(treeItem);
			} else if (propName.matches("simple" + ".*")) {
				editSimpleProperty(treeItem);
			}
		}

		FeatureMap xmlPropertyMap = getModelPropertiesFromEditor().getProperties();
		checkProps(xmlPropertyMap);
	}

	/**
	 * Edit property values in the respective XML file and confirm that they update in the properties view
	 * @throws IOException
	 */
	@Test
	public void editXmlCheckPropertyViewTest() throws IOException {
		prepareObject();

		// TODO: Just using this to expand everything, we will populate later in the test
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, propTabName);
		populatePropertyMap(propTree.getAllItems());
		propertyMap.clear();

		ComponentProperties componentProps = updatePropsInXml(propTree, getModelPropertiesFromEditor());
		writeModelPropertiesToEditor(componentProps);
		FeatureMap xmlPropertyMap = componentProps.getProperties();

		selectDiagramTabInEditor();
		selectObject();

		// Update property map and Check property view to see if all values are accurate
		propTree = ViewUtils.selectPropertiesTab(bot, propTabName);
		populatePropertyMap(propTree.getAllItems());
		checkProps(xmlPropertyMap);
	}

	private ComponentProperties updatePropsInXml(SWTBotTree propTree, ComponentProperties componentProps) {

		for (SWTBotTreeItem treeItem : propTree.getAllItems()) {
			String propName = treeItem.cell(0);

			if (propName.matches("structSeq" + ".*")) {
				componentProps.getStructSequenceRef().add(createStructSeq(treeItem));
			} else if (propName.matches("struct" + ".*")) {
				componentProps.getStructRef().add(createStruct(treeItem));
			} else if (propName.matches("simpleSeq" + ".*")) {
				componentProps.getSimpleSequenceRef().add(createSimpleSeq(treeItem));
			} else if (propName.matches("simple" + ".*")) {
				componentProps.getSimpleRef().add(createSimple(treeItem));
			}
		}

		return componentProps;
	}

	// #################### UTILITY COMPARISON METHODS ############################### //
	// Compares values found in the XML tab with those found in the properties view //

	private void checkProps(FeatureMap xmlPropertyMap) {
		for (Entry entry : xmlPropertyMap) {
			if (entry.getValue() instanceof SimpleRef) {
				simpleComparison((SimpleRef) entry.getValue());
			} else if (entry.getValue() instanceof SimpleSequenceRef) {
				simpleSeqComparison((SimpleSequenceRef) entry.getValue());
			} else if (entry.getValue() instanceof StructRef) {
				simpleComparison(((StructRef) entry.getValue()).getSimpleRef().get(0));
				simpleSeqComparison(((StructRef) entry.getValue()).getSimpleSequenceRef().get(0));
			} else if (entry.getValue() instanceof StructSequenceRef) {
				simpleComparison(((StructSequenceRef) entry.getValue()).getStructValue().get(0).getSimpleRef().get(0));
				simpleSeqComparison(((StructSequenceRef) entry.getValue()).getStructValue().get(0).getSimpleSequenceRef().get(0));
			}
		}
	}

	private void simpleComparison(SimpleRef simpleRef) {
		String viewValue = propertyMap.get(simpleRef.getRefID());
		Assert.assertNotNull("Property [" + simpleRef.getRefID() + "] was not found in the Properties View", viewValue);
		Assert.assertEquals("Property value for [" + simpleRef.getRefID() + "] was not consistent between Properties View and the XML", simpleRef.getValue(),
			viewValue);
	}

	private void simpleSeqComparison(SimpleSequenceRef simpleSeqRef) {
		String viewValue = propertyMap.get(simpleSeqRef.getRefID());
		Assert.assertNotNull("Property [" + simpleSeqRef.getRefID() + "] was not found in the Properties View", viewValue);
		Assert.assertEquals("Property value for [" + simpleSeqRef.getRefID() + "] was not consistent between Properties View and the XML",
			simpleSeqRef.getValues().getValue().toString(), viewValue);
	}

	// #################### UTILITY XML TAB EDIT METHODS ############################### //
	private SimpleRef createSimple(SWTBotTreeItem treeItem) {
		SimpleRef simple = PrfFactory.eINSTANCE.createSimpleRef();
		simple.setRefID(treeItem.cell(0));

		String oldValue = treeItem.cell(1);
		if ("true".equals(oldValue)) {
			simple.setValue(Boolean.FALSE.toString());
		} else if ("false".equals(oldValue)) {
			simple.setValue(Boolean.TRUE.toString());
		} else {
			simple.setValue(PREPENDER + oldValue);
		}

		return simple;
	}

	private SimpleSequenceRef createSimpleSeq(SWTBotTreeItem treeItem) {
		SimpleSequenceRef simpleSeq = PrfFactory.eINSTANCE.createSimpleSequenceRef();
		simpleSeq.setRefID(treeItem.cell(0));

		String[] tmpArray = treeItem.cell(1).substring(1, treeItem.cell(1).length() - 1).split(", ");
		Values values = PrfFactory.eINSTANCE.createValues();
		for (int i = 0; i < tmpArray.length; ++i) {
			String oldValue = tmpArray[i];
			if ("true".equals(oldValue)) {
				values.getValue().add(Boolean.FALSE.toString());
			} else if ("false".equals(oldValue)) {
				values.getValue().add(Boolean.TRUE.toString());
			} else {
				values.getValue().add(PREPENDER + oldValue);
			}
		}
		simpleSeq.setValues(values);
		return simpleSeq;
	}

	private StructRef createStruct(SWTBotTreeItem treeItem) {
		StructRef struct = PrfFactory.eINSTANCE.createStructRef();
		struct.setRefID(treeItem.cell(0));
		for (SWTBotTreeItem subItem : treeItem.getItems()) {
			String propName = subItem.cell(0);
			if (propName.matches("structSimpleSeq" + ".*")) {
				struct.getSimpleSequenceRef().add(createSimpleSeq(subItem));
			} else if (propName.matches("structSimple" + ".*")) {
				struct.getSimpleRef().add(createSimple(subItem));
			}
		}
		return struct;
	}

	private StructSequenceRef createStructSeq(SWTBotTreeItem treeItem) {
		StructSequenceRef structSeq = PrfFactory.eINSTANCE.createStructSequenceRef();
		structSeq.setRefID(treeItem.cell(0));
		structSeq.getStructValue().add(createStructValue(treeItem));
		return structSeq;
	}

	private StructValue createStructValue(SWTBotTreeItem treeItem) {
		StructValue struct = PrfFactory.eINSTANCE.createStructValue();
		SWTBotTreeItem[] subItems = treeItem.getItems()[0].getItems();
		for (SWTBotTreeItem subItem : subItems) {
			String propName = subItem.cell(0);
			if (propName.matches("structSeqStructSimpleSeq" + ".*")) {
				struct.getSimpleSequenceRef().add(createSimpleSeq(subItem));
			} else if (propName.matches("structSeqStructSimple" + ".*")) {
				struct.getSimpleRef().add(createSimple(subItem));
			}
		}
		return struct;
	}

	// #################### UTILITY PROPERTY VIEW EDIT METHODS ############################### //
	private void editSimpleProperty(SWTBotTreeItem treeItem) {
		String oldValue = treeItem.cell(1);
		String newValue;
		if ("true".equals(oldValue)) {
			treeItem.select();
			treeItem.click(1);
			newValue = Boolean.FALSE.toString();
			keyboard.pressShortcut(Keystrokes.UP);
		} else if ("false".equals(oldValue)) {
			treeItem.select();
			treeItem.click(1);
			newValue = Boolean.TRUE.toString();
			keyboard.pressShortcut(Keystrokes.DOWN);
		} else {
			newValue = PREPENDER + oldValue;
			StandardTestActions.writeToCell(gefBot, treeItem, 1, newValue);
		}

		propertyMap.put(treeItem.cell(0), newValue);
	}

	private void editSimpleSeqProperty(SWTBotTreeItem treeItem) throws InterruptedException {
		// Open the edit shell
		treeItem.select();
		treeItem.click(1);
		keyboard.pressShortcut(Keystrokes.SPACE);

		SWTBotShell editShell = bot.shell("Edit Property Value");
		editShell.setFocus();
		bot.waitUntil(Conditions.shellIsActive("Edit Property Value"));
		SWTBotTable table = editShell.bot().table();

		String[] seqValues = new String[3];
		int tableSize = table.rowCount();
		for (int i = 0; i < tableSize; ++i) {
			String oldValue = table.cell(i, 0);
			String newValue;

			if ("true".equals(oldValue)) {
				table.doubleClick(i, 0);
				bot.sleep(250);
				keyboard.pressShortcut(Keystrokes.UP);
				newValue = Boolean.FALSE.toString();
				keyboard.pressShortcut(Keystrokes.CR);
			} else if ("false".equals(oldValue)) {
				table.doubleClick(i, 0);
				bot.sleep(250);
				keyboard.pressShortcut(Keystrokes.DOWN);
				newValue = Boolean.TRUE.toString();
				keyboard.pressShortcut(Keystrokes.CR);
			} else {
				table.doubleClick(i, 0);
				newValue = PREPENDER + oldValue;
				StandardTestActions.writeToCell(gefBot, table, i, 0, newValue, true);
			}
			seqValues[i] = newValue;
		}

		propertyMap.put(treeItem.cell(0), Arrays.toString(seqValues));

		editShell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(editShell));
	}

	private void selectDiagramTabInEditor() {
		editor.bot().cTabItem("Diagram").activate();
	}
}
