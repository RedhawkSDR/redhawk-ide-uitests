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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractContextMenuTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DomainWaveformContextMenuTest extends AbstractContextMenuTest {

	private static final String WAVEFORM_NAME = "ExampleWaveform06";
	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_OUT = "dataFloat_out";

	private String domainName = null;

	@Override
	protected RHBotGefEditor launchDiagram() {
		domainName = DomainWaveformTestUtils.generateDomainName();
		return DomainWaveformTestUtils.launchDomainAndWaveform(bot, domainName, WAVEFORM_NAME);
	}

	@After
	public void after() throws CoreException {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;
			DomainWaveformTestUtils.cleanup(bot, localDomainName);
		}
		super.after();
	}

	@Override
	protected ComponentDescription getTestComponent() {
		return new ComponentDescription(SIG_GEN, null, new String[] { SIG_GEN_OUT });
	}

	/**
	 * IDE-326 No Assembly controller / start order related context menus for runtime
	 * @see {@link AbstractLocalContextMenuTest#getAbsentContextMenuOptions()}
	 */
	protected List<String> getAbsentContextMenuOptions() {
		List<String> newList = new ArrayList<String>(super.getAbsentContextMenuOptions());
		Collections.addAll(newList, "Set As Assembly Controller", "Move Start Order Earlier", "Move Start Order Later");
		return newList;
	}

}
