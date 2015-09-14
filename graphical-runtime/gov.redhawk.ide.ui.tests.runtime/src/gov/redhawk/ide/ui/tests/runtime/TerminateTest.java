package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class TerminateTest extends UIRuntimeTest {

	private static final String SIG_GEN = "rh.SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private static final String SIG_GEN_PROCESS_NAME = "SigGen";

	/**
	 * IDE-946 Ensure there's details about the process exit in the terminal
	 */
	@Test
	public void exitCodeInTerminal() {
		// Launch and then terminate a component
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, "cpp");
		SWTBotTreeItem component = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SIG_GEN_1);
		component.contextMenu("Terminate").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, new String[] { "Sandbox", "Chalkboard" }, SIG_GEN_1);

		// Check the console output
		SWTBotView consoleView = ConsoleUtils.showConsole(bot, SIG_GEN_PROCESS_NAME);
		String consoleText = consoleView.bot().styledText().getText();
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("The IDE detected"));
		Assert.assertTrue("Couldn't find text about process exit", consoleText.contains("SIGTERM"));
	}

}
