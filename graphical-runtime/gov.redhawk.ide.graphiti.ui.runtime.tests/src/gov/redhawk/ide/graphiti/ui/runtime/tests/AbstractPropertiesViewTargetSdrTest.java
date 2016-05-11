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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList.ListElement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

@SuppressWarnings("restriction")
public abstract class AbstractPropertiesViewTargetSdrTest extends UIRuntimeTest {

	protected RHSWTGefBot gefBot; // SUPPRESS CHECKSTYLE shared variable
	protected String PROP_TAB_NAME; // SUPPRESS CHECKSTYLE shared variable
	public static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	protected abstract void selectResource();

	protected abstract void setPropTabName();
	
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
		setPropTabName();

		ViewUtils.selectPropertiesTab(bot, PROP_TAB_NAME);
		checkForAdvancedTab();
	}

	private void checkForAdvancedTab() {
		final String ADVANCED = "Advanced";

		Matcher<TabbedPropertyList> matcher = new BaseMatcher<TabbedPropertyList>() {
			@Override
			public boolean matches(Object item) {
				if (item instanceof TabbedPropertyList) {
					return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Of type TabbedPropertyList");
			}

		};

		SWTBotView view = bot.viewById(PROPERTIES_VIEW_ID);
		view.show();
		TabbedPropertyList list = (TabbedPropertyList) view.bot().widget(matcher);

		for (int index = 0; index < list.getNumberOfElements(); index++) {
			final TabbedPropertyList.ListElement element = (ListElement) list.getElementAt(index);
			if (ADVANCED.equals(element.getTabItem().getText())) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						Assert.fail(String.format("'%s' inner tab should not display for Resources in Target SDR diagrams", ADVANCED));
					}
				});
			}
		}
	}
}
