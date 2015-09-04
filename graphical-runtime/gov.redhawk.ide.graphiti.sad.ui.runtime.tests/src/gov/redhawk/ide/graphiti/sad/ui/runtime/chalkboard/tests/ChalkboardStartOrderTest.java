/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ChalkboardStartOrderTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	/**
	 * IDE-326 Test to make sure the Start Order ellipse doesn't not get drawn in the sandbox
	 */
	@Test
	public void removeStartOrderIconTest() {
		editor = openChalkboardDiagram(gefBot);

		// drag SigGen to diagram and verify loaded in REDHAWK explorer
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);

		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN_1);
		ComponentShapeImpl componentShape = (ComponentShapeImpl) sigGenEditPart.part().getModel();
		Assert.assertNull("Start Order ellipse should not be created during runtime", componentShape.getStartOrderEllipseShape());
	}
}
