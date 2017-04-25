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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.model.sca.util.ModelUtil;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class WaveformWizardTest extends AbstractCreationWizardTest {

	@Override
	protected String getProjectType() {
		return "REDHAWK Waveform Project";
	}

	@Test
	public void testBasicCreate() {
		testBasicCreate("WaveformProj01");
	}

	protected void testBasicCreate(String projectName) {
		bot.textWithLabel("&Project name:").setText(projectName);
		bot.button("Finish").click();

		String baseFilename = getBaseFilename(projectName);

		// Ensure SAD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem(projectName).select();
		view.bot().tree().getTreeItem(projectName).expand();
		view.bot().tree().getTreeItem(projectName).getNode(baseFilename + ".sad.xml");

		SWTBotEditor editorBot = bot.editorByTitle(projectName);
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals(projectName, editorBot.bot().textWithLabel("Name:").getText());
	}

	@Test
	public void testCreateFromTemplate() throws IOException {
		bot.textWithLabel("&Project name:").setText("WaveformProj01");
		bot.radio("Use existing waveform as a template").click();
		bot.textWithLabel("SAD File:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		String path = System.getenv("SDRROOT") + "/dom/waveforms/ExampleWaveform05/ExampleWaveform05.sad.xml";
		File file = new File(path);
		bot.textWithLabel("SAD File:").setText(file.getAbsolutePath());
		bot.button("Finish").click();

		// Ensure SAD file was created and the editor opens
		ProjectExplorerUtils.waitUntilNodeAppears(bot, "WaveformProj01", "WaveformProj01.sad.xml");

		SWTBotEditor editorBot = bot.editorByTitle("WaveformProj01");
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals("WaveformProj01", editorBot.bot().textWithLabel("Name:").getText());
		Assert.assertNotEquals("DCE:64a7d543-7055-494d-936f-30225b3b283e", editorBot.bot().textWithLabel("ID:").getText());
	}

	@Test
	public void testWithAssemblyController() throws IOException {
		testSelectAssemblyController("rh.SigGen", "SigGen_1");
	}

	@Test
	public void testWithNamespacedAssemblyController() throws IOException {
		testSelectAssemblyController("ide1112.test.name.spaced.comp1", "comp1_1");
	}

	protected void testSelectAssemblyController(String acName, String ciName) throws IOException {
		bot.textWithLabel("&Project name:").setText("WaveformProj01");
		bot.button("Next >").click();
		StandardTestActions.selectNamespacedTreeItem(bot, bot.tree(), acName);
		bot.button("Finish").click();

		// Ensure SAD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem("WaveformProj01").select();
		view.bot().tree().getTreeItem("WaveformProj01").expand();
		view.bot().tree().getTreeItem("WaveformProj01").getNode("WaveformProj01.sad.xml");

		SWTBotEditor editorBot = bot.editorByTitle("WaveformProj01");
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals("WaveformProj01", editorBot.bot().textWithLabel("Name:").getText());

		if (ciName == null) {
			ciName = acName + "_1";
		}
		URI uri = URI.createPlatformResourceURI("/WaveformProj01/WaveformProj01.sad.xml", true);
		SoftwareAssembly sad = ModelUtil.loadSoftwareAssembly(uri);
		Assert.assertEquals(1, sad.getPartitioning().getComponentPlacement().size());
		Assert.assertEquals(1, sad.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().size());
		Assert.assertEquals(ciName, sad.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0).getUsageName());
		Assert.assertEquals(sad.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0),
			sad.getAssemblyController().getComponentInstantiationRef().getInstantiation());
	}

	/**
	 * IDE-1111: Test creation of waveform with dots in the name
	 * IDE-1673: Ensure waveform .spec file directory block generates correctly
	 */
	@Test
	public void testNamespacedWaveformCreation() {
		final String waveformName = "namespaced.waveform.IDE1111";

		// Test basic project creation
		testBasicCreate(waveformName);

		// Add a component to the waveform so that the .spec file generates
		RHSWTGefBot gefBot = new RHSWTGefBot();
		gefBot.editorByTitle(waveformName).bot().cTabItem("Diagram").activate();
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, "rh.SigGen", 10, 10);
		editor.save();

		final SWTBotTreeItem projectNode = ProjectExplorerUtils.selectNode(gefBot, waveformName);
		String expectedDirectoryBlock = ProjectCreator.createDirectoryBlock("%dir %{_prefix}/dom/waveforms/" + waveformName.replace('.', '/'));
		final String[] expectedDirPaths = expectedDirectoryBlock.split("\n");

		// Check that .spec file directory block is correct
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IProject project = (IProject) projectNode.widget.getData();
				File file = project.getFile(waveformName + ".spec").getLocation().toFile();
				try {
					List<String> fileContents = Files.readAllLines(file.toPath(), Charset.defaultCharset());
					for (String path : expectedDirPaths) {
						if (fileContents.contains(path)) {
							continue;
						}
						Assert.fail("Expected directory path " + path + " was not found in the project spec file");
					}
				} catch (IOException e) {
					Assert.fail(e.getMessage());
				}
			}
		});
	}

	/**
	 * Tests a race condition - it was possible to enter an illegal character in the waveform name while completing
	 * the new REDHAWK waveform wizard.
	 * IDE-826 Race condition naming project / clicking finish
	 * @throws Exception
	 */
	@Test
	public void projectNameRaceCondition() throws Exception {
		final String PROJECT_NAME = "test_IDE_826";

		// Type the project name, hit enter, type one additional bad character
		SWTBotText projectNameField = getWizardBot().textWithLabel("Project name:");
		projectNameField.typeText(PROJECT_NAME + "\n" + "\\");

		try {
			bot.waitUntil(Conditions.shellCloses(getWizardShell()));
			Assert.fail("Wizard should not have closed");
		} catch (TimeoutException e) {
			// PASS - we expect this
		}

		getWizardBot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(getWizardShell()));
	}
}
