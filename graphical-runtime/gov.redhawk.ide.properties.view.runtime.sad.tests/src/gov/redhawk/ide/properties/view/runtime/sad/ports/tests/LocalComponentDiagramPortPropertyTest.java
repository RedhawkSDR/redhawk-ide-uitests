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

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Before;

import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public class LocalComponentDiagramPortPropertyTest extends LocalComponentPortPropertyTest {

	private RHSWTGefBot gefBot;

	@Before
	public void before() throws Exception {
		super.before();
		gefBot = new RHSWTGefBot();
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1, PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return PROVIDES_DESC;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_1, ComponentState.STOPPED);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1, USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return USES_DESC;
	}
}
