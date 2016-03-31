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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractContextMenuTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DevMgrDomainContextMenuTest extends AbstractContextMenuTest {

	private static final String DEVICE_MANAGER = "DevMgr_with_bulkio";
	protected static final String DEVICE_STUB = "DeviceStub";

	private String domainName = null;

	@Override
	protected RHBotGefEditor launchDiagram() {
		domainName = DevMgrDomainTestUtils.generateDomainName();
		return DevMgrDomainTestUtils.launchDomainAndDevMgr(bot, domainName, DEVICE_MANAGER);
	}

	@After
	public void after() {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;
			DevMgrDomainTestUtils.cleanup(bot, localDomainName);
		}
	}

	@Override
	protected ComponentDescription getTestComponent() {
		return new ComponentDescription(DEVICE_STUB, null, new String[] { "dataFloat_out" });
	}

}
