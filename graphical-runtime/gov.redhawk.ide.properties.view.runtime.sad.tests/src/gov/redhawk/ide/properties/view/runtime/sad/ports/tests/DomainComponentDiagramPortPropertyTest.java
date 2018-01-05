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
package gov.redhawk.ide.properties.view.runtime.sad.ports.tests;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Before;

import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DomainComponentDiagramPortPropertyTest extends DomainComponentPortPropertyTest {

	private SWTGefBot gefBot;

	@Before
	public void before() throws Exception {
		super.before();
		gefBot = new SWTGefBot();
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		PortDescription portDesc = super.prepareProvidesPort();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Waveforms" }, WAVEFORM, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveformInstanceName());
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, EXAMPLE_PY_COMP_1, PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return portDesc;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		PortDescription portDesc = super.prepareUsesPort();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Waveforms" }, WAVEFORM, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveformInstanceName());
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, EXAMPLE_PY_COMP_1, USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return portDesc;
	}

	@Override
	protected void prepareProvidesPortAdvanced() {
		super.prepareProvidesPortAdvanced();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Waveforms" }, WAVEFORM6, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveformInstanceName());
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1, HARD_LIMIT_PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
	}

	@Override
	protected void prepareUsesPortAdvanced() {
		super.prepareUsesPortAdvanced();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Waveforms" }, WAVEFORM6, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveformInstanceName());
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1, HARD_LIMIT_USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
	}
}
