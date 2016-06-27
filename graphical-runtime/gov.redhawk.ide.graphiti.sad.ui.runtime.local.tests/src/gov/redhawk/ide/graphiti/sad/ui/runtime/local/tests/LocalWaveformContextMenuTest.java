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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLocalContextMenuTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class LocalWaveformContextMenuTest extends AbstractLocalContextMenuTest {

	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = { "Sandbox" };
	private static final String LOCAL_WAVEFORM = "ExampleWaveform05";
	private static final String[] SIG_GEN_PARENT_PATH = { "Sandbox", LOCAL_WAVEFORM };

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
		// Launch Local Waveform From Target SDR
		RHSWTGefBot gefBot = new RHSWTGefBot();
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(new RHSWTGefBot(), LOCAL_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM).collapse();
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SIG_GEN_PARENT_PATH, getTestComponent().getShortName(1));

		// Open Local Waveform Diagram
		String waveFormFullName = ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		return gefBot.rhGefEditor(waveFormFullName);
	}

	@After
	public void after() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		ConsoleUtils.removeTerminatedLaunches(bot);
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
