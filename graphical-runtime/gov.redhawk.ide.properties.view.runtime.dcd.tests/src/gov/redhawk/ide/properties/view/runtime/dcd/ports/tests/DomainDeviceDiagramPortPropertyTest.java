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
package gov.redhawk.ide.properties.view.runtime.dcd.ports.tests;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Before;

import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DomainDeviceDiagramPortPropertyTest extends DomainDevicePortPropertyTest {

	private SWTGefBot gefBot;

	@Before
	public void before() throws Exception {
		super.before();
		gefBot = new SWTGefBot();
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		PortDescription portDesc = super.prepareProvidesPort();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Device Managers" }, DEVICE_MANAGER,
			DiagramType.GRAPHITI_NODE_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEVICE_MANAGER);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_1, PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return portDesc;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		PortDescription portDesc = super.prepareUsesPort();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Device Managers" }, DEVICE_MANAGER,
			DiagramType.GRAPHITI_NODE_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEVICE_MANAGER);
		DiagramTestUtils.waitForComponentState(gefBot, editor, DEVICE_STUB_1, ComponentState.STOPPED);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return portDesc;
	}

	@Override
	protected void prepareProvidesPortAdvanced() {
		super.prepareProvidesPortAdvanced();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Device Managers" }, DEV_MGR_USRP,
			DiagramType.GRAPHITI_NODE_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEV_MGR_USRP);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, USRP_1, USRP_PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
	}

	@Override
	protected void prepareUsesPortAdvanced() {
		super.prepareUsesPortAdvanced();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Device Managers" }, DEV_MGR_USRP,
			DiagramType.GRAPHITI_NODE_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEV_MGR_USRP);
		DiagramTestUtils.waitForComponentState(gefBot, editor, USRP_1, ComponentState.STOPPED);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, USRP_1, USRP_USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
	}
}
