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
package gov.redhawk.ide.properties.view.runtime.sad.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.commands.ScaModelCommand;

/**
 * Tests properties of a locally launched component selected in the REDHAWK Explorer View
 */
public class LocalComponentPropertyTest extends AbstractPropertiesViewRuntimeTest {

	public static final String WAVEFORM = "AllPropertyTypesWaveform";
	protected static final String COMP_NAME = "AllPropertyTypesComponent";
	protected static final String COMP_INST = COMP_NAME + "_1";

	protected static final String WAVEFORM_2 = "PropertyFilteringWaveform";
	protected static final String COMP_NAME_2 = "PropertyFilteringComp";
	protected static final String COMP_INST_2 = COMP_NAME_2 + "_1";

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
		super.afterTest();
	};

	private void launch(String componentName, String componentInstance) {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, componentName, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, componentInstance);
		treeItem.select();
	}

	@Override
	protected void prepareObject() {
		launch(COMP_NAME, COMP_INST);
	}

	@Override
	protected List<ScaAbstractProperty< ? >> getModelObjectProperties() {
		LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		List<ScaAbstractProperty< ? >> props = ScaModelCommand.runExclusive(localSca, () -> {
			for (ScaComponent c : localSca.getSandboxWaveform().getComponents()) {
				if (COMP_NAME.equals(c.getProfileObj().getName())) {
					return new ArrayList<>(c.getProperties());
				}
			}
			return null;
		});
		return props;
	}

	@Override
	protected void setupPropertyFiltering() {
		launch(COMP_NAME_2, COMP_INST_2);
	}

	@Override
	protected Set<String> getNonFilteredPropertyIDs() {
		Set<String> nonFilteredIDs = new HashSet<>();
		Collections.addAll(nonFilteredIDs, //
			"prop_ro", "prop_rw", "prop_wo", //
			"exec_ro", "exec_rw", //
			"config_ro", "config_rw", "config_wo", //
			"commandline_ro", "commandline_rw", "commandline_wo");
		return nonFilteredIDs;
	}
}
