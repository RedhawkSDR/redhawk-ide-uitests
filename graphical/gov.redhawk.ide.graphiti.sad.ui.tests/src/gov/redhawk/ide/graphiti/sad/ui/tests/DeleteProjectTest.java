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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.condition.WaitForTargetSdrRootLoad;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DeleteProjectTest extends AbstractGraphitiTest {

	private static final String SIG_GEN = "rh.SigGen";

	/**
	 * IDE-880
	 * Diagram editor should close if the respective project is deleted
	 */
	@Test
	public void project() {
		String waveformName = "DeleteProjectTest_project";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		List< ? extends SWTBotEditor> editors = gefBot.editors();
		Assert.assertEquals("Editor not found", 1, editors.size());

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		MenuUtils.save(editor);

		// Delete project from the project explorer
		SWTBotView projectExplorerView = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		projectExplorerView.setFocus();
		gefBot.tree().select(waveformName).contextMenu("Delete").click();

		gefBot.shell("Delete Resources").setFocus();
		gefBot.checkBox(0).click();
		gefBot.button("OK").click();

		// Make sure the editor closed
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return gefBot.editors().size() == 0;
			}

			@Override
			public String getFailureMessage() {
				return "Editor did not close";
			}

		});
		editors = gefBot.editors();
		Assert.assertEquals("Editor did not close", 0, editors.size());
	}

	/**
	 * IDE-2054 - Make sure editor closes when it's resource is deleted from the TargetSDR
	 * @throws CoreException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void targetSDR() throws CoreException, IOException, URISyntaxException {
		String waveformName = getClass().getSimpleName();

		// Copy the test waveform to the target SDR
		URI sourceURI = FileLocator.toFileURL(getClass().getResource("/testFiles/" + waveformName)).toURI();
		IFileStore source = EFS.getStore(sourceURI);
		IFileStore target = EFS.getStore(URI.create("sdrdom:/waveforms/" + waveformName));
		addSdrDomCleanupPath(new Path("/waveforms").append(waveformName));
		source.copy(target, EFS.NONE, null);

		// Refresh target SDR
		ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Target SDR" }, null).contextMenu().menu("Refresh").click();
		bot.waitUntil(new WaitForTargetSdrRootLoad(), WaitForTargetSdrRootLoad.TIMEOUT);

		// Open editor for the waveform
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Target SDR", "Waveforms" }, waveformName);
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Target SDR", "Waveforms" }, waveformName, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		bot.editorByTitle(getClass().getSimpleName());

		// Delete the waveform from the Target SDR
		ScaExplorerTestUtils.deleteFromTargetSdr(bot, new String[] { "Target SDR", "Waveforms" }, waveformName);

		// Confirm that the editor closes
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return ((SWTWorkbenchBot) bot).editors().size() == 0;
			}

			@Override
			public String getFailureMessage() {
				return "Editor did not close";
			}
		});
	}
}
