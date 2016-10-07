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
package gov.redhawk.ide.ui.tests.runtime;

import java.net.URI;
import java.util.Arrays;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaFileStore;

public class FileManagerTest extends UIRuntimeTest {

	/**
	 * IDE-1537
	 * Show the details of a file manager error by clicking the Details button in the Properties view.
	 * @throws CoreException
	 */
	@Test
	public void showErrorDetails() throws CoreException {
		// Create an unreadable directory in the SDRROOT
		IFileStore readOnlyDirectory = EFS.getStore(URI.create("sdrdom:/readonly"));
		readOnlyDirectory.mkdir(EFS.NONE, null);
		addSdrDomCleanupPath(new Path("/readonly"));
		IFileInfo info = readOnlyDirectory.fetchInfo();
		info.setAttribute(EFS.ATTRIBUTE_OWNER_READ, false);
		info.setAttribute(EFS.ATTRIBUTE_GROUP_READ, false);
		info.setAttribute(EFS.ATTRIBUTE_OTHER_READ, false);
		readOnlyDirectory.putInfo(info, EFS.SET_ATTRIBUTES, null);

		// Launch a domain
		final String domainName = getClass().getSimpleName() + "_showErrorDetails";
		ScaExplorerTestUtils.launchDomain(bot, domainName, null);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);

		// Don't let the console steal focus, show the properties view
		ConsoleUtils.disableAutoShowConsole(bot);
		SWTBotView propertiesView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propertiesView.show();

		// Browse to the directory, try to expand it to trigger an error
		final SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { domainName, "File Manager" }, "readonly");
		treeItem.expand();

		// Wait for the directory fetch to error
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				Object data =  UIThreadRunnable.syncExec(treeItem.display, new Result<Object>() {

					@Override
					public Object run() {
						return treeItem.widget.getData();
					}
				});
				if (data instanceof ScaFileStore) {
					IStatus status = ((ScaFileStore) data).getStatus();
					return status != null && !status.isOK();
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "File exception did not occur";
			}
		});

		// Select the directory, bring up the status details from the properties view
		treeItem.select();
		SWTBotTreeItem statusItem = StandardTestActions.waitForTreeItemToAppear(propertiesView.bot(), propertiesView.bot().tree(), Arrays.asList("Status"));
		statusItem.click(0);
		propertiesView.bot().button("Details").click();

		// Wait for details dialog, then close
		SWTBotShell shell = bot.shell("Event Details");
		shell.bot().button("Ok").click();
		bot.waitUntil(Conditions.shellCloses(shell));
	}

	@After
	public void after() throws CoreException {
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
		super.after();
	}
}
