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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;

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
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard", COMP_NAME + "_1");
		
		ConsoleUtils.disableAutoShowConsole(gefBot);
		
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, COMP_NAME + "_1");
		treeItem.select();
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Properties";
	}

	@Override
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		for (ScaComponent c : localSca.getSandboxWaveform().getComponents()) {
			if (COMP_NAME.equals(c.getProfileObj().getName())) {
				return c.getProperties();
			}
		}
		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}
