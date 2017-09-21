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
package gov.redhawk.ide.graphiti.ui.runtime.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaSimpleSequenceProperty;
import gov.redhawk.model.sca.ScaStructProperty;
import gov.redhawk.model.sca.ScaStructSequenceProperty;
import gov.redhawk.model.sca.impl.ScaSimplePropertyImpl;
import gov.redhawk.model.sca.impl.ScaSimpleSequencePropertyImpl;
import gov.redhawk.model.sca.impl.ScaStructPropertyImpl;
import gov.redhawk.model.sca.impl.ScaStructSequencePropertyImpl;
import mil.jpeojtrs.sca.prf.AccessType;

public abstract class AbstractPropertiesViewRuntimeTest extends UIRuntimeTest {

	private static final String PROP_TAB_NAME = "Properties";

	/**
	 * Used in the property edit tests. Must be a String numeral, to be valid with both all property types. Booleans are
	 * special cases.
	 */
	protected final String PREPENDER = "5"; // SUPPRESS CHECKSTYLE shared variable

	/** A Map of all properties found in the IDE's property view */
	protected Map<String, String> propertyMap = new HashMap<String, String>(); // SUPPRESS CHECKSTYLE shared variable

	protected RHSWTGefBot gefBot; // SUPPRESS CHECKSTYLE shared variable
	protected Keyboard keyboard = KeyboardFactory.getSWTKeyboard(); // SUPPRESS CHECKSTYLE shared variable

	/**
	 * Method should take necessary steps to:
	 * - launch the test object,
	 * - select editor,
	 * - set property tab name,
	 * - do initial object selection
	 */
	protected abstract void prepareObject();

	protected abstract List<ScaAbstractProperty< ? >> getModelObjectProperties();

	@BeforeClass
	public static void disableAutoShowConsole() {
		ConsoleUtils.disableAutoShowConsole();
	}

	@Before
	public void beforeTest() {
		gefBot = new RHSWTGefBot();
	}

	@After
	public void afterTest() {
		propertyMap.clear();
		StandardTestActions.resetRefreshInterval();
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

	/**
	 * Test that object selected in the SCA Explorer correctly displays properties in properties view
	 * 
	 * IDE-1302 (for child classes that use devices)
	 * IDE-1320 (for child classes that use a diagram)
	 */
	@Test
	public void checkPropValuesAtLaunch() {
		prepareObject();

		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, PROP_TAB_NAME);
		populatePropertyMap(propTree.getAllItems());

		List<ScaAbstractProperty< ? >> modelProps = getModelObjectProperties();
		for (ScaAbstractProperty< ? > modelProp : modelProps) {
			Class< ? > propType = modelProp.getClass();

			if (propType.equals(ScaSimplePropertyImpl.class)) {
				checkSimpleProperty((ScaSimplePropertyImpl) modelProp);
			} else if (propType.equals(ScaSimpleSequencePropertyImpl.class)) {
				checkSimpleSeqProperty((ScaSimpleSequencePropertyImpl) modelProp);
			} else if (propType.equals(ScaStructPropertyImpl.class)) {
				checkStructProperty((ScaStructPropertyImpl) modelProp);
			} else if (propType.equals(ScaStructSequencePropertyImpl.class)) {
				checkStructSeqProperty((ScaStructSequencePropertyImpl) modelProp);
			} else {
				Assert.fail("Unknown property type");
			}
		}

		for (SWTBotTreeItem prop : propTree.getAllItems()) {
			propertyMap.put(prop.cell(0), prop.cell(1));
		}
	}

	/**
	 * Edit property values for a resource in the Property View and confirm that the associated model object updates
	 * 
	 * IDE-1302 (for child classes that use devices)
	 * IDE-1320 (for child classes that use a diagram)
	 * @throws InterruptedException
	 */
	@Test
	public void editPropertyViewToModel() throws InterruptedException {
		StandardTestActions.setRefreshInterval(1000);
		long readyTime = System.currentTimeMillis() + 10000;

		prepareObject();

		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, PROP_TAB_NAME);
		populatePropertyMap(propTree.getAllItems());
		List<ScaAbstractProperty< ? >> modelProps = getModelObjectProperties();

		// Ensure our changes to the refresh interval have all taken effect (previous tasks have already come up for
		// re-scheduling)
		long sleepTime = readyTime - System.currentTimeMillis();
		if (sleepTime > 0) {
			Thread.sleep(sleepTime);
		}

