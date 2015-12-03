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

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.junit.Before;
import org.osgi.framework.FrameworkUtil;

import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

public abstract class AbstractPropertyTabTest extends UITest {

	protected SWTBotEditor editor; // SUPPRESS CHECKSTYLE SWTBot variable

	@Before
	public void before() throws Exception {
		super.before();

		StandardTestActions.importProject(FrameworkUtil.getBundle(AbstractPropertyTabTest.class), new Path("workspace/PropTest_Comp"), null);

		bot.tree().getTreeItem("PropTest_Comp").select();
		bot.tree().getTreeItem("PropTest_Comp").expand();
		bot.tree().getTreeItem("PropTest_Comp").getNode("PropTest_Comp.spd.xml").doubleClick();
		editor = bot.editorByTitle("PropTest_Comp");

		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
	}

	protected void assertFormValid() {
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SPD_EDITOR_PROPERTIES_TAB_ID);
	}

	protected void assertFormInvalid() {
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SPD_EDITOR_PROPERTIES_TAB_ID);
	}

}
