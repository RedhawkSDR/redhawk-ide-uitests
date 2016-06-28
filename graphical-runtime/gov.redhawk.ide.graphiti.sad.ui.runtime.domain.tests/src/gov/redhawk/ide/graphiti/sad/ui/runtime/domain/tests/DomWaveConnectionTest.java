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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.SadConnectInterface;

public class DomWaveConnectionTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	protected String getWaveformName() {
		return "LargeWaveform";
	}

	/**
	 * IDE-1136 & IDE-1524 SAD runtime explorers shouldn't allow connection changes
	 * @throws AWTException
	 */
	@Test
	public void waveformExplorerConnectionTest() throws AWTException {
		final String DATA_CONVERTER_4 = "DataConverter_4";

		// Test that connections cannot be deleted
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		SWTBotGefEditPart sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN_1);
		SWTBotGefConnectionEditPart sigGenConnection = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesEditPart).get(0);
		String connectionId = getConnectionId(sigGenConnection);

		// Have to try and delete with the hot-key since the context menu for "delete" should not exist
		sigGenConnection.select();
		sigGenConnection.click();
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_DELETE);
		robot.keyRelease(KeyEvent.VK_DELETE);

		sigGenConnection = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesEditPart).get(0);
		Assert.assertEquals("Expected connection ID was not found", connectionId, getConnectionId(sigGenConnection));

		// Attempt to add a connection - Assumes the DataConverter_4 has no outgoing connections
		SWTBotGefEditPart dataConverterUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER_4);
		SWTBotGefEditPart hardimitProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT_1);
		Assert.assertEquals("DATA_CONVERTER_4 is not expected to have any source connections", 0,
			DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterUsesEditPart).size());

		DiagramTestUtils.drawConnectionBetweenPorts(editor, dataConverterUsesEditPart, hardimitProvidesEditPart);
		dataConverterUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER_4);
		Assert.assertEquals("DATA_CONVERTER_4 is not expected to have any source connections", 0,
			DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterUsesEditPart).size());
	}

	private String getConnectionId(SWTBotGefConnectionEditPart connection) {
		SadConnectInterface modelObj = (SadConnectInterface) ((FreeFormConnection) connection.part().getModel()).getLink().getBusinessObjects().get(0);
		return modelObj.getId();
	}
}
