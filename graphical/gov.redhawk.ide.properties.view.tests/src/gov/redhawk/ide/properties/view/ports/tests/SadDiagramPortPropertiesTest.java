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
package gov.redhawk.ide.properties.view.ports.tests;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

public class SadDiagramPortPropertiesTest extends AbstractPortPropertiesTest {

	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String PROVIDES_PORT = "dataFloat_in";
	private static final String USES_PORT = "dataFloat_out";

	@Override
	protected PortDescription prepareProvidesPort() {
		final String projectName = getClass().getSimpleName() + "_providesPort";
		WaveformUtils.createNewWaveform(bot, projectName, HARD_LIMIT);
		SWTBotGefEditor editor = new SWTGefBot().gefEditor(projectName);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1, PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return new PortDescription("IDL:BULKIO/dataFloat:1.0", "Float input port for data before hard limit is applied. ");

	}

	@Override
	protected PortDescription prepareUsesPort() {
		final String projectName = getClass().getSimpleName() + "_usesPort";
		WaveformUtils.createNewWaveform(bot, projectName, HARD_LIMIT);
		SWTBotGefEditor editor = new SWTGefBot().gefEditor(projectName);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1, USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return new PortDescription("IDL:BULKIO/dataFloat:1.0", "Float output port for data after hard limit is applied. ");
	}

}
