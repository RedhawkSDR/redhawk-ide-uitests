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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;

public class LocalWaveformRuntimeConsoleTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	/**
	 * IDE-1054 Making sure Console title is set correctly for component process
	 */
	@Test
	public void checkConsoleTitle() {
		String[] titles = ConsoleUtils.getConsoleTitles(bot);
		for (String title : titles) {
			if (title.contains(SIGGEN_1)) {
				Assert.assertTrue("Console title does not start with component and waveform name",
					title.startsWith(SIGGEN_1 + " [" + getWaveFormFullName() + "] "));
			}
		}

	}

}
