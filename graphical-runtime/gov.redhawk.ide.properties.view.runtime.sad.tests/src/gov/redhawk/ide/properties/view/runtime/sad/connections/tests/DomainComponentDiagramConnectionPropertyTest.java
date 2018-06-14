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
package gov.redhawk.ide.properties.view.runtime.sad.connections.tests;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Before;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

public class DomainComponentDiagramConnectionPropertyTest extends DomainComponentConnectionPropertyTest {

	private SWTGefBot gefBot;

	@Before
	public void before() throws Exception {
		super.before();
		gefBot = new SWTGefBot();
	}

	@Override
	protected void prepareConnection() {
		super.prepareConnection();
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveformInstanceName());
		SWTBotGefEditPart portEditPart = DiagramTestUtils.getDiagramUsesPort(editor, RESOURCE1, USES_PORT1);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, portEditPart);
		connections.get(0).select();
	}

	@Override
	protected void prepareNegotiatorComponentConnection() {
		super.prepareNegotiatorComponentConnection();
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveformInstanceName());
		SWTBotGefEditPart portEditPart = DiagramTestUtils.getDiagramUsesPort(editor, RESOURCE2, USES_PORT2);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, portEditPart);
		connections.get(0).select();
	}
}
