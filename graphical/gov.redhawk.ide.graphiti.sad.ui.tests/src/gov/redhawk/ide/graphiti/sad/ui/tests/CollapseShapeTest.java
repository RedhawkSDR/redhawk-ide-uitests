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

import java.util.List;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class CollapseShapeTest extends AbstractGraphitiTest {
	static final String DATA_READER    = "DataReader";
	static final String DATA_CONVERTER = "DataConverter";
	static final String DATA_WRITER    = "DataWriter";
	static final String HARD_LIMIT     = "HardLimit";
	static final String SIGGEN         = "SigGen";

	private String waveformName;

	/**
	 * IDE-1026
	 * Exercise creating several components. Collapse all components and create connections via super ports
	 * Verify connections created, collapse and expand shapes and verify appropriate shapes are displayed and
	 * connections are tied to appropriate shapes.
	 * Verify deletion of collapsed component removes all associated connections
	 */
	@Test
	public void checkCollapseExpandComponents() {
		waveformName = "IDE-1026-checkCollapseExpandComponents";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);

		// Add components to diagram from palette
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.maximizeActiveWindow(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_READER, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 150);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 300, 150);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_WRITER, 600, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 600, 150);

		// Get gefEditParts for port shapes
		SWTBotGefEditPart dataConverterDataFloat = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataFloat");
		SWTBotGefEditPart dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		SWTBotGefEditPart dataConverterDataFloatOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataFloat_out");
		SWTBotGefEditPart dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		SWTBotGefEditPart hardLimitDataFloatIn = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataFloat_in");

		// for simplicity just verify a couple port shapes
		Assert.assertNotNull("DataConverter dataDouble port shape does not exist", dataConverterDataDouble);
		Assert.assertNotNull("DataConverter dataDouble_out port shape does not exist", dataConverterDataDoubleOut);
		Assert.assertNotNull("HardLimit dataFloat_in port shape does not exist", hardLimitDataFloatIn);

		// collapse all shapes
		editor.setFocus();
		editor.click(300, 0);
		editor.clickContextMenu("Collapse All Shapes");

		// verify some port shapes were hidden
		dataConverterDataDouble = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		hardLimitDataFloatIn = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataFloat_in");
		Assert.assertNull("DataConverter dataDouble port shape exists", dataConverterDataDouble);
		Assert.assertNull("DataConverter dataDouble_out port shape exists", dataConverterDataDoubleOut);
		Assert.assertNull("HardLimit dataFloat_in port shape exists", hardLimitDataFloatIn);

		// super ports
		SWTBotGefEditPart dataReaderSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_READER);
		SWTBotGefEditPart dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		SWTBotGefEditPart dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);
		SWTBotGefEditPart dataWriterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_WRITER);
		SWTBotGefEditPart hardLimitSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, HARD_LIMIT);

		// verify super ports exist (check a few)
		Assert.assertNotNull("DataConverter Super Provides shape does not exist", dataConverterSuperProvides);
		Assert.assertNotNull("DataConverter Super Uses port shape does not exist", dataConverterSuperUses);
		Assert.assertNotNull("HardLimit Super Provides port shape does not exist", hardLimitSuperProvides);

		// create connections via super ports and verify
		Assert.assertTrue("Connection DataReader -> DataConverter via super ports failed",
			DiagramTestUtils.drawConnectionBetweenPorts(editor, dataReaderSuperUses, dataConverterSuperProvides));
		Assert.assertTrue("Connection DataConverter -> DataWriter via super ports failed",
			DiagramTestUtils.drawConnectionBetweenPorts(editor, dataConverterSuperUses, dataWriterSuperProvides));
		Assert.assertTrue("Connection DataConverter -> HardLimit via super ports failed",
			DiagramTestUtils.drawConnectionBetweenPorts(editor, dataConverterSuperUses, hardLimitSuperProvides));
		// expand data converter only
		SWTBotGefEditPart dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Expand Shape");

		// verify data converter super ports are gone
		dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);
		Assert.assertNull("DataConverter Super Provides shape exists", dataConverterSuperProvides);
		Assert.assertNull("DataConverter Super Uses port shape exists", dataConverterSuperUses);

		// verify data convert individual port shapes exist
		dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataFloat = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataFloat");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		dataConverterDataFloatOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataFloat_out");
		Assert.assertNotNull("DataConverter dataDouble port shape do not exist", dataConverterDataDouble);
		Assert.assertNotNull("DataConverter dataFloat port shape do not exist", dataConverterDataFloat);
		Assert.assertNotNull("DataConverter dataDouble_out port shape do not exist", dataConverterDataDoubleOut);
		Assert.assertNotNull("DataConverter dataFloat_out port shape do not exist", dataConverterDataFloatOut);

		// verify connections exist on individual ports
		// source connections
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataDoubleOut);
		Assert.assertEquals("Data Converter dataFloat_out connections", 0, sourceConnections.size());
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataFloatOut);
		Assert.assertEquals("Data Converter dataFloat_out connections", 2, sourceConnections.size());
		// target connections
		List<SWTBotGefConnectionEditPart> targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataDouble);
		Assert.assertEquals("Data Converter dataDouble connections", 0, targetConnections.size());
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataFloat);
		Assert.assertEquals("Data Converter dataFloat connections", 1, targetConnections.size());

		// collapse data converter
		dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Collapse Shape");

		// verify super ports exist
		dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);
		Assert.assertNotNull("DataConverter Super Provides shape do not exist", dataConverterSuperProvides);
		Assert.assertNotNull("DataConverter Super Uses port shape do not exist", dataConverterSuperUses);

		// verify connections exist on super ports
		// source connections
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterSuperUses);
		Assert.assertEquals("Data Converter Super Usess Port connections", 2, sourceConnections.size());
		// target connections
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterSuperProvides);
		Assert.assertEquals("Data Converter Super Provides Port connections", 1, targetConnections.size());

		// verify individual port shapes hidden
		dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		Assert.assertNull("DataConverter dataDouble port shape exists", dataConverterDataDouble);
		Assert.assertNull("DataConverter dataDouble_out port shape exists", dataConverterDataDoubleOut);

		// expand all shapes
		editor.setFocus();
		editor.click(300, 0);
		editor.clickContextMenu("Expand All Shapes");

		// verify some individual port shapes exist
		dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		hardLimitDataFloatIn = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataFloat_in");
		Assert.assertNotNull("DataConverter dataDouble port shape does not exist", dataConverterDataDouble);
		Assert.assertNotNull("DataConverter dataDouble_out port shape does not exist", dataConverterDataDoubleOut);
		Assert.assertNotNull("HardLimit dataDouble_in port shape does not exist", hardLimitDataFloatIn);

		// verify connections exist on individual port shapes
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataDoubleOut);
		Assert.assertEquals("Data Converter dataDouble_out connections", 0, sourceConnections.size());
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataFloatOut);
		Assert.assertEquals("Data Converter dataFloat_out connections", 2, sourceConnections.size());
		// target connections
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataDouble);
		Assert.assertEquals("Data Converter dataDouble connections", 0, targetConnections.size());
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataFloat);
		Assert.assertEquals("Data Converter dataFloat connections", 1, targetConnections.size());

		// collapse DataConvert Shape
		dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Collapse Shape");

		// delete DataConverter
		dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Delete");

		// verify no connections in diagram
		Diagram diagram = DUtil.findDiagram((ContainerShape) editor.getEditPart(HARD_LIMIT).part().getModel());
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
	}

	@Test
	public void collapseExpandPrefPageTest() {

		waveformName = "IDE-1026-checkCollapseExpandPreference";
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Set preference to collapse new components
		setPortCollapsePreference(true);

		// Add component to waveform and make sure it is collapsed
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		SWTBotGefEditPart hardLimitDataFloatIn = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataFloat_in");
		SWTBotGefEditPart hardLimitDataFloatOut = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataFloat_out");
		Assert.assertNull("HardLimit should be collapsed, but dataFouble_in port shape exists", hardLimitDataFloatIn);
		Assert.assertNull("HardLimit should be collapsed, but dataFouble_out port shape exists", hardLimitDataFloatOut);

		SWTBotGefEditPart hardLimitSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, HARD_LIMIT);
		SWTBotGefEditPart hardLimitSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, HARD_LIMIT);
		Assert.assertNotNull("HardLimit Super Provides port shape does not exist", hardLimitSuperProvides);
		Assert.assertNotNull("HardLimit Super Uses port shape does not exist", hardLimitSuperUses);

		// Set preference to collapse new components
		setPortCollapsePreference(false);

		// Add component to waveform and make sure it is not collapsed
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 200, 200);

		SWTBotGefEditPart dataConverterDataFloat = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataFloat");
		SWTBotGefEditPart dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		SWTBotGefEditPart dataConverterDataFloatOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataFloat_out");
		SWTBotGefEditPart dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		Assert.assertNotNull("DataConverter dataFloat port shape does not exist", dataConverterDataFloat);
		Assert.assertNotNull("DataConverter dataDouble port shape does not exist", dataConverterDataDouble);
		Assert.assertNotNull("DataConverter dataFloat_out port shape does not exist", dataConverterDataFloatOut);
		Assert.assertNotNull("DataConverter dataDouble_out port shape does not exist", dataConverterDataDoubleOut);

		SWTBotGefEditPart dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		SWTBotGefEditPart dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);
		Assert.assertNull("DataConverter Super Provides shape should not exist", dataConverterSuperProvides);
		Assert.assertNull("DataConverter Super Uses port shape should not exist", dataConverterSuperUses);

	}

	/**
	 * IDE-1026
	 */
	@Test
	public void superPortWizardTest() {
		waveformName = "IDE-1026-superPortWizard";
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		setPortCollapsePreference(true);

		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 0, 0);

		SWTBotGefEditPart dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		SWTBotGefEditPart dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);

		DiagramTestUtils.drawConnectionBetweenPorts(editor, dataConverterSuperUses, dataConverterSuperProvides);

		bot.waitUntil(Conditions.shellIsActive("Connect"));
		SWTBot connectBot = bot.shell("Connect").bot();
		String[] ports = { "dataOctet", "dataUshort", "dataShort", "dataUlong", "dataLong", "dataFloat", "dataDouble" };
		SWTBotButton finishButton = connectBot.button("Finish");

		Assert.assertTrue("Finish Button should not be enabled unless source and target are selected", !finishButton.isEnabled());

		SWTBotList sourceGroup = connectBot.listInGroup(DATA_CONVERTER + "_1 (Source)");
		for (String port : ports) {
			sourceGroup.select(port + "_out");
		}

		SWTBotList targetGroup = connectBot.listInGroup(DATA_CONVERTER + "_1 (Target)");
		for (String port : ports) {
			targetGroup.select(port);
		}

		Assert.assertTrue("Finish Button is not enabled", finishButton.isEnabled());
		finishButton.click();

		SWTBotGefEditPart dataConverterOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterOut);
		Assert.assertTrue("Data Converter dataDouble_out doesn't have a connection", sourceConnections.size() == 1);

		setPortCollapsePreference(false);

	}

	private void setPortCollapsePreference(boolean shouldCollapse) {
		bot.menu("Window").menu("Preferences").click();
		bot.waitUntil(Conditions.shellIsActive("Preferences"), 10000);
		SWTBot prefBot = bot.shell("Preferences").bot();
		SWTBotTreeItem redhawkNode = prefBot.tree().expandNode("REDHAWK");
		redhawkNode.select("Graphiti Diagram Preferences");
		SWTBotCheckBox prefCheckBox = prefBot.checkBox(0);
		if ((shouldCollapse && !prefCheckBox.isChecked()) || (!shouldCollapse && prefCheckBox.isChecked())) {
			prefCheckBox.click();
		}
		prefBot.button("OK").click();
	}
}
