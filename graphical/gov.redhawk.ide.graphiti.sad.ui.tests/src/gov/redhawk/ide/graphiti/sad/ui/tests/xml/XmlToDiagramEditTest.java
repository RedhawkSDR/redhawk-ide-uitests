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

import java.math.BigInteger;
import java.util.List;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.tests.SadTestUtils;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

/**
 * Test class that deals with editing elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramEditTest extends AbstractGraphitiTest {
	static final String SIG_GEN = "rh.SigGen";
	static final String SIG_GEN_1 = "SigGen_1";
	static final String HARD_LIMIT = "rh.HardLimit";
	static final String HARD_LIMIT_1 = "HardLimit_1";
	static final String HARD_LIMIT_2 = "HardLimit_2";
	static final String DATA_CONVERTER = "rh.DataConverter";
	static final String DATA_CONVERTER_1 = "DataConverter_1";
	static final String HOST_CO_NAME = "collocation_1";

	private String waveformName;

	/**
	 * IDE-853
	 * Test editing component properties in the sad.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editComponentInXmlTest() {
		waveformName = "Edit_Component_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		final RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 0, 200);
		MenuUtils.save(editor);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace(HARD_LIMIT_1, HARD_LIMIT_2);
		editorText = editorText.replace("startorder=\"1\"", "startorder=\"3\"");
		editorText = editorText.replace("startorder=\"2\"", "startorder=\"1\"");
		editorText = editorText.replace("startorder=\"3\"", "startorder=\"2\"");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		gefBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return HARD_LIMIT_2.equals(DiagramTestUtils.getComponentObject(editor, HARD_LIMIT_2).getUsageName());
			}

			@Override
			public String getFailureMessage() {
				return "Usage Name did not update correctly. Expected [" + HARD_LIMIT_2 + "] Found ["
					+ DiagramTestUtils.getComponentObject(editor, HARD_LIMIT).getUsageName() + "]";
			}
		}, 10000, 1000);

		SadComponentInstantiation componentObj = DiagramTestUtils.getComponentObject(editor, HARD_LIMIT);
		Assert.assertEquals("Component ID did not update correctly", HARD_LIMIT_2, componentObj.getId());
		Assert.assertEquals("Naming Service did not update correctly", HARD_LIMIT_2, componentObj.getFindComponent().getNamingService().getName());
		Assert.assertEquals("Start Order did not update correctly", BigInteger.valueOf(2), componentObj.getStartOrder());

	}

	/**
	 * IDE-854
	 * Test editing connection properties in the sad.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editConnectionInXmlTest() {
		waveformName = "Edit_Connection_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		final RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add components to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 10);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 10);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 0, 150);

		// Get port edit parts
		SWTBotGefEditPart sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart hardLimitProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesEditPart, hardLimitProvidesEditPart);
		MenuUtils.save(editor);
bot.sleep(5000);
		Assert.assertEquals("Wrong number of connections before edit", 1, DiagramTestUtils.getSourceConnectionsFromPort(editor, DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1)).size());

		// Edit content of sad.xml
		final String connectionProvidesBefore = "<providesidentifier>dataFloat_in</providesidentifier>";
		final String connectionProvidesAfter = "<providesidentifier>dataFloat</providesidentifier>";
		final String connectionCiBefore = "<componentinstantiationref refid=\"HardLimit_1\"/>";
		final String connectionCiAfter = "<componentinstantiationref refid=\"DataConverter_1\"/>";
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("Connection does not reference correct provides port before editing", editorText.contains(connectionProvidesBefore));
		Assert.assertTrue("Connection does not reference correct component instantiation before editing", editorText.contains(connectionCiBefore));
		editorText = editorText.replace(connectionProvidesBefore, connectionProvidesAfter);
		editorText = editorText.replace(connectionCiBefore, connectionCiAfter);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("Connection does not reference correct provides port after editing", editorText.contains(connectionProvidesAfter));
		Assert.assertTrue("Connection does not reference correct component instantiation after editing", editorText.contains(connectionCiAfter));

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// TODO: test wait
		bot.sleep(500);

		bot.waitUntil(new DefaultCondition() {
			private List<SWTBotGefConnectionEditPart> sourceConnections;

			@Override
			public boolean test() throws Exception {
				sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1));
				return (sourceConnections.size() == 1);
			}

			@Override
			public String getFailureMessage() {
				return "Wrong number of connections found, expected 1, but found " + sourceConnections.size();
			}

		});

		// Check that SigGen connection data has changed
		sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		final List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesEditPart);
		Assert.assertEquals("Wrong number of connections found", 1, sourceConnections.size());
		final Connection connection = (Connection) sourceConnections.get(0).part().getModel();

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) sigGenUsesEditPart.part().getModel()));

		final SWTBotGefEditPart dataConverterProvidesPort = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER_1, "dataFloat");
		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", DUtil.getBusinessObject((ContainerShape) dataConverterProvidesPort.part().getModel()),
			providesPort);

		// Check that HardLimit is no longer a part of a connection
		hardLimitProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		List<SWTBotGefConnectionEditPart> providesConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, hardLimitProvidesEditPart);
		Assert.assertTrue("HardLimit should not be the target of a connection", providesConnections.isEmpty());
	}

	/**
	 * IDE-855
	 * Test editing assembly controller refid in sad.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editAssemblyControllerInXmlTest() {
		waveformName = "Edit_Assembly_Controller_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		final RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);
		MenuUtils.save(editor);

		// Verify componentOne is set as assembly Controller
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		ComponentShape componentShapeOne = DiagramTestUtils.getComponentShape(editor, SIG_GEN_1);
		Assert.assertEquals("Setup for test is flawed, componentOne is not the assembly controller", ComponentUtils.getStartOrderText(componentShapeOne).getValue(), "0");
		ComponentShape componentShapeTwo = DiagramTestUtils.getComponentShape(editor, HARD_LIMIT_1);
		Assert.assertEquals("Setup for test is flawed, componentTwo is the assembly controller", ComponentUtils.getStartOrderText(componentShapeTwo).getValue(), "1");

		// Edit content of sad.xml, change assembly controller from componentOne to componentTwo
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<componentinstantiationref refid=\"" + SIG_GEN_1 + "\"/>", "<componentinstantiationref refid=\"" + HARD_LIMIT_1 + "\"/>");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits reflect that componentTwo is now assembly controller
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		gefBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return !ComponentUtils.isAssemblyController(DiagramTestUtils.getComponentShape(editor, SIG_GEN_1))
					&& ComponentUtils.isAssemblyController(DiagramTestUtils.getComponentShape(editor, HARD_LIMIT_1));
			}

			@Override
			public String getFailureMessage() {
				return "Diagram does not represent newly changed assembly controller";
			}
		}, 10000, 1000);
	}

	/**
	 * IDE-856
	 * User should be able to edit the contents of a host collocation object in the sad.xml
	 * and have the graphical representation update accordingly the next time they view the diagram.
	 */
	@Test
	public void editHostCoInXmlTest() {
		waveformName = "Edit_Host_Co_XML";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		final RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add host collocation to the waveform
		DiagramTestUtils.addHostCollocationToDiagram(editor);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 20, 20);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 150);
		MenuUtils.save(editor);

		// Verify that the host collocation has two components
		ContainerShape hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
		Assert.assertEquals("Two components are expected", 2, hostCoShape.getChildren().size());

		// Edit sad.xml to remove SigGen component placement
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int begin = editorText.indexOf("<componentfile id=\"SigGen");
		int end = editorText.indexOf("</componentfile>", begin) + "</componentfile>".length();
		String sigGenCfText = editorText.substring(begin, end);
		begin = editorText.indexOf("<componentplacement>");
		end = editorText.indexOf("</componentplacement>", begin) + "</componentplacement>".length();
		String sigGenCpText = editorText.substring(begin, end); // save this
		editorText = editorText.substring(0, begin) + editorText.substring(end);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm SigGen component was removed from Host Collocation
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);

		gefBot.waitUntil(new DefaultCondition() {
			private ContainerShape shape;

			@Override
			public boolean test() throws Exception {
				shape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
				return shape.getChildren().size() == 1;
			}

			@Override
			public String getFailureMessage() {
				return "Wrong number of components found. Expected [1] Found [" + shape.getChildren().size() + "]";
			}
		}, 10000, 1000);

		editor.drag(HOST_CO_NAME, 20, 20); // Need to do a save to prevent override dialog from showing on next edit
		MenuUtils.save(editor);
		ComponentShape componentShape = (ComponentShape) hostCoShape.getChildren().get(0);
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		Assert.assertEquals("HardLimit component expected", HARD_LIMIT_1, ci.getId());

		// Edit sad.xml to replace SigGen component
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<componentfile id", sigGenCfText + " \n <componentfile id");
		editorText = editorText.replace("<componentplacement>", sigGenCpText + " \n <componentplacement>");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm SigGen component was added back to Host Collocation
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);

		gefBot.waitUntil(new DefaultCondition() {
			private ContainerShape shape;

			@Override
			public boolean test() throws Exception {
				shape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
				return shape.getChildren().size() == 2;
			}

			@Override
			public String getFailureMessage() {
				return "Wrong number of components found. Expected [2] Found [" + shape.getChildren().size() + "]";
			}
		}, 10000, 1000);

		editor.drag(HOST_CO_NAME, 20, 20); // Need to do a save to prevent override dialog from showing on next edit
		if (editor.isDirty()) {
			MenuUtils.save(editor);
		}
		componentShape = (ComponentShape) hostCoShape.getChildren().get(0);
		ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		Assert.assertEquals("SigGen component expected", SIG_GEN_1, ci.getId());

		// Edit sad.xml to change Host Collocation name
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<hostcollocation name=\"" + HOST_CO_NAME + "\">", "<hostcollocation name=\"" + HOST_CO_NAME + "_1\">");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm that Host Collocation name updated in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		gefBot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return editor.getEditPart(HOST_CO_NAME + "_1") != null;
			}

			@Override
			public String getFailureMessage() {
				return "Host Collocation " + HOST_CO_NAME + "_1" + " does not exist";
			}
		}, 10000, 1000);
	}

	/**
	 * IDE-124
	 * Edit use device to the diagram via the sad.xml
	 */
	@Test
	public void editUseDeviceInXmlTest() {
		waveformName = "Edit_UseDevice_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String usesDevice = "<assemblycontroller/><usesdevicedependencies><usesdevice id=\"FrontEndTuner_1\"></usesdevicedependencies>";
		editorText = editorText.replace("<assemblycontroller/>", usesDevice);
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		SWTBotGefEditPart useDeviceEditPart = editor.getEditPart(SadTestUtils.USE_DEVICE);
		SadTestUtils.assertUsesDevice(useDeviceEditPart);

		// edit device id via xml
		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();

		editorText = editorText.replace("<usesdevice id=\"FrontEndTuner_1\">", "<usesdevice id=\"FrontEndTuner_2\">");
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		useDeviceEditPart = editor.getEditPart(SadTestUtils.USE_DEVICE);
		SadTestUtils.assertUsesDevice(useDeviceEditPart);
	}
}
