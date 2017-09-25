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

import java.util.Set;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

/**
 * Tests properties of a locally launched device selected in the Node Chalkboard Diagram
 */
public class LocalDeviceDiagramPropertyTest extends LocalDevicePropertyTest {

	@Override
	protected void prepareObject() {
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_NAME, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_NAME_NUM);
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_NAME_NUM, ComponentState.STOPPED);
		editor.click(DEVICE_NAME_NUM);
	}

	@Override
	protected Set<String> setupPropertyFiltering() {
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_NAME_2, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_INST_2);
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_INST_2, ComponentState.STOPPED);
		editor.click(DEVICE_INST_2);
		return getNonFilteredPropertyIDs();
	}
}
