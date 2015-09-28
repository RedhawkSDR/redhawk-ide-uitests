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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.PropertyValueType;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class StructSimpleSequenceTest extends UITest {

	SWTBotEditor editor;

	@Test
	public void structSimpleSequenceTest() throws IOException {
		final String projectName = "ssTest";
		final String progLanguage = "C++";
		final String structId = "TestStruct";
		final String simpleSeqId = "SimpleSequence";
		final String simpleSeqValue = "string1";
		final String simpleSeqUnits = "unit";
		final String simpleSeqMin = "1";
		final String simpleSeqMax = "10";
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
		bot.textWithLabel("Description:").typeText(simpleSeqDescription);

		MenuUtils.save(editor);

		// Get the model for the PRF XML
		DiagramTestUtils.openTabInEditor(editor, projectName + ".prf.xml");
		String xmlText = editor.bot().styledText().getText();
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final Resource resource = resourceSet.createResource(URI.createURI("mem://StructSimpleSequenceTest.prf.xml"), PrfPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(xmlText.getBytes()), null);
		Properties props = Properties.Util.getProperties(resource);

		// Ensure our changes are present
		Struct struct = (Struct) props.getProperties().getValue(0);
		SimpleSequence ss = (SimpleSequence) struct.getFields().getValue(0);
		Assert.assertEquals(simpleSeqId, ss.getId());
		Assert.assertEquals(PropertyValueType.STRING, ss.getType());
		Assert.assertEquals(simpleSeqDescription, ss.getDescription());
		Assert.assertEquals(1, ss.getValues().getValue().size());
		Assert.assertEquals(simpleSeqValue, ss.getValues().getValue().get(0));
		Assert.assertEquals(simpleSeqUnits, ss.getUnits());
		Assert.assertEquals(simpleSeqMax, ss.getRange().getMax());
		Assert.assertEquals(simpleSeqMin, ss.getRange().getMin());

		// Remove the simple sequence property and validate xml
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		editor.bot().tree().getTreeItem(structId).getNode(simpleSeqId).select();
		bot.button("Remove").click();

		// Re-load the model
		DiagramTestUtils.openTabInEditor(editor, projectName + ".prf.xml");
		xmlText = editor.bot().styledText().getText();
		resource.unload();
		resource.load(new ByteArrayInputStream(xmlText.getBytes()), null);
		props = Properties.Util.getProperties(resource);

		// Ensure things were removed
		struct = (Struct) props.getProperties().getValue(0);
		Assert.assertTrue(struct.getSimpleSequence().isEmpty());
	}
}
