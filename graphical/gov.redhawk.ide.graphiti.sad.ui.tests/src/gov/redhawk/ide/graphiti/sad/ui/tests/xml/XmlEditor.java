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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class XmlEditor extends UITest {

	private static final String[] WAVEFORM_PARENT_PATH = new String[] { "Target SDR", "Waveforms" };
	private static final String WAVEFORM_NAME = "ExampleWaveform06";

	/**
	 * Tests that the XML editor, and not a text editor, is used for a SAD editor opened for something in the SDRROOT,
	 * rather than the workspace.
	 */
	@Test
	public void xmlEditor() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, WAVEFORM_PARENT_PATH, WAVEFORM_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		SWTBotEditor editor = bot.editorByTitle(WAVEFORM_NAME);
		DiagramTestUtils.openTabInEditor(editor, WAVEFORM_NAME + ".sad.xml");

		final IEditorPart editorPart = bot.activeEditor().getReference().getEditor(false);
		final List<IEditorPart> containedEditors = new ArrayList<IEditorPart>();
		bot.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Collections.addAll(containedEditors, ((MultiPageEditorPart) editorPart).findEditors(editorPart.getEditorInput()));
			}
		});
		Assert.assertEquals("org.eclipse.wst.sse.ui.StructuredTextEditor", containedEditors.get(0).getClass().getName());
	}
}
