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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import gov.redhawk.ide.graphiti.ui.tests.CollapseShapeAbstractTest;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

public class CollapseShapeTest extends CollapseShapeAbstractTest {

	@Override
	protected ComponentDescription getComponentADescription() {
		return new ComponentDescription("rh.SigGen", new String[0], new String[] { "dataFloat_out", "dataShort_out" });
	}

	@Override
	protected ComponentDescription getComponentBDescription() {
		return new ComponentDescription("rh.DataConverter", new String[] { "dataFloat_in", "dataShort_in" }, new String[0]);
	}

	@Override
	protected ComponentDescription getComponentCDescription() {
		return new ComponentDescription("rh.HardLimit", new String[] { "dataFloat_in" }, new String[] { "dataFloat_out" });
	}

	protected void setup(String projectName, ComponentDescription description1, ComponentDescription description2) {
		// Create new diagram with our two components
		createNewDiagram(projectName);
		setEditor(gefBot.rhGefEditor(projectName));
		DiagramTestUtils.maximizeActiveWindow(gefBot);
		DiagramTestUtils.addHostCollocationToDiagram(getEditor());
		DiagramTestUtils.addFromPaletteToDiagram(getEditor(), description1.getFullName(), 15, 15);
		DiagramTestUtils.addFromPaletteToDiagram(getEditor(), description2.getFullName(), 300, 0);
	}

	@Override
	protected void createNewDiagram(String diagramName) {
		WaveformUtils.createNewWaveform(bot, diagramName, null);
	}

	@Override
	protected EditorType getEditorType() {
		return EditorType.SAD;
	}
}
