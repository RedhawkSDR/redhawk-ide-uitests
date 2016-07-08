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

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DesignServicePropertyTest extends DesignDevicePropertyTest {

	private final String SERVICE_NAME = "AllPropertyTypesService";

	@Override
	protected void prepareObject() {
		NodeUtils.createNewNodeProject(bot, NODE_NAME, DOMAIN_NAME);
		setPropTabName();
		setEditor();
		DiagramTestUtils.addFromPaletteToDiagram((RHBotGefEditor) editor, SERVICE_NAME, 0, 0);
		selectObject();
	}

	@Override
	protected void selectObject() {
		editor.click(SERVICE_NAME);
	}

	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Service Properties";
	}

	@Override
	protected void setEditor() {
		editor = gefBot.rhGefEditor(NODE_NAME);
	}
}
