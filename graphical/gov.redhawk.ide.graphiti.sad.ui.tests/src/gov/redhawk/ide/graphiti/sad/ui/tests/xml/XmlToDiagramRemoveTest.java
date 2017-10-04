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
package gov.redhawk.ide.graphiti.sad.ui.tests.xml;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.ui.tests.SadTestUtils;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.finder.RHBot;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

/**
 * Test class that deals with removing elements from the sad.xml
 * and making sure they are removed correctly from the diagram
 */
public class XmlToDiagramRemoveTest extends AbstractGraphitiTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String HOSTCOLLOCATION_INSTANCE_NAME = "collocation_1";

	private String waveformName;

	/**
	 * IDE-851
	 * Remove a connection from the diagram via the sad.xml
	 */
	@Test
	public void removeConnectionInXmlTest() {
		waveformName = "Remove_Connection_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		// Remove connection from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endString = editorText.indexOf("<connections>");
		editorText = editorText.substring(0, endString);
		editor.toTextEditor().setText(editorText);

		// Confirm that the connection no longer exists
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		SWTBotGefEditPart componentEditPart = editor.getEditPart(SIG_GEN_1);
		ContainerShape containerShape = (ContainerShape) componentEditPart.part().getModel();
		Diagram diagram = DUtil.findDiagram(containerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());

		// Confirm both components exist in diagram
		SadComponentInstantiation hardLimitComponentObj = DiagramTestUtils.getComponentObject(editor, HARD_LIMIT_1);
		Assert.assertNotNull(HARD_LIMIT_1 + " should continue to exist regardless of connection", hardLimitComponentObj);
		SadComponentInstantiation sigGenComponentObj = DiagramTestUtils.getComponentObject(editor, SIG_GEN_1);
		Assert.assertNotNull(SIG_GEN_1 + " should continue to exist regardless of connection", sigGenComponentObj);
	}

	/**
	 * IDE-850
	 * Remove a component from the diagram via the sad.xml
	 */
	@Test
	public void removeComponentInXmlTest() {
		waveformName = "Remove_Component_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);
		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		// Remove component from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endIndex = editorText.indexOf("<componentplacement>");
		String partOneText = editorText.substring(0, endIndex);
		int startIndex = editorText.indexOf("<componentplacement>", endIndex + 1);
		String partTwoText = editorText.substring(startIndex);
		editor.toTextEditor().setText(partOneText + partTwoText);

		// Confirm that the connection no longer exists
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		SWTBotGefEditPart componentEditPart = editor.getEditPart(HARD_LIMIT_1);
		ContainerShape containerShape = (ContainerShape) componentEditPart.part().getModel();
		Diagram diagram = DUtil.findDiagram(containerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());

		// Confirm only one component exists in diagram
		SadComponentInstantiation hardLimitComponentObj = DiagramTestUtils.getComponentObject(editor, HARD_LIMIT_1);
		Assert.assertNotNull(HARD_LIMIT_1 + " should continue to exist regardless of connection", hardLimitComponentObj);
		SadComponentInstantiation sigGenComponentObj = DiagramTestUtils.getComponentObject(editor, SIG_GEN_1);
		Assert.assertNull(SIG_GEN_1 + " should have been deleted", sigGenComponentObj);
	}

	/**
	 * IDE-852
	 * Remove a host collocation from the diagram via the sad.xml
	 */
	@Test
	public void removeHostCollocationInXmlTest() {
		waveformName = "Remove_HostCollocation_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add host collocation to the diagram
		DiagramTestUtils.addHostCollocationToDiagram(editor);

		//add component inside host collocation (so host collocation is valid)
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 5, 5);

		// Remove host collocation from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endIndex = editorText.indexOf("<hostcollocation");
		String partOneText = editorText.substring(0, endIndex);
		int startIndex = editorText.indexOf("</partitioning>");
		String partTwoText = editorText.substring(startIndex);
		editor.toTextEditor().setText(partOneText + partTwoText);

		// Confirm the host collocation no longer exists in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		HostCollocation hostCollocationObj = DiagramTestUtils.getHostCollocationObject(editor, HOSTCOLLOCATION_INSTANCE_NAME);
		Assert.assertNull(HOSTCOLLOCATION_INSTANCE_NAME + " should have been deleted", hostCollocationObj);
	}

	/**
	 * IDE-978, IDE-965
	 * Add an external port to the diagram via the sad.xml
	 */
	@Test
	public void removeExternalPortsInXmlTest() {
		waveformName = "Add_ExternalPort_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);

		// Edit content of (add external port) sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String externalports = "</assemblycontroller> <externalports><port>"
			+ "<usesidentifier>dataFloat_out</usesidentifier>"
			+ "<componentinstantiationref refid=\"HardLimit_1\"/>"
			+ "</port> </externalports>";
		editorText = editorText.replace("</assemblycontroller>", externalports);
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);
		//assert port set to external in diagram
		SWTBotGefEditPart hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, true, HARD_LIMIT_1 + ":uses");

		//switch to overview tab and verify there are external ports
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		Assert.assertEquals("There are no external ports", 1, new RHBot(bot).section("External Ports").bot().table().rowCount());

		// Edit content of (remove external port) sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		int extPortStartIndex = editorText.indexOf("<externalports>");
		int extPortEndIndex = editorText.indexOf("</externalports>") + ("</externalports>".length());
		externalports = editorText.substring(extPortStartIndex, extPortEndIndex);
		editorText = editorText.replace(externalports, "");
		editor.toTextEditor().setText(editorText);

		// Confirm that no external ports exist in diagram
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);
		hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, false, HARD_LIMIT_1 + ":uses");

		//switch to overview tab and verify there are no external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("There are external ports", 0, new RHBot(bot).section("External Ports").bot().table().rowCount());
	}

	/**
	 * IDE-124
	 * Edit use device to the diagram via the sad.xml
	 */
	@Test
	public void removeUseDeviceInXmlTest() {
		waveformName = "Edit_UseDevice_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		//add uses device
		String usesDevice = "<assemblycontroller/><usesdevicedependencies><usesdevice id=\"FrontEndTuner_1\"/></usesdevicedependencies>";
		editorText = editorText.replace("<assemblycontroller/>", usesDevice);
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		SWTBotGefEditPart useDeviceEditPart = editor.getEditPart(SadTestUtils.USE_DEVICE);
		SadTestUtils.assertUsesDevice(useDeviceEditPart);

		//remove device id via xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();

		editorText = editorText.replace("<usesdevice id=\"FrontEndTuner_1\"/>", "");
		editor.toTextEditor().setText(editorText);

		// Confirm use device shape disappears
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		useDeviceEditPart = editor.getEditPart(SadTestUtils.USE_DEVICE);
		Assert.assertNull("Uses device exists but should have disappeared", useDeviceEditPart);
	}
}
