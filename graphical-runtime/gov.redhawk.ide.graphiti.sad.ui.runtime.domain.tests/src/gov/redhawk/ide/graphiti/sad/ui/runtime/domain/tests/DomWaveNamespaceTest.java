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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.condition.WaitForModalContext;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomWaveNamespaceTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	protected String getWaveformName() {
		return "namespaceWF";
	}

	/**
	 * IDE-1187 Opens Graphiti diagram using the Waveform Explorer editor.
	 * Diagram should contain namespaced components
	 */
	@Test
	public void waveformExplorerNamespaceComponentsTest() {
		final String comp1 = "comp_1";
		final String comp2 = "comp_2";

		// check for components
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		Assert.assertNotNull(editor.getEditPart(comp1));
		Assert.assertNotNull(editor.getEditPart(comp2));
		SWTBotGefEditPart providesPort = DiagramTestUtils.getDiagramProvidesPort(editor, comp2);
		SWTBotGefEditPart providesAnchor = DiagramTestUtils.getDiagramPortAnchor(providesPort);
		Assert.assertTrue(providesAnchor.targetConnections().size() == 1);
	}

	@Test
	public void namespaceWaveformTest() {
		final String waveformNamespace = "a.b.c.d";
		final String waveformName = waveformNamespace + ".waveform";

		SWTBotTreeItem domainTreeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { DOMAIN }, null);
		domainTreeItem.contextMenu("Launch Waveform...").click();

		SWTBotShell wizardShell = bot.shell("Launch Waveform");
		SWTBot wizardBot = wizardShell.bot();

		// Wait for the waveform list to load (it's a deferred content adapter). Afterwards, the first waveform will be
		// automatically selected, which will trigger loading of associated PRF file(s) via a modal progress context.
		wizardBot.waitWhile(Conditions.treeHasRows(wizardBot.tree(), 1));
		wizardBot.waitUntil(new WaitForModalContext());
		bot.sleep(ScaExplorerTestUtils.WIZARD_POST_MODAL_PROGRESS_DELAY);

		// Make sure finish is disallowed when selecting a
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList(waveformNamespace.split("\\.")));
		treeItem.select();
		Assert.assertFalse(wizardBot.button("Finish").isEnabled());
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());

		// Find our waveform and select. Again, selection will trigger a modal progress context.
		treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList(waveformName.split("\\.")));
		treeItem.select();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());
		wizardBot.waitUntil(new WaitForModalContext());
		bot.sleep(ScaExplorerTestUtils.WIZARD_POST_MODAL_PROGRESS_DELAY);

		// Finish will launch the waveform, again triggering a modal progress context, then closing the dialog
		bot.button("Finish").click();
		wizardBot.waitUntil(new WaitForModalContext(), 30000);
		bot.waitUntil(Conditions.shellCloses(wizardShell));

		bot.waitUntil(new WaitForEditorCondition(), WaitForEditorCondition.DEFAULT_WAIT_FOR_EDITOR_TIME);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveformName());
	}
}
