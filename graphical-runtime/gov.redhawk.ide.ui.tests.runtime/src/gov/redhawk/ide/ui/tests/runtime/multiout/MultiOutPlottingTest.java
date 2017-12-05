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
import org.junit.Test;

public class MultiOutPlottingTest extends AbstractMultiOutPortTest {

	@Override
	protected String getContextMenu() {
		return "Plot Port Data";
	}

	@Override
	protected void testActionResults() {
		waitForConnection(0);
		SWTBotView plotView = bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
		Assert.assertEquals("dataShort_out", plotView.getReference().getTitle());
	}
	
	@Test
	public void advancedPlotWizardTest() {
		// Need to test selecting a supplied connection ID, trying to select an IN-USE ID, and inputing an ID manually (pick one that will receive data)
		Assert.fail();
	}

}
