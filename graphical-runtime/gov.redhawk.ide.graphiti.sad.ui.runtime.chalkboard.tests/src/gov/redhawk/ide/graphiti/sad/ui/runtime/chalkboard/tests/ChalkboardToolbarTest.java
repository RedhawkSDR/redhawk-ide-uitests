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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ChalkboardToolbarTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	/**
	 * IDE-1076 - Make sure start, stop, and release toolbar buttons appear/function during runtime
	 */
	@Test
	public void checkChalkboardToolbarButtons() {
		final String[] waveformPath = ScaExplorerTestUtils.joinPaths(CHALKBOARD_PARENT_PATH, new String[] { CHALKBOARD });

		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 200);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Start Waveform").click();
		final String[] waveformStartedPath = ScaExplorerTestUtils.joinPaths(CHALKBOARD_PARENT_PATH, new String[] { CHALKBOARD + " STARTED"});
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformStartedPath, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(bot, waveformStartedPath, HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STARTED);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Stop Waveform").click();
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, waveformPath, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(bot, waveformPath, HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STOPPED);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Release Waveform").click();
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, SIGGEN);
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, HARD_LIMIT);

	}
}
