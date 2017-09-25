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
package gov.redhawk.ide.properties.view.runtime.sad.tests;

import java.util.Set;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

/**
 * Tests properties of a domain launched component selected in the Chalkboard Diagram
 */
public class LocalComponentDiagramPropertyTest extends LocalComponentPropertyTest {

	@Override
	protected void prepareObject() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, COMP_NAME, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, COMP_INST);
		DiagramTestUtils.waitForComponentState(bot, editor, COMP_INST, ComponentState.STOPPED);
		editor.click(COMP_INST);
	}

	@Override
	protected Set<String> setupPropertyFiltering() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, COMP_NAME_2, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, COMP_INST_2);
		DiagramTestUtils.waitForComponentState(bot, editor, COMP_INST_2, ComponentState.STOPPED);
		editor.click(COMP_INST_2);
		return getNonFilteredPropertyIDs();
	}
}
