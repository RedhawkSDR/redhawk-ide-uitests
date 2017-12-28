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

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;

public class GDiagramFilterTest extends AbstractGraphitiTest {
	private String waveformName;

	/**
	 * IDE-910
	 * The *.sad_GDiagram file should be filtered from the Project Explorer view by default
	 */
	@Test
	public void filterGDiagramResourceTest() {
		waveformName = "Filter_Resource";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		SWTBotView projectExplorerView = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		projectExplorerView.setFocus();

		// XML file should be present
		StandardTestActions.waitForTreeItemToAppear(bot, projectExplorerView.bot().tree(), Arrays.asList(waveformName, waveformName + ".sad.xml"));

		// GDiagram file should be filtered
		SWTBotTreeItem projectNode = projectExplorerView.bot().tree().getTreeItem(waveformName);
		try {
			projectNode.getNode(waveformName + ".sad_GDiagram");
			Assert.fail("GDiagram file is not filtered");
		} catch (WidgetNotFoundException e) {
			// PASS - expected behavior
		}
	}
}
