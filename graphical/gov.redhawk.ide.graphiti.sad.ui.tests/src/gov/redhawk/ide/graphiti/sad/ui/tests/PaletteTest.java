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

import gov.redhawk.ide.graphiti.ui.tests.AbstractPaletteTest;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.tests.util.FilterInfo;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class PaletteTest extends AbstractPaletteTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String IDE1112 = "ide1112.test.name.spaced.comp1";

	protected RHBotGefEditor createDiagram(String name) {
		WaveformUtils.createNewWaveform(gefBot, name, null);
		return gefBot.rhGefEditor(name);
	}

	protected ComponentDescription[] getComponentsToFilter() {
		return new ComponentDescription[] {
			new ComponentDescription(SIG_GEN, null, null),
			new ComponentDescription(HARD_LIMIT, null, null),
			new ComponentDescription(IDE1112, null, null)
		};
	}

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
		return new ComponentDescription(SIG_GEN, null, null);
	}
}
