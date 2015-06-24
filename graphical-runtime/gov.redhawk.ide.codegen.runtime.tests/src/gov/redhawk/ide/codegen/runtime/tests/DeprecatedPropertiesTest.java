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

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

public class DeprecatedPropertiesTest extends UITest {

	private final String compName = "DeprecatedPropertiesTest";
	private final String compLanguage = "Python";
	private String compPrf = compName + ".prf.xml";

	/**
	 * IDE-1235. Tests upgrading a project with 'configure' and 'execparam' properties.
	 */
	@Test
	public void test() {
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
		propShell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(propShell));

		SWTBotShell fileShell = bot.shell("Regenerate Files");
		fileShell.bot().button("OK").click();

		try {
			SWTBotShell genShell = bot.shell("Generating...");
			bot.waitUntil(Conditions.shellCloses(genShell));
		} catch (WidgetNotFoundException e) {
			// PASS
		}

		// The properties shouldn't have 'configure' or 'execparam' any more
		editor.show();
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String newPrfText = editor.bot().styledText().getText();
		Assert.assertFalse(newPrfText.contains("configure"));
		Assert.assertFalse(newPrfText.contains("execparam"));
	}

}
