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

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public class DcdDiagramPortPropertiesTest extends AbstractPortPropertiesTest {

	private static final String DEVICE_STUB = "DeviceStub";
	private static final String DEVICE_STUB_1 = "DeviceStub_1";
	private static final String PROVIDES_PORT = "dataDouble_in";
	private static final String USES_PORT = "dataFloat_out";

	@Override
	protected PortDescription prepareProvidesPort() {
		final String projectName = getClass().getSimpleName() + "_providesPort";
		NodeUtils.createNewNodeProject(bot, projectName, "REDHAWK_DEV");
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_1, PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return new PortDescription("IDL:BULKIO/dataDouble:1.0", "Input port 1 description");

	}

	@Override
	protected PortDescription prepareUsesPort() {
		final String projectName = getClass().getSimpleName() + "_usesPort";
		NodeUtils.createNewNodeProject(bot, projectName, "REDHAWK_DEV");
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 0, 0);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return new PortDescription("IDL:BULKIO/dataFloat:1.0", "Output port 1 description");
	}

}
