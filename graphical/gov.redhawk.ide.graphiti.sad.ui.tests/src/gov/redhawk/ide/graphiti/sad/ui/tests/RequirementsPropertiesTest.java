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

import gov.redhawk.ide.graphiti.ui.tests.AbstractRequirementsPropertiesTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import mil.jpeojtrs.sca.partitioning.Requirements;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class RequirementsPropertiesTest extends AbstractRequirementsPropertiesTest {

	private static final String SIGGEN_1 = "SigGen_1";
	private static final String RH_SIGGEN = "rh.SigGen";
	private SadComponentInstantiation compInst;

	@Override
	protected void createProject() {
		final String projectName = "Requirements_Waveform";
		WaveformUtils.createNewWaveform(gefBot, projectName, null);
		final RHBotGefEditor editor = gefBot.rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, RH_SIGGEN, 0, 0);

		editor.getEditPart(SIGGEN_1).select();
		this.compInst = DiagramTestUtils.getComponentObject(editor, SIGGEN_1);
	}

	@Override
	protected void openTargetSdrProject() {
		final String waveformName = "RequirementsWaveform";
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Target SDR", "Waveforms" }, waveformName, DiagramType.GRAPHITI_WAVEFORM_EDITOR);

		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		editor.getEditPart(SIGGEN_1).select();
	}

	@Override
	protected Requirements getRequirements() {
		return compInst.getDeviceRequires();
	}
}
