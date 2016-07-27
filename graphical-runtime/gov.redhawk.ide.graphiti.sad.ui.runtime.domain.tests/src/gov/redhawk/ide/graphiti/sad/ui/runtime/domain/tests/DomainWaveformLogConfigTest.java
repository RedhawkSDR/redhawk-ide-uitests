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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLogConfigTest;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DomainWaveformLogConfigTest extends AbstractLogConfigTest {

	private static final String DEVICE_MANAGER = "DevMgr_localhost";
	private static final String TEST_WAVEFORM = "SigGenToHardLimitWF";
	private static final String SIGGEN_1 = "SigGen_1";

	private String domainName = null;
	private RHBotGefEditor resourceDiagram = null;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		domainName = DomainWaveformTestUtils.generateDomainName();
		resourceDiagram  = DomainWaveformTestUtils.launchDomainAndWaveform(bot, domainName, TEST_WAVEFORM);
		String[] sigGenParentPath = new String[] { domainName, "Waveforms", TEST_WAVEFORM };
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(gefBot, sigGenParentPath, SIGGEN_1);
	}

	@After
	public void after() throws CoreException {
		resourceDiagram = null;
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;
			DomainWaveformTestUtils.cleanup(bot, localDomainName);
		}
		super.after();
	}

	@Override
	protected SWTBotView showConsole() {
		return ConsoleUtils.showConsole(gefBot, DEVICE_MANAGER);
	}

	@Override
	protected SWTBotGefEditPart openResourceDiagram() {
		resourceDiagram.setFocus();
		return resourceDiagram.getEditPart(SIGGEN_1);
	}

	@Override
	protected SWTBotGefEditor getDiagramEditor() {
		return resourceDiagram;
	}

}
