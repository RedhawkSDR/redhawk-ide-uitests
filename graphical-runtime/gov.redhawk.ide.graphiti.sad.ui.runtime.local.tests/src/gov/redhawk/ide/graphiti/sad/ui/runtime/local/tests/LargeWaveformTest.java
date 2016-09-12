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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.debug.impl.LocalScaWaveformImpl;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformExplorerEditor;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformSandboxEditor;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

/**
 * IDE-1451 - Error dialogs when opening sandbox waveform/components. Tested in all tests by virtue of opening the
 * diagram.
 */
public class LargeWaveformTest extends UIRuntimeTest {

	private static final String WAVEFORM_NAME = "LargeWaveform";

	@Before
	public void before() throws Exception {
		super.before();
		bot.waitUntil(new WaitForLaunchTermination(true), 30000);
		WaveformUtils.launchLocalWaveform(bot, WAVEFORM_NAME);
	}

	@After
	public void after() throws CoreException {
		waitUntilEditorsClose();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox" }, WAVEFORM_NAME);
		bot.waitUntil(new WaitForLaunchTermination());
		super.after();
	}

	/**
	 * Release a large waveform with the explorer diagram open.
	 * 
	 * IDE-1120 - Ensure check that class hierarchy and input type are as expected
	 */
	@Test
	public void releaseWaveform_Explorer() {
		releaseWaveform(DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
	}

	/**
	 * Release a large waveform with the chalkboard diagram open.
	 * 
	 * IDE-1120 - Ensure check that class hierarchy and input type are as expected
	 */
	@Test
	public void releaseWaveform_Chalkboard() {
		releaseWaveform(DiagramType.GRAPHITI_CHALKBOARD);
	}

	/**
	 * IDE-1367
	 * Terminate a large waveform with the explorer diagram open
	 */
	@Test
	public void terminateWaveform_Explorer() {
		terminateWaveform(DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
	}

	/**
	 * IDE-1367
	 * Terminate a large waveform with the chalkboard diagram open
	 */
	@Test
	public void terminateWaveform_Chalkboard() {
		terminateWaveform(DiagramType.GRAPHITI_CHALKBOARD);
	}

	private void releaseWaveform(DiagramType diagramType) {
		String fullName = ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Sandbox" }, WAVEFORM_NAME, diagramType);
		SWTBotEditor editor = bot.editorByTitle(fullName);

		// IDE-1120
		if (DiagramType.GRAPHITI_CHALKBOARD.equals(diagramType)) {
			Assert.assertEquals("Editor class should be GraphitiWaveformSandboxEditor", GraphitiWaveformSandboxEditor.class,
				editor.getReference().getPart(false).getClass());
		} else {
			Assert.assertEquals("Editor class should be GraphitiWaveformExplorerEditor", GraphitiWaveformExplorerEditor.class,
				editor.getReference().getPart(false).getClass());
		}
		GraphitiWaveformExplorerEditor editorPart = (GraphitiWaveformExplorerEditor) editor.getReference().getPart(false);
		Assert.assertEquals("Sandbox editors should have LocalScaWaveform as their input", LocalScaWaveformImpl.class, editorPart.getWaveform().getClass());

		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, WAVEFORM_NAME);
	}

	private void terminateWaveform(DiagramType digramType) {
		String fullName = ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Sandbox" }, WAVEFORM_NAME, digramType);
		bot.editorByTitle(fullName);

		ScaExplorerTestUtils.terminate(bot, new String[] { "Sandbox" }, WAVEFORM_NAME);
	}

	private void waitUntilEditorsClose() {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return new SWTWorkbenchBot().editors().size() == 0;
			}

			@Override
			public String getFailureMessage() {
				return "Editors were still open";
			}
		});
	}
}
