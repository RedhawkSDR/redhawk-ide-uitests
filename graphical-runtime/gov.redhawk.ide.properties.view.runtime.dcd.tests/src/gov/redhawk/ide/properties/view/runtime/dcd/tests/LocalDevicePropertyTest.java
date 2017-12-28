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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

	protected static final String DEVICE_NAME_2 = "PropertyFilteringDev";
	protected static final String DEVICE_INST_2 = DEVICE_NAME_2 + "_1";

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.shutdown(bot, new String[] { "Sandbox" }, "Device Manager");
		super.afterTest();
	}

	private void launch(String deviceName, String deviceInstance) {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, deviceName, "python");
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, deviceInstance);
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, deviceInstance);
		treeItem.select();
	}

	@Override
	protected void prepareObject() {
		launch(DEVICE_NAME, DEVICE_NAME_NUM);
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

	@Override
	protected void setupPropertyFiltering() {
		launch(DEVICE_NAME_2, DEVICE_INST_2);
	}

	@Override
	protected Set<String> getNonFilteredPropertyIDs() {
		Set<String> nonFilteredIDs = new HashSet<>();
		Collections.addAll(nonFilteredIDs, //
			"prop_ro", "prop_rw", "prop_wo", //
			"alloc_ro", "alloc_rw", //
			"exec_ro", "exec_rw", //
			"config_ro", "config_rw", "config_wo", //
			"commandline_ro", "commandline_rw", "commandline_wo");
		return nonFilteredIDs;
	}
}
