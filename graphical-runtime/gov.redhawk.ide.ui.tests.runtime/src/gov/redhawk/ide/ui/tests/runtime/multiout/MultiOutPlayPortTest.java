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
package gov.redhawk.ide.ui.tests.runtime.multiout;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.Assert;

public class MultiOutPlayPortTest extends AbstractMultiOutPortTest {

	@Override
	protected String getContextMenu() {
		return "Play Port";
	}

	@Override
	protected void testActionResults(int allocationIndex) {
		waitForConnection(allocationIndex);
		SWTBotView playAudioView = bot.viewById("gov.redhawk.ui.port.playaudio.view");
		Assert.assertEquals("Incorrect audio receiver ID displayed: ", "RX_Digitizer_Sim_1 -> dataShort_out", playAudioView.bot().list().selection()[0]);
	}

}
