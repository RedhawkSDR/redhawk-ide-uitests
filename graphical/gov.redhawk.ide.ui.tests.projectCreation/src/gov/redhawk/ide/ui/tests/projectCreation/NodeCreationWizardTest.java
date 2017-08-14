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
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public class NodeCreationWizardTest extends AbstractCreationWizard2Test {

	private static final String DOMAIN_COMBO_LABEL = "Domain Manager:";

	private RHSWTGefBot gefBot = new RHSWTGefBot();

	@Override
	protected String getProjectType() {
		return "REDHAWK Node Project";
	}

	/**
	 * IDE-1111: Test creation of node with dots in the name
	 * IDE-1500: Test adding a device and service during node creation
	 * IDE-1673: Ensure waveform .spec file directory block generates correctly
	 * @throws IOException
	 * @throws WidgetNotFoundException
	 */
	@Test
	public void testNamespacedNodeCreation() throws WidgetNotFoundException, IOException {
		final String projectName = "namespaced.node";
		final String deviceName = "name.space.device";
		final String serviceName = "name.space.service";

		// Finish new node wizard
		bot.textWithLabel("&Project name:").setText(projectName);
		setDomainName();
		bot.button("Next >").click();
		StandardTestActions.selectNamespacedTreeItem(bot, bot.tree(0), deviceName);
		StandardTestActions.selectNamespacedTreeItem(bot, bot.tree(1), serviceName);
		bot.button("Finish").click();

		// Ensure DCD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().getTreeItem(projectName).expand().getNode("DeviceManager.dcd.xml");
		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		SWTBotEditor editor = bot.activeEditor();
		editor.bot().cTabItem("Overview").activate();
		Assert.assertEquals(projectName, editor.bot().textWithLabel("Name:").getText());

		// Make sure Device and Service show up in the diagram
		editor.bot().cTabItem("Diagram").activate();
		SWTBotGefEditor gefEditor = gefBot.rhGefEditor(projectName);
		gefEditor.getEditPart(deviceName);
		gefEditor.getEditPart(serviceName);

		// Check that .spec file directory block is correct
		final SWTBotTreeItem projectNode = ProjectExplorerUtils.selectNode(bot, projectName);
		String expectedDirectoryBlock = ProjectCreator.createDirectoryBlock("%dir %{_prefix}/dev/nodes/" + projectName.replace('.', '/'));
		final String[] expectedDirPaths = expectedDirectoryBlock.split("\n");
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

	@Override
	public void nonDefaultLocation() throws IOException {
		setDomainName();

		super.nonDefaultLocation();
	}

	@Override
	public void uuid() {
		setDomainName();

		super.uuid();
	}

	private void setDomainName() {
		SWTBotCombo combo = bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL);
		combo.setSelection(0);
		if ("".equals(combo.getText())) { // allow test case to proceed if no items in drop down selection
			combo.setText("RHIDE_NodeCreationWizardTest");
		}
	}
}
