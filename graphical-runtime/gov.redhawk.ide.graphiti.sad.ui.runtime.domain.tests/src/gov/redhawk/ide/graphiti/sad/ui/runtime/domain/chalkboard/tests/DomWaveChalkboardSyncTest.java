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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.chalkboard.tests;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractSyncTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DomWaveChalkboardSyncTest extends AbstractSyncTest {

	private String domainName;

	@Override
	protected RHBotGefEditor launchDiagram() {
		domainName = DomWaveChalkboardTestUtils.generateDomainName();
		return DomWaveChalkboardTestUtils.launchDomainAndWaveform(bot, domainName, getWaveformOrNodeName());
	}

	@After
	public void after() throws CoreException {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;
			DomWaveChalkboardTestUtils.cleanup(bot, localDomainName);
		}
		super.after();
	}

	@Override
	protected String[] getWaveformOrNodeParent() {
		return new String[] { domainName, "Waveforms" };
	}

	@Override
	protected String getWaveformOrNodeName() {
		return "ExampleWaveform06";
	}

	@Override
	protected ComponentDescription resourceA() {
		return new ComponentDescription("rh.SigGen", null, null);
	}

	@Override
	protected ComponentDescription resourceB() {
		return new ComponentDescription("rh.HardLimit", null, null);
	}

	protected String resourceA_doubleProperty() {
		return "magnitude";
	}

	protected double resourceA_doubleProperty_startingValue() {
		return 100.0;
	}

	@Override
	protected boolean supportsParentResourceStartStop() {
		return true;
	}
}
