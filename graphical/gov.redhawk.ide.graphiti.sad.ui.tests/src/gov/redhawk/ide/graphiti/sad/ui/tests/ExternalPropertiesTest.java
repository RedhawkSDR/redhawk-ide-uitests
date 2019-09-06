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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class ExternalPropertiesTest extends AbstractGraphitiTest {

	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";

	/**
	 * IDE-1510 - Deleting a component from the SAD diagram doesn't delete its external properties or external ports
	 * @throws IOException
	 */
	@Test
	public void externalPropertiesTest() throws IOException {
		final String hardLimitProp = "upper_limit";
		final String hardLimitExternalId = "new_limit_id";

		String waveformName = "DeleteWithExternalPort";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);
		MenuUtils.save(editor);

		// Set an external property
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		List<String> path = Arrays.asList(HARD_LIMIT_1, hardLimitProp);
		SWTBotTreeItem propertyItem = StandardTestActions.waitForTreeItemToAppear(editorBot, editorBot.tree(), path);
		StandardTestActions.writeToCell(editorBot, propertyItem, 1, hardLimitExternalId);

		// Check model for external property
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);

		EList<ExternalProperty> externalProperties = sad.getExternalProperties().getProperties();
		Assert.assertTrue("External properties were not updated in .sad.xml", externalProperties.size() == 1);
		ExternalProperty externalProp = externalProperties.get(0);
		Assert.assertEquals("External property comprefid is incorrect", HARD_LIMIT_1, externalProp.getCompRefID());
		Assert.assertEquals("External Property ID is incorrect", hardLimitProp, externalProp.getPropID());
		Assert.assertEquals("External Property external ID is incorrect", hardLimitExternalId, externalProp.getExternalPropID());

		// Delete component from diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));

		// Check that external ports were removed from model
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		resourceSet = ScaResourceFactoryUtil.createResourceSet();
		resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		Assert.assertNull("External ports were not removed from .sad.xml", sad.getExternalProperties());
	}
}
