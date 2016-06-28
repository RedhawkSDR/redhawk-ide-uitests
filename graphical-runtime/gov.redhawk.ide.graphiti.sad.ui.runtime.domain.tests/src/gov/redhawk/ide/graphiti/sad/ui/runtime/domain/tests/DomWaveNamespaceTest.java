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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

public class DomWaveNamespaceTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	protected String getWaveformName() {
		return "namespaceWF";
	}

	/**
	 * IDE-1187 Opens Graphiti diagram using the Waveform Explorer editor.
	 * Diagram should contain namespaced components
	 */
	@Test
	public void waveformExplorerNamespaceComponentsTest() {
		final String comp1 = "comp_1";
		final String comp2 = "comp_2";

		// check for components
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		Assert.assertNotNull(editor.getEditPart(comp1));
		Assert.assertNotNull(editor.getEditPart(comp2));
		SWTBotGefEditPart providesPort = DiagramTestUtils.getDiagramProvidesPort(editor, comp2);
		SWTBotGefEditPart providesAnchor = DiagramTestUtils.getDiagramPortAnchor(providesPort);
		Assert.assertTrue(providesAnchor.targetConnections().size() == 1);
	}
}
