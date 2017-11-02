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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainWaveformTest extends UIRuntimeTest {

	private static final String DEVICE_MANAGER = "DevMgr_localhost";

	private final String DOMAIN = "SWTBOT_SAD_TEST_" + (int) (1000.0 * Math.random()); // SUPPRESS CHECKSTYLE INLINE
	private final String[] DOMAIN_WAVEFORM_PARENT_PATH = { DOMAIN, "Waveforms" }; // SUPPRESS CHECKSTYLE INLINE

	@Override
	@Before
	public void before() throws Exception {
		super.before();

		ScaExplorerTestUtils.launchDomainViaWizard(bot, DOMAIN, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);
	}

	@Test
	public void mistypedPropWaveform() {
		final String waveformName = "MistypedPropWaveform";

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, waveformName);
		bot.waitUntil(new WaitForEditorCondition(), WaitForEditorCondition.DEFAULT_WAIT_FOR_EDITOR_TIME);
		SWTBotTreeItem waveformItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveformName);

		waveformItem.select();
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();
		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem treeItem = propTable.getTreeItem("lower_limit");
		Assert.assertEquals(treeItem.cell(1), "-1.0");
		treeItem = propTable.getTreeItem("upper_limit");
		Assert.assertEquals(treeItem.cell(1), "1.0");
		treeItem = propTable.getTreeItem("frequencyExt");
		Assert.assertEquals(treeItem.cell(1), "1000.0 Hz");
	}

	/**
	 * IDE-1347 - Test that waveform properties are visible when the waveform diagram is selected
	 */
	@Test
	public void waveformEditorPropertyView() {
		final String waveformName = "MistypedPropWaveform";

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, waveformName);
		bot.waitUntil(new WaitForEditorCondition(), WaitForEditorCondition.DEFAULT_WAIT_FOR_EDITOR_TIME);
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).show();

		final String waveFormFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveformName);
		RHBotGefEditor editor = new RHSWTGefBot().rhGefEditor(waveFormFullName);
		editor.click(0, 0);

		SWTBotTree propTable = ViewUtils.selectPropertiesTab(bot, "Properties");
		SWTBotTreeItem treeItem = propTable.getTreeItem("lower_limit");
		Assert.assertEquals(treeItem.cell(1), "-1.0");
		treeItem = propTable.getTreeItem("upper_limit");
		Assert.assertEquals(treeItem.cell(1), "1.0");
		treeItem = propTable.getTreeItem("frequencyExt");
		Assert.assertEquals(treeItem.cell(1), "1000.0 Hz");
	}
}
