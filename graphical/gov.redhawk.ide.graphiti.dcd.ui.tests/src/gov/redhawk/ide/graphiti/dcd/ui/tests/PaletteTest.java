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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import gov.redhawk.ide.graphiti.ui.tests.AbstractPaletteTest;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.tests.util.FilterInfo;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class PaletteTest extends AbstractPaletteTest {

	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";
	private static final String NS_DEV = "name.space.device";

	@Override
	protected RHBotGefEditor createDiagram(String name) {
		NodeUtils.createNewNodeProject(gefBot, name, DOMAIN_NAME);
		return gefBot.rhGefEditor(name);
	}

	@Override
	protected ComponentDescription[] getComponentsToFilter() {
		return new ComponentDescription[] {
			new ComponentDescription(GPP, null, null),
			new ComponentDescription(DEVICE_STUB, null, null),
			new ComponentDescription(NS_DEV, null, null)
		};
	}

	@Override
	protected FilterInfo[] getFilterInfo() {
		return new FilterInfo[] {
			new FilterInfo("g", true, false, false),
			new FilterInfo("sh", false, false, false),
			new FilterInfo("d", false, true, true),
			new FilterInfo("dE", false, true, true),
			new FilterInfo(".", false, false, true),
			new FilterInfo(".sP", false, false, true),
			new FilterInfo("", true, true, true)
		};
	}

	@Override
	protected ComponentDescription getMultipleImplComponent() {
		return new ComponentDescription("multipleImplDevice", null, null);
	}

}
