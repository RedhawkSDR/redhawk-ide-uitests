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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

public class BadDevMgrSPDTest extends UITest {

	/**
	 * Ensures the overview page still shows data even if the device manager's SPD file doesn't exist.
	 * @throws CoreException
	 */
	@Test
	public void badDevMgrSPD() throws CoreException {
		StandardTestActions.importProject(FrameworkUtil.getBundle(BadDevMgrSPDTest.class), new Path("workspace/BadDevMgrSPD"), null);
		ProjectExplorerUtils.openProjectInEditor(bot, "BadDevMgrSPD", "DeviceManager.dcd.xml");
		SWTBotEditor editor = bot.editorByTitle("BadDevMgrSPD");
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		Assert.assertEquals("DCE:caa3e1e4-cd55-4eb5-8731-04fc76838d2f", editor.bot().textWithLabel("ID:").getText());
	}

}
