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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class LargeWaveformTest extends UITest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String DATA_CONVERTER = "rh.DataConverter";
	private static final String SIG_GEN_PREFIX = "SigGen_";
	private static final String HARD_LIMIT_PREFIX = "HardLimit_";
	private static final String DATA_CONVERTER_PREFIX = "DataConverter_";
	private static final long TIMEOUT = 30000;

	private SWTBotGefEditor editor;

	@Before
	public void before() throws Exception {
		super.before();

		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard", DiagramType.GRAPHITI_CHALKBOARD);
		editor = new SWTGefBot().gefEditor("Chalkboard");

		for (int i = 1; i <= 4; i++) {
			ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, "cpp");
			ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "cpp");
			ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, DATA_CONVERTER, "cpp");

			DiagramTestUtils.waitForComponentState(bot, editor, SIG_GEN_PREFIX + i, ComponentState.STOPPED, TIMEOUT);
			DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT_PREFIX + i, ComponentState.STOPPED, TIMEOUT);
			DiagramTestUtils.waitForComponentState(bot, editor, DATA_CONVERTER_PREFIX + i, ComponentState.STOPPED, TIMEOUT);
		}
	}

	@After
	public void after() throws Exception {
		waitUntilEditorEmpty();
		waitUntilChalkboardTreeEmpty();
		bot.waitUntil(new WaitForLaunchTermination());
		super.after();
	}

	/**
	 * Perform a release on the chalkboard with lots of components
	 */
	@Test
	public void releaseChalkboard() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
	}

	/**
	 * Perform a terminate on the chalkboard with lots of components
	 */
	@Test
	public void terminateChalkboard() {
		ScaExplorerTestUtils.terminateFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
	}

	private void waitUntilEditorEmpty() {
		editor.bot().waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return editor.rootEditPart().children().get(0).children().size() == 0;
			}

			@Override
			public String getFailureMessage() {
				return "Component(s) failed to disappear from the diagram";
			}
		});
	}

	private void waitUntilChalkboardTreeEmpty() {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(new SWTWorkbenchBot(), new String[] { "Sandbox" }, "Chalkboard");
				return treeItem.getNodes().size() == 0;
			}

			@Override
			public String getFailureMessage() {
				return "Component(s) failed to disappear from the diagram";
			}
		});
	}
}
