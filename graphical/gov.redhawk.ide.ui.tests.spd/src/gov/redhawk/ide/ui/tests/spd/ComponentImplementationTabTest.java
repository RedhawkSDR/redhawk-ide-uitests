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

import java.math.BigInteger;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.finder.RHBot;
import gov.redhawk.ide.swtbot.finder.widgets.RHBotSection;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComponentImplementationTabTest extends UITest {

	private SWTBotEditor editor;
	private RHBot editorBot;
	private SoftPkg spd;

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
		editorBot = new RHBot(editor.bot());
		editorBot.cTabItem("Implementations").activate();

		SCAFormEditor spdEditor = (SCAFormEditor) editor.getReference().getEditor(false);
		spd = SoftPkg.Util.getSoftPkg(spdEditor.getMainResource());
	}

	@Test
	public void testCodeEntryPoint() {
		RHBotSection section = editorBot.section("Code");
		section.expand();
		editorBot.textWithLabel("Entry Point:").setText("someentrypoint");
		editorBot.sleep(600);
		Assert.assertEquals(spd.getImplementation().get(0).getCode().getEntryPoint(), "someentrypoint");
	}
	
	@Test
	public void testCodePriority() {
		RHBotSection section = editorBot.section("Code");
		section.expand();
		editorBot.textWithLabel("Priority:").setText("1");
		editorBot.sleep(600);
		Assert.assertEquals(spd.getImplementation().get(0).getCode().getPriority(), BigInteger.valueOf(1));
	}
	
	@Test
	public void testCodeLocalFile() {
		RHBotSection section = editorBot.section("Code");
		section.expand();
		editorBot.textWithLabel("File*:").setText("somelocation");
		editorBot.sleep(600);
		Assert.assertEquals(spd.getImplementation().get(0).getCode().getLocalFile().getName(), "somelocation");
	}
	
	@Test
	public void testCodeStackSize() {
		RHBotSection section = editorBot.section("Code");
		section.expand();
		editorBot.textWithLabel("Stack Size:").setText("2");
		editorBot.sleep(600);
		Assert.assertEquals(spd.getImplementation().get(0).getCode().getStackSize(), BigInteger.valueOf(2));
	}

}
