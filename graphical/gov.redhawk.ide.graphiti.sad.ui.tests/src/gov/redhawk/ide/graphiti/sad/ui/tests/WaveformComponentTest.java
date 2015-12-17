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

import static org.junit.Assert.assertEquals;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sdr.ComponentsSubContainer;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class WaveformComponentTest extends AbstractGraphitiTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String HARD_LIMIT_2 = "HardLimit_2";
	private static final String DATA_CONVERTER = "rh.DataConverter";
	private static final String DATA_CONVERTER_1 = "DataConverter_1";
	private static final String HOST_CO_NAME = "collocation_1";

	private String waveformName;

	private static final String[] TARGET_SDR_ITEMS_NOT_IN_PALETTE = { "fftlib", "RedhawkDevUtils" };

	/**
	 * IDE-726
	 * Create the pictogram shape in the waveform diagram that represents the component business object.
	 * This includes the ContainerShape for the component, labels for Usage Name and ID, port shapes and labels,
	 * start order icon, and component supported interface.
	 */
	@Test
	public void checkComponentPictogramElements() {
		waveformName = "IDE-726-Test";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT_1), true);

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragComponentFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT_2), false);
	}

	/**
	 * IDE-669
	 * Components are removed with the delete button (trashcan image) that appears when you select the component,
	 * but the delete context menu does not remove the component from the diagram. In most cases, the delete and
	 * remove context menu options are grayed out and not selectable.
	 */
	@Test
	public void checkComponentContextMenuDelete() {
		waveformName = "IDE-669-Test";
		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		for (String s : new String[] { SIG_GEN, HARD_LIMIT, DATA_CONVERTER }) {
			// Add component to diagram from palette
			DiagramTestUtils.addFromPaletteToDiagram(editor, s, 0, 0);
		}

		for (String s : new String[] { SIG_GEN_1, HARD_LIMIT_1, DATA_CONVERTER_1 }) {
			// Drill down to graphiti component shape
			SWTBotGefEditPart gefEditPart = editor.getEditPart(s);
			DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
			Assert.assertNull(editor.getEditPart(s));
		}
	}

	/**
	 * IDE-881
	 * Ensure components that are located within host collocation are deleted both in the diagram and model when
	 * clicking the delete action (trashcan)
	 */
	@Test
	public void checkComponentInHostCollocationContextMenuDelete() {
		waveformName = "HC_Context_Menu_Delete";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add host collocation to the waveform
		DiagramTestUtils.addHostCollocationToDiagram(editor);

		// Add component to the host collocation
		editor.setFocus();
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 200);

		// Check pictogram elements
		SWTBotGefEditPart hostCoEditPart = editor.getEditPart(HOST_CO_NAME);
		Assert.assertNotNull(HOST_CO_NAME + " edit part not found", hostCoEditPart);
		ContainerShape hostCollocationContainerShape = (ContainerShape) hostCoEditPart.part().getModel();
		String shapeType = Graphiti.getPeService().getPropertyValue(hostCollocationContainerShape, DUtil.SHAPE_TYPE);
		Assert.assertTrue("Host Collocation property is missing or wrong", shapeType.equals(HostCollocationPattern.HOST_COLLOCATION_OUTER_CONTAINER_SHAPE));

		// Check model object values
		Object bo = DUtil.getBusinessObject(hostCollocationContainerShape);
		Assert.assertTrue("Business object should be instance of HostCollocation", bo instanceof HostCollocation);

		HostCollocation hostCo = (HostCollocation) bo;
		EList<SadComponentPlacement> components = hostCo.getComponentPlacement();
		Assert.assertEquals("Expected component \'" + HARD_LIMIT_1 + "\' was not found", HARD_LIMIT_1,
			components.get(0).getComponentInstantiation().get(0).getId());
		Assert.assertEquals("Expected component \'" + HARD_LIMIT_2 + "\' was not found", HARD_LIMIT_2,
			components.get(1).getComponentInstantiation().get(0).getId());
		Assert.assertNotNull("ComponentFile for " + HARD_LIMIT + " should exist", hostCo.getComponentPlacement().get(1).getComponentFileRef().getFile());

		// delete component
		SWTBotGefEditPart gefEditPart = editor.getEditPart(HARD_LIMIT_1);
		DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);

		// IMPORTANT: We must do this step over because model has now been rebuilt from documents
		hostCo = (HostCollocation) DUtil.getBusinessObject(hostCollocationContainerShape);

		// ensure HardLimit_2 shape still exists
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT_2));
		// ensure HardLimit_1 component business object is deleted
		Assert.assertTrue("Expected there to be only 1 component left after deletion", hostCo.getComponentPlacement().size() == 1);
		Assert.assertNotNull("ComponentFile for " + HARD_LIMIT + " no longer exists", hostCo.getComponentPlacement().get(0).getComponentFileRef().getFile());

	}

	/**
	 * IDE-728
	 * Components selected in the diagram should have the properties of their corresponding
	 * model objects correctly exposed in the default Eclipse properties view.
	 */
	@Test
	public void checkChangesToPropertiesReflectedInSad() {
		waveformName = "IDE-728-Test";

		WaveformUtils.createNewWaveform(gefBot, waveformName, SIG_GEN);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		editor.getEditPart(SIG_GEN_1).click();
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propView.show();
		String propertyname = propView.bot().tree().cell(0, "Property").toString();
		String newValue = "0.0";
		for (SWTBotTreeItem item : propView.bot().tree().getAllItems()) {
			if (item.getText().equals(propertyname)) {
				item.click(1).pressShortcut(Keystrokes.create('0')[0]);
				break;
			}
		}
		editor.getEditPart(SIG_GEN_1).click();
		MenuUtils.save(editor);
		String regex = DiagramTestUtils.regexStringForProperty(propertyname, newValue);
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include HardLimit's changed property", editorText.matches(regex));
	}

	/**
	 * IDE-729
	 * New components should be added to sad.xml when the diagram is saved. All edits to components
	 * (such as changes to the usage name) should also be reflected in the sad.xml on save.
	 */
	@Test
	public void checkComponentsInSad() {
		waveformName = "IDE-729-Test";

		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add a SigGen component instantiation to the diagram and save
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		MenuUtils.save(editor);

		// Add a HardLimit component instantiation to the diagram.  Should be added to the sad.xml, even without a save
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		// Find expected xml string for SigGen and HardLimit components
		final String sigGenSad = DiagramTestUtils.regexStringForComponent((ComponentShape) editor.getEditPart(SIG_GEN_1).part().getModel());
		final String hardLimitSad = DiagramTestUtils.regexStringForComponent((ComponentShape) editor.getEditPart(HARD_LIMIT_1).part().getModel());

		// Check to see if SigGen is included in the sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include SigGen's software assembly", editorText.matches(sigGenSad));
		Assert.assertTrue("The sad.xml should include HardLimit's software assembly", editorText.matches(hardLimitSad));
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// Save project and check to see if HardLimit is now in the sad.xml
		MenuUtils.save(editor);
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include SigGen's software assembly", editorText.matches(sigGenSad));
		Assert.assertTrue("The sad.xml should include HardLimit's software assembly", editorText.matches(hardLimitSad));
	}

	/**
	 * IDE-1131
	 * Name-spaced components should have their component file id set to basename_UUID, not the fully qualified name
	 */
	@Test
	public void checkNameSpacedComponentInSad() {
		waveformName = "NameSpacedComponentTest";
		String componentName = "name.space.comp";
		String componentBaseName = "comp";

		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add namespaced component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, componentName, 0, 0);
		MenuUtils.save(editor);

		// Build expected xml string for component
		final String componentFileString = "(?s).*<componentfile id=\"" + componentBaseName + ".*";
		final String componentXmlString = DiagramTestUtils.regexStringForComponent((ComponentShape) editor.getEditPart(componentName).part().getModel());

		// Check sad.xml for string
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The componentfile should only include the basename_UUID", editorText.matches(componentFileString));
		Assert.assertTrue("The sad.xml should include " + componentName + "'s software assembly", editorText.matches(componentXmlString));
	}

	/**
	 * IDE-741 Palette has all Components in Target SDR.
	 */
	@Test
	public void checkTargetSDRComponentsInPalette() {
		waveformName = "IDE-741-Test";
		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		editor.setFocus();

		List<String> sdrComponents = WaveformComponentTest.getTargetSdrComponents(gefBot);
		for (String item : TARGET_SDR_ITEMS_NOT_IN_PALETTE) { // remove SharedLibrary from list
			sdrComponents.remove(item);
		}
		LinkedList<String> paletteComponents = new LinkedList<String>();
		LinkedList<String> missingSdrComponentSet = new LinkedList<String>();
		for (String compName : sdrComponents) {
			try {
				if (componentIsPresent(editor, compName)) {
					paletteComponents.add(compName);
				}
			} catch (WidgetNotFoundException ex) {
				missingSdrComponentSet.add(compName);
			}
		}

		assertEquals("Missing Target SDR Components from Palette: " + missingSdrComponentSet, 0, missingSdrComponentSet.size());
		assertEquals("Palette contains all Target SDR Components - size", sdrComponents.size(), paletteComponents.size());
		assertEquals("Palette contains all Target SDR Components - contents", sdrComponents, paletteComponents);
	}

	private boolean componentIsPresent(SWTBotGefEditor editor, final String compName) {
		String[] impls = { "", " (cpp)", " (java)", " (python)" };
		for (int i = 0; i < impls.length; i++) {
			try {
				editor.activateTool(compName + impls[i]);
				return true;
			} catch (WidgetNotFoundException e) {
				if (i == impls.length - 1) {
					throw e;
				} else {
					continue;
				}
			}
		}
		return false;
	}

	/**
	 * IDE-766
	 * The delete context menu should not appear when ports are selected
	 */
	@Test
	public void doNotDeletePortsTest() {
		waveformName = "IDE-766-Test";
		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		editor.setFocus();

		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);

		List<SWTBotGefEditPart> anchors = new ArrayList<SWTBotGefEditPart>();
		anchors.add(DiagramTestUtils.getDiagramPortAnchor(provides));
		anchors.add(DiagramTestUtils.getDiagramPortAnchor(uses));

		for (SWTBotGefEditPart anchor : anchors) {
			try {
				anchor.select();
				editor.clickContextMenu("Delete");
				Assert.fail();
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(e.getMessage(), "Delete", e.getMessage());
			}
		}
	}

	static List<String> getTargetSdrComponents(final SWTWorkbenchBot bot) {
		LinkedList<String> list = new LinkedList<String>();

		SWTBotView scaExplorerView = bot.viewByTitle("REDHAWK Explorer");
		SWTBotTree scaTree = scaExplorerView.bot().tree();
		SWTBotTreeItem componentsItem = scaTree.expandNode("Target SDR", "Components");
		SWTBotTreeItem[] sdrComponents = componentsItem.getItems();

		for (final SWTBotTreeItem item : sdrComponents) {

			// Don't include name-spaced components in this list,
			// we test those elsewhere, specifically in the DiagramPaletteFilterTest
			RunnableWithResult<Boolean> softPkgAssert = new RunnableWithResult<Boolean>() {

				private boolean isSoftPkg;

				@Override
				public void run() {
					if (item.widget.getData() instanceof ComponentsSubContainer) {
						isSoftPkg = false;
					} else {
						isSoftPkg = true;
					}
				}

				@Override
				public Boolean getResult() {
					return isSoftPkg;
				}

				@Override
				public void setStatus(IStatus status) {
				}

				@Override
				public IStatus getStatus() {
					return null;
				}

			};

			Display.getDefault().syncExec(softPkgAssert);

			if (softPkgAssert.getResult()) {
				final String compName = item.getText();
				list.add(compName);
			}
		}

		return list;
	}

	/**
	 * IDE-978, IDE-965
	 * Add an external port to the diagram via the sad.xml
	 */
	@Test
	public void addRemoveExternalPortsViaOverviewTest() {
		waveformName = "AddRemove_ExternalPort_Overview";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);
		MenuUtils.save(editor);

		// add port via Overview tab
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		bot.button("Add").click();
		SWTBotShell addExternalPortShell = bot.shell("Add external Port");
		final SWTBot wizardBot = addExternalPortShell.bot();
		addExternalPortShell.activate();
		wizardBot.table(1).select(1);
		wizardBot.button("Finish").click();
		Assert.assertEquals("External ports not added", 1, bot.table(0).rowCount());

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		// assert port set to external in diagram
		SWTBotGefEditPart hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, true);

		// remove port via Overview tab
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		bot.table(0).select(0);
		bot.button("Remove").click();
		Assert.assertEquals("External ports not removed", 0, bot.table(0).rowCount());

		// Confirm that no external ports exist in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, false);

	}

	/**
	 * IDE-965
	 * Make sure correct ports get marked and unmarked as external
	 */
	@Test
	public void addRemoveExternalPortsInDiagram() {
		waveformName = "AddRemove_ExternalPort_Diagram";
		final String HARDLIMIT = "rh.HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 200);
		MenuUtils.save(editor);

		// Make sure all 4 port anchors can be found
		SWTBotGefEditPart hardLimit1UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_1);
		Assert.assertNotNull(hardLimit1UsesEditPart);
		SWTBotGefEditPart hardLimit2UsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT_2);
		Assert.assertNotNull(hardLimit2UsesEditPart);
		SWTBotGefEditPart hardLimit1ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		Assert.assertNotNull(hardLimit1ProvidesEditPart);
		SWTBotGefEditPart hardLimit2ProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_2);
		Assert.assertNotNull(hardLimit2ProvidesEditPart);

		SWTBotGefEditPart hardLimit1UsesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit1UsesEditPart);
		Assert.assertNotNull(hardLimit1UsesAnchor);
		SWTBotGefEditPart hardLimit2UsesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit2UsesEditPart);
		Assert.assertNotNull(hardLimit2UsesAnchor);
		SWTBotGefEditPart hardLimit1ProvidesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit1ProvidesEditPart);
		Assert.assertNotNull(hardLimit1ProvidesAnchor);
		SWTBotGefEditPart hardLimit2ProvidesAnchor = DiagramTestUtils.getDiagramPortAnchor(hardLimit2ProvidesEditPart);
		Assert.assertNotNull(hardLimit2ProvidesAnchor);

		// make sure all ports start as non-external
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, false);

		// make sure correct ports are marked and unmarked
		hardLimit1UsesAnchor.select();
		editor.clickContextMenu("Mark External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, false);

		hardLimit2UsesAnchor.select();
		editor.clickContextMenu("Mark External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, false);

		hardLimit1ProvidesAnchor.select();
		editor.clickContextMenu("Mark External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, false);

		hardLimit2ProvidesAnchor.select();
		editor.clickContextMenu("Mark External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, true);

		hardLimit1UsesAnchor.select();
		editor.clickContextMenu("Mark Non-External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, true);

		hardLimit2UsesAnchor.select();
		editor.clickContextMenu("Mark Non-External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, true);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, true);

		hardLimit1ProvidesAnchor.select();
		editor.clickContextMenu("Mark Non-External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, true);

		hardLimit2ProvidesAnchor.select();
		editor.clickContextMenu("Mark Non-External Port");
		DiagramTestUtils.assertExternalPort(hardLimit1UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2UsesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit1ProvidesEditPart, false);
		DiagramTestUtils.assertExternalPort(hardLimit2ProvidesEditPart, false);

	}

	/**
	 * Private helper method for {@link #checkComponentPictogramElements()} and
	 * {@link #checkComponentPictogramElementsWithAssemblyController()}.
	 * Asserts the given SWTBotGefEditPart is a HardLimit component and is/isn't assembly controller
	 * @param gefEditPart
	 */
	private static void assertHardLimit(SWTBotGefEditPart gefEditPart, boolean isAssemblyController) {
		Assert.assertNotNull("gefEditPart is not null for HardLimit", gefEditPart);
		// Drill down to graphiti component shape
		ComponentShape componentShape = (ComponentShape) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue("business object should be of type SadComponentInstantiation", bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", HARD_LIMIT, ComponentUtils.getOuterText(componentShape).getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), ComponentUtils.getInnerText(componentShape).getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", ComponentUtils.getLollipop(componentShape));
		Assert.assertNotNull("start order shape/text should not be null", ComponentUtils.getStartOrderText(componentShape));
		String acMessage = "should" + ((isAssemblyController) ? "" : " not") + " be assembly controller";
		Assert.assertTrue(acMessage, isAssemblyController == ComponentUtils.isAssemblyController(componentShape));

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);

		// Both ports are of type dataFloat
		Assert.assertEquals("dataFloat", componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName());
		Assert.assertEquals("dataFloat", componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName());
	}

}
