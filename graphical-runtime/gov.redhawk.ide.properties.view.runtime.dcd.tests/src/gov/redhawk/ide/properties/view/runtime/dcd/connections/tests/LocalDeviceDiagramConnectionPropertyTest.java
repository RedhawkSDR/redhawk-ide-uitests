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
package gov.redhawk.ide.properties.view.runtime.dcd.connections.tests;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Before;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class LocalDeviceDiagramConnectionPropertyTest extends LocalDeviceConnectionPropertyTest {

	private SWTGefBot gefBot;

	@Before
	public void before() throws Exception {
		super.before();
		gefBot = new SWTGefBot();
	}

	@Override
	protected TransportType prepareConnection() {
		super.prepareConnection();
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		SWTBotGefEditPart portEditPart = DiagramTestUtils.getDiagramUsesPort(editor, USRP_1, USES_PORT);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, portEditPart);
		connections.get(0).select();
		return TransportType.SHMIPC;
	}

}
