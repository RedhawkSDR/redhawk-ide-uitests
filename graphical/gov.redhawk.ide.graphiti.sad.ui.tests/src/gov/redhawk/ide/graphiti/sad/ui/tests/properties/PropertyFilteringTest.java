/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.sad.ui.tests.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaveformUtils;

public class PropertyFilteringTest extends UITest {

	private static final String COMP_NAME = "PropertyFilteringComp";
	private static final String COMP_INST = COMP_NAME + "_1";

	/**
	 * IDE-2082 Ensure only properties that can be overridden or be marked external are shown.
	 */
	@Test
	public void filtering() {
		WaveformUtils.createNewWaveform(bot, getClass().getSimpleName(), COMP_NAME);
		SWTBot editorBot = bot.editorByTitle(getClass().getSimpleName()).bot();
		editorBot.cTabItem("Properties").activate();

		Set<String> requiredIDs = new HashSet<>();
		Collections.addAll(requiredIDs, //
			"prop_ro", "prop_rw", "prop_wo", //
			"exec_rw", "exec_wo", //
			"config_ro", "config_rw", "config_wo", //
			"commandline_ro", "commandline_rw", "commandline_wo");

		SWTBotTreeItem compItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), Arrays.asList(COMP_INST));
		compItem.expand();
		SWTBotTreeItem[] treeItems = compItem.getItems();
		for (SWTBotTreeItem treeItem : treeItems) {
			String id = treeItem.cell(0);
			if (!requiredIDs.remove(id)) {
				Assert.fail("Found property '" + id + "' in the properties tab that should not be present");
			}
		}

		if (requiredIDs.size() > 0) {
			Assert.fail("The properties view didn't contain the following properties: " + requiredIDs.toString());
		}
	}
}
