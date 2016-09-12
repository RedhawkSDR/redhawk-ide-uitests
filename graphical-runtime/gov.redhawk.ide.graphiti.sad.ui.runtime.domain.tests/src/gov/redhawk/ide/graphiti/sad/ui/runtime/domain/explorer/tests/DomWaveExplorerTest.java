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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.explorer.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.impl.ScaWaveformImpl;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;

public class DomWaveExplorerTest extends UIRuntimeTest {

	private static final String EDITOR_NAME = "gov.redhawk.core.graphiti.sad.ui.editor.GraphitiWaveformExplorerEditor";
	private static final String WAVEFORM_NAME = "ExampleWaveform06";
	private static final String WAVEFORM_SDR_PATH = "/waveforms/" + WAVEFORM_NAME + "/" + WAVEFORM_NAME + ".sad.xml";

	private String domainName;
	private RHBotGefEditor editor;

	@Before
	public void before() throws Exception {
		super.before();
		domainName = DomWaveTestUtils.generateDomainName();
		editor = DomWaveTestUtils.launchDomainAndWaveform(bot, domainName, WAVEFORM_NAME);
	}

	@After
	public void after() throws CoreException {
		if (domainName != null) {
			String localDomainName = domainName;
			domainName = null;
			DomWaveTestUtils.cleanup(bot, localDomainName);
		}
		super.after();
	}

	/**
	 * Test the most basic functionality / presence of the waveform explorer diagram (on a domain waveform).
	 * IDE-1120 Check the type of editor that opens as well as its input
	 * IDE-1593 Ensure the right editor opens after launching a domain waveform
	 * IDE-1668 Correct diagram titles and tooltips
	 */
	@Test
	public void domainWaveformExplorerTest() {
		// IDE-1120, IDE-1593
		IEditorPart editorPart = editor.getReference().getEditor(false);
		Assert.assertEquals("Waveform explorer editor class is incorrect", EDITOR_NAME, editorPart.getClass().getName());
		IEditorInput editorInput = editorPart.getEditorInput();
		Assert.assertEquals("Waveform explorer editor's input object is incorrect", ScaFileStoreEditorInput.class, editorInput.getClass());
		Assert.assertEquals("Waveform explorer editor's input SCA object is incorrect", ScaWaveformImpl.class, ((ScaFileStoreEditorInput) editorInput).getScaObject().getClass());

		// IDE-1668
		Assert.assertEquals("Incorrect title", ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, new String[] { domainName, "Waveforms" }, WAVEFORM_NAME), editorPart.getTitle());
		Assert.assertEquals("Incorrect tooltip", domainName + " - " + WAVEFORM_SDR_PATH, editorPart.getTitleToolTip());
	}

}
