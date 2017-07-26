/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.ui.tests;

import org.eclipse.gef.EditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.core.graphiti.ui.preferences.DiagramPreferenceConstants;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.PortUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

/**
 * IDE-1026 - Expanding / collapsing shapes in the diagrams
 */
public abstract class CollapseShapeAbstractTest extends AbstractGraphitiTest {

	private RHBotGefEditor editor;
	private String projectName = "CollapseShapeAbstractTest";

	/**
	 * There must be two ports such that A[0] -> B[0], A[1] -> B[1] are possible. There shouldn't
	 * be duplicates of any of the ports' IDL types for the same component.
	 */
	protected abstract ComponentDescription getComponentADescription();

	private ComponentDescription compA = getComponentADescription();

	/**
	 * See above
	 */
	protected abstract ComponentDescription getComponentBDescription();

	private ComponentDescription compB = getComponentBDescription();

	/**
	 * A component which has ONLY ONE port in and ONE port out, of same type.
	 */
	protected abstract ComponentDescription getComponentCDescription();

	private ComponentDescription compC = getComponentCDescription();

	protected enum EditorType {
		SAD,
		DCD
	};

	protected abstract EditorType getEditorType();

	@Before
	@After
	public void resetPortDisplayPreferences() {
		GraphitiUIPlugin.getDefault().getPreferenceStore().setValue(DiagramPreferenceConstants.HIDE_DETAILS, false);
	}

	/**
	 * Test expanding/collapsing everything in the diagram
	 */
	@Test
	public void collapseExpand_all() {
		setup(compA, compB);

		// Expanded
		assertExpanded(compA, false, true);
		assertExpanded(compB, true, false);

		// Collapsed
		editor.click(editor.rootEditPart());
		editor.clickContextMenu("Collapse All Shapes");
		assertCollapsed(compA, false, true);
		assertCollapsed(compB, true, false);

		// Expanded
		editor.click(editor.rootEditPart());
		editor.clickContextMenu("Expand All Shapes");
		assertExpanded(compA, false, true);
		assertExpanded(compB, true, false);
	}

	/**
	 * Test expanding/collapsing individual components
	 */
	@Test
	public void collapseExpand_individual() {
		setup(compA, compB);

		// Expanded
		assertExpanded(compA, false, true);
		assertExpanded(compB, true, false);

		// Collapse A
		editor.click(editor.getEditPart(compA.getShortName(1)));
		editor.clickContextMenu("Collapse Shape");
		assertCollapsed(compA, false, true);
		assertExpanded(compB, true, false);

		// Collapse B as well
		editor.click(editor.getEditPart(compB.getShortName(1)));
		editor.clickContextMenu("Collapse Shape");
		assertCollapsed(compA, false, true);
		assertCollapsed(compB, true, false);

		// Expand A
		editor.click(editor.getEditPart(compA.getShortName(1)));
		editor.clickContextMenu("Expand Shape");
		assertExpanded(compA, false, true);
		assertCollapsed(compB, true, false);

		// Expand B as well
		editor.click(editor.getEditPart(compB.getShortName(1)));
		editor.clickContextMenu("Expand Shape");
		assertExpanded(compA, false, true);
		assertExpanded(compB, true, false);
	}

