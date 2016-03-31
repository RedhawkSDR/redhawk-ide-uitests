/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPaletteTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.runtime.tests.util.FilterInfo;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class LocalWaveformPaletteTest extends AbstractPaletteTest {

	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String NAME_SPACE_COMP = "name.space.comp";
	private String waveFormFullName;

	@Override
	protected RHBotGefEditor launchDiagram() {
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM_PARENT_PATH,
			AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM);
		waveFormFullName = ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM_PARENT_PATH,
			AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		return gefBot.rhGefEditor(waveFormFullName);
	}

	@Override
	public void after() throws Exception {
		// Release the waveform if it exists
		try {
			ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM_PARENT_PATH, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM);
		} catch (WidgetNotFoundException ex) {
			return;
		}
		ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM_PARENT_PATH, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(gefBot, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM_PARENT_PATH, AbstractGraphitiLocalWaveformRuntimeTest.LOCAL_WAVEFORM);
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	@Override
	protected ComponentDescription[] getComponentsToFilter() {
		return new ComponentDescription[] {
			new ComponentDescription(AbstractGraphitiLocalWaveformRuntimeTest.SIGGEN, null, null),
			new ComponentDescription(HARD_LIMIT, null, null),
			new ComponentDescription(NAME_SPACE_COMP, null, null)
		};
	}

	@Override
	protected FilterInfo[] getFilterInfo() {
		return new FilterInfo[] {
			new FilterInfo("s", true, false, true),
			new FilterInfo("sh", false, false, false),
			new FilterInfo("h", true, true, false),
			new FilterInfo("hA", false, true, false),
			new FilterInfo(".", true, true, true),
			new FilterInfo(".sI", true, false, false),
			new FilterInfo("", true, true, true)
		};
	}

	@Override
	protected ComponentDescription getMultipleImplComponent() {
		return new ComponentDescription(AbstractGraphitiLocalWaveformRuntimeTest.SIGGEN, null, null);
	}

}
