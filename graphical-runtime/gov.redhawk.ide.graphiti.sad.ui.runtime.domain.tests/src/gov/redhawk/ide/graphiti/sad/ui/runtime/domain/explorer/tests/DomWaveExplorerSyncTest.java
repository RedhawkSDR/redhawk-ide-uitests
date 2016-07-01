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
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformExplorerEditor;
import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractSyncText;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.model.sca.ScaWaveform;

public class DomWaveExplorerSyncTest extends AbstractSyncText {

	private String domainName;

	@Override
	protected RHBotGefEditor launchDiagram() {
		domainName = DomWaveTestUtils.generateDomainName();
		return DomWaveTestUtils.launchDomainAndWaveform(bot, domainName, getWaveformOrNodeName());
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

	@Override
	protected String[] getWaveformOrNodeParent() {
		return new String[] { domainName, "Waveforms" };
	}

	@Override
	protected String getWaveformOrNodeName() {
		return "ExampleWaveform06";
	}

	@Override
	protected ComponentDescription resourceA() {
		return new ComponentDescription("rh.SigGen", null, null);
	}

	@Override
	protected ComponentDescription resourceB() {
		return new ComponentDescription("rh.HardLimit", null, null);
	}

	protected String resourceA_doubleProperty() {
		return "magnitude";
	}

	protected double resourceA_doubleProperty_startingValue() {
		return 100.0;
	}

	protected String propertiesTabName() {
		return "Component Properties";
	}

	@Override
	protected boolean supportsParentResourceStartStop() {
		return true;
	}

	/**
	 * IDE-1120 Check the editor's type and input
	 * IDE-1593 Ensure the right editor opens after launching a domain waveform
	 */
	@Test
	public void checkEditorTypeAndInput() {
		RHBotGefEditor editor = launchDiagram();
		Assert.assertEquals("Editor class should be GraphitiWaveformExplorerEditor", GraphitiWaveformExplorerEditor.class,
			editor.getReference().getPart(false).getClass());
		GraphitiWaveformExplorerEditor editorPart = (GraphitiWaveformExplorerEditor) editor.getReference().getPart(false);
		Assert.assertTrue("Chalkboard editors in a domain should have ScaWaveform as their input",
			ScaWaveform.class.isAssignableFrom(editorPart.getWaveform().getClass()));
		Assert.assertFalse("Chalkboard editors in a domain should NOT have LocalScaWaveform as their input",
			LocalScaWaveform.class.isAssignableFrom(editorPart.getWaveform().getClass()));
	}

}
