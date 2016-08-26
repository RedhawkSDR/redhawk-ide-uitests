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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.sad.ui.ext.ComponentShape;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class LocalWaveformRuntimeStartOrderTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	/**
	 * IDE-326
	 * Test to make sure the Start Order ellipse doesn't not get drawn in the sandbox
	 */
	@Test
	public void removeStartOrderIconTest() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN_1);
		ComponentShape componentShape = (ComponentShape) sigGenEditPart.part().getModel();
		Assert.assertNull("Start Order ellipse should not be created during runtime", ComponentUtils.getStartOrderEllipseShape(componentShape));
	}
}
