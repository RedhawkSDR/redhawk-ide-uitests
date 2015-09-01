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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.DiagramPreferenceConstants;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.GraphitiPreferencesNLS;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.PortStatisticsPreferencePage;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;

public class PortStatsPrefPageTest extends AbstractGraphitiTest {

	private String errorMessage;

	/**
	 * IDE-1067
	 * Create a preference page to allow users to edit values relating the
	 * GUI port statistics color-change events
	 */
	@Test
	public void prefPageValidationTest() {
		bot.menu("Window").menu("Preferences").click();
		SWTBotShell prefShell = bot.shell("Preferences");
		SWTBot prefBot = prefShell.bot();
		prefBot.tree().getTreeItem("REDHAWK").expand().getNode("Port Statistics").select();

		SWTBotButton applyButton = prefBot.button("Apply");
		SWTBotButton defaultButton = prefBot.button("Restore Defaults");
		SWTBotButton okButton = prefBot.button("OK");

		Assert.assertNull("No error message should be present", getErrorMessage(prefShell));
		Assert.assertTrue(applyButton.isEnabled());
		Assert.assertTrue(defaultButton.isEnabled());
		Assert.assertTrue(okButton.isEnabled());

		// TEST INVALID INPUT
		SWTBotText queueLevelText = prefBot.textWithLabel(GraphitiPreferencesNLS.PortStatPreference_warningQueueLevel);
		queueLevelText.typeText("NotANumber");
		Assert.assertTrue("Error label not displayed as expected.  Expected: " + GraphitiPreferencesNLS.PortStatPreference_warningQueueLevelError
			+ " but displayed: " + getErrorMessage(prefShell), GraphitiPreferencesNLS.PortStatPreference_warningQueueLevelError.equals(getErrorMessage(prefShell)));
		Assert.assertTrue(!applyButton.isEnabled());
		Assert.assertTrue(defaultButton.isEnabled());
		Assert.assertTrue(!okButton.isEnabled());

		SWTBotText timeSinceLastPushText = prefBot.textWithLabel(GraphitiPreferencesNLS.PortStatPreference_warningNoData);
		timeSinceLastPushText.typeText("NotANumber");
		Assert.assertTrue("Error label not displayed as expected.  Expected: " + GraphitiPreferencesNLS.PortStatPreference_warningNoDataError + " but displayed: "
			+ getErrorMessage(prefShell), GraphitiPreferencesNLS.PortStatPreference_warningNoDataError.equals(getErrorMessage(prefShell)));
		Assert.assertTrue(!applyButton.isEnabled());
		Assert.assertTrue(defaultButton.isEnabled());
		Assert.assertTrue(!okButton.isEnabled());

		SWTBotText queueFlushText = prefBot.textWithLabel(GraphitiPreferencesNLS.PortStatPreference_errorQueueFlush);
		queueFlushText.typeText("NotANumber");
		Assert.assertTrue("Error label not displayed as expected.  Expected: " + GraphitiPreferencesNLS.PortStatPreference_errorQueueFlushError
			+ " but displayed: " + getErrorMessage(prefShell), GraphitiPreferencesNLS.PortStatPreference_errorQueueFlushError.equals(getErrorMessage(prefShell)));
		Assert.assertTrue(!applyButton.isEnabled());
		Assert.assertTrue(defaultButton.isEnabled());
		Assert.assertTrue(!okButton.isEnabled());

		// RESTORE DEFUALTS
		defaultButton.click();
		IPreferenceStore store = SADUIGraphitiPlugin.getDefault().getPreferenceStore();
		Assert.assertNull("No error message should be present", getErrorMessage(prefShell));
		Assert.assertTrue("Default value not reset for " + queueLevelText,
			store.getDefaultDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_LEVEL) == Double.valueOf(queueLevelText.getText()));
		Assert.assertTrue(
			"Default value not reset for " + timeSinceLastPushText,
			store.getDefaultDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_NO_DATA_PUSHED_SECONDS) == Double.valueOf(timeSinceLastPushText.getText()));
		Assert.assertTrue("Default value not reset for " + queueFlushText,
			store.getDefaultDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY) == Double.valueOf(queueFlushText.getText()));

		Assert.assertTrue(applyButton.isEnabled());
		Assert.assertTrue(defaultButton.isEnabled());
		Assert.assertTrue(okButton.isEnabled());

		// MAKE SURE VALID INPUT PERSISTS AFTER CLOSING/REOPENING
		queueLevelText.selectAll().typeText("10.0");
		timeSinceLastPushText.selectAll().typeText("10.0");
		queueFlushText.selectAll().typeText("10.0");

		Assert.assertTrue(applyButton.isEnabled());
		Assert.assertTrue(defaultButton.isEnabled());
		Assert.assertTrue(okButton.isEnabled());

		okButton.click();

		bot.menu("Window").menu("Preferences").click();
		prefShell = bot.shell("Preferences");
		prefBot = prefShell.bot();
		queueLevelText = prefBot.textWithLabel(GraphitiPreferencesNLS.PortStatPreference_warningQueueLevel);
		timeSinceLastPushText = prefBot.textWithLabel(GraphitiPreferencesNLS.PortStatPreference_warningNoData);
		queueFlushText = prefBot.textWithLabel(GraphitiPreferencesNLS.PortStatPreference_errorQueueFlush);
		Assert.assertTrue("Input did not persist for " + queueLevelText, 10.0 == Double.valueOf(queueLevelText.getText()));
		Assert.assertTrue("Input did not persist for " + timeSinceLastPushText, 10.0 == Double.valueOf(timeSinceLastPushText.getText()));
		Assert.assertTrue("Input did not persist for " + queueFlushText, 10.0 == Double.valueOf(queueFlushText.getText()));
		prefShell.close();
	}

	// Have to do some fancy dancing to get the preference page error message
	private String getErrorMessage(final SWTBotShell shell) {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				Shell w = shell.widget;
				PreferenceDialog data = (PreferenceDialog) w.getData();
				PortStatisticsPreferencePage page = (PortStatisticsPreferencePage) data.getSelectedPage();
				setErrorMessage(page.getErrorMessage());
			}
		});

		return this.errorMessage;
	}

	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
