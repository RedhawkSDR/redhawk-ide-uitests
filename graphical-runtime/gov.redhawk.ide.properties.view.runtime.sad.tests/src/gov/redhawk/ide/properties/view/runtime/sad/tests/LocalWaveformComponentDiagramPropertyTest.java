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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Assert;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;

/**
 * Tests properties of a component contained within a locally launched waveform selected in the Chalkboard Diagram
 */
public class LocalWaveformComponentDiagramPropertyTest extends LocalComponentDiagramPropertyTest {

	public static final String WAVEFORM_NAME = "AllPropertyTypesWaveform";
	private String waveformFullName;

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, waveformFullName);
	}

	@Override
	protected void prepareObject() {
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, WAVEFORM_NAME);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, new String[] { "Sandbox" }, WAVEFORM_NAME);
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { "Sandbox" }, WAVEFORM_NAME, DiagramType.GRAPHITI_CHALKBOARD);
		waveformFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, new String[] { "Sandbox" }, WAVEFORM_NAME);
		Assert.assertNotNull("Waveform full name did not populate", waveformFullName);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformFullName);
		
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, COMP_NAME + "_1");
		DiagramTestUtils.waitForComponentState(bot, editor, COMP_NAME + "_1", ComponentState.STOPPED);

		ConsoleUtils.disableAutoShowConsole();
		
		editor.click(COMP_NAME);
	}

	@Override
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		EList<LocalScaWaveform> localWaveforms = localSca.getWaveforms();
		for (LocalScaWaveform waveform : localWaveforms) {
			if (waveformFullName.equals(waveform.getName())) {
				for (ScaComponent c : waveform.getComponents()) {
					if (COMP_NAME.equals(c.getProfileObj().getName())) {
						return c.getProperties();
					}
				}
			}
		}
		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}
