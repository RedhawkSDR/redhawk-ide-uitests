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

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewTargetSdrTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DeviceSdrTest extends AbstractPropertiesViewTargetSdrTest {
	final private String[] NODE_PARENT_PATH = { "Target SDR", "Nodes" };
	final private String NODE_NAME = "AllPropertyTypes_DevMgr";
	final private String DEVICE_NAME = "AllPropertyTypesDevice";
	
	@Override
	protected void selectResource() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, NODE_PARENT_PATH, NODE_NAME, DiagramType.GRAPHITI_NODE_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(NODE_NAME);
		editor.select(DEVICE_NAME);
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Device Properties";
	}

}
