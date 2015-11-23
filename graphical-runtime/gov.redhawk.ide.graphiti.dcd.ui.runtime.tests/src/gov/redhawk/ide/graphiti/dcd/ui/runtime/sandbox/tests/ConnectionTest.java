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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ConnectionAbstractTest;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class ConnectionTest extends ConnectionAbstractTest {

	@Override
	protected ComponentDescription getSlowComponentDescription() {
		return new ComponentDescription("SlowLaunchDevice", "SlowLaunchDevice", new String[] { "dataFloat_in" }, new String[] { "dataFloat_out" });
	}

	@Override
	protected ComponentDescription getFastComponentDescription() {
		return new ComponentDescription("DeviceStub", "DeviceStub", new String[] { "dataFloat_in" }, new String[] { "dataFloat_out" });
	}

	@Override
	protected RHBotGefEditor openDiagram() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Sandbox" }, "Device Manager", DiagramType.GRAPHITI_CHALKBOARD);
		return new RHSWTGefBot().rhGefEditor("Device Manager Chalkboard");
	}

}
