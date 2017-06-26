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
package gov.redhawk.ide.ui.tests.scd;

import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.condition.SelectIDL;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;

public abstract class AbstractPortsTabTest extends UITest {
	private static final String PORTS_TAB_NAME = "Ports";
	private static final String PROG_LANG_CPP = "C++";

	private static final String LABEL_NAME = "Name*:";
	private static final String LABEL_DIRECTION = "Direction:";
	private static final String LABEL_TYPE = "Type:";
	private static final String LABEL_INTERFACE = "Interface:";
	private static final String LABEL_DESCRIPTION = "Description:";

	private static final String BUTTON_ADD = "Add";
	private static final String BUTTON_BROWSE = "Browse...";
	private static final String BUTTON_REMOVE = "Remove";
	private static final String SHELL_SELECT_INTERFACE = "Select an interface";
	private static final String CHECK_SHOW_ALL = "Show all interfaces";

	private static final String IDL_MODULE = "IDETEST";
	private static final String IDL_INTF = "SampleInterface";
	private static final String IDL_INTF2 = "SampleInterface2";
	private static final String EXPECTED_IDL_SAMPLE1 = "IDL:" + IDL_MODULE + "/" + IDL_INTF + ":1.0";
	private static final String EXPECTED_IDL_SAMPLE2 = "IDL:" + IDL_MODULE + "/" + IDL_INTF2 + ":1.0";

	private static final String PROJECT_NAME = "TestCppComponent";

	private SWTBotEditor editor;
	private SWTBot editorBot;

	private SoftPkg spd;

	@Before
	public void before() throws Exception {
		super.before();

		// Create a project, close the editor
		ComponentUtils.createComponentProject(bot, PROJECT_NAME, PROG_LANG_CPP);
		bot.editorByTitle(PROJECT_NAME).close();

		// Let the derived class open the correct editor
		editor = openEditor(PROJECT_NAME);

		// Setup
		editor.setFocus();
		this.editorBot = editor.bot();
		this.editorBot.cTabItem(PORTS_TAB_NAME).activate();

		SCAFormEditor spdEditor = (SCAFormEditor) editor.getReference().getEditor(false);
		this.spd = SoftPkg.Util.getSoftPkg(spdEditor.getMainResource());

		checkNoPortDetails();
	}

	protected abstract SWTBotEditor openEditor(String projectName);

