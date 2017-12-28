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
package gov.redhawk.ide.properties.view.tests;

import java.io.IOException;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class WaveformPropertiesTest extends AbstractGraphitiTest {

	@Test
	public void waveformProperties() throws IOException {
		String projectName = "PropertiesWaveform";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, projectName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Validate existing properties
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties").tree();
		SWTBotTreeItem treeItem = propTable.getTreeItem("Id");
		Assert.assertTrue(treeItem.cell(1).matches("DCE.*"));
		treeItem = propTable.getTreeItem("Name");
		Assert.assertEquals(projectName, treeItem.cell(1));

		// Modify a property and check that the model object updates
		final String versionText = "2.1.2";
		treeItem = propTable.getTreeItem("Version");
		StandardTestActions.writeToCell(bot, treeItem, 1, versionText);
		SoftwareAssembly sad = WaveformUtils.getSoftwareAssembly(editor);
		Assert.assertEquals(versionText, sad.getVersion());
	}
}
