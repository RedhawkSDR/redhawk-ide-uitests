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

import org.eclipse.emf.common.util.EList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;

/**
 * Tests properties of a domain launched device selected in the REDHAWK Explorer View
 */
public class DomainDevicePropertyTest extends AbstractPropertiesViewRuntimeTest {

	protected static final String DEVICE_MANAGER = "AllPropertyTypes_DevMgr";
	protected static final String DEVICE = "AllPropertyTypesDevice";
	protected static final String DEVICE_NUM = DEVICE + "_1";

	// Pulled this from AbstractGraphitiDomainNodeRuntimeTest -- Should be able to make another abstract test for all
	// domain property tests that is
	// in charge of launch and tearing down the domain
	private String domain = DomainDevicePropertyTest.class.getSimpleName() + "_" + (int) (1000.0 * Math.random());

	private String[] deviceParentPath;

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.deleteDomainInstance(bot, domain);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);

		super.afterTest();
	}

	@Override
	protected void prepareObject() {
		domain = domain + (int) (1000.0 * Math.random());
		deviceParentPath = new String[] { domain, "Device Managers", DEVICE_MANAGER };
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, deviceParentPath, DEVICE_NUM);
		treeItem.select();
	}

	@Override
	protected List<ScaAbstractProperty< ? >> getModelObjectProperties() {
		final ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		List<ScaAbstractProperty< ? >> props = ScaModelCommand.runExclusive(registry, () -> {
			ScaDomainManager dom = registry.findDomain(domain);
			EList<ScaDeviceManager> devMgrs = dom.getDeviceManagers();
			for (ScaDevice< ? > dev : devMgrs.get(0).getRootDevices()) {
				if (DEVICE_NUM.equals(dev.getLabel())) {
					return dev.getProperties().stream() //
							.filter(prop -> PropertiesUtil.canConfigureOrQuery(prop.getDefinition())) //
							.collect(Collectors.toList());
				}
			}
			return null;
		});
		return props;
	}

	protected String getDomain() {
		return domain;
	}
}
