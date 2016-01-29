package gov.redhawk.ide.properties.view.runtime.tests;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;

import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public abstract class AbstractPropertiesViewTest extends UITest {
	/**
	 * Used in the property edit tests. Must be a String numeral, to be valid with both all property types. Booleans are
	 * special cases.
	 */
	protected final String PREPENDER = "5";

	protected String PROP_TAB_NAME;

	/** A Map of all properties found in the IDE's property view */
	protected Map<String, String> propertyMap = new HashMap<String, String>();

	/**
	 * Method should take necessary steps to:
	 * - launch the test object,
	 * - select editor,
	 * - set property tab name,
	 * - do initial object selection
	 */
	protected abstract void prepareObject();

	protected abstract void setPropTabName();

	protected RHSWTGefBot gefBot;
	protected Keyboard keyboard = KeyboardFactory.getSWTKeyboard();

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
}
