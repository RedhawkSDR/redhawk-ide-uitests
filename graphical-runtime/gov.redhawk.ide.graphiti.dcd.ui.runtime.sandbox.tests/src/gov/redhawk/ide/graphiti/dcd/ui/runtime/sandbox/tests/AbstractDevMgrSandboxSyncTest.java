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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * 
 */
public abstract class AbstractDevMgrSandboxSyncTest extends AbstractDeviceManagerSandboxTest {

	protected RHBotGefEditor editor; // SUPPRESS CHECKSTYLE shared variable

	@Override
	public void before() throws Exception {
		super.before();
		editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
	}

	/**
	 * @return either "device" or "service"
	 */
	protected abstract String getType();

	protected abstract String getResourceId();

	protected abstract String getResourceLaunchId();

	protected abstract String getSecondResourceLaunchId();

	/**
	 * IDE-1879 - Adds resource via diagram palette, then removes with the delete hot key
	 */
	@Test
	public void addRemoveWithHotKey_Diagram() {
		// Launch resource
		launchResourceInDiagram(getResourceId(), getResourceLaunchId());

		// Press delete key
		editor.getEditPart(getResourceId()).select();
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.DELETE);

		// Wait until resource not present in REDHAWK Explorer & Diagram
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, SANDBOX_DEVMGR_PATH, getResourceLaunchId());
		Assert.assertNull(editor.getEditPart(getResourceId()));
	}

	/**
	 * IDE-1879 - Adds resource via REDHAWK Explorer context menu, then removes with the delete hot key
	 */
	@Test
	public void addRemoveWithHotKey_Explorer() {
		// Launch resources
		launchResourceInExplorer(bot, getResourceId(), getResourceLaunchId());

		// Press delete key
		SWTBotTreeItem item = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, SANDBOX_DEVMGR_PATH, getResourceLaunchId());
		item.select().pressShortcut(Keystrokes.DELETE);

		// Verify diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, getResourceLaunchId());
	}

	/**
	 * IDE-1037, IDE-1119
	 * Adds, then terminates a service via dev manager diagram.
	 * Verify it's no longer present in the explorer view or diagram.
	 */
	@Test
	public void addTerminateInDiagram() {
		// Launch resource
		launchResourceInDiagram(getResourceId(), getResourceLaunchId());

		// Terminate
		DiagramTestUtils.terminateFromDiagram(editor, editor.getEditPart(getResourceId()));

		// Wait until resource not present in REDHAWK Explorer & Diagram
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, SANDBOX_DEVMGR_PATH, getResourceLaunchId());
		Assert.assertNull(editor.getEditPart(getResourceId()));
	}

	/**
	 * IDE-1882
	 * Add two resources with the REDHAWK Explorer, confirming they have unique names
	 * 
	 * IDE-1119, IDE-1037
	 * Adds, then terminates a resource via the REDHAWK Explorer. Verify it's no longer present in the diagram.
	 */
	@Test
	public void addTerminateInExplorerView() {
		// Launch resources
		launchResourceInExplorer(bot, getResourceId(), getResourceLaunchId());
		launchResourceInExplorer(bot, getResourceId(), getSecondResourceLaunchId());

		// Terminate service from the explorer view
		ScaExplorerTestUtils.terminate(bot, SANDBOX_DEVMGR_PATH, getResourceLaunchId());
		ScaExplorerTestUtils.terminate(bot, SANDBOX_DEVMGR_PATH, getSecondResourceLaunchId());

		// Verify diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, getResourceLaunchId());
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, getSecondResourceLaunchId());
	}

	/**
	 * IDE-1119, IDE-1880
	 * Adds a resource, then terminates the device manager in the REDHAWK Explorer.
	 * Verify it's no longer present in the diagram.
	 */
	@Test
	public void terminateDeviceManagerInExplorerView() {
		// Launch resource
		launchResourceInExplorer(bot, getResourceId(), getResourceLaunchId());

		// Terminate device manager
		ScaExplorerTestUtils.terminate(bot, SANDBOX_PATH, DEVICE_MANAGER);

		// Verify diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, getResourceLaunchId());
	}

	/**
	 * IDE-1119, IDE-1881
	 * Adds a resource, then shuts down the device manager in the REDHAWK Explorer.
	 * Verify it's no longer present in the diagram.
	 */
	@Test
	public void shutdownDeviceManagerInExplorerView() {
		// Launch device
		launchResourceInExplorer(bot, getResourceId(), getResourceLaunchId());

		// Shutdown device manager
		ScaExplorerTestUtils.shutdown(bot, SANDBOX_PATH, DEVICE_MANAGER);

		// Verify diagram
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, getResourceLaunchId());
	}

	/**
	 * Launch a resource from the palette and wait for it to appear in the "Stopped" state in the diagram and the
	 * explorer view
	 * 
	 * @param editor
	 * @param bot
	 * @param resourceId - Base ID (e.g. "DeviceStub")
	 * @param launchId - Runtime ID (e.g. "DeviceStub_1")
	 */
	protected void launchResourceInDiagram(String resourceId, String launchId) {
		DiagramTestUtils.addFromPaletteToDiagram(editor, resourceId, 0, 0);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, launchId);
		DiagramTestUtils.waitForComponentState(bot, editor, launchId, ComponentState.STOPPED);
	}

	/**
	 * Launch a resource from the explorer view and wait for it to appear in the "Stopped" state in the diagram and the
	 * explorer view
	 * 
	 * @param editor
	 * @param bot
	 * @param type - "device" or "service"
	 * @param resourceId - Base ID (e.g. "DeviceStub")
	 * @param launchId - Runtime ID (e.g. "DeviceStub_1")
	 */
	protected void launchResourceInExplorer(SWTWorkbenchBot bot, String resourceId, String launchId) {
		if ("service".equals(getType())) {
			ScaExplorerTestUtils.launchServiceFromTargetSDR(bot, resourceId, "python");
		} else {
			ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, resourceId, "python");
		}
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, launchId);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, SANDBOX_DEVMGR_PATH, launchId);
	}

}
