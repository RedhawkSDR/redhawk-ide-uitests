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
package gov.redhawk.ide.graphiti.dcd.ui.tests.formeditor;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;

import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.util.DceUuidUtil;

public class NodeOverviewTabTest extends UITest {

	private SWTBotEditor editor;
	private SWTBot editorBot;
	private DeviceConfiguration dcd;

	@Before
	public void before() throws Exception {
		super.before();

		// Import project and open editor to overview tab
		StandardTestActions.importProject(FrameworkUtil.getBundle(NodeOverviewTabTest.class), new Path("/workspace/Node01"), null);
		ProjectExplorerUtils.openProjectInEditor(bot, "Node01", "DeviceManager.dcd.xml");
		this.editor = bot.editorByTitle("Node01");
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		this.editorBot = editor.bot();

		Resource resource = ((SCAFormEditor) editor.getReference().getEditor(false)).getMainResource();
		this.dcd = DeviceConfiguration.Util.getDeviceConfiguration(resource);
	}

	@Test
	public void testIDField() {
		// Test generating a new ID
		String oldID = editorBot.textWithLabel("ID:").getText();
		editorBot.button("Generate").click();
		String newID = bot.textWithLabel("ID:").getText();
		Assert.assertNotEquals(oldID, newID);
		Assert.assertTrue("Not valid DCE UUID", DceUuidUtil.isValid(newID));
		EditorUtils.assertEditorTabValid(editor, EditorUtils.DCD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals(newID, dcd.getId());

		// Test an invalid ID
		editorBot.textWithLabel("ID:").setText("DCE");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.DCD_EDITOR_OVERVIEW_TAB_ID);

		// Back to a valid ID
		editorBot.textWithLabel("ID:").setText("");
		editorBot.textWithLabel("ID:").typeText("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.DCD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db", dcd.getId());

		// Test empty ID
		editorBot.textWithLabel("ID:").setText("");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.DCD_EDITOR_OVERVIEW_TAB_ID);
	}

	@Test
	public void testName() {
		editorBot.textWithLabel("Name:").setText("");
		editorBot.textWithLabel("Name:").typeText("foo");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.DCD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("foo", dcd.getName());

		/* TODO: Right now, it's valid XML to not have a name, although the CF has issues with it in all versions
		editorBot.textWithLabel("Name:").setText("");
		editorBot.sleep(600);
		EditorActions.assertEditorTabInvalid(editor, EditorActions.DCD_EDITOR_OVERVIEW_TAB_ID);
		*/
	}

	@Test
	public void testDescription() {
		editorBot.textWithLabel("Description:").typeText("A test description");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.DCD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("A test description", dcd.getDescription());

		editorBot.textWithLabel("Description:").setText("");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.DCD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertNull(dcd.getDescription());
	}

}
