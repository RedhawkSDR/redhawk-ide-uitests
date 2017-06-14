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

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class GmfGraphitiTest extends AbstractGraphitiTest {

	private String waveformName;

	@Test
	public void openDiagramGmfAndGraphiti() {
		waveformName = "Diagram_Type";

		// Create a new empty waveform, close editor
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		SWTBotEditor editor = gefBot.gefEditor(waveformName);
		editor.close();

		// Find the SAD file in the project explorer
		SWTBotView projectView = ViewUtils.getProjectView(bot);
		projectView.setFocus();
		SWTBotTree projectTree = projectView.bot().tree();
		SWTBotTreeItem sadFileTreeItem = projectTree.expandNode(waveformName).getNode(waveformName + ".sad.xml");

		// Open Graphiti editor, then close
		sadFileTreeItem.select().contextMenu("Open With").menu("Waveform Editor").click();
		editor = gefBot.editorById("gov.redhawk.ide.graphiti.sad.ui.editor.presentation.SadEditorID");
		editor.close();

		// Open GMF editor, then close
		sadFileTreeItem.select().contextMenu("Open With").menu("Legacy Waveform Editor").click();
		editor = gefBot.editorById("gov.redhawk.ide.sad.ui.editor.presentation.SadEditorID");
		editor.close();
	}
}
