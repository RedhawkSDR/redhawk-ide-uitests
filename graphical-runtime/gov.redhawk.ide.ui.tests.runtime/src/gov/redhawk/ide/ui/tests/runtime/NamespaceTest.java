/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.ui.tests.runtime;

import java.io.File;

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.condition.WaitForSeverityMarkers;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.util.ModelUtil;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * These tests create projects that use a namespace, build them, and install them to SDRROOT. Their presence in the
 * explorer view is verified, and then they're deleted.
 */
public class NamespaceTest extends UIRuntimeTest {

	private static final String PREFIX_DOTS = "runtime.test.";
	private static final String SIGGEN = "rh.SigGen";

	@After
	public void after() {
		// Cleanup anything left in the SDRROOT that we installed
		IPath domPath = SdrUiPlugin.getDefault().getTargetSdrDomPath();
		final String[] domSubdirs = new String[] { "components/runtime", "waveforms/runtime", "deps/runtime" };
		for (String subdir : domSubdirs) {
			File dir = domPath.append(subdir).toFile();
			if (dir.isDirectory()) {
				dir.delete();
			}
		}

		IPath devPath = SdrUiPlugin.getDefault().getTargetSdrDevPath();
		final String[] devSubdirs = new String[] { "devices/runtime", "services/runtime", "nodes/runtime" };
		for (String subdir: devSubdirs) {
			File dir = devPath.append(subdir).toFile();
			if (dir.isDirectory()) {
				dir.delete();
			}
		}
	}

	/**
	 * IDE-1122, IDE-1182, IDE-1183, IDE-1185
	 * Check that name-spaced component projects can be created, generated and exported.
	 * They should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorComponents() {
		final String componentBaseName = "component";

		String projectName = PREFIX_DOTS + "cpp." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "C++");
		generateProjectAndBuild(projectName, componentBaseName + ".cpp");

		projectName = PREFIX_DOTS + "java." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "Java");
		generateProjectAndBuild(projectName, componentBaseName + ".java");

		projectName = PREFIX_DOTS + "python." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "Python");
		generateProjectAndBuild(projectName, componentBaseName);

		exportProject(PREFIX_DOTS + "cpp." + componentBaseName);
		exportProject(PREFIX_DOTS + "java." + componentBaseName);
		exportProject(PREFIX_DOTS + "python." + componentBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "runtime", "test", "cpp" }, componentBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "runtime", "test", "java" }, componentBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "runtime", "test", "python" }, componentBaseName);
	}

	/**
	 * IDE-1122, IDE-1182, IDE-1183, IDE-1185
	 * Check that name-spaced device projects can be created, generated and exported.
	 * They should also be represented in the SCA Explorer.
	 * @throws InterruptedException
	 * @throws OperationCanceledException
	 */
	@Test
	public void namespaceBehaviorDevices() throws OperationCanceledException, InterruptedException {
		final String deviceBaseName = "dev";

		String projectName = PREFIX_DOTS + "cpp." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "C++");
		generateProjectAndBuild(projectName, deviceBaseName + ".cpp");

