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
import java.util.List;

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

	protected static final String COMP_NAME = "AllPropertyTypesComponent";

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMP_NAME + "_1");
		super.afterTest();
	};

	@Override
	protected void prepareObject() {
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, COMP_NAME, "python");
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMP_NAME + "_1");
		treeItem.select();
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
}
