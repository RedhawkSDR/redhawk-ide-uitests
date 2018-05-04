package gov.redhawk.ide.runconfig.tests;

import static gov.redhawk.ide.swtbot.condition.NotCondition.not;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.sad.SadPackage;

/**
 * Tests for "Sandbox Waveform" run configurations
 */
public class SandboxWaveformTest extends UITest {

	private LocalScaWaveform[] launchedWaveform = new LocalScaWaveform[1];

	@After
	public void after() throws CoreException {
		if (launchedWaveform[0] != null) {
			SpdLauncherUtil.terminate(launchedWaveform[0]);
			launchedWaveform[0] = null;
		}
		super.after();
	}

	/**
	 * Creates a sandbox waveform run configuration for a waveform in the workspace.
	 * IDE-2197 Validation after choosing SAD file
	 * IDE-2198 Tests the workspace file choice dialog
	 */
	@Test
	public void workspace() {
		String projectName = getClass().getSimpleName() + "_workspace";
		List<String> componentNames = Arrays.asList("SigGen_1");

		WaveformUtils.createNewWaveform(bot, projectName, "rh.SigGen");

		bot.menu().menu("Run", "Run Configurations...").click();
		SWTBotShell runShell = bot.shell("Run Configurations");
		SWTBot runBot = runShell.bot();

		runBot.tree().getTreeItem("Sandbox Waveform").doubleClick();

		runBot.textWithLabel("Name:").setText(projectName);
		SWTBotButton button = runBot.button("Run");
		runBot.waitUntil(not(Conditions.widgetIsEnabled(button)));
		runBot.button("Browse Workspace...").click();

		// IDE-2198 Test the workspace file choice dialog
		SWTBotShell selectShell = runBot.shell("Select a File");
		StandardTestActions.waitForTreeItemToAppear(selectShell.bot(), selectShell.bot().tree(), Arrays.asList(projectName)).select();
		Assert.assertFalse(selectShell.bot().button("OK").isEnabled());
		StandardTestActions.waitForTreeItemToAppear(selectShell.bot(), selectShell.bot().tree(),
			Arrays.asList(projectName, projectName + SadPackage.FILE_EXTENSION)).select();
		selectShell.bot().button("OK").click();
		runBot.waitUntil(Conditions.shellCloses(selectShell));

		Assert.assertEquals("${workspace_loc:/" + projectName + '/' + projectName + ".sad.xml}",
			runBot.textInGroup("Location of Software Assembly File (sad.xml)").getText());

		// IDE-2197 Validation should be good now - a SAD file is all we must have
		Assert.assertTrue(runBot.button("Apply").isEnabled());
		Assert.assertTrue(runBot.button("Run").isEnabled());

		runBot.cTabItem("Properties").activate();
		StandardTestActions.waitForTreeItemToAppear(runBot, runBot.tree(1), Arrays.asList("chan_rf"));
		// TODO: We could property values and test that the launched component has those values

		runBot.cTabItem("Implementation").activate();
		runBot.waitWhile(Conditions.tableHasRows(runBot.table(), 0));
		Assert.assertEquals(componentNames.get(0), runBot.table().cell(0, 0));
		Assert.assertEquals("cpp", runBot.table().cell(0, 1));
		// TODO: We could choose a non-default implementation and test that the launched component used it

		runBot.button("Run").click();
		bot.waitUntil(Conditions.shellCloses(runShell));

		bot.waitUntil(findWaveformAndComponent(projectName, componentNames));
	}

	/**
	 * Creates a sandbox waveform run configuration for a waveform in the Target SDR.
	 * IDE-929 Test external waveform properties are shown
	 * IDE-2197 Validation after choosing SAD file
	 */
	@Test
	public void targetSdr() {
		String projectName = getClass().getSimpleName() + "_targetSdr";
		String waveformLocation = "${SdrRoot}/dom/waveforms/SigGenToHardLimitWaveforms/SigGenToHardLimitExtPortsPropsWF.sad.xml";
		List<String> componentNames = Arrays.asList("SigGen_1", "HardLimit_1");

		bot.menu().menu("Run", "Run Configurations...").click();
		SWTBotShell runShell = bot.shell("Run Configurations");
		SWTBot runBot = runShell.bot();

		runBot.tree().getTreeItem("Sandbox Waveform").doubleClick();

		runBot.textWithLabel("Name:").setText(projectName);
		SWTBotButton button = runBot.button("Run");
		runBot.waitUntil(not(Conditions.widgetIsEnabled(button)));

		runBot.textInGroup("Location of Software Assembly File (sad.xml)").setText(waveformLocation);

		// IDE-2197 Validation should be good now - a SAD file is all we must have
		Assert.assertTrue(runBot.button("Apply").isEnabled());
		Assert.assertTrue(runBot.button("Run").isEnabled());

		// IDE-929 Include an external property amongst those we check
		runBot.cTabItem("Properties").activate();
		StandardTestActions.waitForTreeItemToAppear(runBot, runBot.tree(1), Arrays.asList("chan_rf"));
		StandardTestActions.waitForTreeItemToAppear(runBot, runBot.tree(1), Arrays.asList("external_sri_blocking"));
		// TODO: We could property values and test that the launched component has those values

		runBot.cTabItem("Implementation").activate();
		runBot.waitWhile(Conditions.tableHasRows(runBot.table(), 0));
		Assert.assertEquals(componentNames.get(0), runBot.table().cell(0, 0));
		Assert.assertEquals("cpp", runBot.table().cell(0, 1));
		Assert.assertEquals(componentNames.get(1), runBot.table().cell(1, 0));
		Assert.assertEquals("cpp", runBot.table().cell(1, 1));
		// TODO: We could choose a non-default implementation and test that the launched component used it

		runBot.button("Run").click();
		bot.waitUntil(Conditions.shellCloses(runShell));

		bot.waitUntil(findWaveformAndComponent(projectName, componentNames));
	}

	private ICondition findWaveformAndComponent(String waveformName, List<String> componentNames) {
		return new DefaultCondition() {

			String errorMessage;

			@Override
			public boolean test() throws Exception {
				errorMessage = "Waveform not found";

				LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
				return ScaModelCommand.runExclusive(localSca, () -> {
					for (ScaWaveform waveform : localSca.getWaveforms()) {
						launchedWaveform[0] = (LocalScaWaveform) waveform;
						if (launchedWaveform[0].getName() != null && launchedWaveform[0].getName().contains(waveformName)) {
							errorMessage = "Component(s) not found";

							Set<String> componentNamesSet = new HashSet<>(componentNames);
							for (ScaComponent component : waveform.getComponents()) {
								componentNamesSet.remove(component.getName());
							}
							if (componentNamesSet.isEmpty()) {
								errorMessage = "";
								return true;
							} else {
								errorMessage = "Component(s) not found: " + componentNamesSet.toString();
								return false;
							}
						}
					}
					return false;
				});
			}

			@Override
			public String getFailureMessage() {
				return errorMessage;
			}
		};
	}
}
