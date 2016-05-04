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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLogConfigTest;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DevManagerSandboxLogConfigTest extends AbstractLogConfigTest {

	private static final String GPP = "GPP";
	private static final String GPP_1 = "GPP_1";
	private static final String[] GPP_PARENT_PATH = { "Sandbox", "Device Manager" };

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, GPP, "cpp");
		return ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, GPP_PARENT_PATH, GPP_1 + " STARTED");
	}

	@Override
	protected SWTBotView showConsole() {
		return ConsoleUtils.showConsole(gefBot, GPP);
	}

	@Override
	protected SWTBotGefEditPart openResourceDiagram() {
		DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		return gefBot.rhGefEditor("Device Manager Chalkboard").getEditPart(GPP);
	}

	@Override
	protected SWTBotGefEditor getDiagramEditor() {
		return gefBot.rhGefEditor("Device Manager Chalkboard");
	}

}
