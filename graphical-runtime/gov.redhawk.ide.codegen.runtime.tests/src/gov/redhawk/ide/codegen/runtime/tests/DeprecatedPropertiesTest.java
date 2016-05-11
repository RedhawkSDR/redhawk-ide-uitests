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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.prf.AccessType;
import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructPropertyConfigurationType;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * Test upgrading projects with deprecated properties (kinds 'configure' and 'execparam').
 */
public class DeprecatedPropertiesTest extends UIRuntimeTest {

	private final String compName = "DeprecatedPropertiesTest";
	private final String compLanguage = "Python";
	private final String compScd = compName + ".scd.xml";
	private final String compPrf = compName + ".prf.xml";

	public void before() throws Exception {
		super.before();

		ComponentUtils.createComponentProject(bot, compName, compLanguage);
		SWTBotEditor editor = bot.editorByTitle(compName);

		// Replace the SCD with one that has event port/interface
		DiagramTestUtils.openTabInEditor(editor, compScd);
		String scdAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/DeprecatedPropertiesTest.scd.xml"));
		editor.bot().styledText().setText(scdAsString);
		MenuUtils.save(editor);

		// Replace the PRF with one that has 'configure' and 'execparam' properties
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/DeprecatedPropertiesTest.prf.xml"));
		Assert.assertTrue(prfAsString.contains("configure"));
		Assert.assertTrue(prfAsString.contains("execparam"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);

		// Go back to the overview
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
	}

	/**
	 * IDE-1235 Test canceling the upgrade
	 * @throws IOException
	 */
	@Test
	public void cancel() throws IOException {
		// Click the generate button, verify we get the dialog and can cancel it
		SWTBotEditor editor = bot.editorByTitle(compName);
		editor.bot().toolbarButton(0).click();
		SWTBotShell propShell = bot.shell("Deprecated property kinds");
		propShell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(propShell));

		// The properties should have 'configure' and 'execparam' still
		editor.show();
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String newPrfText = editor.bot().styledText().getText();
		Assert.assertTrue(newPrfText.contains("configure"));
		Assert.assertTrue(newPrfText.contains("execparam"));
	}

	/**
	 * IDE-1235 Tests upgrading deprecated properties
	 * IDE-1532 Ensure that execparams which are read-write become read-only command-line properties
	 * IDE-1534 Ensure properties with 'event' kind-type upgrade properly
	 * @throws IOException
	 */
	@Test
	public void upgrade() throws IOException {
		// Generate again, but allow the upgrade this time
		SWTBotEditor editor = bot.editorByTitle(compName);
		editor.bot().toolbarButton(0).click();
		SWTBotShell propShell = bot.shell("Deprecated property kinds");
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

		// Verify properties are correct
		Simple simpleConfigure = (Simple) props.getProperty("simpleConfigure");
		checkProperty(simpleConfigure, Arrays.asList(PropertyConfigurationType.PROPERTY), AccessType.READWRITE, false);

		Simple simpleConfigureEvent = (Simple) props.getProperty("simpleConfigureEvent");
		checkProperty(simpleConfigureEvent, Arrays.asList(PropertyConfigurationType.PROPERTY), AccessType.READWRITE, false);

		Simple simpleExecparamReadWrite = (Simple) props.getProperty("simpleExecparamReadWrite");
		checkProperty(simpleExecparamReadWrite, Arrays.asList(PropertyConfigurationType.PROPERTY), AccessType.READONLY, true);

		SimpleSequence simpleSeqConfigure = (SimpleSequence) props.getProperty("simpleSeqConfigure");
		checkProperty(simpleSeqConfigure, Arrays.asList(PropertyConfigurationType.PROPERTY), AccessType.READWRITE);

		Struct structConfigure = (Struct) props.getProperty("structConfigure");
		checkProperty(structConfigure, Arrays.asList(StructPropertyConfigurationType.PROPERTY), AccessType.READWRITE);

		StructSequence structSeqConfigure = (StructSequence) props.getProperty("structSeqConfigure");
		checkProperty(structSeqConfigure, Arrays.asList(StructPropertyConfigurationType.PROPERTY), AccessType.READWRITE);
	}

	private void checkProperty(Simple property, Collection<PropertyConfigurationType> types, AccessType accessType, boolean commandLine) {
		Assert.assertEquals(types.size(), property.getKind().size());
		for (PropertyConfigurationType type : types) {
			boolean found = false;
			for (Kind kind : property.getKind()) {
				if (type.equals(kind.getType())) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("Could not find type " + type.getName() + " in property " + property.getName(), found);
		}
		Assert.assertEquals(accessType, property.getMode());
		Assert.assertEquals(commandLine, property.isCommandLine());
	}

	private void checkProperty(SimpleSequence property, Collection<PropertyConfigurationType> types, AccessType accessType) {
		Assert.assertEquals(types.size(), property.getKind().size());
		for (PropertyConfigurationType type : types) {
			boolean found = false;
			for (Kind kind : property.getKind()) {
				if (type.equals(kind.getType())) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("Could not find type " + type.getName() + " in property " + property.getName(), found);
		}
		Assert.assertEquals(accessType, property.getMode());
	}

	private void checkProperty(Struct property, List<StructPropertyConfigurationType> types, AccessType accessType) {
		Assert.assertEquals(types.size(), property.getConfigurationKind().size());
		for (StructPropertyConfigurationType type : types) {
			boolean found = false;
			for (ConfigurationKind kind : property.getConfigurationKind()) {
				if (type.equals(kind.getType())) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("Could not find type " + type.getName() + " in property " + property.getName(), found);
		}
		Assert.assertEquals(accessType, property.getMode());

		for (Simple innerSimple : property.getSimple()) {
			Assert.assertTrue("Property " + property.getName() + "." + innerSimple.getName() + " has kind(s)", innerSimple.getKind().isEmpty());
		}
		for (SimpleSequence simpleSequence : property.getSimpleSequence()) {
			Assert.assertTrue("Property " + property.getName() + "." + simpleSequence.getName() + " has kind(s)", simpleSequence.getKind().isEmpty());
		}
	}

	private void checkProperty(StructSequence property, List<StructPropertyConfigurationType> types, AccessType accessType) {
		Assert.assertEquals(types.size(), property.getConfigurationKind().size());
		for (StructPropertyConfigurationType type : types) {
			boolean found = false;
			for (ConfigurationKind kind : property.getConfigurationKind()) {
				if (type.equals(kind.getType())) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("Could not find type " + type.getName() + " in property " + property.getName(), found);
		}
		Assert.assertEquals(accessType, property.getMode());

		for (Simple innerSimple : property.getStruct().getSimple()) {
			Assert.assertTrue("Property " + property.getName() + "." + innerSimple.getName() + " has kind(s)", innerSimple.getKind().isEmpty());
		}
		for (SimpleSequence simpleSequence : property.getStruct().getSimpleSequence()) {
			Assert.assertTrue("Property " + property.getName() + "." + simpleSequence.getName() + " has kind(s)", simpleSequence.getKind().isEmpty());
		}
	}

	/**
	 * IDE-1387. Tests skipping upgrading a project with 'configure' and 'execparam' properties.
	 */
	@Test
	public void skipUpgrade() {
		// Generate, but skip the upgrade
		SWTBotEditor editor = bot.editorByTitle(compName);
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
