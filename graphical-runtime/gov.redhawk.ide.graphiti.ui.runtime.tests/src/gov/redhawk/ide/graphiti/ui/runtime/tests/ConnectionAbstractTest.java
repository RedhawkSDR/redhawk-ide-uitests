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
package gov.redhawk.ide.graphiti.ui.runtime.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public abstract class ConnectionAbstractTest extends UITest {

	/**
	 * Must have ports such that Slow_out[0] -> Fast_in[0] is possible, and Fast_out[0] -> Slow_out[0]
	 */
	protected abstract ComponentDescription getSlowComponentDescription();

	private ComponentDescription slowComp = getSlowComponentDescription();

	/**
	 * See above
	 */
	protected abstract ComponentDescription getFastComponentDescription();

	private ComponentDescription fastComp = getFastComponentDescription();

	/**
	 * Ensure connections cannot be made while a resource is starting up
	 */
	@Test
	public void noConnectionsDuringStartup() {
		RHBotGefEditor editor = openDiagram();

		DiagramTestUtils.addFromPaletteToDiagram(editor, slowComp.getFullName(), 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, fastComp.getFullName(), 150, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, slowComp.getShortName(1), ComponentState.LAUNCHING);
		DiagramTestUtils.waitForComponentState(bot, editor, fastComp.getShortName(1), ComponentState.STOPPED);

		// Draw both possible port connections, in both forward and reverse directions
		SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, slowComp.getShortName(1), slowComp.getOutPort(0));
		SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, fastComp.getShortName(1), fastComp.getInPort(0));
		DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, provides, uses);
		uses = DiagramTestUtils.getDiagramUsesPort(editor, fastComp.getShortName(1), fastComp.getOutPort(0));
		provides = DiagramTestUtils.getDiagramProvidesPort(editor, slowComp.getShortName(1), slowComp.getInPort(0));
		DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, provides, uses);

		// There should be no connections
		uses = DiagramTestUtils.getDiagramUsesPort(editor, slowComp.getShortName(1), slowComp.getOutPort(0));
		Assert.assertEquals(0, DiagramTestUtils.getSourceConnectionsFromPort(editor, uses).size());
		uses = DiagramTestUtils.getDiagramUsesPort(editor, fastComp.getShortName(1), fastComp.getOutPort(0));
		Assert.assertEquals(0, DiagramTestUtils.getSourceConnectionsFromPort(editor, uses).size());
	}
	
	@After
	public void after_connections() {
		bot.waitUntil(new WaitForLaunchTermination(true));
	}

	/**
	 * Open the appropriate diagram
	 */
	protected abstract RHBotGefEditor openDiagram();
}
