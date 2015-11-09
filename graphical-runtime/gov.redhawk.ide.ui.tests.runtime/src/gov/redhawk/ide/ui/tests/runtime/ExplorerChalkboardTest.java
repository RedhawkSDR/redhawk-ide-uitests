/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.finder.widgets.RHBotTreeItem;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ExplorerChalkboardTest extends UIRuntimeTest {
	private static final String COMPONENT_NAME = "ExamplePythonComponent";
	private static final String COMPONENT_NAME_1 = COMPONENT_NAME + "_1";
	private static final String COMPONENT_IMPL = "python";
	private static final String COMPONENT_PROVIDES_IN_PORT = "dataDouble";
	private static final String COMPONENT_USES_OUT_PORT = "dataFloat";

	/**
	 * IDE-1171 - Update tooltips to include optional Port description in the REDHAWK Explorer view
	 */
	@Test
	public void portDescriptionToolTip() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, COMPONENT_NAME, COMPONENT_IMPL);
		SWTBotTreeItem componentTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" },
			COMPONENT_NAME_1);

		SWTBotTreeItem providesInPort = new RHBotTreeItem(componentTreeItem.expandNode(COMPONENT_PROVIDES_IN_PORT));
		String tooltip = providesInPort.getToolTipText();
		Assert.assertNotNull(tooltip);
		Assert.assertTrue("Description for " + COMPONENT_PROVIDES_IN_PORT + " provides Port", tooltip.contains("Example description of a provides/input Port"));

		SWTBotTreeItem usesOutPort = new RHBotTreeItem(componentTreeItem.expandNode(COMPONENT_USES_OUT_PORT));
		tooltip = usesOutPort.getToolTipText();
		Assert.assertNotNull(tooltip);
		Assert.assertTrue("Description for " + COMPONENT_USES_OUT_PORT + " uses Port", tooltip.contains("Example description of a uses/output Port"));

		// Release test Component
		componentTreeItem.contextMenu("Release").click();
	}
}
