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
package gov.redhawk.ide.graphiti.ui.runtime.tests;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public abstract class AbstractPropertiesViewTargetSdrTest extends UIRuntimeTest {

	public static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet";
	private static final String PROP_TAB_NAME = "Properties";

	protected RHSWTGefBot gefBot; // SUPPRESS CHECKSTYLE shared variable

	protected abstract void selectResource();

	@Before
	public void beforeTest() {
		gefBot = new RHSWTGefBot();
	}

	/**
	 * IDE-1326 Advanced tab shown in properties view for waveforms / nodes in the target SDR
	 */
	@Test
	public void checkInnerTabs() {
		selectResource();

		ViewUtils.selectPropertiesTab(bot, PROP_TAB_NAME);
		checkForAdvancedTab();
	}

	private void checkForAdvancedTab() {
		try {
			ViewUtils.selectPropertiesTab(gefBot, "Advanced");
			Assert.fail("Advanced properties tab should not display for design-time diagrams");
		} catch (WidgetNotFoundException e) {
			// PASS
		}
	}
}
