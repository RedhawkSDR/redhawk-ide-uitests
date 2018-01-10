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
package gov.redhawk.ide.graphiti.sad.ui.tests.findby;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.ConnectionUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.ConnectionUtils.ConnectionState;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public abstract class AbstractFindByTest extends AbstractGraphitiTest {

	protected static final String SIG_GEN = "rh.SigGen";
	protected static final String SIG_GEN_1 = "SigGen_1";
	protected static final String HARD_LIMIT = "rh.HardLimit";
	protected static final String HARD_LIMIT_1 = "HardLimit_1";
	protected static final String PORT_COMP = "AllPortTypesTestComponent";
	protected static final String PORT_COMP_1 = "AllPortTypesTestComponent_1";

	protected static final String[] PROVIDES_PORTS = { "provides_in" };
	protected static final String[] USES_PORTS = { "uses_out" };
	protected static final String NEW_USES_PORT = "dataFloat_out";

	protected abstract String getFindByType();

	protected abstract String getFindByName();

	protected abstract String getEditTextLabel();

	protected RHBotGefEditor editor; // SUPPRESS CHECKSTYLE INLINE
	protected String waveformName; // SUPPRESS CHECKSTYLE INLINE

	/**
	 * IDE-652 & IDE-908
	 * Edit existing FindBy Elements
	 * Change names, add & remove ports
	 * 
	 * IDE-1403 Editing FindBy name creates duplicate item in diagram
	 *
	 * IDE-1374 - FindBy direct edit dialog incorrectly enables Delete button
	 * @throws IOException
	 */
	@Test
	public void editFindBy() throws IOException {
		waveformName = "FindBy_Edit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		editor = gefBot.rhGefEditor(waveformName);

		// Add components to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 20);
		DiagramTestUtils.addFromPaletteToDiagram(editor, getFindByType(), 0, 150);
		FindByUtils.completeFindByWizard(gefBot, getFindByType(), getFindByName(), PROVIDES_PORTS, USES_PORTS);

		createFindByConnections();

		// IDE-1403 - Closing and opening the editor is required to check for this bug
		MenuUtils.save(editor);
		editor.close();
		ProjectExplorerUtils.openProjectInEditor(bot, waveformName, waveformName + ".sad.xml");
		editor = gefBot.rhGefEditor(waveformName);

		// Open FindBy edit wizard and change name
		getEditor().getEditPart(getFindByName()).select();
		getEditor().clickContextMenu("Edit " + getFindByType());

		final String newFindByName = "Edited" + getFindByName();
		gefBot.textWithLabel(getEditTextLabel()).setText(newFindByName);
		try {
			gefBot.button("Finish").click();
		} catch (WidgetNotFoundException e) {
			gefBot.button("OK").click();
		}

		// Check XML to see if connection details were updated
		checkXMLForUpdates(editor, newFindByName);

		// Make sure FindBy updated on the diagram
		SWTBotGefEditPart fbEditPart = editor.getEditPart(getFindByName());
		Assert.assertNull("FindBy Element name was not updated", fbEditPart);
		fbEditPart = editor.getEditPart(newFindByName);
		Assert.assertNotNull("FindBy Element not found", fbEditPart);

		// Edit FindBy ports
		editFindByPorts(newFindByName);
		RHContainerShape findByShape = (RHContainerShape) fbEditPart.part().getModel();
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);
		validateFindByPortEdits(findByShape, findByObject);

		// Confirm that changes were made in then diagram
		Assert.assertEquals("Inner Text was not updated", newFindByName, findByShape.getInnerText().getValue());
		String findByDomainName = FindByUtils.FIND_BY_NAME.equals(getFindByType()) ? findByObject.getNamingService().getName()
			: findByObject.getDomainFinder().getName();
		Assert.assertEquals("Diagram object and domain object names don't match", newFindByName, findByDomainName);
	}

	@Test
	public void componentSupportedInterfaceTest() {
		waveformName = "FindBy_Lollipop_Connection";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		editor = gefBot.rhGefEditor(waveformName);

		// Add components to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, PORT_COMP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, getFindByType(), 375, 50);
		if (FindByUtils.FIND_BY_SERVICE.equals(getFindByType())) {
			// Want to make sure the FindByService uses the ExecutableDevice IDL
			FindByUtils.completeFindByWizard(gefBot, getFindByType(), getFindByName(), PROVIDES_PORTS, USES_PORTS, true);
		} else {
			FindByUtils.completeFindByWizard(gefBot, getFindByType(), getFindByName(), PROVIDES_PORTS, USES_PORTS);
		}


		SWTBotGefEditPart findByLollipopPart = DiagramTestUtils.getComponentSupportedInterface(editor, getFindByName());
		SWTBotGefEditPart portCompUsesPart = null;

		switch (getFindByType()) {
		case FindByUtils.FIND_BY_DOMAIN_MANAGER:
			portCompUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), PORT_COMP_1, PortCompPorts.DOM_MGR.toString());
			break;
		case FindByUtils.FIND_BY_EVENT_CHANNEL:
			portCompUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), PORT_COMP_1, PortCompPorts.EVENT_CHANNEL.toString());
			break;
		case FindByUtils.FIND_BY_FILE_MANAGER:
			portCompUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), PORT_COMP_1, PortCompPorts.FILE_MGR.toString());
			break;
		case FindByUtils.FIND_BY_NAME:
			portCompUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), PORT_COMP_1, PortCompPorts.RESOURCE.toString());
			break;
		case FindByUtils.FIND_BY_SERVICE:
			portCompUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), PORT_COMP_1, PortCompPorts.EXECUTABLE_DEVICE.toString());
			break;
		default:
			break;
		}
		DiagramTestUtils.drawConnectionBetweenPorts(editor, portCompUsesPart, findByLollipopPart);

		// Check for normal (allowed) connections
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, portCompUsesPart);
		Assert.assertEquals("Connection was not added", 1, sourceConnections.size());
		ConnectionUtils.assertConnectionStyling(sourceConnections.get(0), ConnectionState.NORMAL);

		// Check for illegal (error) connections
		if (!(FindByUtils.FIND_BY_NAME.equals(getFindByType()))) {
			portCompUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), PORT_COMP_1, PortCompPorts.BULKIO.toString());
			DiagramTestUtils.drawConnectionBetweenPorts(editor, portCompUsesPart, findByLollipopPart);

			sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, portCompUsesPart);
			Assert.assertEquals("Connection was not added", 1, sourceConnections.size());
			ConnectionUtils.assertConnectionStyling(sourceConnections.get(0), ConnectionState.ERROR);
		}

		// IDE-1032 && IDE-1521 - Deleting a Find By with a connection to the lollipop fails
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(getFindByName()));
		Assert.assertNull("FindBy delete action failed", editor.getEditPart(getFindByName()));
	}

	protected void createFindByConnections() {
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), SIG_GEN_1);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, getFindByName());
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);

		SWTBotGefEditPart findByUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), getFindByName());
		SWTBotGefEditPart hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, findByUsesPart, hardLimitProvidesPart);

		SWTBotGefEditPart findByLollipopPart = DiagramTestUtils.getComponentSupportedInterface(editor, getFindByName());
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByLollipopPart);

		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesPart);
		Assert.assertEquals("Connection was not added", 2, sourceConnections.size());

		List<SWTBotGefConnectionEditPart> targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, hardLimitProvidesPart);
		Assert.assertEquals("Connection was not added", 1, targetConnections.size());
	}

	protected void checkXMLForUpdates(RHBotGefEditor editor, String newFindByName) throws IOException {
		editor.bot().cTabItem(waveformName + ".sad.xml").activate();
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		String editorText = editor.toTextEditor().getText();
		Resource resource = resourceSet.createResource(org.eclipse.emf.common.util.URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		for (SadConnectInterface conn : sad.getConnections().getConnectInterface()) {
			FindBy fb = null;
			if (conn.getProvidesPort() != null) {
				fb = conn.getProvidesPort().getFindBy();
			} else {
				fb = conn.getComponentSupportedInterface().getFindBy();
			}

			if (fb != null) {
				if (FindByUtils.FIND_BY_NAME.equals(getFindByType())) {
					Assert.assertEquals("FindBy Naming Service did not update", newFindByName, fb.getNamingService().getName());
				} else if (FindByUtils.FIND_BY_SERVICE.equals(getFindByType()) || FindByUtils.FIND_BY_EVENT_CHANNEL.equals(getFindByType())) {
					Assert.assertEquals("FindBy Naming Service did not update", newFindByName, fb.getDomainFinder().getName());
				}
			}

			fb = conn.getUsesPort().getFindBy();
			if (fb == null) {
				continue;
			}
			if (FindByUtils.FIND_BY_NAME.equals(getFindByType())) {
				Assert.assertEquals("FindBy Naming Service did not update", newFindByName, fb.getNamingService().getName());
			} else if (FindByUtils.FIND_BY_SERVICE.equals(getFindByType()) || FindByUtils.FIND_BY_EVENT_CHANNEL.equals(getFindByType())) {
				Assert.assertEquals("FindBy Naming Service did not update", newFindByName, fb.getDomainFinder().getName());
			}

		}
		editor.bot().cTabItem("Diagram").activate();
	}

	protected void editFindByPorts(String newFindByName) {
		editor.getEditPart(newFindByName).select();
		editor.clickContextMenu("Edit " + getFindByType());

		// IDE-1374 test
		Assert.assertFalse("Delete button should not be enabled without a selection",
			gefBot.buttonInGroup("Delete", "Port(s) to use for connections", 0).isEnabled());
		Assert.assertFalse("Delete button should not be enabled without a selection",
			gefBot.buttonInGroup("Delete", "Port(s) to use for connections", 1).isEnabled());

		// Delete existing provides port
		gefBot.listInGroup("Port(s) to use for connections", 0).select(PROVIDES_PORTS[0]);
		gefBot.button("Delete", 0).click();

		// Add new uses port
		gefBot.textInGroup("Port(s) to use for connections", 1).setText(NEW_USES_PORT);
		gefBot.button("Add Uses Port").click();

		gefBot.button("Finish").click();
	}

	protected void validateFindByPortEdits(RHContainerShape findByShape, FindByStub findByObject) {
		List<EObject> usesPortStubs = findByShape.getUsesPortsContainerShape().getLink().getBusinessObjects();
		List<EObject> providesPortStubs = findByShape.getProvidesPortsContainerShape().getLink().getBusinessObjects();
		Assert.assertTrue("Number of ports is incorrect", usesPortStubs.size() == 2 && providesPortStubs.size() == 0);
		Assert.assertEquals("Uses port name is incorrect", NEW_USES_PORT, ((UsesPortStub) usesPortStubs.get(1)).getName());
		Assert.assertEquals("Diagram uses and domain uses don't match", NEW_USES_PORT, findByObject.getUses().get(1).getName());

		// Confirm that connections properly updated
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesPart);
		Assert.assertTrue("SigGen connection should have been removed", connections.size() == 1);

		SWTBotGefEditPart hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		connections = DiagramTestUtils.getTargetConnectionsFromPort(editor, hardLimitProvidesPart);
		Assert.assertEquals("HardLimit should only have one incoming connection", 1, connections.size());

		SWTBotGefConnectionEditPart connectionPart = connections.get(0);
		Connection connection = (Connection) connectionPart.part().getModel();
		UsesPortStub connectionSource = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		ProvidesPortStub connectionTarget = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connection source incorrect", USES_PORTS[0], connectionSource.getName());
		Assert.assertEquals("Connection target incorrect", "dataFloat_in", connectionTarget.getName());
	}

	public RHBotGefEditor getEditor() {
		return editor;
	}

	private enum PortCompPorts {
		DOM_MGR("dommgr_out"),
		FILE_MGR("filemgr_out"),
		EVENT_CHANNEL("eventchannel_out"),
		RESOURCE("resource_out"),
		DEVICE("device_out"),
		AGGREGATE_DEVICE("aggdev_out"),
		EXECUTABLE_DEVICE("execdev_out"),
		BULKIO("dataFloatBIO_out");

		private String portName;

		PortCompPorts(String portName) {
			this.portName = portName;
		}

		@Override
		public String toString() {
			return this.portName;
		}
	}
}
