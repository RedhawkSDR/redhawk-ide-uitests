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
package gov.redhawk.ide.graphiti.sad.ui.tests.xml;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

import gov.redhawk.ide.graphiti.ui.tests.xml.AbstractXmlEditorTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class XmlEditorTest extends AbstractXmlEditorTest {

	private static final String[] WAVEFORM_PARENT_PATH = new String[] { "Target SDR", "Waveforms" };
	private static final String WAVEFORM_NAME = "ExampleWaveform06";

	protected SWTBotEditor openEditorFromSdrRoot() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		SWTBotEditor editor = bot.editorByTitle(WAVEFORM_NAME);
		DiagramTestUtils.openTabInEditor(editor, WAVEFORM_NAME + ".sad.xml");
		return editor;
	}
}
