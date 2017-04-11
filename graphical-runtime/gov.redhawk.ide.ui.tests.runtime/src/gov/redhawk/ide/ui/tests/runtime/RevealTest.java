/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import CF.ExecutableDevice;
import CF.ExecutableDeviceHelper;
import CF.ExecutableDevicePOATie;
import CF.InvalidObjectReference;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.ui.tests.runtime.stubs.AnalogDevice;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.sca.util.OrbSession;

public class RevealTest extends UITest {

	@BeforeClass
	public static void createSession() throws Exception {
		session = OrbSession.createSession(RevealTest.class.getCanonicalName());
		session.getPOA();
	}

	@AfterClass
	public static void disposeSession() throws Exception {
		if (session != null) {
			session.dispose();
			session = null;
		}
	}

	private ExecutableDevice ref;
	private SWTBotView explorerView;
	private SWTBot viewBot;
	private SWTBotTree explorerTree;
	private LocalSca localSca;
	private LocalScaDeviceManager devMgr;
	private static OrbSession session;

	/**
	 * IDE-803 Tests that a device launched in the sandbox is revealed in the explorer view's tree.
	 * @throws Exception
	 */
	@Test
	public void revealLaunchedDevice() throws Exception {
		explorerView = ViewUtils.getExplorerView(bot);
		explorerView.show();
		viewBot = explorerView.bot();
		explorerTree = viewBot.tree();

		localSca = ScaDebugPlugin.getInstance().getLocalSca(null);
		explorerTree.collapseNode("Sandbox");
		devMgr = localSca.getSandboxDeviceManager();

		AnalogDevice stubDevice = new AnalogDevice();
		ref = ExecutableDeviceHelper.narrow(session.getPOA().servant_to_reference(new ExecutableDevicePOATie(stubDevice)));
		devMgr.registerDevice(ref);

		devMgr.fetchDevices(new NullProgressMonitor(), RefreshDepth.NONE);
		ScaDevice< ? > device = devMgr.getDevice("analogDevice");
		device.refresh(null, RefreshDepth.SELF);
		viewBot.sleep(500);

		viewBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem treeItem = explorerTree.getTreeItem("Sandbox");
				if (!treeItem.isExpanded()) {
					return false;
				}
				treeItem = treeItem.getNode("Device Manager");
				if (!treeItem.isExpanded()) {
					return false;
				}
				treeItem.getNode("analogDevice");
				return true;
			}

			@Override
			public String getFailureMessage() {
				return "The launched device was not automatically revealed in the tree";
			}
		});
	}

	@After
	public void unregisterDevice() throws Exception {
		if (ref != null) {
			try {
				devMgr.unregisterDevice(ref);
			} catch (InvalidObjectReference e) {
				// PASS
			}
			devMgr.fetchDevices(new NullProgressMonitor(), null);
			viewBot.sleep(500);
		}
	}

}