	/**
	 * Test that connections persist through expand/collapse
	 */
	@Test
	public void collapseExpand_connections() {
		setup(compA, compB);

		// Create two separate connections
		for (int i = 0; i < 2; i++) {
			SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, compA.getShortName(1), compA.getOutPort(i));
			SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, compB.getShortName(1), compB.getInPort(i));
			DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);

			SWTBotGefEditPart usesAnchor = DiagramTestUtils.getDiagramPortAnchor(uses);
			SWTBotGefEditPart providesAnchor = DiagramTestUtils.getDiagramPortAnchor(provides);
			Assert.assertEquals(1, usesAnchor.sourceConnections().size());
			Assert.assertEquals(1, providesAnchor.targetConnections().size());
		}

		SWTBotGefEditPart editPartA = editor.getEditPart(compA.getShortName(1));
		SWTBotGefEditPart editPartB = editor.getEditPart(compB.getShortName(1));

		// Attempt all possible states and state transitions
		for (int startState = 0; startState < 4; startState++) {
			for (int endState = 0; endState < 4; endState++) {
				if (startState == endState) {
					continue;
				}
				boolean startA = startState % 2 == 0;
				boolean startB = startState / 2 == 0;
				boolean endA = endState % 2 == 0;
				boolean endB = endState / 2 == 0;

				setExpanded(editPartA, startA);
				setExpanded(editPartB, startB);
				setExpanded(editPartA, endA);
				setExpanded(editPartB, endB);
				assertConnections(compA, 1, endA, compB, 1, endB, 2);
			}
		}
	}

	/**
	 * Make connections when provides side is collapsed
	 */
	@Test
	public void connect_expandedToCollapsed() {
		setup(compA, compB);
		setExpanded(editor.getEditPart(compA.getShortName(1)), true);
		setExpanded(editor.getEditPart(compB.getShortName(1)), false);

		// Create connections & assert
		for (int i = 0; i < 2; i++) {
			SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, compA.getShortName(1), compA.getOutPort(i));
			SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, compB.getShortName(1));
			DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
		}
		assertConnections(compA, 1, true, compB, 1, false, 2);

		// Expand & re-assert
		setExpanded(editor.getEditPart(compA.getShortName(1)), true);
		setExpanded(editor.getEditPart(compB.getShortName(1)), true);
		assertConnections(compA, 1, true, compB, 1, true, 2);
	}

	/**
	 * Make connections when uses side is collapsed
	 */
	@Test
	public void connect_collapsedToExpanded() {
		setup(compA, compB);
		setExpanded(editor.getEditPart(compA.getShortName(1)), false);
		setExpanded(editor.getEditPart(compB.getShortName(1)), true);

		// Create connections & assert
		for (int i = 0; i < 2; i++) {
			SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesSuperPort(editor, compA.getShortName(1));
			SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, compB.getShortName(1), compB.getInPort(i));
			DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
		}
		assertConnections(compA, 1, false, compB, 1, true, 2);

		// Expand & re-assert
		setExpanded(editor.getEditPart(compA.getShortName(1)), true);
		setExpanded(editor.getEditPart(compB.getShortName(1)), true);
		assertConnections(compA, 1, true, compB, 1, true, 2);
	}

	/**
	 * Make connections with both sides collapsed; unambiguous ports
	 */
	@Test
	public void connect_collapsedToCollapsed_noWizard() {
		setup(compC, compC);
		setExpanded(editor.getEditPart(compC.getShortName(1)), false);
		setExpanded(editor.getEditPart(compC.getShortName(2)), false);

		// Create connection
		SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesSuperPort(editor, compC.getShortName(1));
		SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, compC.getShortName(2));
		DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
		assertConnections(compC, 1, false, compC, 2, false, 1);

		// Expand & re-assert
		setExpanded(editor.getEditPart(compC.getShortName(1)), true);
		setExpanded(editor.getEditPart(compC.getShortName(2)), true);
		assertConnections(compC, 1, true, compC, 2, true, 1);
	}

	/**
	 * IDE-1472
	 * Make connections with both sides collapsed; ambiguous ports requiring the wizard
	 */
	@Test
	public void connect_collapsedToCollapsed_wizard() {
		setup(compA, compB);
		setExpanded(editor.getEditPart(compA.getShortName(1)), false);
		setExpanded(editor.getEditPart(compB.getShortName(1)), false);

		// Create connections using wizard
		for (int i = 0; i < 2; i++) {
			SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesSuperPort(editor, compA.getShortName(1));
			SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, compB.getShortName(1));
			DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);

			SWTBotShell shell = bot.shell("Connect");
			Assert.assertFalse(bot.button("Finish").isEnabled());

			SWTBotList sourceGroup = null;
			SWTBotList targetGroup = null;
			if (getEditorType() == EditorType.SAD) {
				sourceGroup = bot.listInGroup(compA.getShortName(1) + " (Source)");
				targetGroup = bot.listInGroup(compB.getShortName(1) + " (Target)");
			} else {
				sourceGroup = bot.listInGroup(projectName + ":" + compA.getShortName(1) + " (Source)");
				targetGroup = bot.listInGroup(projectName + ":" + compB.getShortName(1) + " (Target)");
			}

			sourceGroup.select(compA.getOutPort(i));
			targetGroup.select(compB.getInPort(i));

			bot.button("Finish").click();
			bot.waitUntil(Conditions.shellCloses(shell));
		}
		assertConnections(compA, 1, false, compB, 1, false, 2);

		// Expand & re-assert
		setExpanded(editor.getEditPart(compA.getShortName(1)), true);
		setExpanded(editor.getEditPart(compB.getShortName(1)), true);
		assertConnections(compA, 1, true, compB, 1, true, 2);
	}

	/**
	 * Ensure connections go away when a collapsed component is deleted
	 */
	@Test
	public void deleteCollapsedConnectedComponent() {
		setup(compA, compB);

		for (int side = 0; side < 2; side++) {
			// Create connections
			for (int i = 0; i < 2; i++) {
				SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, compA.getShortName(1), compA.getOutPort(i));
				SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, compB.getShortName(1), compB.getInPort(i));
				DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
			}

			// Collapse, then delete one side
			setExpanded(editor.getEditPart(compA.getShortName(1)), false);
			setExpanded(editor.getEditPart(compB.getShortName(1)), false);
			if (side == 0) {
				DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(compA.getShortName(1)));
			} else {
				DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(compB.getShortName(1)));
			}

			for (SWTBotGefEditPart editPart : editor.editParts(Is.isA(EditPart.class))) {
				Assert.assertEquals(0, editPart.sourceConnections().size());
				Assert.assertEquals(0, editPart.targetConnections().size());
			}

			// Delete the other side
			if (side != 0) {
				DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(compA.getShortName(1)));
			} else {
				DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(compB.getShortName(1)));
			}

			// Re-add
			DiagramTestUtils.addFromPaletteToDiagram(editor, compA.getFullName(), 0, 0);
			DiagramTestUtils.addFromPaletteToDiagram(editor, compB.getFullName(), 150, 0);
		}
	}

	/**
	 * Test the initial state of components based on the preference
	 */
	@Test
	public void addComponent_collapsePreference() {
		// Create new waveform
		createNewDiagram(projectName);
		editor = gefBot.rhGefEditor(projectName);
		DiagramTestUtils.maximizeActiveWindow(gefBot);

		// Check adding with preference = collapsed
		setPortCollapsePreference(true);
		DiagramTestUtils.addFromPaletteToDiagram(editor, compA.getFullName(), 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, compB.getFullName(), 150, 0);
		assertCollapsed(compA, false, true);
		assertCollapsed(compB, true, false);

		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(compA.getShortName(1)));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(compB.getShortName(1)));

		// Check adding with preference = expanded
		setPortCollapsePreference(false);
		DiagramTestUtils.addFromPaletteToDiagram(editor, compA.getFullName(), 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, compB.getFullName(), 150, 0);
		assertExpanded(compA, false, true);
		assertExpanded(compB, true, false);
	}

	private void setup(ComponentDescription description1, ComponentDescription description2) {
		setup(projectName, description1, description2);
	}

	protected void setup(String projectName, ComponentDescription description1, ComponentDescription description2) {
		// Create new diagram with our two components
		createNewDiagram(projectName);
		setEditor(gefBot.rhGefEditor(projectName));
		DiagramTestUtils.maximizeActiveWindow(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(getEditor(), description1.getFullName(), 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(getEditor(), description2.getFullName(), 150, 0);
	}

	public void setEditor(RHBotGefEditor newEditor) {
		editor = newEditor;
	}

	public RHBotGefEditor getEditor() {
		return editor;
	}

	protected abstract void createNewDiagram(String diagramName);

	private void setExpanded(SWTBotGefEditPart editPart, boolean expanded) {
		editor.click(editPart);
		if (expanded) {
			editor.clickContextMenu("Expand Shape");
		} else {
			editor.clickContextMenu("Collapse Shape");
		}
	}

	/**
	 * Check connection count out of the ports
	 */
	private void assertConnections(ComponentDescription descriptionUses, int usesNumber, boolean usesExpanded, ComponentDescription descriptionProvides,
		int providesNumber, boolean providesExpanded, int connectionCount) {
		if (usesExpanded) {
			for (int i = 0; i < connectionCount; i++) {
				SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, descriptionUses.getShortName(usesNumber), descriptionUses.getOutPort(i));
				SWTBotGefEditPart usesAnchor = DiagramTestUtils.getDiagramPortAnchor(uses);
				Assert.assertEquals(1, usesAnchor.sourceConnections().size());
			}
		} else {
			SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesSuperPort(editor, descriptionUses.getShortName(usesNumber));
			SWTBotGefEditPart usesAnchor = DiagramTestUtils.getDiagramPortAnchor(uses);
			Assert.assertEquals(connectionCount, usesAnchor.sourceConnections().size());
		}

		if (providesExpanded) {
			for (int i = 0; i < connectionCount; i++) {
				SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, descriptionProvides.getShortName(providesNumber),
					descriptionProvides.getInPort(i));
				SWTBotGefEditPart providesAnchor = DiagramTestUtils.getDiagramPortAnchor(provides);
				Assert.assertEquals(1, providesAnchor.targetConnections().size());
			}
		} else {
			SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, descriptionProvides.getShortName(providesNumber));
			SWTBotGefEditPart providesAnchor = DiagramTestUtils.getDiagramPortAnchor(provides);
			Assert.assertEquals(connectionCount, providesAnchor.targetConnections().size());
		}
	}

	private void assertExpanded(ComponentDescription description, boolean checkProvides, boolean checkUses) {
		Assert.assertNull(DiagramTestUtils.getDiagramProvidesSuperPort(editor, description.getShortName(1)));
		Assert.assertNull(DiagramTestUtils.getDiagramUsesSuperPort(editor, description.getShortName(1)));
		if (checkProvides) {
			Assert.assertNotNull(DiagramTestUtils.getDiagramProvidesPort(editor, description.getShortName(1), description.getInPort(0)));
		}
		if (checkUses) {
			Assert.assertNotNull(DiagramTestUtils.getDiagramUsesPort(editor, description.getShortName(1), description.getOutPort(0)));
		}
	}

	private void assertCollapsed(ComponentDescription description, boolean checkProvides, boolean checkUses) {
		Assert.assertEquals(0, PortUtils.getProvidesPortContainerBots(editor.getEditPart(description.getShortName(1))).size());
		Assert.assertEquals(0, PortUtils.getUsesPortContainerBots(editor.getEditPart(description.getShortName(1))).size());
		Assert.assertNotNull(DiagramTestUtils.getDiagramProvidesSuperPort(editor, description.getShortName(1)));
		Assert.assertNotNull(DiagramTestUtils.getDiagramUsesSuperPort(editor, description.getShortName(1)));
		if (checkProvides) {
			Assert.assertNull(DiagramTestUtils.getDiagramProvidesPort(editor, description.getShortName(1), description.getInPort(0)));
		}
		if (checkUses) {
			Assert.assertNull(DiagramTestUtils.getDiagramUsesPort(editor, description.getShortName(1), description.getOutPort(0)));
		}
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
		prefBot.button("Apply and Close").click();
	}
}
