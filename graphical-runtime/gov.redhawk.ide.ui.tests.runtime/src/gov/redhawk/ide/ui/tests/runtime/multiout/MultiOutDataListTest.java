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
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;

public class MultiOutDataListTest extends AbstractMultiOutPortTest {

	@Override
	protected String getContextMenu() {
		return "Data List";
	}

	@Override
	protected void testActionResults() {
		SWTBotView dataListView = bot.viewById("gov.redhawk.datalist.ui.views.DataListView");
		SWTBot dataListBot = dataListView.bot();
		
		// Simply check that the table populates with values.  If it does, then a connection was made with the correct connection ID
		dataListBot.buttonWithTooltip("Start Acquire").click();
		
		// Ignore the fact that the device isn't started, it is still pushing data
		bot.waitUntil(Conditions.shellIsActive("Started?"));
		SWTBotShell startedShell = bot.shell("Started?");
		startedShell.bot().button("Yes").click();
		bot.waitUntil(Conditions.shellCloses(startedShell));

		// Allow some time for data collection
		bot.sleep(500);
		SWTBotTable table = dataListBot.table(0);
		Assert.assertTrue("No data was generated, connection ID may not be correct", table.rowCount() > 1);
	}
}
