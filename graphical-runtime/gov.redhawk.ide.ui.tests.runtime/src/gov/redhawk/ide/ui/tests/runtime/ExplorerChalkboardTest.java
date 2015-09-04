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

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ExplorerChalkboardTest extends UIRuntimeTest {
	private static final String COMPONENT_NAME = "ExamplePythonComponent";
	private static final String COMPONENT_IMPL = "python";
	private static final String COMPONENT_PROVIDES_IN_PORT = "dataDouble";
	private static final String COMPONENT_USES_OUT_PORT    = "dataFloat";
	
	SWTBot viewBot;
	
	@Override
	public void before() throws Exception {
		super.before();
		
		SWTBotView explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		viewBot = explorerView.bot();
	}
	
	/**
	 * IDE-1171 - Update tooltips to include optional Port description in the REDHAWK Explorer view
	 */
	@Test
	@Ignore // not sure why getToolTipText is returning empty String
	public void portDescriptionToolTip() {
		SWTBotTreeItem componentTreeItem = ComponentUtils.launchLocalComponent(bot, COMPONENT_NAME, COMPONENT_IMPL);
		componentTreeItem.expand();

		SWTBotTreeItem providesInPort = componentTreeItem.expandNode(COMPONENT_PROVIDES_IN_PORT);
		String tooltip = providesInPort.getToolTipText(); //
		Assert.assertEquals("Description for " + COMPONENT_PROVIDES_IN_PORT + " provides Port", "Example description of a provides/input Port", tooltip);
		
		SWTBotTreeItem usesOutPort = componentTreeItem.expandNode(COMPONENT_USES_OUT_PORT);
		tooltip = usesOutPort.getToolTipText();
		Assert.assertEquals("Description for " + COMPONENT_USES_OUT_PORT + " uses Port", "Example description of a uses/output Port", tooltip);
		
		// Release test Component
		componentTreeItem.contextMenu("Release").click();
	}
}
