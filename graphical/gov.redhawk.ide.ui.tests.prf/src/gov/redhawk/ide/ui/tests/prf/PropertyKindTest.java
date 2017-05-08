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
package gov.redhawk.ide.ui.tests.prf;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

/**
 * Tests involving the PRF editor and the 'property' property kind.
 */
public class PropertyKindTest extends UITest {

	private final String compName = "PropertyKindTest";
	private final String compLanguage = "Python";
	private final String compSpd = compName + ".spd.xml";
	private final String compPrf = compName + ".prf.xml";

	/**
	 * Ensure 'configure' and 'execparam' are present only when there are deprecated properties. Also check that the
	 * default new property kind is 'property'.
	 * IDE-1676 - Filter 'message' property kind for non-struct properties
	 */
	@Test
	public void checkAvailableKinds() {
		ComponentUtils.createComponentProject(bot, compName, compLanguage);
		SWTBotEditor editor = bot.editorByTitle(compName);

		// Ensure 'configure' and 'execparam' aren't in the list when adding a property
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		for (String buttonText : new String[] { "Add Simple", "Add Sequence", "Add Struct", "Add StructSeq" }) {
			editor.bot().button(buttonText).click();
			SWTBotCombo combo = editor.bot().comboBoxWithLabel("Kind:");

			// Property should be of kind 'property'
			Assert.assertTrue(String.format("After clicking %s, expected a new property of kind 'property'", buttonText), combo.getText().contains("property"));

			// Assert configure, execparam, message are no present
			for (String item : combo.items()) {
				if (item.contains("configure")) {
					Assert.fail(String.format("After clicking %s, found configure in the list of combo items", buttonText));
				}
				if (item.contains("execparam")) {
					Assert.fail(String.format("After clicking %s, found execparam in the list of combo items", buttonText));
				}
				if (item.contains("message") && !"Add Struct".equals(buttonText)) {
					Assert.fail(String.format("After clicking %s, found message in the list of combo items", buttonText));
				}
			}
		}

		// Replace the PRF contents
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/PropertyKindTest.prf.xml"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);
		editor.close();

		// Ensure 'configure' and 'execparam' are now present
		ProjectExplorerUtils.openProjectInEditor(bot, compName, compSpd);
		editor = bot.editorByTitle(compName);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		SWTBotTree tree = editor.bot().tree().select(0);
		for (SWTBotTreeItem item : tree.getAllItems()) {
			item.select();
			SWTBotCombo combo = editor.bot().comboBoxWithLabel("Kind:");
			boolean foundConfigure = false, foundExecParam = false;
			for (String comboItem : combo.items()) {
				if (comboItem.contains("configure")) {
					foundConfigure = true;
					continue;
				}
				if (comboItem.contains("execparam")) {
					foundExecParam = true;
					continue;
				}
			}
			Assert.assertTrue(String.format("Couldn't find configure in the list of combo items for property %s", item.getText()), foundConfigure);

			if ("simple".equals(item.getText())) {
				Assert.assertTrue(String.format("Couldn't find execparam in the list of combo items for property %s", item.getText()), foundExecParam);
			} else {
				Assert.assertFalse(String.format("Execparam should not display in the list of combo items for property %s", item.getText()), foundExecParam);
			}
		}

		// Replace the PRF contents
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/PropertyKindMessageTest.prf.xml"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);
		editor.close();

		// Ensure 'message' is now present only for Struct properties
		ProjectExplorerUtils.openProjectInEditor(bot, compName, compSpd);
		editor = bot.editorByTitle(compName);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		tree = editor.bot().tree().select(0);
		for (SWTBotTreeItem item : tree.getAllItems()) {
			item.select();
			SWTBotCombo combo = editor.bot().comboBoxWithLabel("Kind:");
			boolean foundMessage = false;
			for (String comboItem : combo.items()) {
				if (comboItem.contains("message")) {
					foundMessage = true;
					break;
				}
			}

			if ("struct".equals(item.getText())) {
				Assert.assertTrue(String.format("Couldn't find message in the list of combo items for property %s", item.getText()), foundMessage);
			} else {
				Assert.assertFalse(String.format("Message should not display in the list of combo items for property %s", item.getText()), foundMessage);
			}

			// IDE-1923 - make sure that 'message' remains an available property type for Structs, even when not currently selected
			if ("struct".equals(item.getText())) {
				combo.setSelection("property (default)");
				for (String comboItem : combo.items()) {
					if (comboItem.contains("message")) {
						foundMessage = true;
						break;
					}
				}
				Assert.assertTrue(String.format("Couldn't find message in the list of combo items for property %s", item.getText()), foundMessage);
			}
		}
	}

	/**
	 * Ensure the editor is okay with multiple kinds.
	 */
	@Test
	public void checkMultiKind() {
		ComponentUtils.createComponentProject(bot, compName, compLanguage);
		SWTBotEditor editor = bot.editorByTitle(compName);

		// Replace the PRF contents
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/PropertyKindTest2.prf.xml"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);
		editor.close();

		// Ensure the editor shows 'configure'
		ProjectExplorerUtils.openProjectInEditor(bot, compName, compSpd);
		editor = bot.editorByTitle(compName);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		SWTBotCombo combo = editor.bot().comboBoxWithLabel("Kind:");
		Assert.assertTrue("Expected property to show 'configure' kind in the PRF editor", combo.getText().contains("configure"));
	}
}
