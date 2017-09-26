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
package gov.redhawk.ide.properties.view.tests;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DesignServicePropertyTest extends DesignDevicePropertyTest {

	private static final String SERVICE_NAME = "AllPropertyTypesService";

	private static final String SERVICE_NAME_2 = "PropertyFilteringService";
	private static final String SERVICE_INST_2 = SERVICE_NAME_2 + "_1";

	@Override
	protected void prepareObject() {
		NodeUtils.createNewNodeProject(bot, NODE_NAME, DOMAIN_NAME);
		setEditor();
		DiagramTestUtils.addFromPaletteToDiagram((RHBotGefEditor) editor, SERVICE_NAME, 0, 0);
		selectObject();
	}

	@Override
	protected void selectObject() {
		editor.click(SERVICE_NAME);
	}

	@Override
	protected void setEditor() {
		editor = gefBot.rhGefEditor(NODE_NAME);
	}

	@Override
	protected Set<String> setupPropertyFiltering() {
		NodeUtils.createNewNodeProject(bot, NODE_NAME_2, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(NODE_NAME_2);
		DiagramTestUtils.addFromPaletteToDiagram((RHBotGefEditor) editor, SERVICE_NAME_2, 0, 0);
		editor.click(SERVICE_INST_2);

		Set<String> nonFilteredIDs = new HashSet<>();
		Collections.addAll(nonFilteredIDs, //
			"prop_ro", "prop_rw", "prop_wo", //
			"exec_rw", "exec_wo", //
			"config_rw", "config_wo", //
			"commandline_ro", "commandline_rw", "commandline_wo");
		return nonFilteredIDs;
	}
}
