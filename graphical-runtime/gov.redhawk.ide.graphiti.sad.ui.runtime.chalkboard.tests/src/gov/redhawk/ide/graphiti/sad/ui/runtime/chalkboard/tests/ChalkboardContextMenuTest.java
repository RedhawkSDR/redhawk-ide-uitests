/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLocalContextMenuTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ChalkboardContextMenuTest extends AbstractLocalContextMenuTest {

	private static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	private static final String[] CHALKBOARD_PATH = { "Sandbox", CHALKBOARD };

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_OUT = "dataFloat_out";

	@Override
	protected ComponentDescription getTestComponent() {
		return new ComponentDescription(SIG_GEN, null, new String[] { SIG_GEN_OUT });
	}

	@Override
	protected ComponentDescription getLocalTestComponent() {
		return getTestComponent();
	}

	@Override
	protected RHBotGefEditor launchDiagram() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(new RHSWTGefBot());

		DiagramTestUtils.addFromPaletteToDiagram(editor, getTestComponent().getFullName(), 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, getTestComponent().getShortName(1));

		return editor;
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.terminate(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		ConsoleUtils.removeTerminatedLaunches(bot);
		bot.closeAllEditors();
		super.after();
	}

	/**
	 * IDE-326 No Assembly controller / start order related context menus for runtime
	 * @see {@link AbstractLocalContextMenuTest#getAbsentContextMenuOptions()}
	 */
	protected List<String> getAbsentContextMenuOptions() {
		List<String> newList = new ArrayList<String>(super.getAbsentContextMenuOptions());
		Collections.addAll(newList, "Set As Assembly Controller", "Move Start Order Earlier", "Move Start Order Later");
		return newList;
	}

	@Override
	protected boolean supportsTailLog() {
		return false;
	}
}
