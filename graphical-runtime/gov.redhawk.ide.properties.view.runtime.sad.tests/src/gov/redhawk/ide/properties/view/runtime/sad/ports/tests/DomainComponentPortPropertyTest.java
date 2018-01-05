/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.properties.view.runtime.sad.ports.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractPortPropertiesTest;
import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainComponentPortPropertyTest extends AbstractPortPropertiesTest {

	protected static final String DEVICE_MANAGER = "DevMgr_localhost";
	protected static final String WAVEFORM = "ExampleWaveform01";
	protected static final String EXAMPLE_PY_COMP_1 = "ExamplePythonComponent_1";
	protected static final String PROVIDES_PORT = "dataDouble";
	protected static final String USES_PORT = "dataFloat";
	private static final PortDescription PROVIDES_DESC = new PortDescription("IDL:BULKIO/dataDouble:1.0", "Example description of a provides/input Port");
	private static final PortDescription USES_DESC = new PortDescription("IDL:BULKIO/dataFloat:1.0", "Example description of a uses/output Port");

	private String domain = getClass().getSimpleName() + "_" + (int) (1000.0 * Math.random());
	private String waveformInstanceName;

	@After
	public void afterTest() {
		ScaExplorerTestUtils.deleteDomainInstance(bot, domain);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM, EXAMPLE_PY_COMP_1 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, PROVIDES_PORT);
		treeItem.select();
		return PROVIDES_DESC;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM, EXAMPLE_PY_COMP_1 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, USES_PORT);
		treeItem.select();
		return USES_DESC;
	}

	protected String getDomain() {
		return domain;
	}

	protected String getWaveformInstanceName() {
		return waveformInstanceName;
	}

}
