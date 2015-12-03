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
package gov.redhawk.ide.ui.tests.projectCreation;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;

import java.util.Arrays;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class GeneralTest extends UITest {

	private void testNewProjectWizardAccess(String menuItem, String shellTitle, boolean shortcut) {
		if (shortcut) {
			bot.menu().menu("File", "New", menuItem).click();
			SWTBotShell wizardShell = bot.shell(shellTitle);
			wizardShell.close();
		}

		bot.menu().menu("File", "New", "Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardShell.bot(), wizardShell.bot().tree(), Arrays.asList("REDHAWK", menuItem));
		treeItem.select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
	}

	@Test
	public void newComponentProject() {
		testNewProjectWizardAccess("REDHAWK Component Project", "New Component Project", true);
	}

	@Test
	public void newWaveformProject() {
		testNewProjectWizardAccess("REDHAWK Waveform Project", "New Waveform Project", true);
	}

	@Test
	public void newDeviceProject() {
		testNewProjectWizardAccess("REDHAWK Device Project", "New Device Project", true);
	}

	@Test
	public void newServiceProject() {
		testNewProjectWizardAccess("REDHAWK Service Project", "New Service Project", true);
	}

	@Test
	public void newNodeProject() {
		testNewProjectWizardAccess("REDHAWK Node Project", "Node Project", true);
	}

	@Test
	public void newControlPanelProject() {
		testNewProjectWizardAccess("REDHAWK Control Panel Project", "New Plug-in Project", false);
	}

	@Test
	public void newFrontEndDeviceProject() {
		testNewProjectWizardAccess("REDHAWK Front End Device Project", "New Device Project", false);
	}

	@Test
	public void newIdlProject() {
		testNewProjectWizardAccess("REDHAWK IDL Project", "IDL Project", false);
	}
	
	@Test
	public void newOctaveProject() {
		testNewProjectWizardAccess("REDHAWK Octave Project", "New Component Project", false);
	}
	
	@Test
	public void newSharedLibraryProject() {
		testNewProjectWizardAccess("REDHAWK Shared Library Project", "New Shared Library Project", false);
	}
}
