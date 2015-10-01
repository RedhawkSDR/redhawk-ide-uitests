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
package gov.redhawk.ide.codegen.runtime.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;
import mil.jpeojtrs.sca.util.collections.FeatureMapList;

public class DeprecatedPropertiesTest extends UITest {

	private final String compName = "DeprecatedPropertiesTest";
	private final String compLanguage = "Python";
	private final String compPrf = compName + ".prf.xml";

	/**
	 * IDE-1235. Tests upgrading a project with 'configure' and 'execparam' properties. Tests both the cancel
	 * functionality, and the upgrade functionality.
	 * @throws IOException
	 */
	@Test
	public void cancel_and_upgrade() throws IOException {
		ComponentUtils.createComponentProject(bot, compName, compLanguage);
		SWTBotEditor editor = bot.editorByTitle(compName);

		// Replace the PRF with one that has 'configure' and 'execparam' properties
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/DeprecatedPropertiesTest.prf.xml"));
		Assert.assertTrue(prfAsString.contains("configure"));
		Assert.assertTrue(prfAsString.contains("execparam"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);

		// Click the generate button, verify we get the dialog and can cancel it
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		editor.bot().toolbarButton(0).click();
		SWTBotShell propShell = bot.shell("Deprecated property kinds");
		propShell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(propShell));

		// Generate again, but allow the upgrade this time
		editor.bot().toolbarButton(0).click();
		propShell = bot.shell("Deprecated property kinds");
		propShell.bot().button("Yes").click();
		bot.waitUntil(Conditions.shellCloses(propShell));

		SWTBotShell fileShell = bot.shell("Regenerate Files");
		fileShell.bot().button("OK").click();

		try {
			SWTBotShell genShell = bot.shell("Generating...");
			bot.waitUntil(Conditions.shellCloses(genShell));
		} catch (WidgetNotFoundException e) {
			// PASS
		}

		// The properties shouldn't have 'configure' or 'execparam' any more, we should have a commandline attribute
		editor.show();
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String newPrfText = editor.bot().styledText().getText();
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final Resource resource = resourceSet.createResource(URI.createURI("mem://DeprecatedPropertiesTest.prf.xml"), PrfPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(newPrfText.getBytes()), null);
		Properties props = Properties.Util.getProperties(resource);
		for (AbstractProperty prop : new FeatureMapList<AbstractProperty>(props.getProperties(), AbstractProperty.class)) {
			Assert.assertFalse("Property " + prop.getName() + " shouldn't contain configure kind", prop.isKind(PropertyConfigurationType.CONFIGURE));
			Assert.assertFalse("Property " + prop.getName() + " shouldn't contain execparam kind", prop.isKind(PropertyConfigurationType.EXECPARAM));
			switch (prop.eClass().getClassifierID()) {
			case PrfPackage.STRUCT:
				Struct struct = (Struct) prop;
				for (Simple innerSimple : struct.getSimple()) {
					Assert.assertTrue("Property " + prop.getName() + "." + innerSimple.getName() + " has kind(s)", innerSimple.getKind().isEmpty());
				}
				for (SimpleSequence simpleSequence : struct.getSimpleSequence()) {
					Assert.assertTrue("Property " + prop.getName() + "." + simpleSequence.getName() + " has kind(s)", simpleSequence.getKind().isEmpty());
				}
				break;
			case PrfPackage.STRUCT_SEQUENCE:
				StructSequence structSequence = (StructSequence) prop;
				for (Simple innerSimple : structSequence.getStruct().getSimple()) {
					Assert.assertTrue("Property " + prop.getName() + "." + innerSimple.getName() + " has kind(s)", innerSimple.getKind().isEmpty());
				}
				for (SimpleSequence simpleSequence : structSequence.getStruct().getSimpleSequence()) {
					Assert.assertTrue("Property " + prop.getName() + "." + simpleSequence.getName() + " has kind(s)", simpleSequence.getKind().isEmpty());
				}
				break;
			default:
				break;
			}
		}
		Assert.assertTrue(((Simple) props.getProperty("simple2")).isCommandLine());
	}

	/**
	 * IDE-1235. Tests skipping upgrading a project with 'configure' and 'execparam' properties.
	 */
	@Test
	public void skip_upgrade() {
		ComponentUtils.createComponentProject(bot, compName, compLanguage);
		SWTBotEditor editor = bot.editorByTitle(compName);

		// Replace the PRF with one that has 'configure' and 'execparam' properties
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/DeprecatedPropertiesTest.prf.xml"));
		Assert.assertTrue(prfAsString.contains("configure"));
		Assert.assertTrue(prfAsString.contains("execparam"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);

		// Generate, but skip the upgrade
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		editor.bot().toolbarButton(0).click();
		SWTBotShell propShell = bot.shell("Deprecated property kinds");
		propShell.bot().button("No").click();
		bot.waitUntil(Conditions.shellCloses(propShell));

		SWTBotShell fileShell = bot.shell("Regenerate Files");
		fileShell.bot().button("OK").click();

		try {
			SWTBotShell genShell = bot.shell("Generating...");
			bot.waitUntil(Conditions.shellCloses(genShell));
		} catch (WidgetNotFoundException e) {
			// PASS
		}

		// The properties should have 'configure' and 'execparam' still
		editor.show();
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String newPrfText = editor.bot().styledText().getText();
		Assert.assertTrue(newPrfText.contains("configure"));
		Assert.assertTrue(newPrfText.contains("execparam"));
	}

}
