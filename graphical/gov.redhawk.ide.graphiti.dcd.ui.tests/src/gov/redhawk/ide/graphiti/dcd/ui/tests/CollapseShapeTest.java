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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import gov.redhawk.ide.graphiti.ui.tests.CollapseShapeAbstractTest;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.NodeUtils;

public class CollapseShapeTest extends CollapseShapeAbstractTest {

	@Override
	protected ComponentDescription getComponentADescription() {
		return new ComponentDescription("DeviceStub", "DeviceStub", new String[0], new String[] { "dataFloat_out", "dataDouble_out" });
	}

	@Override
	protected ComponentDescription getComponentBDescription() {
		return new ComponentDescription("DeviceStub2", "DeviceStub2", new String[] { "dataFloat_in", "dataDouble_in" }, new String[0]);
	}

	@Override
	protected ComponentDescription getComponentCDescription() {
		return new ComponentDescription("DeviceStub3", "DeviceStub3", new String[] { "dataShort_in" }, new String[] { "dataShort_out" });
	}

	@Override
	protected void createNewDiagram(String diagramName) {
		NodeUtils.createNewNodeProject(bot, diagramName, "REDHAWK_DEV");
	}
}