		for (ScaAbstractProperty< ? > modelProp : modelProps) {
			String name = modelProp.getName();
			Assert.assertNotNull("Model property name not found", name);
			SWTBotTreeItem treeItem = propTree.getTreeItem(name);
			Assert.assertNotNull("Model object [" + modelProp.getName() + "] is not found in the Property View", treeItem);

			Class< ? > propType = modelProp.getClass();
			if (propType.equals(ScaSimplePropertyImpl.class)) {
				editSimpleViewProperty(treeItem, (ScaSimpleProperty) modelProp);
			} else if (propType.equals(ScaSimpleSequencePropertyImpl.class)) {
				editSimpleViewSeqProperty(treeItem, (ScaSimpleSequenceProperty) modelProp);
			} else if (propType.equals(ScaStructPropertyImpl.class)) {
				editStructViewProperty(treeItem, (ScaStructProperty) modelProp);
			} else if (propType.equals(ScaStructSequencePropertyImpl.class)) {
				editStructSeqViewProperty(treeItem, (ScaStructSequenceProperty) modelProp);
			}
		}
	}

	/**
	 * Edit property values for a resource in the model and confirm that the associated Property View values update
	 * 
	 * IDE-1302 (for child classes that use devices)
	 * @throws InterruptedException
	 */
	@Test
	public void editPropertyModelToView() throws InterruptedException {
		StandardTestActions.setRefreshInterval(1000);
		long readyTime = System.currentTimeMillis() + 10000;

		prepareObject();

		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, PROP_TAB_NAME);
		populatePropertyMap(propTree.getAllItems());
		List<ScaAbstractProperty< ? >> modelProps = getModelObjectProperties();

		// Ensure our changes to the refresh interval have all taken effect (previous tasks have already come up for
		// re-scheduling)
		long sleepTime = readyTime - System.currentTimeMillis();
		if (sleepTime > 0) {
			Thread.sleep(sleepTime);
		}

		for (ScaAbstractProperty< ? > modelProp : modelProps) {
			// Check that the model property exists in the view
			String name = modelProp.getName();
			Assert.assertNotNull("Model property name not found", name);
			SWTBotTreeItem treeItem = propTree.getTreeItem(name);
			Assert.assertNotNull("Model object [" + modelProp.getName() + "] is not found in the Property View", treeItem);

			// Update all values in the model, and confirm that the corresponding cell in the view updates
			Class< ? > propType = modelProp.getClass();
			if (propType.equals(ScaSimplePropertyImpl.class)) {
				editSimpleModelProperty((ScaSimpleProperty) modelProp, treeItem);
			} else if (propType.equals(ScaSimpleSequencePropertyImpl.class)) {
				editSimpleSeqModelProperty((ScaSimpleSequenceProperty) modelProp, treeItem);
			} else if (propType.equals(ScaStructPropertyImpl.class)) {
				editStructModelProperty((ScaStructProperty) modelProp, treeItem);
			} else if (propType.equals(ScaStructSequencePropertyImpl.class)) {
				editStructSeqModelProperty((ScaStructSequenceProperty) modelProp, treeItem);
			}
		}
	}

	// ################################ EDIT PROPERTY TESTS MODEL TO VIEW ##################################/
	private void editSimpleModelProperty(ScaSimpleProperty modelProp, SWTBotTreeItem treeItem) {
		if (modelProp.getMode().equals(AccessType.READONLY)) {
			return;
		}

		Object newValue = null;
		if (modelProp.getValue() instanceof Boolean) {
			if ((Boolean) modelProp.getValue()) {
				newValue = Boolean.FALSE;
			} else {
				newValue = Boolean.TRUE;
			}
		} else if (modelProp.getValue() instanceof Double) {
			newValue = Double.valueOf(PREPENDER + modelProp.getValue());
		} else if (modelProp.getValue() instanceof Short) {
			newValue = Short.valueOf(PREPENDER + modelProp.getValue());
		} else {
			newValue = PREPENDER + modelProp.getValue();
		}

		Assert.assertNotNull("New Value should not be null", newValue);
		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(modelProp);
		Command cmd = SetCommand.create(editingDomain, modelProp, ScaPackage.Literals.SCA_SIMPLE_PROPERTY__VALUE, newValue);
		editingDomain.getCommandStack().execute(cmd);

		viewCheck(newValue.toString(), treeItem);
	}

	private void editSimpleSeqModelProperty(ScaSimpleSequenceProperty modelProp, SWTBotTreeItem treeItem) {
		Object[] values = modelProp.getValue();

		if (values[0] instanceof Boolean) {
			for (int i = 0; i < values.length; ++i) {
				if ((Boolean) values[i]) {
					values[i] = Boolean.FALSE;
				} else {
					values[i] = Boolean.TRUE;
				}
			}
		} else if (values[0] instanceof Double) {
			for (int i = 0; i < values.length; ++i) {
				values[i] = Double.valueOf(PREPENDER + values[i]);
			}
		} else if (values[0] instanceof Short) {
			for (int i = 0; i < values.length; ++i) {
				values[i] = Short.valueOf(PREPENDER + values[i]);
			}
		} else {
			for (int i = 0; i < values.length; ++i) {
				values[i] = PREPENDER + values[i];
			}
		}

		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(modelProp);
		Command cmd = SetCommand.create(editingDomain, modelProp, ScaPackage.Literals.SCA_SIMPLE_SEQUENCE_PROPERTY__VALUES, Arrays.asList(values));
		editingDomain.getCommandStack().execute(cmd);

		viewCheck(Arrays.toString(values), treeItem);
	}

	private void editStructModelProperty(ScaStructProperty modelProp, SWTBotTreeItem treeItem) {
		EList<ScaAbstractProperty< ? >> fields = modelProp.getFields();
		for (ScaAbstractProperty< ? > field : fields) {
			SWTBotTreeItem subItem = treeItem.getNode(field.getName());
			if (field instanceof ScaSimpleProperty) {
				editSimpleModelProperty((ScaSimpleProperty) field, subItem);
			} else if (field instanceof ScaSimpleSequenceProperty) {
				editSimpleSeqModelProperty((ScaSimpleSequenceProperty) field, subItem);
			}
		}
	}

	private void editStructSeqModelProperty(ScaStructSequenceProperty modelProp, SWTBotTreeItem treeItem) {
		ScaStructProperty struct = modelProp.getStructs().get(0);
		editStructModelProperty(struct, treeItem.getItems()[0]);
	}

	// ########################### EDIT PROPERTY TESTS VIEW TO MODEL ###################################/
	private void editSimpleViewProperty(SWTBotTreeItem treeItem, ScaSimpleProperty modelProp) {
		Object modelValue = modelProp.getValue();
		if (modelProp.getMode().equals(AccessType.READONLY)) {
			return;
		}

		if (modelValue instanceof Boolean) {
			editSimpleViewBool(treeItem, modelProp);
		} else {
			String newValue = PREPENDER + modelProp.getValue();
			Assert.assertNotNull("TreeItem was null for: " + modelProp.getName(), treeItem);
			treeItem.click(0);
			StandardTestActions.writeToCell(gefBot, treeItem, 1, newValue);

			viewCheck(newValue, treeItem);
			modelCheckSimple(newValue, modelProp);
		}
	}

	private void editSimpleViewBool(SWTBotTreeItem treeItem, ScaSimpleProperty modelProp) {
		boolean boolValue = (Boolean) modelProp.getValue();
		String newValue;

		treeItem.select();
		treeItem.click(1);
		if (boolValue) {
			newValue = Boolean.FALSE.toString();
			keyboard.pressShortcut(Keystrokes.UP);
		} else {
			newValue = Boolean.TRUE.toString();
			keyboard.pressShortcut(Keystrokes.DOWN);
		}
		keyboard.pressShortcut(Keystrokes.CR);

		viewCheck(newValue, treeItem);
		modelCheckSimple(newValue, modelProp);
	}

	private void editSimpleViewSeqProperty(SWTBotTreeItem treeItem, ScaSimpleSequenceProperty modelProp) {
		Object[] modelValues = modelProp.getValue();

		// Open the edit shell
		treeItem.select();
		treeItem.click(1);
		keyboard.pressShortcut(Keystrokes.SPACE);

		SWTBotShell editShell = bot.shell("Edit Property Value");
		editShell.setFocus();
		SWTBotTable table = editShell.bot().table();

		String[] newValueArray = new String[3];

		if (modelValues[0] instanceof Boolean) {
			for (int i = 0; i < modelValues.length; ++i) {
				boolean boolValue = (Boolean) modelValues[i];
				newValueArray[i] = new Boolean(!boolValue).toString();
				StandardTestActions.selectComboListFromCell(gefBot, table, i, 0, newValueArray[i], true);
			}
		} else {
			for (int i = 0; i < table.rowCount(); ++i) {
				String text = table.getTableItem(i).getText();
				newValueArray[i] = PREPENDER + text;
				StandardTestActions.writeToCell(gefBot, table, i, 0, newValueArray[i], true);
			}
			bot.sleep(100);
		}

		editShell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(editShell));

		viewCheck(Arrays.toString(newValueArray), treeItem);
		modelCheckSimpleSeq(Arrays.toString(newValueArray), modelProp);
	}

	private void editStructViewProperty(SWTBotTreeItem treeItem, ScaStructProperty modelProp) {
		SWTBotTreeItem[] simpleItems = treeItem.getItems();
		EList<ScaAbstractProperty< ? >> fields = modelProp.getFields();

		for (int i = 0; i < simpleItems.length; ++i) {
			SWTBotTreeItem subItem = null;
			ScaAbstractProperty< ? > field = fields.get(i);
			for (SWTBotTreeItem tmp : simpleItems) {
				if (tmp.cell(0).equals(field.getName())) {
					subItem = tmp;
					break;
				}
			}
			Assert.assertNotNull("Could not find property: ", field.getName());

			if (field instanceof ScaSimpleProperty) {
				editSimpleViewProperty(subItem, (ScaSimplePropertyImpl) field);
			} else if (field instanceof ScaSimpleSequenceProperty) {
				editSimpleViewSeqProperty(subItem, (ScaSimpleSequencePropertyImpl) field);
			}
		}
	}

	private void editStructSeqViewProperty(SWTBotTreeItem treeItem, ScaStructSequenceProperty modelProp) {
		SWTBotTreeItem[] structItems = treeItem.getItems();
		EList<ScaStructProperty> structs = modelProp.getStructs();

		for (int i = 0; i < structItems.length; ++i) {
			ScaStructProperty struct = structs.get(i);

			for (SWTBotTreeItem tmp : structItems) {
				String itemName = tmp.cell(0);
				itemName = itemName.substring(0, itemName.indexOf(' '));

				if (itemName.equals(struct.getName())) {
					editStructViewProperty(structItems[i], struct);
					break;
				}
			}
		}
	}

	// #################################### VALUE CHECKS ####################################### //

	/**
	 * Checks to see that the model object has updated with the new value
	 */
	private void modelCheckSimple(final String newValue, final ScaSimpleProperty modelProp) {
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return (newValue.equals(modelProp.getValue().toString()));
			}

			@Override
			public String getFailureMessage() {
				return "Model object did not update: " + modelProp.getName();
			}
		}, 15000);
	}

	private void modelCheckSimpleSeq(final String newValue, final ScaSimpleSequenceProperty modelProp) {
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return (newValue.equals(Arrays.toString(modelProp.getValue())));
			}

			@Override
			public String getFailureMessage() {
				return "Model object did not update: " + modelProp.getName();
			}
		}, 15000);
	}

	/**
	 * Checks to see that the respective Property View table cell has updated with the new value
	 */
	private void viewCheck(final String newValue, final SWTBotTreeItem treeItem) {
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return (newValue.equals(treeItem.cell(1)));
			}

			@Override
			public String getFailureMessage() {
				return "Property cell did not update: " + treeItem.cell(0);
			}
		}, 15000);
	}

	// #################################### CHECK PROPERTY AT LAUNCH TESTS #####################################/
	private void checkSimpleProperty(ScaSimpleProperty modelProp) {
		String propId = modelProp.getId();
		String modelValue = modelProp.getValue().toString();

		// Get the value that shows up on the Property view
		String viewValue = propertyMap.get(propId);
		Assert.assertNotNull("Property view does not contain expected property: " + propId, viewValue);

		// Compare property view value with the value found in the model object
		Assert.assertEquals("Property value for " + propId + " is inconsistent between property view (" + viewValue + ") and model (" + modelValue + ")",
			modelValue, viewValue);
	}

	private void checkSimpleSeqProperty(ScaSimpleSequenceProperty modelProp) {
		String propId = modelProp.getId();
		Object[] modelValues = modelProp.getValue();

		// Need to build out view values
		String valueString = propertyMap.get(propId);
		Assert.assertNotNull("Property view does not contain expected property: " + propId, valueString);
		String[] viewValues = valueString.substring(1, valueString.length() - 1).split(", ");

		for (int i = 0; i < modelValues.length; ++i) {
			Assert.assertEquals(
				"Property value for " + propId + " is inconsistent between property view (" + viewValues[i] + ") and model (" + modelValues[i] + ")",
				modelValues[i].toString(), viewValues[i]);
		}
	}

	private void checkStructProperty(ScaStructProperty modelProp) {
		String propId = "";
		try {
			propId = modelProp.getId();
			Assert.assertNotNull("Property view does not contain expected property: " + propId, propertyMap.get(propId));
		} catch (AssertionError er) {
			// We do this because of how structs are automatically indexed within struct sequences
			propId = propId + " [0]";
			Assert.assertNotNull("Property view does not contain expected property: " + propId, propertyMap.get(propId));
		}

		for (ScaAbstractProperty< ? > field : modelProp.getFields()) {
			Class< ? > propType = field.getClass();
			if (propType.equals(ScaSimplePropertyImpl.class)) {
				checkSimpleProperty((ScaSimplePropertyImpl) field);
			} else if (propType.equals(ScaSimpleSequencePropertyImpl.class)) {
				checkSimpleSeqProperty((ScaSimpleSequencePropertyImpl) field);
			}
		}
	}

	private void checkStructSeqProperty(ScaStructSequenceProperty modelProp) {
		String propId = modelProp.getId();
		Assert.assertNotNull("Property view does not contain expected property: " + propId, propertyMap.get(propId));

		for (ScaStructProperty structProp : modelProp.getStructs()) {
			checkStructProperty(structProp);
		}
	}
}
