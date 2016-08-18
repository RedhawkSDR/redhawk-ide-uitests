/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.After;
import org.junit.Before;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

/**
 * Before: Starts a waveform locally in the sandbox and opens the Graphiti editor for it.
 * After: Releases the waveform if it's still running and ensures it shuts down.
 */
public abstract class AbstractGraphitiLocalWaveformRuntimeTest extends UIRuntimeTest {

	protected static final String[] LOCAL_WAVEFORM_PARENT_PATH = { "Sandbox" };
	protected static final String LOCAL_WAVEFORM = "ExampleWaveform05";
	protected static final String SIGGEN = "rh.SigGen";
	protected static final String SIGGEN_1 = "SigGen_1";

	protected RHSWTGefBot gefBot; // SUPPRESS CHECKSTYLE INLINE
	private String waveFormFullName; //full name of waveform that is launched

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHSWTGefBot();
		
		// Launch Local Waveform From Target SDR
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, LOCAL_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);

		// Open Local Waveform Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		waveFormFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
	}
	
	@After
	public void afterTest() {
		// Release the waveform if it exists
		try {
			ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		} catch (WidgetNotFoundException ex) {
			return;
		}
		ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
	}

	public String getWaveFormFullName() {
		return waveFormFullName;
	}

	public void setWaveFormFullName(String waveFormFullName) {
		this.waveFormFullName = waveFormFullName;
	}

	public String[] getWaveformPath() {
		return new String[] { "Sandbox", getWaveFormFullName() };
	}

}
