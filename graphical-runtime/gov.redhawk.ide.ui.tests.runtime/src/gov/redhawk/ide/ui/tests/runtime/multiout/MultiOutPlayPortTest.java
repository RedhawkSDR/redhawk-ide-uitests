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
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;

public class MultiOutPlayPortTest extends AbstractMultiOutPortTest {

	@Override
	protected String getContextMenu() {
		return "Play Port";
	}

	@Override
	protected void testActionResults() {
		waitForConnection(0);

		// TODO: CHECKSTYLE:OFF
		SWTBotView playAudioView = bot.viewById("gov.redhawk.ui.port.playaudio.view");
		bot.waitUntil(new DefaultCondition() {
			
			@Override
			public boolean test() throws Exception {
				return !playAudioView.bot().textWithLabel("Encoding:").getText().equals("");
			}
			
			@Override
			public String getFailureMessage() {
				return "View never populated with data";
			}
		});
		Assert.assertEquals("Incorrect audio receiver ID displayed: ", "RX_Digitizer_Sim_1 -> dataShort_out", playAudioView.bot().list().selection()[0]);
		Assert.assertEquals("Incorrect encoding value displayed: ", "PCM_SIGNED", playAudioView.bot().textWithLabel("Encoding:").getText());
		Assert.assertEquals("Incorrect sample rate displayed: ", "2,500,000", playAudioView.bot().textWithLabel("Sample Rate:").getText());
	}

}
