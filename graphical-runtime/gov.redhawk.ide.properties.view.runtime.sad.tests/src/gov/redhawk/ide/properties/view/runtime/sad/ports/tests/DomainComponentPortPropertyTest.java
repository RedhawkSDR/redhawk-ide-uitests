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

import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainComponentPortPropertyTest extends AbstractComponentPortPropertiesTest {

	protected static final String DEVICE_MANAGER = "DevMgr_localhost";
	protected static final String WAVEFORM = "ExampleWaveform01";
	protected static final String EXAMPLE_PY_COMP_1 = "ExamplePythonComponent_1";
	protected static final String PROVIDES_PORT = "dataDouble";
	protected static final String USES_PORT = "dataFloat";
	private static final PortDescription PROVIDES_DESC = new PortDescription("IDL:BULKIO/dataDouble:1.0", "Example description of a provides/input Port");
	private static final PortDescription USES_DESC = new PortDescription("IDL:BULKIO/dataFloat:1.0", "Example description of a uses/output Port");

	protected static final String WAVEFORM6 = "ExampleWaveform06";
	protected static final String HARD_LIMIT_1 = "HardLimit_1";
	protected static final String HARD_LIMIT_PROVIDES_PORT = "dataFloat_in";
	protected static final String HARD_LIMIT_USES_PORT = "dataFloat_out";

	protected static final String WAVEFORM_NEGOTIATORS = "NegotiatorSimConnection";
	protected static final String NEGOTIATOR_1 = "Negotiator_1";
	protected static final String NEGOTIATOR_PROVIDES_PORT = "negotiable_in";
	protected static final String NEGOTIATOR_USES_PORT = "negotiable_out";

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

	@Override
	protected void prepareProvidesPortAdvanced() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM6);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM6, HARD_LIMIT_1 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, HARD_LIMIT_PROVIDES_PORT);
		treeItem.select();
	}

	@Override
	protected void prepareUsesPortAdvanced() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM6);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM6, HARD_LIMIT_1 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, HARD_LIMIT_USES_PORT);
		treeItem.select();
	}

	@Override
	protected void prepareNegotiatorComponentProvides() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM_NEGOTIATORS);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM_NEGOTIATORS, NEGOTIATOR_1 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, NEGOTIATOR_PROVIDES_PORT);
		treeItem.select();
	}

	@Override
	protected void prepareNegotiatorComponentUses() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM_NEGOTIATORS);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM_NEGOTIATORS, NEGOTIATOR_1 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, NEGOTIATOR_USES_PORT);
		treeItem.select();
	}

	protected String getDomain() {
		return domain;
	}

	protected String getWaveformInstanceName() {
		return waveformInstanceName;
	}

}
