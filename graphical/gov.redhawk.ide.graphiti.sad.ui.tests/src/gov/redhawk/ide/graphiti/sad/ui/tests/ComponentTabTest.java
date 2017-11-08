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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class ComponentTabTest extends AbstractGraphitiTest {

	private static final String PROJECT_NAME = "TestWaveform";
	private static final String RH_SIGGEN = "rh.SigGen";
	private static final String SIGGEN = "SigGen";
	private static final String SIGGEN_1 = "SigGen_1";
	private static final String SIGGEN_2 = "SigGen_2";

	private RHBotGefEditor editor;
	private SWTBot editorBot;
	private SoftwareAssembly sad;

	@Override
	@Before
	public void before() throws Exception {
		super.before();

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, PROJECT_NAME, null);
		editor = gefBot.rhGefEditor(PROJECT_NAME);
		editorBot = editor.bot();

		editorBot.cTabItem(PROJECT_NAME + ".sad.xml").activate();
		sad = WaveformUtils.getSoftwareAssembly(editor);
	}

	/**
	 * IDE-1969 - Test adding and removing a component via the Component tab
	 * @throws IOException
	 */
	@Test
	public void addRemoveComponent() throws IOException {
		// Test adding components
		addElement(SIGGEN_1, RH_SIGGEN);
		// NOTE: Added second component to avoid invalid model error during the remove operation
		addElement(SIGGEN_2, RH_SIGGEN);

		// Make sure the components were added to the SCA Model
		sad = WaveformUtils.getSoftwareAssembly(editor);
		String[] componentIds = { SIGGEN_1, SIGGEN_2 };
		for (String id : componentIds) {
			boolean componentFound = false;
			for (SadComponentPlacement placement : sad.getPartitioning().getComponentPlacement()) {
				String ciId = placement.getComponentInstantiation().get(0).getId();
				if (id.equals(ciId)) {
					componentFound = true;
					break;
				}
			}

			Assert.assertTrue(SIGGEN + "was not added to the SCA model", componentFound);
			Assert.assertNotNull(sad.getComponentFiles());
		}

		// Make sure components also appears in the diagram
		editorBot.cTabItem("Diagram").activate();
		Assert.assertNotNull(SIGGEN_1 + " was not added to the diagram", editor.getEditPart(SIGGEN_1));
		Assert.assertNotNull(SIGGEN_2 + " was not added to the diagram", editor.getEditPart(SIGGEN_2));

		// Test removing a component
		editorBot.cTabItem("Components").activate();
		editorBot.tree().getTreeItem(SIGGEN_1).select();
		editorBot.button("Remove").click();
		boolean componentFound = false;
		waitForTreeItemToBeRemoved(SIGGEN_1);
		sad = WaveformUtils.getSoftwareAssembly(editor);
		for (SadComponentPlacement placement : sad.getPartitioning().getComponentPlacement()) {
			String ciId = placement.getComponentInstantiation().get(0).getId();
			if (SIGGEN_1.equals(ciId)) {
				componentFound = true;
			}
		}

		// Need to save make sure no EMF error about elements with missing eResources occurs
		editor.save();
		Assert.assertFalse(SIGGEN_1 + "was not removed from the SCA model", componentFound);

		// Make sure component was also removed from the Diagram
		editorBot.cTabItem("Diagram").activate();
		Assert.assertNull(SIGGEN + " was not removed from the diagram", editor.getEditPart(SIGGEN_1));
	}

	private SWTBotTreeItem addElement(String name, String fullyQualifiedName) {
		editorBot.cTabItem("Components").activate();
		editorBot.button("Add...").click();
		bot.waitUntil(Conditions.shellIsActive("Add Components Wizard"));
		SWTBotShell shell = bot.shell("Add Components Wizard");
		StandardTestActions.selectNamespacedTreeItem(gefBot, shell.bot().tree(), fullyQualifiedName).click();
		shell.bot().button("Finish").click();

		SWTBotTree tree = editorBot.tree(0);
		return tree.getTreeItem(name);
	}

	private void waitForTreeItemToBeRemoved(final String elementId) {
		editorBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem treeItem;
				try {
					treeItem = editorBot.tree(0).getTreeItem(elementId);
				} catch (WidgetNotFoundException e) {
					return true;
				}
				if (treeItem == null) {
					return true;
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return elementId + " was not removed from 'All Components' tree";
			}
		});
	}
}
