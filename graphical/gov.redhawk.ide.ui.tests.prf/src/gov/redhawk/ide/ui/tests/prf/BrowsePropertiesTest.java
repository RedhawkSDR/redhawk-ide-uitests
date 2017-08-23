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
package gov.redhawk.ide.ui.tests.prf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;

import gov.redhawk.ide.swtbot.StandardTestActions;

/**
 * Tests for using the "Browse..." button on the "Properties" page of the SPD/PRF editors.
 */
public class BrowsePropertiesTest extends AbstractPropertyTabTest {

	@Test
	public void addFromTargetSdr() {
		bot.button("Browse...").click();
		SWTBot dialogBot = bot.shell("Browse Properties").bot();

		dialogBot.text().setText("frequency");
		List<String> path = Arrays.asList("Target SDR", "Components", "rh.SigGen", "frequency");
		StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), path).select();
		dialogBot.button("Finish").click();

		assertFormValid();
		editor.bot().tree().getTreeItem("frequency").select();
		Assert.assertEquals("double (64-bit)", bot.comboBoxWithLabel("Type*:").getText());
	}

	@Test
	public void validation() {
		bot.button("Browse...").click();
		SWTBot dialogBot = bot.shell("Browse Properties").bot();

		List<String> path = Arrays.asList("Target SDR", "Components", "rh.SigGen", "frequency");
		for (int i = 1; i < path.size(); i++) {
			SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), path.subList(0, i));
			treeItem.select();
			Assert.assertFalse("Finish button should not be enabled.", dialogBot.button("Finish").isEnabled());
		}

		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), path);
		treeItem.select();
		Assert.assertTrue("Finish button should be enabled.", dialogBot.button("Finish").isEnabled());

		dialogBot.button("Cancel").click();
		assertFormValid();
		Assert.assertFalse("Properties should be empty.", editor.bot().tree().hasItems());
	}

	/**
	 * IDE-1438 Test adding properties from a project in the workspace.
	 * @throws CoreException
	 * @throws IOException
	 */
	@Test
	public void addFromWorkspace() throws CoreException, IOException {
		StandardTestActions.importProject(FrameworkUtil.getBundle(getClass()), new Path("workspace/ReferenceComp"), null);

		editorBot.button("Browse...").click();
		SWTBot dialogBot = bot.shell("Browse Properties").bot();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), Arrays.asList("Workspace", "ReferenceComp"));
		treeItem.select("a", "b", "c", "f");
		dialogBot.button("Finish").click();

		editorBot.button("Browse...").click();
		dialogBot = bot.shell("Browse Properties").bot();
		treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), Arrays.asList("Workspace", "ReferenceComp", "children1"));
		treeItem.select("j", "k");
		dialogBot.button("Finish").click();

		editorBot.button("Browse...").click();
		dialogBot = bot.shell("Browse Properties").bot();
		treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), Arrays.asList("Workspace", "ReferenceComp", "children2"));
		treeItem.select("l");
		dialogBot.button("Finish").click();

		editorBot.button("Browse...").click();
		dialogBot = bot.shell("Browse Properties").bot();
		treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(),
			Arrays.asList("Workspace", "ReferenceComp", "children3", "children4"));
		treeItem.select("n");
		dialogBot.button("Finish").click();

		bot.saveAllEditors();
		try (
			InputStream left = ResourcesPlugin.getWorkspace().getRoot().getProject("PropTest_Comp").getFile("PropTest_Comp.prf.xml").getContents();
			InputStream right = getClass().getResourceAsStream("/testFiles/BrowseProperties_Workspace.prf.xml");
			BufferedReader brLeft = new BufferedReader(new InputStreamReader(left));
			BufferedReader brRight = new BufferedReader(new InputStreamReader(right))) {
			// Begin at the properties tag
			String leftLine = brLeft.readLine();
			while (leftLine != null && !leftLine.contains("properties")) {
				leftLine = brLeft.readLine();
			}
			String rightLine = brRight.readLine();
			while (rightLine != null && !rightLine.contains("properties")) {
				rightLine = brRight.readLine();
			}

			// Compare line by line
			while (leftLine != null && rightLine != null) {
				Assert.assertEquals(rightLine, leftLine);
				leftLine = brLeft.readLine();
				rightLine = brRight.readLine();
			}
			if (leftLine != rightLine) {
				Assert.fail("Unexpected EOF");
			}
		}
	}

	/**
	 * IDE-1773 Test adding well-known properties.
	 */
	@Test
	public void addWellKnown() {
		final String CAT_DESC = "Well-known REDHAWK properties";
		final String PROP_NAME = "LOGGING_CONFIG_URI";
		final String PROP_DESC = "A URI that points to a log4j configuration file used for the device manager and all devices spawned by this device.";
		final String PROP_TYPE = "string";

		editorBot.button("Browse...").click();
		SWTBot dialogBot = bot.shell("Browse Properties").bot();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(dialogBot, dialogBot.tree(), Arrays.asList("Well-known properties", "REDHAWK"));
		treeItem.select();
		Assert.assertEquals(CAT_DESC, dialogBot.textWithLabel("Description:").getText());
		treeItem.select(PROP_NAME);
		Assert.assertEquals(PROP_DESC, dialogBot.textWithLabel("Description:").getText());
		dialogBot.button("Finish").click();

		assertFormValid();
		editor.bot().tree().getTreeItem(PROP_NAME).select();
		Assert.assertEquals(PROP_TYPE, bot.comboBoxWithLabel("Type*:").getText());
	}
}
