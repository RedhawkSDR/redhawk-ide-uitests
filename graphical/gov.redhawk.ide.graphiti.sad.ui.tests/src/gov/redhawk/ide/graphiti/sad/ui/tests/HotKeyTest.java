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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import gov.redhawk.ide.graphiti.ui.tests.AbstractHotKeyTest;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.WaveformUtils;

public class HotKeyTest extends AbstractHotKeyTest {

	@Override
	protected ComponentDescription getComponent() {
		return new ComponentDescription("rh.SigGen", null, null);
	}

	@Override
	protected String createNewProject(String projectName) {
		WaveformUtils.createNewWaveform(gefBot, projectName, null);
		return projectName;
	}

}
