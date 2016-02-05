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
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class FindByTest extends AbstractGraphitiTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";

	private String waveformName;

	/**
	 * IDE-736 Create the pictogram shape in the waveform diagram that
	 * represents the FindBy business object. This includes the ContainerShape
	 * for the element, outer and inner text, port shapes and labels,
	 * and component supported interface.
	 * 
	 * IDE-737 Create wizards to get user input when adding FindBy Name, FindBy Service,
	 * and FindBy Event Channel elements to the SAD Diagram.
	 */
	@Test
	public void checkFindByPictogramElements() {
		waveformName = "FindBy_Pictogram";
		final String findByName = "FindBy";
		final String[] provides = { "dataFloat_in" };
		final String[] uses = { "dataFloat_out" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, findByName, provides, uses);
		MenuUtils.save(editor);

		// Grab the associated business object and confirm it is a FindBy element
		SWTBotGefEditPart fbEditPart = editor.getEditPart(findByName);
		Assert.assertNotNull("FindBy Element not found", fbEditPart);
		RHContainerShapeImpl findByShape = (RHContainerShapeImpl) fbEditPart.part().getModel();
		Assert.assertTrue("Element is not a FindBy", DUtil.getBusinessObject(findByShape) instanceof FindByStub);
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);

		// Run assertions on expected properties: outer/inner text, lollipop, port number, type(provides-uses), name
		Assert.assertEquals("Outer Text does not match input", FindByUtils.FIND_BY_NAME, findByShape.getOuterText().getValue());
		Assert.assertEquals("Inner Text does not match input", findByName, findByShape.getInnerText().getValue());
		Assert.assertEquals("Diagram object and domain object names don't match", findByName, findByObject.getNamingService().getName());
		Assert.assertNotNull("component supported interface graphic should not be null", findByShape.getLollipop());

		Assert.assertTrue("Number of ports is incorrect", findByShape.getUsesPortStubs().size() == 1 && findByShape.getProvidesPortStubs().size() == 1);
		Assert.assertEquals("Uses port name is incorrect", uses[0], findByShape.getUsesPortStubs().get(0).getName());
		Assert.assertEquals("Diagram uses and domain uses don't match", uses[0], findByObject.getUses().get(0).getName());
		Assert.assertEquals("Provides port name is incorrect", provides[0], findByShape.getProvidesPortStubs().get(0).getName());
		Assert.assertEquals("Diagram provides and provides uses don't match", provides[0], findByObject.getProvides().get(0).getName());
	}

	/**
	 * <ul>
	 * <li>IDE-737 Test adding a findby to the SAD diagram and completing the wizard</li>
	 * <li>IDE-669 Test the delete context menu item</li>
	 * </ul>
	 */
	@Test
	public void addDeleteFindBys() {
		waveformName = "FindBy_Delete";
		String[] findByList = { FindByUtils.FIND_BY_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, FindByUtils.FIND_BY_EVENT_CHANNEL,
			FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		for (String s : findByList) {
			// Add component to diagram from palette
			DiagramTestUtils.addFromPaletteToDiagram(editor, s, 0, 0);
			FindByUtils.completeFindByWizard(gefBot, s, null, new String[] { "p1", "p2" }, new String[] { "u1", "u2" });
		}

		for (String s : findByList) {
			// Drill down to graphiti component shape
			SWTBotGefEditPart gefEditPart = editor.getEditPart(FindByUtils.getFindByDefaultName(s));
			DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
			Assert.assertNull(editor.getEditPart(s));
		}
	}

	/**
	 * IDE-1414 Collapsing and expanding FindBy causes duplicate ports
	 */
	@Test
	public void collapseExpandFindBy() {
		waveformName = "Collapse_Expand_FindBy";
		final String findByName = "FindBy";
		final String[] provides = { "data_in" };
		final String[] uses = { "data_out" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add FindBy to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, findByName, provides, uses);
		MenuUtils.save(editor);

		// Check that only two ports exist and what they are
		Assert.assertNotNull(DiagramTestUtils.getDiagramProvidesPort(editor, findByName, provides[0]));
		Assert.assertNotNull(DiagramTestUtils.getDiagramUsesPort(editor, findByName, uses[0]));

		// Collapse the resource
		editor.click(editor.rootEditPart());
		editor.clickContextMenu("Collapse All Shapes");

		// Check that only the global ports are now available
		Assert.assertNotNull(DiagramTestUtils.getDiagramProvidesSuperPort(editor, findByName));
		Assert.assertNotNull(DiagramTestUtils.getDiagramUsesSuperPort(editor, findByName));

		// Expand the resource
		editor.click(editor.rootEditPart());
		editor.clickContextMenu("Expand All Shapes");

		// Check that only two ports exist and what they are
		Assert.assertNotNull(DiagramTestUtils.getDiagramProvidesPort(editor, findByName, provides[0]));
		Assert.assertNotNull(DiagramTestUtils.getDiagramUsesPort(editor, findByName, uses[0]));

	}

	/**
	 * Ensure that deleting a FindBy that is part of a connection removes the connection from the sad.xml
	 */
	@Test
	public void deleteFindByWithConnection() {
		waveformName = "Delete_FindBy";
		final String FIND_BY_NAME = "FindByName";
		final String[] provides = { "data_in" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, FIND_BY_NAME, provides, null);
		MenuUtils.save(editor);

		// Create connection on diagram
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, FIND_BY_NAME);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);
		MenuUtils.save(editor);

		// Check sad.xml for connection
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection. Expected: <findby><namingservice name=\"" + FIND_BY_NAME + "\"/>",
			editorText.matches("(?s).*" + "<connectinterface.*<findby>.*<namingservice name=\"" + FIND_BY_NAME + "\"/>" + ".*"));

		// Delete Findby
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(FIND_BY_NAME));

		try {
			gefBot.shell("Confirm Delete").setFocus();
			Assert.fail("Deleting FindBy elements should not require a dialog");
		} catch (WidgetNotFoundException e) {
			Assert.assertTrue(e.getMessage().matches(".*" + "Confirm Delete" + ".*"));
		}

		editor = gefBot.rhGefEditor(waveformName);
		editor.setFocus();
		gefBot.menu("File").menu("Save").click();

		Assert.assertNull("FindBy shape was not removed", editor.getEditPart(FIND_BY_NAME));

		// Ensure Findby connection is removed from the XML
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("FindBy Connection was not removed", editorText.matches("(?s).*" + "<connections/>" + ".*"));
	}

	/**
	 * IDE-652 & IDE-908
	 * Edit existing FindBy Elements
	 * Change names, add & remove ports
	 * 
	 * IDE-1403 Editing FindBy name creates duplicate item in diagram
	 * @throws IOException
	 */
	@Test
	public void editFindBy() throws IOException {
		waveformName = "FindBy_Connection";
		final String FIND_BY_NAME = "FindBy";
		final String newFindByName = "NewFindByName";
		final String[] provides = { "data_in" };
		final String[] uses = { "data_out" };
		final String NEW_USES_PORT = "dataFloat_out";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 20);
		DiagramTestUtils.addFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, FIND_BY_NAME, provides, uses);
		MenuUtils.save(editor);

		// Create connection on diagram
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, FIND_BY_NAME);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);

		SWTBotGefEditPart findByUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, FIND_BY_NAME);
		SWTBotGefEditPart hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, findByUsesPart, hardLimitProvidesPart);

		MenuUtils.save(editor);

		// IDE-1403 - Closing and opening the editor is required to check for this bug
		editor.close();
		ProjectExplorerUtils.openProjectInEditor(bot, waveformName, waveformName + ".sad.xml");
		editor = gefBot.rhGefEditor(waveformName);

		// Open FindBy edit wizard and change name
		editor.getEditPart(FIND_BY_NAME).select();
		editor.clickContextMenu("Edit Find By Name");
		gefBot.textWithLabel("Component Name:").setText(newFindByName);
		gefBot.button("Finish").click();

		// IDE-1403 - Check XML to see if connection details were updated and make sure old fb object is gone
		checkFindByName(editor, newFindByName);
		SWTBotGefEditPart oldFbEditPart = editor.getEditPart(FIND_BY_NAME);
		Assert.assertNull("Old FindBy Element not removed from diagram", oldFbEditPart);

		// Open FindBy edit wizard and change name, remove existing port, and add a new one
		editor.getEditPart(newFindByName).select();
		editor.clickContextMenu("Edit Find By Name");

		// Delete existing provides port
		gefBot.listInGroup("Port(s) to use for connections", 0).select(provides[0]);
		gefBot.button("Delete", 0).click();

		// Add new uses port
		gefBot.textInGroup("Port(s) to use for connections", 1).setText(NEW_USES_PORT);
		gefBot.button("Add Uses Port").click();

		gefBot.button("Finish").click();

		// Confirm that changes were made
		SWTBotGefEditPart fbEditPart = editor.getEditPart(newFindByName);
		Assert.assertNotNull("FindBy Element not found", fbEditPart);
		RHContainerShapeImpl findByShape = (RHContainerShapeImpl) fbEditPart.part().getModel();
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);

		Assert.assertEquals("Inner Text was not updated", newFindByName, findByShape.getInnerText().getValue());
		Assert.assertEquals("Diagram object and domain object names don't match", newFindByName, findByObject.getNamingService().getName());
		Assert.assertTrue("Number of ports is incorrect", findByShape.getUsesPortStubs().size() == 2 && findByShape.getProvidesPortStubs().size() == 0);
		Assert.assertEquals("Uses port name is incorrect", NEW_USES_PORT, findByShape.getUsesPortStubs().get(1).getName());
		Assert.assertEquals("Diagram uses and domain uses don't match", NEW_USES_PORT, findByObject.getUses().get(1).getName());

		// Confirm that connections properly updated
		sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesPart);
		Assert.assertTrue("SigGen connection should have been removed", connections.size() == 0);

		hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		connections = DiagramTestUtils.getTargetConnectionsFromPort(editor, hardLimitProvidesPart);
		Assert.assertEquals("HardLimit should only have one incoming connection", 1, connections.size());

		SWTBotGefConnectionEditPart connectionPart = connections.get(0);
		Connection connection = (Connection) connectionPart.part().getModel();
		UsesPortStub connectionSource = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		ProvidesPortStub connectionTarget = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connection source incorrect", uses[0], connectionSource.getName());
		Assert.assertEquals("Connection target incorrect", "dataFloat_in", connectionTarget.getName());
	}

	private void checkFindByName(RHBotGefEditor editor, String newFindByName) throws IOException {
		editor.bot().cTabItem(waveformName + ".sad.xml").activate();
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		String editorText = editor.toTextEditor().getText();
		Resource resource = resourceSet.createResource(org.eclipse.emf.common.util.URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		for (SadConnectInterface conn : sad.getConnections().getConnectInterface()) {
			FindBy fb = conn.getProvidesPort().getFindBy();
			if (fb == null) {
				fb = conn.getUsesPort().getFindBy();
				if (fb == null) {
					continue;
				}
			}
			Assert.assertEquals("FindBy Naming Service did not update", newFindByName, fb.getNamingService().getName());
		}
		editor.bot().cTabItem("Diagram").activate();
	}

	/**
	 * IDE-738
	 * Allow connections that include FindBy elements.
	 * Update the sad.xml to show the resultant connection details
	 */
	@Test
	public void findByConnection() {
		waveformName = "FindBy_Connection";
		final String findByName = "FindBy";
		final String[] provides = { "data_in" };
		final String[] uses = { "data_out" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, findByName, provides, uses);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 300);
		MenuUtils.save(editor);

		// Create connection on diagram
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIG_GEN_1);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, findByName);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);
		SWTBotGefEditPart findByUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, findByName);
		SWTBotGefEditPart hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		Assert.assertTrue("Failed to draw connection from FindBy uses port",
			DiagramTestUtils.drawConnectionBetweenPorts(editor, findByUsesPart, hardLimitProvidesPart));
		MenuUtils.save(editor);

		// Check sad.xml for connection
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection. Expected: <findby><namingservice name=\"" + findByName + "\"/>",
			editorText.matches("(?s).*" + "<connectinterface.*<findby>.*<namingservice name=\"" + findByName + "\"/>" + ".*"));
	}
}
