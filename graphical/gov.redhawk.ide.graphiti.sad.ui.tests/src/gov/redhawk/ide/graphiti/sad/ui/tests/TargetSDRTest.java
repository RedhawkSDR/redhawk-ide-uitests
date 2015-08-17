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
import java.math.BigInteger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.ui.PartInitException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * Tests relating to opening a waveform from the target SDR (i.e. the design-time editor, which should be read-only).
 */
public class TargetSDRTest extends AbstractGraphitiTest {

	private static final String[] WAVEFORM_PARENT_PATH = new String[] { "Target SDR", "Waveforms" };
	private static final String WAVEFORM_NAME = "ExampleWaveform06";
	private static final String SIGGEN_1 = "SigGen_1";
	private static final String SIGGEN_PORT_FLOAT_OUT = "dataFloat_out";
	private static final String HARDLIMIT_1 = "HardLimit_1";

	/**
	 * IDE-1323
	 * Checks the context menus available on a component in a waveform opened from the Target SDR.
	 * @throws PartInitException
	 * @throws IOException
	 */
	@Test
	public void componentContextMenus() throws PartInitException, IOException {
		// Open waveform diagram from the Target SDR
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(WAVEFORM_NAME);

		SWTBotGefEditPart component1 = editor.getEditPart(SIGGEN_1);
		Assert.assertNotNull(SIGGEN_1 + " component not found in diagram", component1);
		component1.select();

		// Check that runtime options are missing on the component
		String[] runtimeComponentOptions = { "Start", "Stop", "Show Console", "Log Level", "Release", "Terminate" };
		for (String menuText : runtimeComponentOptions) {
			ensureContextMenuNotPresent(editor, menuText);
		}

		// Check that design-time editor options are present, but disabled on the component. We'll attempt to click the
		// menu, but then have to make sure nothing actually happened to know the menu was present, but disabled.
		editor.clickContextMenu("Delete");
		Assert.assertNotNull("Device should not have been deleted", editor.getEditPart(SIGGEN_1));

		editor.clickContextMenu("Move Start Order Later");
		editor.getEditPart(HARDLIMIT_1).select();
		editor.clickContextMenu("Move Start Order Earlier");
		editor.clickContextMenu("Set As Assembly Controller");
		editor.save(); // Primarily to ensure any changes would have gone into affect

		// Switch to the XML tab and get contents
		DiagramTestUtils.openTabInEditor(editor, WAVEFORM_NAME + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		// Parse the XML with EMF
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(URI.createURI(WAVEFORM_NAME + ".sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		SoftwareAssembly sad = (SoftwareAssembly) resource.getContents().get(0);

		// Verify assembly controller / start order were not changed
		SadComponentInstantiation compInst1 = sad.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0);
		SadComponentInstantiation compInst2 = sad.getPartitioning().getComponentPlacement().get(1).getComponentInstantiation().get(0);
		Assert.assertEquals(BigInteger.ZERO, compInst1.getStartOrder());
		Assert.assertEquals(BigInteger.ONE, compInst2.getStartOrder());
		Assert.assertEquals(SIGGEN_1, sad.getAssemblyController().getComponentInstantiationRef().getInstantiation().getUsageName());
	}

	/**
	 * IDE-1323
	 * Checks the context menus available on the port of a component in a waveform opened from the Target SDR.
	 * @throws PartInitException
	 * @throws IOException
	 */
	@Test
	public void portContextMenus() throws PartInitException, IOException {
		// Open waveform diagram from the Target SDR
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(WAVEFORM_NAME);
		SWTBotGefEditPart port = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1, SIGGEN_PORT_FLOAT_OUT);
		Assert.assertNotNull(SIGGEN_PORT_FLOAT_OUT + " port not found on component " + SIGGEN_1, port);
		port.select();

		// Check that runtime options are missing on the component's port
		String[] runtimePortOptions = { "Plot Port Data", "Data List", "Monitor Ports", "Display SRI", "Snapshot", "Play Port" };
		for (String menuText : runtimePortOptions) {
			ensureContextMenuNotPresent(editor, menuText);
		}

		// Check that design-time editor options are present, but disabled on the component's ports. We'll attempt to
		// click the menu, but then have to make sure nothing actually happened to know the menu was present, but
		// disabled.
		editor.clickContextMenu("Mark External Port");
		editor.save(); // Primarily to ensure any changes would have gone into affect

		// Switch to the XML tab and get contents
		DiagramTestUtils.openTabInEditor(editor, WAVEFORM_NAME + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		// Parse the XML with EMF
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(URI.createURI(WAVEFORM_NAME + ".sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		SoftwareAssembly sad = (SoftwareAssembly) resource.getContents().get(0);

		// Verify the port was not marked external
		Assert.assertNull(sad.getExternalPorts());
	}

	private void ensureContextMenuNotPresent(SWTBotGefEditor editor, String menuText) {
		try {
			editor.clickContextMenu(menuText);

			// The only way to get here is if the undesired context menu option appears
			Assert.fail("The menu '" + menuText + "' was present, but should not be");
		} catch (WidgetNotFoundException e) {
			Assert.assertEquals(e.getMessage(), menuText, e.getMessage());
		}
	}

}
