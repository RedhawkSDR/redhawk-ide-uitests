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
package gov.redhawk.ide.properties.view.runtime.sad.tests;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.sca.ScaPlugin;

/**
 * Tests properties of a domain launched component selected in the REDHAWK Explorer View
 */
public class DomainComponentPropertyTest extends AbstractPropertiesViewRuntimeTest {
	private String domain = DomainComponentPropertyTest.class.getSimpleName() + "_";
	protected static final String DEVICE_MANAGER = "DevMgr_localhost";
	protected static final String WAVEFORM = "AllPropertyTypesWaveform";

	protected static final String COMPONENT = "AllPropertyTypesComponent";
	protected static final String COMPONENT_NUM = COMPONENT + "_1";

	protected String[] domainWaveformParentPath;
	protected String waveformFullName;
	
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
		domain = DomainComponentPropertyTest.class.getSimpleName() + "_" + (int) (1000.0 * Math.random());
		domainWaveformParentPath = new String[] { domain, "Waveforms" };
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, domainWaveformParentPath, WAVEFORM);
		waveformFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, domainWaveformParentPath, WAVEFORM);

		ArrayList<String> componentParentPath = new ArrayList<>(Arrays.asList(domainWaveformParentPath));
		componentParentPath.add(waveformFullName);
		
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, componentParentPath.toArray(new String[] {}), COMPONENT_NUM);
		treeItem.select();
	}

	@Override
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(Display.getCurrent());
		ScaWaveform wave = null;
		for (ScaWaveform waveform : registry.findDomain(domain).getWaveforms()) {
			if (waveformFullName.equals(waveform.getName())) {
				wave = waveform;
				break;
			}
		}

		Assert.assertNotNull("Waveform " + waveformFullName + " could not be found in domain: " + domain, wave);

		for (ScaComponent c : wave.getComponents()) {
			if (COMPONENT_NUM.equals(c.getName())) {
				return c.getProperties();
			}
		}

		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}
