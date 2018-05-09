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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

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
		RHContainerShape findByShape = (RHContainerShape) fbEditPart.part().getModel();
		Assert.assertTrue("Element is not a FindBy", DUtil.getBusinessObject(findByShape) instanceof FindByStub);
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);

		// Run assertions on expected properties: outer/inner text, lollipop, port number, type(provides-uses), name
		Assert.assertEquals("Outer Text does not match input", FindByUtils.FIND_BY_NAME, findByShape.getOuterText().getValue());
		Assert.assertEquals("Inner Text does not match input", findByName, findByShape.getInnerText().getValue());
		Assert.assertEquals("Diagram object and domain object names don't match", findByName, findByObject.getNamingService().getName());
		Assert.assertNotNull("component supported interface graphic should not be null", findByShape.getLollipop());

		List<EObject> usesPortStubs = findByShape.getUsesPortsContainerShape().getLink().getBusinessObjects();
		List<EObject> providesPortStubs = findByShape.getProvidesPortsContainerShape().getLink().getBusinessObjects();
		Assert.assertTrue("Number of ports is incorrect", usesPortStubs.size() == 1 && providesPortStubs.size() == 1);
		Assert.assertEquals("Uses port name is incorrect", uses[0], ((UsesPortStub) usesPortStubs.get(0)).getName());
		Assert.assertEquals("Diagram uses and domain uses don't match", uses[0], findByObject.getUses().get(0).getName());
		Assert.assertEquals("Provides port name is incorrect", provides[0], ((ProvidesPortStub) providesPortStubs.get(0)).getName());
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
			String defaultName = FindByUtils.getFindByDefaultName(s);
			SWTBotGefEditPart gefEditPart = editor.getEditPart(defaultName);
			DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
			Assert.assertNull(editor.getEditPart(defaultName));
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
