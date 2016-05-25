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
package gov.redhawk.ide.graphiti.ui.tests.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class XmlTestUtils {
	
	private XmlTestUtils() {
		
	}

	// TODO: Need to expand this utility to also handle XML for SPD's, PRF's, SCD's, etc.
	public enum EditorType {
		SAD,
		DCD
	};

	/**
	 * Caller needs to cast the return object to the appropriate profile object (SoftwareAssembly, DeviceConfiguration, etc)
	 */
	public static Object getModelFromEditorXml(SWTBotEditor editor, String testName, EditorType editorType) throws IOException {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		if (editorType == EditorType.SAD) {
			DiagramTestUtils.openTabInEditor(editor, testName + ".sad.xml");
			String editorText = editor.toTextEditor().getText();
			Resource resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
			resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
			return SoftwareAssembly.Util.getSoftwareAssembly(resource);
		} else {
			DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
			String editorText = editor.toTextEditor().getText();
			Resource resource = resourceSet.createResource(URI.createURI("mem://DeviceManager.dcd.xml"), DcdPackage.eCONTENT_TYPE);
			resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
			return DeviceConfiguration.Util.getDeviceConfiguration(resource);
		}
	}

	public static void writeModelToXmlEditor(SWTBotEditor editor, String testName, EditorType editorType, Object profileObj) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (editorType == EditorType.SAD) {
			DiagramTestUtils.openTabInEditor(editor, testName + ".sad.xml");
			((SoftwareAssembly) profileObj).eResource().save(outputStream, null);
		} else {
			DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
			((DeviceConfiguration) profileObj).eResource().save(outputStream, null);
		}
		editor.toTextEditor().setText(outputStream.toString());
	}
}
