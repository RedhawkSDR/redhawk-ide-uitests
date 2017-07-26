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

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.finder.RHBot;
import gov.redhawk.ide.swtbot.finder.widgets.RHBotSection;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class ComponentImplementationTabTest extends UITest {

	private SWTBotEditor editor;
	private RHBot editorBot;
	private SoftPkg spd;

	@BeforeClass
	public static void setupPyDev() throws Exception {
		StandardTestActions.configurePyDev();
	}

	@Before
	public void before() throws Exception {
		super.before();

		final String projectName = "CppComTest";

		StandardTestActions.importProject(SpdUiTestsActivator.getInstance().getBundle(), new Path("/workspace/" + projectName), null);

		// Open SPD file, switch to Implementation tab
		ProjectExplorerUtils.openProjectInEditor(bot, projectName, projectName + ".spd.xml");
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

	@Test
	/**
	 * Test that all usesdevice properties are visible and display expected labels
	 */
	public void testUsesDevice() {
		SWTBotTree implementationTree = editorBot.tree();

		// Detail all the expected tree nodes
		String implementation = "cpp *";
		String usesDevice = "Uses Device usesdevice";
		String propRef = "propRefID";
		String simpleRef = "simpleRefID";
		String simpleSeqRef = "simpleSequenceRefID";
		String structRef = "structRefID";
		String structRefSimple = "structSimpleRefID";
		String structRefSimpleSeq = "structSimpleSequenceRefID";
		String structSeqRef = "structSequenceRefID";
		String structValue = "Struct [0]";
		String structValueSimple = "structValueSimpleRefID";
		String structValueSimpleSeq = "structValueSimpleSequenceRefID";

		// Check each node (either directly or indirectly as part of a chain to a child node)
		SWTBotTreeItem implNode = implementationTree.getTreeItem(implementation);
		implNode.expand().select();
		implNode.expandNode(usesDevice, propRef).select();
		implNode.expandNode(usesDevice, simpleRef).select();
		implNode.expandNode(usesDevice, simpleSeqRef).select();
		implNode.expandNode(usesDevice, structRef, structRefSimple).select();
		implNode.expandNode(usesDevice, structRef, structRefSimpleSeq).select();
		implNode.expandNode(usesDevice, structSeqRef, structValue, structValueSimple).select();
		implNode.expandNode(usesDevice, structSeqRef, structValue, structValueSimpleSeq).select();
	}

}
