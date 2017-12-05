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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Assert;

public class MultiOutDisplaySriTest extends AbstractMultiOutPortTest {

	@Override
	protected String getContextMenu() {
		return "Display SRI";
	}

	@Override
	protected void testActionResults(int allocationIndex) {
		waitForConnection(allocationIndex);
		SWTBotView sriView = bot.viewById("gov.redhawk.bulkio.ui.sridata.view");
		SWTBotTree sriTree = sriView.bot().tree();

		Assert.assertEquals("dataShort_out SRI ", sriView.getReference().getTitle());

		// Check that the streamID equals the tuners allocation ID
		Assert.assertEquals("Incorrect stream id detected", getAllocationId(allocationIndex), sriTree.getTreeItem("streamID: ").cell(1));

		// Picked a random prop to for an additional check that the view populates with data
		Assert.assertEquals("SRI View is not streaming data", "false", sriTree.getTreeItem("blocking: ").cell(1));
	}

}
