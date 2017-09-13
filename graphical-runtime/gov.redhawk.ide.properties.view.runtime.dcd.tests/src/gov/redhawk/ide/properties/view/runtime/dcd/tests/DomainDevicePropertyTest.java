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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.sca.ScaPlugin;

/**
 * Tests properties of a domain launched device selected in the REDHAWK Explorer View
 */
public class DomainDevicePropertyTest extends AbstractPropertiesViewRuntimeTest {
	// Pulled this from AbstractGraphitiDomainNodeRuntimeTest -- Should be able to make another abstract test for all
	// domain property tests that is
	// in charge of launch and tearing down the domain
	protected String domain = DomainDevicePropertyTest.class.getSimpleName() + "_" + (int) (1000.0 * Math.random());  // SUPPRESS CHECKSTYLE INLINE - package field
	protected static final String DEVICE_MANAGER = "AllPropertyTypes_DevMgr";
	protected static final String DEVICE = "AllPropertyTypesDevice";
	protected static final String DEVICE_NUM = DEVICE + "_1";
	
	protected String[] deviceParentPath; // SUPPRESS CHECKSTYLE INLINE - package field

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
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(Display.getCurrent());
		ScaDomainManager dom = registry.findDomain(domain);
		EList<ScaDeviceManager> devMgrs = dom.getDeviceManagers();
		for (ScaDevice< ? > dev : devMgrs.get(0).getRootDevices()) {
			if (DEVICE.equals(dev.getProfileObj().getName())) {
				return dev.getProperties();
			}
		}
		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}
