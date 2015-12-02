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
package gov.redhawk.ide.graphiti.dcd.ui.tests.xml;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

import gov.redhawk.ide.graphiti.ui.tests.xml.AbstractXmlEditorTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class XmlEditorTest extends AbstractXmlEditorTest {

	private static final String[] NODE_PARENT_PATH = new String[] { "Target SDR", "Nodes" };
	private static final String NODE_NAME = "DevMgr_with_bulkio";

	protected SWTBotEditor openEditorFromSdrRoot() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, NODE_PARENT_PATH, NODE_NAME, DiagramType.GRAPHITI_NODE_EDITOR);
		SWTBotEditor editor = bot.editorByTitle(NODE_NAME);
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		return editor;
	}
}
