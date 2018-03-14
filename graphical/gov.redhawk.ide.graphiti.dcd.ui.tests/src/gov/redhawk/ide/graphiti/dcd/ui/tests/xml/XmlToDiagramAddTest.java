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
package gov.redhawk.ide.graphiti.dcd.ui.tests.xml;

import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.tests.xml.AbstractXmlToDiagramAddTest;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * Test class that deals with adding elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramAddTest extends AbstractXmlToDiagramAddTest {

	private static final String DOMAIN_NAME = "REDHAWK_DEV";

	private RHSWTGefBot gefBot = new RHSWTGefBot();

	@Override
	protected ComponentDescription getComponentADescription() {
		ComponentDescription description = new ComponentDescription("DeviceStub", new String[0], new String[] { "dataFloat_out" });
		description.setKey("path", "devices");
		return description;
	}

	@Override
	protected ComponentDescription getComponentBDescription() {
		ComponentDescription description = new ComponentDescription("PortSupplierService", new String[] { "dataFloat_in" }, new String[0]);
		description.setKey("path", "services");
		return description;
	}

	@Override
	protected RHBotGefEditor createEditor(String name) {
		NodeUtils.createNewNodeProject(gefBot, name, DOMAIN_NAME);
		return gefBot.rhGefEditor(name);
	}

	@Override
	protected Class< ? > getEditorType() {
		return DeviceConfiguration.class;
	}
}