		projectName = PREFIX_DOTS + "java." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "Java");
		generateProjectAndBuild(projectName, deviceBaseName + ".java");

		projectName = PREFIX_DOTS + "python." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "Python");
		generateProjectAndBuild(projectName, deviceBaseName);

		exportProject(PREFIX_DOTS + "cpp." + deviceBaseName);
		exportProject(PREFIX_DOTS + "java." + deviceBaseName);
		exportProject(PREFIX_DOTS + "python." + deviceBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "runtime", "test", "cpp" }, deviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "runtime", "test", "java" }, deviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "runtime", "test", "python" }, deviceBaseName);
	}

	/**
	 * IDE-1122, IDE-1182, IDE-1183, IDE-1185
	 * Check that a name-spaced service project can be created, generated and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorServices() {
		final String serviceBaseName = "service";
		final String serviceInterface = "IDL:CF/LogEventConsumer:1.0";

		String projectName = PREFIX_DOTS + "cpp." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "C++");
		generateProjectAndBuild(projectName, serviceBaseName + ".cpp");

		projectName = PREFIX_DOTS + "java." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "Java");
		generateProjectAndBuild(projectName, serviceBaseName + ".java");

		projectName = PREFIX_DOTS + "python." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "Python");
		generateProjectAndBuild(projectName, serviceBaseName);

		exportProject(PREFIX_DOTS + "cpp." + serviceBaseName);
		exportProject(PREFIX_DOTS + "java." + serviceBaseName);
		exportProject(PREFIX_DOTS + "python." + serviceBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "runtime", "test", "cpp" }, serviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "runtime", "test", "java" }, serviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "runtime", "test", "python" }, serviceBaseName);
	}

	/**
	 * IDE-1122, IDE-1128, IDE-1332
	 * Check that a name-spaced waveform project can be created and exported.
	 * It should install to the correct location (we install it), and also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorWaveforms() {
		final String waveformBaseName = "waveform";

		// Create with one name, change the XML to another
		WaveformUtils.createNewWaveform(bot, waveformBaseName, SIGGEN);
		final SWTBotEditor editor = bot.editorByTitle(waveformBaseName);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		editor.bot().textWithLabel("Name:").setText(PREFIX_DOTS + waveformBaseName);
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return editor.isDirty();
			}

			@Override
			public String getFailureMessage() {
				return "Editor was never dirty";
			}
		});
		editor.save();

		exportProject(waveformBaseName);
		String[] scaPath = { "Target SDR", "Waveforms", "runtime", "test" };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, scaPath, waveformBaseName);

		// Check that the directory and XML file exist in the appropriate location in the SDRROOT
		IPath waveformDir = SdrUiPlugin.getDefault().getTargetSdrDomPath().append("waveforms");
		for (String segment : PREFIX_DOTS.split("\\.")) {
			waveformDir = waveformDir.append(segment);
		}
		waveformDir = waveformDir.append(waveformBaseName);
		Assert.assertTrue("Directory for waveform doesn't exist in SDRROOT", waveformDir.toFile().exists());
		Assert.assertTrue("SAD XML for waveform doesn't exist in SDRROOT", waveformDir.append(waveformBaseName + ".sad.xml").toFile().exists());

		checkExistsInScaAndRemove(scaPath, waveformBaseName);
	}

	/**
	 * IDE-1122, IDE-1128, IDE-1332
	 * Check that a name-spaced service project can be created and exported.
	 * It should install to the correct location (we install it), and also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorNodes() {
		final String nodeBaseName = "node";
		final String nodeDomain = "REDHAWK_DEV";

		// Create with one name, change the XML to another
		NodeUtils.createNewNodeProject(bot, nodeBaseName, nodeDomain);
		final SWTBotEditor editor = bot.editorByTitle(nodeBaseName);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		editor.bot().textWithLabel("Name:").setText(PREFIX_DOTS + nodeBaseName);
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return editor.isDirty();
			}

			@Override
			public String getFailureMessage() {
				return "Editor was never dirty";
			}
		});
		editor.save();

		exportProject(nodeBaseName);
		String[] scaPath = { "Target SDR", "Nodes", "runtime", "test" };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, scaPath, nodeBaseName);

		// Check that the directory and XML file exist in the appropriate location in the SDRROOT
		IPath nodeDir = SdrUiPlugin.getDefault().getTargetSdrDevPath().append("nodes");
		for (String segment : PREFIX_DOTS.split("\\.")) {
			if (segment.trim().length() > 0) {
				nodeDir = nodeDir.append(segment);
			}
		}
		nodeDir = nodeDir.append(nodeBaseName);
		Assert.assertTrue("Directory for node doesn't exist in SDRROOT", nodeDir.toFile().exists());
		Assert.assertTrue("DCD XML for node doesn't exist in SDRROOT", nodeDir.append("DeviceManager.dcd.xml").toFile().exists());

		checkExistsInScaAndRemove(scaPath, nodeBaseName);
	}

	/**
	 * IDE-1122, IDE-1182, IDE-1185
	 * Check that a name-spaced shared library can be created, generated and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorSharedLib() {
		final String sharedLibraryBaseName = "sharedLibrary";
		final String sharedLibraryType = "C++ Library";

		SharedLibraryUtils.createSharedLibraryProject(bot, PREFIX_DOTS + sharedLibraryBaseName, sharedLibraryType);

		// IDE-1239 Double-check the localfile element
		SoftPkg spd = ModelUtil.getSoftPkg(ResourcesPlugin.getWorkspace().getRoot().getProject(PREFIX_DOTS + sharedLibraryBaseName));
		Assert.assertEquals("localfile element isn't correct", "cpp/lib", spd.getImplementation("cpp").getCode().getLocalFile().getName());

		generateProjectAndBuild(PREFIX_DOTS + sharedLibraryBaseName, sharedLibraryBaseName + ".cpp");

		exportProject(PREFIX_DOTS + sharedLibraryBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Shared Libraries", "runtime", "test" }, sharedLibraryBaseName);
	}

	private void generateProjectAndBuild(String projectName, String editorTabName) {
		// Generate
		SWTBotEditor editor = bot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, editor);

		// Default file editor should open
		bot.editorByTitle(editorTabName);

		// Wait for the build to finish and any error markers to go away, then close editors
		bot.waitUntil(new WaitForBuild(), 30000);
		bot.waitUntil(new WaitForSeverityMarkers(IMarker.SEVERITY_WARNING), 120000);
		bot.closeAllEditors();
	}

	private void exportProject(String projectName) {
		SWTBotTreeItem projectNode = ProjectExplorerUtils.selectNode(bot, projectName);
		projectNode.contextMenu("Export to SDR").click();
	}

	private void checkExistsInScaAndRemove(String[] scaPath, String projectName) {
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, scaPath, projectName);
		SWTBotTreeItem scaNode = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, scaPath, projectName);
		scaNode.select();
		SWTBotMenu deleteContext = scaNode.contextMenu("Delete");
		deleteContext.click();
		bot.button("Yes").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, scaPath, projectName);
	}

}
