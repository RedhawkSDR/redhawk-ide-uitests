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
package gov.redhawk.ide.properties.view.runtime.dcd.tests;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.properties.view.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;

/**
 * Tests properties of a locally launched device selected in the REDHAWK Explorer View
 */
public class LocalDevicePropertyTest extends AbstractPropertiesViewRuntimeTest {

	protected static final String DEVICE_NAME = "AllPropertyTypesDevice";
	protected static final String DEVICE_NAME_NUM = DEVICE_NAME + "_1";

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, DEVICE_NAME_NUM);
		super.afterTest();
	}
	
	@Override
	protected void prepareObject() {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_NAME, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, DEVICE_NAME_NUM);
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, DEVICE_NAME_NUM);
		treeItem.select();
	}

	@Override
	protected List<ScaAbstractProperty< ? >> getModelObjectProperties() {
		LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		List<ScaAbstractProperty< ? >> props = ScaModelCommand.runExclusive(localSca, () -> {
			for (ScaDevice< ? > dev : localSca.getSandboxDeviceManager().getRootDevices()) {
				if (DEVICE_NAME_NUM.equals(dev.getLabel())) {
					return dev.getProperties().stream() //
							.filter(prop -> PropertiesUtil.canConfigureOrQuery(prop.getDefinition())) //
							.collect(Collectors.toList());
				}
			}
			return null;
		});
		return props;
	}
}
