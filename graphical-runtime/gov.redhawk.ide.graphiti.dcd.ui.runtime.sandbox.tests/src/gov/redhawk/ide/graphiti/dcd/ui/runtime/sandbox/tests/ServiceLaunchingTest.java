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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;

import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.runtime.tests.LocalLaunchingAbstractTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class ServiceLaunchingTest extends LocalLaunchingAbstractTest {

	@Override
	protected ComponentDescription getSlowComponentDescription() {
		return new ComponentDescription("SlowLaunchService", null, null);
	}

	@Override
	protected RHBotGefEditor openDiagram() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Sandbox" }, "Device Manager", DiagramType.GRAPHITI_CHALKBOARD);
		return new RHSWTGefBot().rhGefEditor("Device Manager");
	}

	@Override
	protected void contextMenuChecks(SWTBotGefEditor editor, String resourceShortName) {
		try {
			DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(resourceShortName));
			Assert.fail();
		} catch (WidgetNotFoundException ex) {
			// PASS
		}
	}
}