	protected void assertFormValid() {
		this.bot.sleep(SWTBotPreferences.DEFAULT_POLL_DELAY); // Allow form to have time to validate
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SPD_EDITOR_PORTS_TAB_ID);
	}

	protected void assertFormInvalid() {
		this.bot.sleep(SWTBotPreferences.DEFAULT_POLL_DELAY); // Allow form to have time to validate
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SPD_EDITOR_PORTS_TAB_ID);
	}

	@Test
	public void validation_duplicatePortName() {
		editorBot.button(BUTTON_ADD).click();
		editorBot.textWithLabel(LABEL_NAME).setText("portA");
		assertFormValid();

		editorBot.button(BUTTON_ADD).click();
		editorBot.textWithLabel(LABEL_NAME).setText("portA");
		assertFormInvalid();
	}

	@Test
	public void validation_noPortName() {
		editorBot.button(BUTTON_ADD).click();
		assertFormValid();

		editorBot.textWithLabel(LABEL_NAME).setText("");
		assertFormInvalid();
	}

	@Test
	public void addPort() throws IOException {
		editorBot.button(BUTTON_ADD).click();

		editorBot.textWithLabel(LABEL_NAME).setText("inTestPort");
		editorBot.comboBoxWithLabel(LABEL_DIRECTION).setSelection("in <provides>");
		selectIDL(editorBot, IDL_MODULE, IDL_INTF, true);

		// Check type table
		SWTBotTable typeTable = editorBot.tableWithLabel("Type:");
		Assert.assertEquals("Number of Types", 3, typeTable.rowCount());
		typeTable.getTableItem("data").check();
		typeTable.getTableItem("responses").check();
		typeTable.getTableItem("control").check();
		typeTable.getTableItem("control").uncheck();
		typeTable.getTableItem("responses").uncheck();

		final String expectedDescriptionText = "A description for the test Port.";
		editorBot.textWithLabel(LABEL_DESCRIPTION).setText(expectedDescriptionText);
		editorBot.sleep(SWTBotPreferences.DEFAULT_POLL_DELAY * 2); // Must allow description to set

		editorBot.button(BUTTON_ADD).click();
		editorBot.textWithLabel(LABEL_NAME).setText("outTestPort");
		selectIDL(editorBot, IDL_MODULE, IDL_INTF, true);
		editorBot.comboBoxWithLabel(LABEL_DIRECTION).setSelection("out <uses>");

		editorBot.button(BUTTON_ADD).click();
		editorBot.textWithLabel(LABEL_NAME).setText("bidirTestPort");
		selectIDL(editorBot, IDL_MODULE, IDL_INTF, true);
		editorBot.comboBoxWithLabel(LABEL_DIRECTION).setSelection("bi-dir <uses/provides>");

		assertFormValid();
		Assert.assertEquals("Number of ports in ports tree (UI)", 3, editorBot.tree().rowCount());

		// The bi-directional port is actually two ports (one in each direction, both with the same name)
		Ports ports = spd.getDescriptor().getComponent().getComponentFeatures().getPorts();
		Assert.assertEquals("Number of Ports in scd.xml", 4, ports.getAllPorts().size());

		editorBot.tree().getTreeItem("inTestPort").select();
		Assert.assertEquals("inTestPort", editorBot.textWithLabel(LABEL_NAME).getText());
		Assert.assertEquals("in <provides>", editorBot.comboBoxWithLabel(LABEL_DIRECTION).getText());
		Assert.assertEquals(true, editorBot.tableWithLabel(LABEL_TYPE).getTableItem("data").isChecked());
		Assert.assertEquals(false, editorBot.tableWithLabel(LABEL_TYPE).getTableItem("responses").isChecked());
		Assert.assertEquals(false, editorBot.tableWithLabel(LABEL_TYPE).getTableItem("control").isChecked());
		Assert.assertEquals(EXPECTED_IDL_SAMPLE1, editorBot.textWithLabel(LABEL_INTERFACE).getText());
		Assert.assertEquals(expectedDescriptionText, editorBot.textWithLabel(LABEL_DESCRIPTION).getText());

		AbstractPort port = ports.getPort("inTestPort");
		Assert.assertNotNull(port);
		Assert.assertEquals(EXPECTED_IDL_SAMPLE1, port.getRepID());
		Assert.assertTrue(port instanceof Provides);
		Assert.assertEquals(expectedDescriptionText, port.getDescription());

		port = ports.getPort("outTestPort");
		Assert.assertNotNull(port);
		Assert.assertEquals(EXPECTED_IDL_SAMPLE1, port.getRepID());
		Assert.assertTrue(port instanceof Uses);

		boolean foundProvides = false, foundUses = false;
		for (AbstractPort absPort : ports.getAllPorts()) {
			if ("bidirTestPort".equals(absPort.getName())) {
				if (absPort instanceof Provides) {
					Assert.assertEquals(EXPECTED_IDL_SAMPLE1, absPort.getRepID());
					foundProvides = true;
				} else if (absPort instanceof Uses) {
					Assert.assertEquals(EXPECTED_IDL_SAMPLE1, absPort.getRepID());
					foundUses = true;
				}
			}
		}
		Assert.assertTrue("Missing bi-dir port (provides)", foundProvides);
		Assert.assertTrue("Missing bi-dir port (uses)", foundUses);
	}

	@Test
	public void editPort() {
		editorBot.button(BUTTON_ADD).click();
		editorBot.comboBoxWithLabel(LABEL_DIRECTION).setSelection("in <provides>");
		editorBot.textWithLabel(LABEL_NAME).setText("inTestPort");
		selectIDL(editorBot, IDL_MODULE, IDL_INTF, true);

		SWTBotTree tree = editorBot.tree();
		Assert.assertEquals("inTestPort", tree.cell(0, 0));
		Assert.assertEquals(EXPECTED_IDL_SAMPLE1, tree.cell(0, 1));

		editorBot.comboBoxWithLabel(LABEL_DIRECTION).setSelection("out <uses>");
		editorBot.textWithLabel(LABEL_NAME).setText("outTestPort");
		selectIDL(editorBot, IDL_MODULE, IDL_INTF2, true);

		Assert.assertEquals("outTestPort", tree.cell(0, 0));
		Assert.assertEquals(EXPECTED_IDL_SAMPLE2, tree.cell(0, 1));

		// Test clicking to edit and NOT making a change (IDE-1230)
		editorBot.tree().select(0);
		editorBot.sleep(SWTBotPreferences.DEFAULT_POLL_DELAY);
		Assert.assertEquals("outTestPort", tree.cell(0, 0));
		Assert.assertEquals(EXPECTED_IDL_SAMPLE2, tree.cell(0, 1));

		assertFormValid();
	}

	@Test
	public void removePort() {
		Assert.assertFalse("Remove button should be disabled", editorBot.button(BUTTON_REMOVE).isEnabled());

		editorBot.button(BUTTON_ADD).click();
		editorBot.label("Port Details"); // make sure this does not cause a WidgetNotFoundException

		// fill in required Port details
		editorBot.textWithLabel(LABEL_NAME).setText("inTestPortToRemove");
		editorBot.comboBoxWithLabel(LABEL_DIRECTION).setSelection("in <provides>");

		editorBot.tree().select(0);
		editorBot.button(BUTTON_REMOVE).click();

		Assert.assertEquals("Removed Port", 0, editorBot.tree().rowCount());
		Assert.assertFalse("Remove button should be disabled #2", editorBot.button(BUTTON_REMOVE).isEnabled());

		assertFormValid();
		checkNoPortDetails();
	}

	/**
	 * Tests IDE-1389, ensuring BULKIO:dataChar isn't shown by default
	 */
	@Test
	public void filter_dataChar() {
		editorBot.button(BUTTON_ADD).click();
		boolean dataCharShown = false;
		try {
			selectIDL(editorBot, "BULKIO", "dataChar", false);
			dataCharShown = true;
		} catch (TimeoutException ex) {
			// PASS - This is expected
		}
		Assert.assertFalse(dataCharShown);
		selectIDL(editorBot, "BULKIO", "dataChar", true);
	}

	/**
	 * Tests IDE-943, ensuring that a non-namespaced module (like Echo) isn't shown by default
	 */
	@Test
	public void filter_Echo() {
		editorBot.button(BUTTON_ADD).click();
		boolean dataCharShown = false;
		try {
			selectIDL(editorBot, null, "Echo", false);
			dataCharShown = true;
		} catch (TimeoutException ex) {
			// PASS - This is expected
		}
		Assert.assertFalse(dataCharShown);
		selectIDL(editorBot, null, "Echo", true);
	}

	private void selectIDL(SWTBot bot, String module, String intf, boolean showAll) {
		bot.button(BUTTON_BROWSE).click();
		SWTBotShell dialogShell = bot.shell(SHELL_SELECT_INTERFACE);
		SWTBot dialogBot = dialogShell.bot();
		if (showAll) {
			dialogBot.checkBox(CHECK_SHOW_ALL).select();
		}
		try {
			dialogBot.waitUntil(new SelectIDL(module, intf));
		} catch (TimeoutException ex) {
			dialogBot.button("Cancel").click();
			bot.waitUntil(Conditions.shellCloses(dialogShell));
			throw ex;
		}
		dialogBot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(dialogShell));
	}

	private void checkNoPortDetails() {
		final long origTimeout = SWTBotPreferences.TIMEOUT;
		SWTBotPreferences.TIMEOUT = 500; // reduce time to find widget from 5s default
		try {
			try {
				editorBot.label("Port Details");
				Assert.fail("Found Port Details section - label");
			} catch (WidgetNotFoundException ex) {
				// PASS
			}

			try {
				editorBot.textWithLabel(LABEL_NAME);
				Assert.fail("Found Port Details section - name field");
			} catch (WidgetNotFoundException ex) {
				// PASS
			}

			Assert.assertTrue("No Port Details section", true);
		} finally {
			SWTBotPreferences.TIMEOUT = origTimeout; // restore original timeout
		}
	}
}
