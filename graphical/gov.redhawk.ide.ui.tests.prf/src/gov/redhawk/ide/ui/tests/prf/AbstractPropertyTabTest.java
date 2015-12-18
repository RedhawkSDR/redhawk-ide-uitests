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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Before;
import org.osgi.framework.FrameworkUtil;

import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public abstract class AbstractPropertyTabTest extends UITest {

	protected SWTBotEditor editor; // SUPPRESS CHECKSTYLE SWTBot variable

	@Before
	public void before() throws Exception {
		super.before();

		StandardTestActions.importProject(FrameworkUtil.getBundle(AbstractPropertyTabTest.class), new Path("workspace/PropTest_Comp"), null);
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(bot, bot.tree(), Arrays.asList("PropTest_Comp", "PropTest_Comp.spd.xml"));
		treeItem.doubleClick();
		editor = bot.editorByTitle("PropTest_Comp");

		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
	}

	protected Properties getModelFromXml() throws IOException {
		DiagramTestUtils.openTabInEditor(editor, "PropTest_Comp.prf.xml");
		String text = editor.bot().styledText().getText();
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);

		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource resource = resourceSet.createResource(URI.createURI("mem://PropTest_Comp.prf.xml"), PrfPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(text.getBytes()), null);
		return Properties.Util.getProperties(resource);
	}

	protected void assertFormValid() {
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SPD_EDITOR_PROPERTIES_TAB_ID);
	}

	protected void assertFormInvalid() {
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SPD_EDITOR_PROPERTIES_TAB_ID);
	}

}
