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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * We were running into some race conditions when launching waveforms with more than a few components,
 * thus we need to test launching and releasing more complex waveforms
 */
public class LargeDomainWaveformTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	@Override
	protected String getWaveformName() {
		return "LargeWaveform";
	}

	/**
	 * IDE-1146, IDE-1137, IDE-1129
	 */
	@Test
	public void launchLargeWaveform() {
		final String[] waveformPath = ScaExplorerTestUtils.joinPaths(DOMAIN_WAVEFORM_PARENT_PATH, new String[] { getWaveFormFullName() });
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());

		String[] componentsList = { "SigGen_1", "HardLimit_1", "DataConverter_1", "SigGen_2", "SigGen_3", "SigGen_4", "DataConverter_2", "HardLimit_2",
			"DataConverter_3", "DataConverter_4" };
		for (String component : componentsList) {
			ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, waveformPath, component);
			DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, component);
		}
	}

}
