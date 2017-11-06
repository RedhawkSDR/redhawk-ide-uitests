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
package gov.redhawk.ide.properties.view.runtime.dcd.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DomainDeviceDiagramPropertyTest extends DomainDevicePropertyTest {

	@Override
	protected void prepareObject() {
		super.prepareObject();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Device Managers" }, DEVICE_MANAGER,
			DiagramType.GRAPHITI_NODE_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEVICE_MANAGER);
		editor.click(DEVICE_NUM);
	}

	@Override
	protected void setupPropertyFiltering() {
		super.setupPropertyFiltering();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { getDomain(), "Device Managers" }, DEVICE_MANAGER_2,
			DiagramType.GRAPHITI_NODE_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEVICE_MANAGER_2);
		editor.click(DEVICE_INST_2);
	}
}
