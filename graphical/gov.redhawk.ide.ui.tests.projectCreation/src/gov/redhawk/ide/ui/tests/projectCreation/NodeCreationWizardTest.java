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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;

public class NodeCreationWizardTest extends AbstractCreationWizard2Test {

	private static final String DOMAIN_COMBO_LABEL = "Domain Manager:";

	@Override
	protected String getProjectType() {
		return "REDHAWK Node Project";
	}

	/**
	 * IDE-1111: Test creation of node with dots in the name
	 * IDE-1673: Ensure waveform .spec file directory block generates correctly
	 */
	@Test
	public void testNamespacedNodeCreation() {
		final String nodeName = "namespaced.node.IDE1111";
		createNodeWithDevice(nodeName, null, "name.space.device");

		checkFiles(nodeName);
	}

	private void setDomainName(String domainName) {
		SWTBotCombo combo = bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL);
		if (domainName != null && domainName.length() > 0) {
			combo.setSelection(domainName);
		} else {
			combo.setSelection(0);
			if ("".equals(combo.getText())) { // allow test case to proceed if no items in drop down selection
				combo.setText("RHIDE_NodeCreationWizardTest");
			}
		}
	}

	private void checkFiles(final String projectName) {
		// Ensure DCD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem(projectName).select();
		view.bot().tree().getTreeItem(projectName).expand();
		view.bot().tree().getTreeItem(projectName).getNode("DeviceManager.dcd.xml");
		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		SWTBotEditor editorBot = bot.activeEditor();
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals(projectName, editorBot.bot().textWithLabel("Name:").getText());

		final SWTBotTreeItem projectNode = ProjectExplorerUtils.selectNode(bot, projectName);
		String expectedDirectoryBlock = ProjectCreator.createDirectoryBlock("%dir %{_prefix}/dev/nodes/" + projectName.replace('.', '/'));
		final String[] expectedDirPaths = expectedDirectoryBlock.split("\n");

		// Check that .spec file directory block is correct
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IProject project = (IProject) projectNode.widget.getData();
				File file = project.getFile(projectName + ".spec").getLocation().toFile();
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

	private void createNodeWithDevice(String projectName, String domainName, String deviceName) {
		bot.textWithLabel("&Project name:").setText(projectName);
		setDomainName(domainName);
		bot.button("Next >").click();
		StandardTestActions.selectNamespacedTreeItem(bot, bot.tree(), deviceName);
		bot.button("Finish").click();
	}

	@Override
	public void nonDefaultLocation() throws IOException {
		setDomainName(null);

		super.nonDefaultLocation();
	}

	@Override
	public void uuid() {
		setDomainName(null);

		super.uuid();
	}
}
