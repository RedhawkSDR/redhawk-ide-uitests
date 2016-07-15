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
package gov.redhawk.ide.ui.tests.spd;

import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComponentOverviewTabTest extends UITest {

	private SWTBotEditor editor;
	private SoftPkg spd;
	private SWTBot editorBot;

	@Before
	public void before() throws Exception {
		super.before();

		StandardTestActions.importProject(SpdUiTestsActivator.getInstance().getBundle(), new Path("/workspace/CppComTest"), null);
		// Ensure SPD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem("CppComTest").select();
		view.bot().tree().getTreeItem("CppComTest").expand();
		view.bot().tree().getTreeItem("CppComTest").getNode("CppComTest.spd.xml").doubleClick();

		editor = bot.editorByTitle("CppComTest");
		editor.setFocus();
		editorBot = editor.bot();
		editorBot.cTabItem("Overview").activate();

		SCAFormEditor spdEditor = (SCAFormEditor) editor.getReference().getEditor(false);
		spd = SoftPkg.Util.getSoftPkg(spdEditor.getMainResource());
	}

	private void assertFormValid() {
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SPD_EDITOR_OVERVIEW_TAB_ID);
	}

	private void assertFormInvalid() {
		editorBot.sleep(600);
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SPD_EDITOR_OVERVIEW_TAB_ID);
	}

	@Test
	public void testIDField() {
		editorBot.button("Generate").click();

		String idText = bot.textWithLabel("ID*:").getText();
		assertFormValid();
		Assert.assertTrue("Not valid DCE UUID", DceUuidUtil.isValid(idText));
		Assert.assertEquals(spd.getId(), idText);

		editorBot.textWithLabel("ID*:").setText("DCE");
		assertFormInvalid();

		editorBot.textWithLabel("ID*:").setText("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db");
		assertFormValid();
		Assert.assertEquals("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db", spd.getId());

		editorBot.textWithLabel("ID*:").setText("");
		assertFormInvalid();
	}

	@Test
	public void testName() {
		editorBot.textWithLabel("Name*:").setText("TestComponent");
		assertFormValid();
		Assert.assertEquals(spd.getName(), "TestComponent");

		editorBot.textWithLabel("Name*:").setText("");
		assertFormInvalid();
	}

	@Test
	public void testDescription() {
		editorBot.textWithLabel("Description:").setText("A component created for testing.");
		assertFormValid();
		Assert.assertEquals(spd.getDescription(), "A component created for testing.");

		editorBot.textWithLabel("Description:").setText("");
		assertFormValid();
		Assert.assertNull(spd.getDescription());
	}

	@Test
	public void testVersion() {
		editorBot.textWithLabel("Version:").setText("1.0.0");
		assertFormValid();
		Assert.assertEquals(spd.getVersion(), "1.0.0");

		editorBot.textWithLabel("Version:").setText("");
		assertFormValid();
		Assert.assertNull(spd.getVersion());

		editorBot.textWithLabel("Version:").setText("asd23r asd");
		assertFormValid();
	}

	@Test
	public void testTitle() {
		editorBot.textWithLabel("Title:").setText("Test Component");
		assertFormValid();
		Assert.assertEquals(spd.getTitle(), "Test Component");

		editorBot.textWithLabel("Title:").setText("");
		assertFormValid();
		Assert.assertNull(spd.getTitle());
	}

	@Test
	public void testPrf() {
		editorBot.button("Browse...").click();
		bot.tree().getTreeItem("CppComTest").getNode("CppComTest.prf.xml").select();
		bot.button("OK").click();
		assertFormValid();
		editorBot.textWithLabel("Title:", 1).setText("CppComTest.prf.xml");
		assertFormValid();
		editorBot.textWithLabel("Title:", 1).setText("blah.prf.xml");
		assertFormInvalid();
		editorBot.textWithLabel("Title:", 1).setText("CppComTest.prf.xml");
		assertFormValid();
	}

	@Test
	public void testScd() {
		editorBot.button("Browse...", 1).click();
		bot.tree().getTreeItem("CppComTest").getNode("CppComTest.scd.xml").select();
		bot.button("OK").click();
		assertFormValid();
		editorBot.textWithLabel("Title:", 2).setText("CppComTest.scd.xml");
		assertFormValid();
		editorBot.textWithLabel("Title:", 2).setText("blah.scd.xml");
		assertFormInvalid();
		editorBot.textWithLabel("Title:", 2).setText("CppComTest.scd.xml");
		assertFormValid();
	}

	@Test
	public void testGenerateButton() {
		Assert.assertTrue("Generate button should not be disabled.", editorBot.toolbarButtonWithTooltip("Generate All Implementations").isEnabled());
	}

	/**
	 * Test that the control panel button is present, enabled, and that there's a confirmation dialog before it does
	 * anything.
	 * IDE-1404 Confirmation dialog for creating a new control panel
	 */
	@Test
	public void testControlPanelButton() {
		SWTBotToolbarButton button = editorBot.toolbarButtonWithTooltip("New Control Panel Project");
		Assert.assertTrue("Control panel button should not be disabled.", button.isEnabled());
		button.click();
		SWTBotShell shell = bot.shell("Create control panel");
		shell.bot().button("Cancel").click();
	}
}
