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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.ComponentUtils.PortDirection;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class ChalkboardWorkspaceTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	@BeforeClass
	public static void beforeClassSetup() {
		// PyDev needs to be configured before running New REDHAWK * Project Wizards in some of the test cases
		StandardTestActions.configurePyDev(new SWTWorkbenchBot());
	}

	/**
	 * IDE-660 Chalkboard Palette contains Workspace Components
	 */
	@Test
	public void checkHasWorkspaceComponents() {
		// create test Component in workspace
		final String wkspComponentName = "testComponentInWorkspace";
		ComponentUtils.createComponentProject(bot, wkspComponentName, "Python");

		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// validate that workspace Component is in Chalkboard palette
		assertTrue("Workspace Component did not appear in Chalkboard Palette", isToolInPalette(editor, wkspComponentName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspComponentName);
	}

	/**
	 * IDE-1409 New ports do not show up for workspace components on chalkboard
	 */
	@Test
	public void checkUpdatedWorkspaceComponent() {
		final String wkspComponentName = "testWorkspaceComponent";
		final String usesPortName = "usesPort";
		final String providesPortName = "providesPort";

		// create test Component in workspace and generate
		ComponentUtils.createComponentProject(bot, wkspComponentName, "Python");
		SWTBotEditor designEditor = gefBot.editorByTitle(wkspComponentName);
		StandardTestActions.generateProject(bot, designEditor);

		// Bring the component into the chalkboard and confirm there is no provides or uses port
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, wkspComponentName, 0, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, wkspComponentName, ComponentState.STOPPED);
		Assert.assertNull("Component should not have any Uses ports", DiagramTestUtils.getDiagramUsesPort(editor, wkspComponentName));
		Assert.assertNull("Component should not have any Provides ports", DiagramTestUtils.getDiagramProvidesPort(editor, wkspComponentName));

		// Remove the component
		SWTBotGefEditPart componentEditPart = editor.getEditPart(wkspComponentName);
		DiagramTestUtils.releaseFromDiagram(editor, componentEditPart);
		editor.close();

		// Edit the component and add a new provides and uses port.
		designEditor.show();
		ComponentUtils.addComponentPort(designEditor.bot(), providesPortName, PortDirection.IN);
		ComponentUtils.addComponentPort(designEditor.bot(), usesPortName, PortDirection.OUT);

		// Regenerate
		StandardTestActions.generateProject(bot, designEditor);

		// Bring the new component into the Chalkboard
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, wkspComponentName, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, wkspComponentName);
		DiagramTestUtils.waitForComponentState(bot, editor, wkspComponentName, ComponentState.STOPPED);

		// Check that the new ports are now displayed
		SWTBotGefEditPart usesPort = DiagramTestUtils.getDiagramUsesPort(editor, wkspComponentName);
		SWTBotGefEditPart providesPort = DiagramTestUtils.getDiagramProvidesPort(editor, wkspComponentName);
		Assert.assertNotNull("Uses port not found", usesPort);
		Assert.assertNotNull("Provides port not found", providesPort);
	}

	/**
	 * IDE-976 Make sure devices are filtered out of Palette's Workspace compartment
	 */
	@Test
	public void checkNoWorkspaceDevices() {
		// create test Device in workspace
		final String wkspDeviceName = "testDeviceInWorkspace";
		DeviceUtils.createDeviceProject(bot, wkspDeviceName, "Python");

		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		assertFalse("Workspace Device wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspDeviceName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspDeviceName);
	}

	/**
	 * IDE-976 Make sure services are filtered out of Palette's Workspace compartment
	 */
	@Test
	public void checkNoWorkspaceServices() {
		// create test Service in workspace
		final String wkspServiceName = "testServiceInWorkspace";
		ServiceUtils.createServiceProject(bot, wkspServiceName, "IDL:BULKIO/dataDouble:1.0", "Python");

		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		assertFalse("Workspace Service wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspServiceName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspServiceName);
	}

	/**
	 * IDE-976 Make sure softpackages are filtered out of Palette's Workspace compartment
	 */
	@Test
	public void checkNoWorkspaceSoftpackages() {
		// create test Softpackage in workspace
		final String wkspSftpkgName = "testSftpkgInWorkspace";
		SharedLibraryUtils.createSharedLibraryProject(bot, wkspSftpkgName, "C++ Library");

		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		assertFalse("Workspace Softpackage wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspSftpkgName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspSftpkgName);
	}

	private static boolean isToolInPalette(SWTBotGefEditor editor, String toolName) {
		try {
			editor.activateTool(toolName);
			return true;
		} catch (WidgetNotFoundException ex) {
			return false;
		}
	}

}
