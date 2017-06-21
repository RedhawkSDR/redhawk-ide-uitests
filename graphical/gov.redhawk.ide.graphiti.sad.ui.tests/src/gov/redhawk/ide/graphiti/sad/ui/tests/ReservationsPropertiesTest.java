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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.ui.tests.formeditor.AbstractWaveformTabTest;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import mil.jpeojtrs.sca.sad.Reservation;

/**
 * IDE-1971 - Test "Reservations" property section when a HostCollocation is selected in the SAD Diagram tab
 */
public class ReservationsPropertiesTest extends AbstractWaveformTabTest {

	private static final String PROJECT_NAME = "ReservationTestWF";

	@Override
	protected String getProjectName() {
		return PROJECT_NAME;
	}

	@Override
	protected String getTabName() {
		return DiagramTestUtils.DIAGRAM_TAB;
	}

	// Test editing host collocation reservations via the reservation property section
	@Test
	public void reservationSectionTest() {
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		gefBot.rhGefEditor(PROJECT_NAME).getEditPart("collocation_1").select();
		ViewUtils.selectPropertiesTab(bot, "Reservations");
		SWTBotTree reservationsTree = propView.bot().tree(0);
		SWTBotButton addButton = propView.bot().buttonWithTooltip("Add Reservation");
		SWTBotButton removeButton = propView.bot().buttonWithTooltip("Remove Reservation");

		// Check initial state
		Assert.assertEquals("View should not display any 'reservation' elements", 0, reservationsTree.getAllItems().length);
		Assert.assertEquals("Component should not have any 'reservation' elements", 0, getReservationList().size());

		// Add two reservation elements via view buttons and check tree & model for changes
		addButton.click();
		addButton.click();
		Assert.assertEquals("'Reservation' elements not correctly added to view", 2, reservationsTree.getAllItems().length);
		Assert.assertEquals("'Reservation' elements not correctly added to model", 2, getReservationList().size());

		// Edit first reservation element and make check tree & model for changes
		final String newKind = "NewKind";
		final String newValue = "NewValue";
		SWTBotTreeItem treeItem = reservationsTree.getAllItems()[0];
		StandardTestActions.writeToComboCell(bot, treeItem, 0, newKind);
		treeItem = reservationsTree.getAllItems()[0];
		StandardTestActions.writeToCell(bot, treeItem, 1, newValue);
		treeItem = reservationsTree.getAllItems()[0];

		Assert.assertEquals("Reservation Kind did not update", newKind, treeItem.cell(0));
		Assert.assertEquals("Reservation Value did not update", newValue, treeItem.cell(1));

		Reservation reservation = getReservationList().get(0);
		Assert.assertEquals("Model Kind did not update", newKind, reservation.getKind());
		Assert.assertEquals("Model value did not update", newValue, reservation.getValue());

		// Remove both requires elements via view buttons and check tree & model for changes
		removeButton.click();
		Assert.assertEquals("'Reservation' element not correctly removed from view", 1, reservationsTree.getAllItems().length);
		Assert.assertEquals("'Reservation' element not correctly removed from model", 1, getReservationList().size());
		removeButton.click();
		Assert.assertEquals("View should not display any 'requires' elements", 0, reservationsTree.getAllItems().length);
		Assert.assertEquals("'Reservation' element not correctly removed from model", 0, getReservationList().size());
	}

	// Test viewing reservation elements in an existing project
	@Test
	public void checkReservation() {
		editor.close();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Target SDR", "Waveforms" }, PROJECT_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		RHBotGefEditor editor = gefBot.rhGefEditor(PROJECT_NAME);
		editor.getEditPart("collocation_1").select();

		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		ViewUtils.selectPropertiesTab(bot, "Reservations");
		SWTBotTree reservationsTree = propView.bot().tree(0);
		Assert.assertEquals("'Reservation' elements not correctly shown to view", 2, reservationsTree.getAllItems().length);
		for (int i = 0; i < reservationsTree.getAllItems().length; i++) {
			SWTBotTreeItem item = reservationsTree.getAllItems()[i];
			Assert.assertEquals("Reservation ID is incorrect", "Kind_" + (i + 1), item.cell(0));
			Assert.assertEquals("Reservation value is incorrect", "Value_" + (i + 1), item.cell(1));
		}
	}

	private List<Reservation> getReservationList() {
		return sad.getPartitioning().getHostCollocation().get(0).getReservation();
	}

}
