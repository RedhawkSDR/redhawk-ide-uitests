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
package gov.redhawk.ide.graphiti.sad.ui.tests.findby;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import mil.jpeojtrs.sca.partitioning.FindByStub;

public class FindByEventChannelTest extends AbstractFindByTest {

	private static final String FIND_BY_TYPE = FindByUtils.FIND_BY_EVENT_CHANNEL;
	private static final String FIND_BY_NAME = "FindByEventChannel";

	@Override
	protected void createFindByConnections() {
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(getEditor(), SIG_GEN_1);
		SWTBotGefEditPart findByLollipopPart = DiagramTestUtils.getComponentSupportedInterface(editor, getFindByName());
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByLollipopPart);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesPart);
		Assert.assertEquals("Connection was not added", 1, sourceConnections.size());
	}

	@Override
	protected void editFindByPorts(String newFindByName) {
		return; // PASS - Event Channels do not have ports
	}

	@Override
	protected void validateFindByPortEdits(RHContainerShape findByShape, FindByStub findByObject) {
		return; // PASS - Event Channels do not have ports
	}

	@Override
	protected String getFindByType() {
		return FIND_BY_TYPE;
	}

	@Override
	protected String getFindByName() {
		return FIND_BY_NAME;
	}

	@Override
	protected String getEditTextLabel() {
		return "Name:";
	}
}
