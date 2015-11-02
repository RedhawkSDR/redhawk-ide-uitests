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

import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
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
		testSelectAssemblyController("rh.SigGen (/components/rh/SigGen/)", "SigGen_1");
	}

	@Test
	public void testWithNamespacedAssemblyController() throws IOException {
		testSelectAssemblyController("ide1112.test.name.spaced.comp1 (/components/ide1112/test/name/spaced/comp1/)", "comp1_1");
	}

	protected void testSelectAssemblyController(String acName, String ciName) throws IOException {
		bot.textWithLabel("&Project name:").setText("WaveformProj01");
		bot.button("Next >").click();

		bot.activeShell().bot().table().select(acName);
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
	 */
	@Test
	public void testNamespacedWaveformCreation() {
		testBasicCreate("namespaced.waveform.IDE1111");
	}
	
}
