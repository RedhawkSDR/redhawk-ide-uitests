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
package gov.redhawk.ide.graphiti.sad.ui.tests.formeditor;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.Before;
import org.osgi.framework.FrameworkUtil;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public abstract class AbstractWaveformTabTest extends UITest {

	protected abstract String getProjectName();

	protected abstract String getTabName();

	// CHECKSTYLE:OFF expose variables to inheriting classes
	protected SWTBotEditor editor;
	protected SWTBot editorBot;
	protected SoftwareAssembly sad;
	// CHECKSTYLE:ON

	@Before
	public void before() throws Exception {
		super.before();

		String projectName = getProjectName();

		// Import project and open editor to overview tab
		StandardTestActions.importProject(FrameworkUtil.getBundle(AbstractWaveformTabTest.class), new Path("/workspace/" + projectName), null);
		ProjectExplorerUtils.openProjectInEditor(bot, projectName, projectName + ".sad.xml");
		this.editor = bot.editorByTitle(projectName);
		DiagramTestUtils.openTabInEditor(editor, getTabName());
		this.editorBot = editor.bot();

		Resource resource = ((SCAFormEditor) editor.getReference().getEditor(false)).getMainResource();
		this.sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
	}

}
