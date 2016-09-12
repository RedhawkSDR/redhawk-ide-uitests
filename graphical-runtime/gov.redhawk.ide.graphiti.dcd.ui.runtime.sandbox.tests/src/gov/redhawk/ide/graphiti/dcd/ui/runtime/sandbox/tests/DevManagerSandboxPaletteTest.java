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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPaletteTest;
import gov.redhawk.ide.graphiti.ui.runtime.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.runtime.tests.util.FilterInfo;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.PaletteUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class DevManagerSandboxPaletteTest extends AbstractPaletteTest {

	private static final String NS_DEV = "name.space.device";
	private static final String SIM_RX_DIGITIZER = "sim_RX_DIGITIZER";

	@Override
	protected RHBotGefEditor launchDiagram() {
		return DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
	}

	@Override
	public void after() throws CoreException {
		ScaExplorerTestUtils.terminate(gefBot, AbstractDeviceManagerSandboxTest.CHALKBOARD_PARENT_PATH, AbstractDeviceManagerSandboxTest.DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilSandboxDeviceManagerEmpty(gefBot, AbstractDeviceManagerSandboxTest.CHALKBOARD_PARENT_PATH, AbstractDeviceManagerSandboxTest.DEVICE_MANAGER);
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	@Override
	protected ComponentDescription[] getComponentsToFilter() {
		return new ComponentDescription[] {
			new ComponentDescription(AbstractDeviceManagerSandboxTest.GPP, null, null),
			new ComponentDescription(AbstractDeviceManagerSandboxTest.DEVICE_STUB, null, null),
			new ComponentDescription(NS_DEV, null, null)
		};
	}

	@Override
	protected FilterInfo[] getFilterInfo() {
		return new FilterInfo[] {
			new FilterInfo("g", true, false, false),
			new FilterInfo("sh", false, false, false),
			new FilterInfo("d", false, true, true),
			new FilterInfo("dE", false, true, true),
			new FilterInfo(".", false, false, true),
			new FilterInfo(".sP", false, false, true),
			new FilterInfo("", true, true, true)
		};
	}

	@Override
	protected ComponentDescription getMultipleImplComponent() {
		return new ComponentDescription(SIM_RX_DIGITIZER, null, null);
	}

	/**
	 * IDE-1475 DCD diagram palette doesn't refresh with SDR root
	 */
	@Test
	public void paletteRefreshTestDevice() {
		final String DEVICE_NAME = "refreshTestDevice";
		DeviceUtils.createDeviceProject(bot, DEVICE_NAME, "Python");
		refreshTest(DEVICE_NAME);
	}

	/**
	 * IDE-1475 DCD diagram palette doesn't refresh with SDR root
	 */
	@Test
	public void paletteRefreshTestService() {
		final String SERVICE_NAME = "refreshTestService";
		ServiceUtils.createServiceProject(bot, SERVICE_NAME, "IDL:BULKIO/dataDouble:1.0", "Python");
		refreshTest(SERVICE_NAME);
	}

	private void refreshTest(final String projectName) {
		SWTBotEditor designEditor = gefBot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, designEditor);

		final RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		Assert.assertFalse(String.format("'%s' resource already present in the palette", projectName), PaletteUtils.toolIsPresent(editor, projectName));

		StandardTestActions.exportProject(projectName, bot);

		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return PaletteUtils.toolIsPresent(editor, projectName);
			}

			@Override
			public String getFailureMessage() {
				return String.format("Palette did not refresh to display '%s' resource", projectName);
			}
		}, 10000);
	}

}
