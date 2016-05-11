package gov.redhawk.ide.ui.tests.runtime;

import java.net.URI;
import java.util.Arrays;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class FileManagerTest extends UIRuntimeTest {

	/**
	 * IDE-1537
	 * Show the details of a file manager error by clicking the Details button in the Properties view.
	 * @throws CoreException
	 */
	@Test
	public void showErrorDetails() throws CoreException {
		// Create a read-only directory in the SDRROOT
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
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { domainName, "File Manager" }, "readonly");
		treeItem.expand();

		// Select the directory, bring up the status details from the properties view
		treeItem.select();
		SWTBotTreeItem statusItem = StandardTestActions.waitForTreeItemToAppear(propertiesView.bot(), propertiesView.bot().tree(), Arrays.asList("Status"));
		statusItem.click();
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
