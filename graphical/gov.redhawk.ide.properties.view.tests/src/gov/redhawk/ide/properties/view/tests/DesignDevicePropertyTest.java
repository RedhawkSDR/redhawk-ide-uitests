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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class DesignDevicePropertyTest extends AbstractPropertiesViewDesignTest {

	protected static final String DOMAIN_NAME = "REDHAWK_DEV";
	protected static final String NODE_NAME = "AllPropertyTypesNode";
	private static final String DEVICE_NAME = "AllPropertyTypesDevice";
	private DeviceConfiguration dcd = null;

	@Override
	protected void prepareObject() {
		NodeUtils.createNewNodeProject(bot, NODE_NAME, DOMAIN_NAME);
		setPropTabName();
		setEditor();
		DiagramTestUtils.addFromPaletteToDiagram((RHBotGefEditor) editor, DEVICE_NAME, 0, 0);
		selectObject();
	}

	@Override
	protected void selectObject() {
		editor.click(DEVICE_NAME);
	}

	@Override
	protected void setPropTabName() {
		propTabName = "Device Properties";
	}

	@Override
	protected void setEditor() {
		editor = gefBot.rhGefEditor(NODE_NAME);
	}

	@Override
	protected ComponentProperties getModelPropertiesFromEditor() throws IOException {
		editor.bot().cTabItem("DeviceManager.dcd.xml").activate();
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		String editorText = editor.toTextEditor().getText();
		Resource resource = resourceSet.createResource(URI.createURI("mem://temp.dcd.xml"), DcdPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		dcd = DeviceConfiguration.Util.getDeviceConfiguration(resource);
		ComponentProperties componentProps = dcd.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0).getComponentProperties();
		if (componentProps != null) {
			return componentProps;
		} else {
			return PartitioningFactory.eINSTANCE.createComponentProperties();
		}
	}

	@Override
	protected void writeModelPropertiesToEditor(ComponentProperties componentProps) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		dcd.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0).setComponentProperties(componentProps);
		dcd.eResource().save(outputStream, null);
		editor.toTextEditor().setText(outputStream.toString());
	}
}
