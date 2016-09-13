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
package gov.redhawk.ide.properties.view.runtime.sad.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewTargetSdrTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class ComponentSdrTest extends AbstractPropertiesViewTargetSdrTest {
	private static final String[] WAVEFORM_PARENT_PATH = { "Target SDR", "Waveforms" };
	private static final String WAVEFORM_NAME = "AllPropertyTypesWaveform";
	private static final String COMPONENT_NAME = "AllPropertyTypesComponent";

	@Override
	protected void selectResource() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, WAVEFORM_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(WAVEFORM_NAME);
		editor.select(COMPONENT_NAME);
	}
}
