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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.sad.ui.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.tests.SadTestUtils;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.tests.xml.AbstractXmlToDiagramAddTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.finder.RHBot;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SadProvidesPort;
import mil.jpeojtrs.sca.sad.SadUsesPort;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * IDE-1434
 * Test class that deals with adding elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramAddTest extends AbstractXmlToDiagramAddTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private static final String SIG_GEN_FLOAT_OUT = "dataFloat_out";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String HARD_LIMIT_FLOAT_OUT = "dataFloat_out";

	private String waveformName;

	private RHSWTGefBot gefBot = new RHSWTGefBot();

	/**
	 * IDE-849
	 * Add a host collocation to the diagram via the sad.xml
	 */
	@Test
	public void addHostCollocationInXmlTest() {
		waveformName = "Add_Host_Collocation_Xml";
		final String HOSTCOLLOCATION_INSTANCE_NAME = "Host A";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 400, 0);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		// add host collocation
		editorText = editorText.replace("<componentplacement>", "<hostcollocation name=\"" + HOSTCOLLOCATION_INSTANCE_NAME + "\"><componentplacement>");
		editorText = editorText.replace("</partitioning>", "</hostcollocation></partitioning>");
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// check the shapes are drawing properly
		ContainerShape hostCollocationShape = DiagramTestUtils.getHostCollocationShape(editor, HOSTCOLLOCATION_INSTANCE_NAME);
		Assert.assertTrue(HOSTCOLLOCATION_INSTANCE_NAME + " host collocation shape does not exist", hostCollocationShape != null);
		ComponentShape componentShape = DiagramTestUtils.getComponentShape(editor, HARD_LIMIT_1);
		Assert.assertTrue(HARD_LIMIT_1 + " component shape does not exist", componentShape != null);
		Assert.assertTrue(HARD_LIMIT_1 + " component shape does not exist within " + HOSTCOLLOCATION_INSTANCE_NAME,
			DiagramTestUtils.childShapeExists(hostCollocationShape, componentShape));

		// verify component exists within host collocation
		HostCollocation hostCollocation = DiagramTestUtils.getHostCollocationObject(editor, HOSTCOLLOCATION_INSTANCE_NAME);
		Assert.assertTrue(HOSTCOLLOCATION_INSTANCE_NAME + " host collocation object does not exist", hostCollocation != null);
		Assert.assertTrue(HARD_LIMIT_1 + " component object does not exist within " + HOSTCOLLOCATION_INSTANCE_NAME,
			hostCollocation.getComponentPlacement().size() == 1 && hostCollocation.getComponentPlacement().get(0).getComponentInstantiation().size() == 1);
	}

	/**
	 * IDE-837
	 * Add two FindBy's connected to the component via the sad.xml. Ensure the diagram gets updated.
	 * @throws IOException
	 */
	@Test
	public void addFindByInXmlTest() throws IOException {
		waveformName = "Add_FindBy_Xml";
		final String FIND_BY_NAME = "FindByName";
		final String FBN_PORT_NAME = "NamePort";
		final String FIND_BY_SERVICE = "FindByService";
		final String FBS_PORT_NAME = "ServicePort";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);

		// Switch to the XML tab and get contents
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		// Parse the XML with EMF
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(URI.createURI(waveformName + ".sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		SoftwareAssembly sad = (SoftwareAssembly) resource.getContents().get(0);

		// Create a connection from SigGen to a findby naming service
		SadConnectInterface connection1 = SadFactory.eINSTANCE.createSadConnectInterface();
		connection1.setId("connection_1");
		SadUsesPort usesPort = SadFactory.eINSTANCE.createSadUsesPort(SIG_GEN_FLOAT_OUT, SIG_GEN_1);
		SadProvidesPort providesPort = SadFactory.eINSTANCE.createSadProvidesPort();
		providesPort.setProvidesIdentifier(FBN_PORT_NAME);
		FindBy findBy = PartitioningFactory.eINSTANCE.createFindByNamingServiceName(FIND_BY_NAME);
		providesPort.setFindBy(findBy);
		connection1.setUsesPort(usesPort);
		connection1.setProvidesPort(providesPort);

		// Create a connection from SigGen to a findby service name
		SadConnectInterface connection2 = SadFactory.eINSTANCE.createSadConnectInterface();
		connection2.setId("connection_2");
		usesPort = SadFactory.eINSTANCE.createSadUsesPort(SIG_GEN_FLOAT_OUT, SIG_GEN_1);
		providesPort = SadFactory.eINSTANCE.createSadProvidesPort();
		providesPort.setProvidesIdentifier(FBS_PORT_NAME);
		findBy = PartitioningFactory.eINSTANCE.createFindByServiceName(FIND_BY_SERVICE);
		providesPort.setFindBy(findBy);
		connection2.setUsesPort(usesPort);
		connection2.setProvidesPort(providesPort);

		// Add the connections and serialize
		sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
		sad.getConnections().getConnectInterface().add(connection1);
		sad.getConnections().getConnectInterface().add(connection2);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		resource.save(outputStream, null);

		// Need to allow the editor to get the changes
		editor.toTextEditor().setText(outputStream.toString());

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		FindByStub findByNameObject = DiagramTestUtils.getFindByObject(editor, FIND_BY_NAME);
		FindByStub findByServiceObject = DiagramTestUtils.getFindByObject(editor, FIND_BY_SERVICE);

		// Check find by object names
		Assert.assertNotNull("FindBy Naming Service did not create correctly", findByNameObject);
		Assert.assertNotNull("FindBy Naming Service did not create correctly", findByNameObject.getNamingService());
		Assert.assertEquals("FindBy Naming Service did not create correctly", FIND_BY_NAME, findByNameObject.getNamingService().getName());
		Assert.assertNotNull("Domain Finder did not create correctly", findByServiceObject);
		Assert.assertNotNull("Domain Finder did not create correctly", findByServiceObject.getDomainFinder());
		Assert.assertEquals("Domain Finder did not create correctly", FIND_BY_SERVICE, findByServiceObject.getDomainFinder().getName());

		// Check port names
		Assert.assertEquals("FindBy Name provides port did not create as expected", FBN_PORT_NAME, findByNameObject.getProvides().get(0).getName());
		Assert.assertEquals("FindBy Service provides port did not create as expected", FBS_PORT_NAME, findByServiceObject.getProvides().get(0).getName());

		// Check that connections were made
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertEquals("The expected number of connections are not present", 2, connections.size());

		SWTBotGefConnectionEditPart nameConnectionPart = connections.get(0);
		Connection nameConnection = (Connection) nameConnectionPart.part().getModel();
		UsesPortStub nameConnectionSource = (UsesPortStub) DUtil.getBusinessObject(nameConnection.getStart());
		ProvidesPortStub nameConnectionTarget = (ProvidesPortStub) DUtil.getBusinessObject(nameConnection.getEnd());
		Assert.assertEquals("FindByName connection source incorrect", SIG_GEN_FLOAT_OUT, nameConnectionSource.getName());
		Assert.assertEquals("FindByName connection target incorrect", FBN_PORT_NAME, nameConnectionTarget.getName());

		SWTBotGefConnectionEditPart serviceConnectionPart = connections.get(1);
		Connection serviceConnection = (Connection) serviceConnectionPart.part().getModel();
		UsesPortStub serviceConnectionSource = (UsesPortStub) DUtil.getBusinessObject(serviceConnection.getStart());
		ProvidesPortStub serviceConnectionTarget = (ProvidesPortStub) DUtil.getBusinessObject(serviceConnection.getEnd());
		Assert.assertEquals("FindByService connection source incorrect", SIG_GEN_FLOAT_OUT, serviceConnectionSource.getName());
		Assert.assertEquals("FindByService connection target incorrect", FBS_PORT_NAME, serviceConnectionTarget.getName());
	}

	/**
	 * IDE-978, IDE-965
	 * Add an external port to the diagram via the sad.xml
	 */
	@Test
	public void addExternalPortsInXmlTest() {
		waveformName = "Add_ExternalPort_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);

		// Confirm that no external ports exist in diagram
		SWTBotGefEditPart hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, false, HARD_LIMIT_1 + ":uses");

		// switch to overview tab and verify there are no external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("There are external ports", 0, new RHBot(bot).section("External Ports").bot().table().rowCount());

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String externalports = "</assemblycontroller> <externalports><port>" + "<usesidentifier>" + HARD_LIMIT_FLOAT_OUT + "</usesidentifier>"
			+ "<componentinstantiationref refid=\"" + HARD_LIMIT_1 + "\"/>" + "</port> </externalports>";
		editorText = editorText.replace("</assemblycontroller>", externalports);
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// assert port set to external in diagram
		hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, true, HARD_LIMIT_1 + ":uses");

		// switch to overview tab and verify there are external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("There are no external ports", 1, new RHBot(bot).section("External Ports").bot().table().rowCount());
	}

	/**
	 * IDE-124
	 * Add use device to the diagram via the sad.xml
	 */
	@Test
	public void addUseDeviceInXmlTest() {
		waveformName = "Add_UseDevice_Xml";

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
	}

	@Override
	protected ComponentDescription getComponentADescription() {
		ComponentDescription description = new ComponentDescription("rh.SigGen", new String[0], new String[] { "dataFloat_out" });
		description.setKey("path", "components");
		return description;
	}

	@Override
	protected ComponentDescription getComponentBDescription() {
		ComponentDescription description = new ComponentDescription("rh.DataConverter", new String[] { "dataFloat" }, new String[0]);
		description.setKey("path", "components");
		return description;
	}

	@Override
	protected RHBotGefEditor createEditor(String name) {
		WaveformUtils.createNewWaveform(gefBot, name, null);
		return gefBot.rhGefEditor(name);
	}

	@Override
	protected Class< ? > getEditorType() {
		return SoftwareAssembly.class;
	}

}
