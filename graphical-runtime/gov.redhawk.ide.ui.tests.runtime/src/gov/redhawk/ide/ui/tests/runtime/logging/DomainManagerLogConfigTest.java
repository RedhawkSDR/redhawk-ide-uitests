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
package gov.redhawk.ide.ui.tests.runtime.logging;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Ignore;

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * IDE-1978 Logging tests against a domain manager.
 */
public class DomainManagerLogConfigTest extends AbstractLogConfigTest {

	private String domainName = null;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		domainName = "SWTBOT_TEST_" + (int) (1000.0 * Math.random());

		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		return ScaExplorerTestUtils.getDomain(bot, domainName);
	}

	@Override
	protected String getLoggingResourceName() {
		return domainName;
	}

	@Override
	protected boolean canTailLog() {
		return true;
	}

	@After
	public void after() {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;

			ScaExplorerTestUtils.deleteDomainInstance(bot, localDomainName);
			NodeBooterLauncherUtil.getInstance().terminateAll();
			ConsoleUtils.removeTerminatedLaunches(bot);
		}
	}

	@Override
	protected String getConsoleTitle() {
		return "Domain Manager " + domainName;
	}

	/**
	 * Ignore due to CF-1803
	 */
	@Override
	@Ignore
	public void tailLog() {
	}
}
