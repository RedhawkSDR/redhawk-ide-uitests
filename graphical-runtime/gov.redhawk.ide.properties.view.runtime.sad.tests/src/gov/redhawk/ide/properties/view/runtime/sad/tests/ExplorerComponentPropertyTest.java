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

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

/**
 * Tests properties of a domain launched component selected in the Explorer Diagram
 */
public class ExplorerComponentPropertyTest extends DomainComponentPropertyTest {

	@Override
	protected void prepareObject() {
		super.prepareObject();
		gefBot.gefEditor(waveformFullName).close();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, domainWaveformParentPath, WAVEFORM, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(waveformFullName);
		editor.click(COMPONENT);
	}
}
