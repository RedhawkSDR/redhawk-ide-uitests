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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLogConfigTest;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class LocalWaveformLogConfigTest extends AbstractLogConfigTest {

	private static final String TEST_WAVEFORM = "SigGenToHardLimitWF";
	private static final String[] WAVEFORM_PARENT_PATH = { "Sandbox" };
	private static final String SIGGEN_1 = "SigGen_1";

	private String waveformFullName;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		// Launch a waveform
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, TEST_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		waveformFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		List<String> sigGenParentPath = new ArrayList<String>(Arrays.asList(WAVEFORM_PARENT_PATH));
		sigGenParentPath.add(waveformFullName);
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(gefBot, sigGenParentPath.toArray(new String[0]), SIGGEN_1);
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	@Override
	protected SWTBotView showConsole() {
		// Stop the console view from constantly redirecting to the waveform
		ConsoleUtils.showConsole(gefBot, waveformFullName);
		ConsoleUtils.disableAutoShowConsole(gefBot);

		return ConsoleUtils.showConsole(gefBot, SIGGEN_1);
	}

	@Override
	protected SWTBotGefEditPart openResourceDiagram() {
		// Open the editor via the sandbox diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		return gefBot.rhGefEditor(waveformFullName).getEditPart(SIGGEN_1);
	}

	@Override
	protected SWTBotGefEditor getDiagramEditor() {
		return gefBot.rhGefEditor(waveformFullName);
	}

}
