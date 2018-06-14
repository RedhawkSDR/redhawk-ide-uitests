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
package gov.redhawk.ide.properties.view.runtime.sad.connections.tests;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportType;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainComponentConnectionPropertyTest extends AbstractComponentConnectionPropertiesTest {

	private static final String DEVICE_MANAGER = "DevMgr_localhost";

	private static final String WAVEFORM1 = "ExampleWaveform06";
	protected static final String RESOURCE1 = "SigGen_1";
	protected static final String USES_PORT1 = "dataFloat_out";
	private static final String CONNECTION1 = "connection_1";

	private static final String WAVEFORM2 = "NegotiatorSimConnection";
	protected static final String RESOURCE2 = "Negotiator_1";
	protected static final String USES_PORT2 = "negotiable_out";
	private static final String CONNECTION2 = "connection_1";

	private String domain = getClass().getSimpleName() + "_" + (int) (1000.0 * Math.random());
	private String waveformInstanceName;

	@After
	public void afterTest() {
		ScaExplorerTestUtils.deleteDomainInstance(bot, domain);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
	}

	@Override
	protected void prepareConnection() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM1);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM1, RESOURCE1, USES_PORT1 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, CONNECTION1);
		treeItem.select();
	}

	@Override
	protected TransportTypeAndProps getConnectionDetails() {
		return new TransportTypeAndProps(TransportType.SHMIPC);
	}

	@Override
	protected void prepareNegotiatorComponentConnection() {
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);
		waveformInstanceName = ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM2);
		String[] parentPath = new String[] { domain, "Waveforms", WAVEFORM2, RESOURCE2, USES_PORT2 };
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, parentPath, CONNECTION2);
		treeItem.select();
	}

	protected String getWaveformInstanceName() {
		return waveformInstanceName;
	}

}
