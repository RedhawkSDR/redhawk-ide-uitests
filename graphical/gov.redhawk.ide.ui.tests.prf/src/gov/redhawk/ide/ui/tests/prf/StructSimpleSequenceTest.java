/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.ui.tests.prf;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class StructSimpleSequenceTest extends UITest {

	SWTBotEditor editor;

	@Test
	public void structSimpleSequenceTest() {
		final String projectName = "ssTest";
		final String progLanguage = "C++";
		final String structId = "TestStruct";
		final String simpleSeqId = "SimpleSequence";
		final String simpleSeqValue = "string1";
		final String simpleSeqUnits = "unit";
		final String simpleSeqMin = "1";
		final String simpleSeqMax = "10";
		final String simpleSeqOptional = "true";
		final String simpleSeqDescription = "A simple sequence contained within a struct";

		ComponentUtils.createComponentProject(bot, projectName, progLanguage);
		editor = bot.editorByTitle(projectName);

		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);

		// Set up the containing struct
		bot.button("Add Struct").click();
		bot.textWithLabel("ID*:").typeText(structId);

		// Remove default simple property
		SWTBotTreeItem structNode = editor.bot().tree().getTreeItem(structId);
		structNode.expand().getNode("Simple").select();
		final SWTBotTreeItem simpleNode = structNode.getNode("Simple");
		bot.waitUntil(new DefaultCondition() {
			
			@Override
			public boolean test() throws Exception {
				return simpleNode.isSelected();
			}
			
			@Override
			public String getFailureMessage() {
				return "Simple Node was not selected";
			}
		});
		bot.button("Remove").click();

		// Add and configure the simple sequence
		structNode.select().contextMenu("New").menu("Simple Sequence").click();
		bot.textWithLabel("ID*:").typeText(simpleSeqId);
		bot.button("Add...").click();
		bot.shell("New Value").bot().text().typeText(simpleSeqValue);
		bot.shell("New Value").bot().button("OK").click();
		bot.textWithLabel("Units:").typeText(simpleSeqUnits);
		bot.checkBoxWithLabel("Range:").click();
		bot.textWithLabel("Min:").typeText(simpleSeqMin);
		bot.textWithLabel("Max:").typeText(simpleSeqMax);
		bot.comboBoxWithLabel("Optional:").setSelection(simpleSeqOptional);
		bot.textWithLabel("Description:").typeText(simpleSeqDescription);

		MenuUtils.save(editor);

		// Check that valid xml was generated
		DiagramTestUtils.openTabInEditor(editor, projectName + ".prf.xml");
		String xmlText = editor.bot().styledText().getText();
		Assert.assertTrue("Simple sequence attributes in XML are incorrect",
			xmlText.matches("(?s).* <simplesequence id=\"" + simpleSeqId + "\" type=\"string\" optional=\"" + simpleSeqOptional + "\">" + ".*"));
		Assert.assertTrue("Simple sequence description in XML is incorrect",
			xmlText.matches("(?s).* <description>" + simpleSeqDescription + "</description>" + ".*"));
		Assert.assertTrue("Simple sequence values in XML are incorrect",
			xmlText.matches("(?s).* <value>" + simpleSeqValue + "</value>" + ".*"));
		Assert.assertTrue("Simple sequence unit in XML is incorrect",
			xmlText.matches("(?s).* <units>" + simpleSeqUnits + "</units>" + ".*"));
		Assert.assertTrue("Simple sequence range in XML is incorrect",
			xmlText.matches("(?s).* <range max=\"" + simpleSeqMax + "\" min=\"" + simpleSeqMin + "\"/>" + ".*"));

		// Remove the simple sequence property and validate xml
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		editor.bot().tree().getTreeItem(structId).getNode(simpleSeqId).select();
		bot.button("Remove").click();

		DiagramTestUtils.openTabInEditor(editor, projectName + ".prf.xml");
		xmlText = editor.bot().styledText().getText();
		Assert.assertFalse("Simple sequence attributes were not removed from XML",
			xmlText.matches("(?s).* <simplesequence id=\"" + simpleSeqId + "\" type=\"string\" optional=\"" + simpleSeqOptional + "\">" + ".*"));
		Assert.assertFalse("Simple sequence description was not removed from XML",
			xmlText.matches("(?s).* <description>" + simpleSeqDescription + "</description>" + ".*"));
		Assert.assertFalse("Simple sequence values were not removed from XML",
			xmlText.matches("(?s).* <value>" + simpleSeqValue + "</value>" + ".*"));
		Assert.assertFalse("Simple sequence unit was not removed from XML",
			xmlText.matches("(?s).* <units>" + simpleSeqUnits + "</units>" + ".*"));
		Assert.assertFalse("Simple sequence range was not removed from XML",
			xmlText.matches("(?s).* <range max=\"" + simpleSeqMax + "\" min=\"" + simpleSeqMin + "\"/>" + ".*"));

	}
}
