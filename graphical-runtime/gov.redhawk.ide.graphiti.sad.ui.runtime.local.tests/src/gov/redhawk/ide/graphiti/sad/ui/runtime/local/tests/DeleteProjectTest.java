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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DeleteProjectTest extends UIRuntimeTest {

	private static final String SIGGEN = "rh.SigGen";

	private RHSWTGefBot gefBot;

	@Before
	@Override
	public void before() throws Exception {
		super.before();

		gefBot = new RHSWTGefBot();
	}

	/**
	 * IDE-2054 - Make sure editor closes when it's resource is deleted from the TargetSDR
	 */
	@Test
	public void closeEditorOnDelete() {
		// Make a simple waveform and export it
		String waveformName = "Test_Waveform";

		// Create a new empty waveform and export it
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(gefBot, editor, SIGGEN);
		editor.save();
		editor.close();
		StandardTestActions.exportProject(waveformName, bot);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, new String[] { "Target SDR", "Waveforms" }, waveformName);

		// Open its editor from the Target SDR
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { "Target SDR", "Waveforms" }, waveformName, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		editor = gefBot.rhGefEditor(waveformName);

		// Delete the waveform from the Target SDR
		ScaExplorerTestUtils.deleteFromTargetSdr(gefBot, new String[] { "Target SDR", "Waveforms" }, waveformName);

		// Confirm that the editor closed
		List< ? extends SWTBotEditor> editors = gefBot.editors();
		Assert.assertEquals("Editor did not close", 0, editors.size());
	}

}
