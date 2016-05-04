package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractLogConfigTest;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DomainWaveformLogConfigTest extends AbstractLogConfigTest {
	private static final String DEVICE_MANAGER = "DevMgr_localhost";
	private static final String TEST_WAVEFORM = "SigGenToHardLimitWF";
	private static final String SIGGEN_1 = "SigGen_1";

	private final String domain = "SWTBOT_TEST_" + (int) (1000.0 * Math.random());
	private final String[] WAVEFORM_PARENT_PATH = { domain, "Waveforms" }; // SUPPRESS CHECKSTYLE
	private String waveformFullName;

	@Override
	protected SWTBotTreeItem launchLoggingResource() {
		ScaExplorerTestUtils.launchDomain(bot, domain, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domain);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, domain, TEST_WAVEFORM);
		bot.waitUntil(new WaitForEditorCondition(), WaitForEditorCondition.DEFAULT_WAIT_FOR_EDITOR_TIME);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		waveformFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, WAVEFORM_PARENT_PATH, TEST_WAVEFORM);
		List<String> SIGGEN_PARENT_PATH = new ArrayList<String>(Arrays.asList(WAVEFORM_PARENT_PATH));
		SIGGEN_PARENT_PATH.add(waveformFullName);
		return ScaExplorerTestUtils.getTreeItemFromScaExplorer(gefBot, SIGGEN_PARENT_PATH.toArray(new String[0]), SIGGEN_1);
	}

	@Override
	protected SWTBotView showConsole() {
		return ConsoleUtils.showConsole(gefBot, DEVICE_MANAGER);
	}

	@Override
	protected SWTBotGefEditPart openResourceDiagram() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, WAVEFORM_PARENT_PATH, waveformFullName, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
		return gefBot.gefEditor(waveformFullName).getEditPart(SIGGEN_1);
	}

	@Override
	protected SWTBotGefEditor getDiagramEditor() {
		return gefBot.gefEditor(waveformFullName);
	}

}
