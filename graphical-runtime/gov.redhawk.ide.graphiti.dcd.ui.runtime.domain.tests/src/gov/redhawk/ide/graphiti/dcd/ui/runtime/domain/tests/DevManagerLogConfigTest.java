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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLogConfigTest;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DevManagerLogConfigTest extends AbstractLogConfigTest {

	private static final String DEVICE_MANAGER = "DevMgr_localhost";
	private static final String GPP_LOCALHOST = "GPP_localhost";

	private String domainName = null;
	private RHBotGefEditor resourceDiagram = null;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		domainName = DevMgrDomainTestUtils.generateDomainName();
		resourceDiagram = DevMgrDomainTestUtils.launchDomainAndDevMgr(bot, domainName, DEVICE_MANAGER);
		String[] parentPath = new String[] { domainName, "Device Managers", DEVICE_MANAGER };
		return ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, GPP_LOCALHOST);
	}

	@After
	public void after() {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;
			DevMgrDomainTestUtils.cleanup(bot, localDomainName);
		}
	}

	@Override
	protected SWTBotView showConsole() {
		return ConsoleUtils.showConsole(gefBot, DEVICE_MANAGER);
	}

	@Override
	protected SWTBotGefEditPart openResourceDiagram() {
		resourceDiagram.setFocus();
		DiagramTestUtils.waitForComponentState(bot, resourceDiagram, GPP_LOCALHOST, ComponentState.STARTED);
		return resourceDiagram.getEditPart(GPP_LOCALHOST);
	}

	@Override
	protected SWTBotGefEditor getDiagramEditor() {
		return resourceDiagram;
	}
}
