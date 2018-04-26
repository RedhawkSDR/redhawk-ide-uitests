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
package gov.redhawk.ide.namebrowser.ui.runtime.tests;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IWorkbenchPart2;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.sca.util.OrbSession;

public class CorbaNameBrowserTest extends UIRuntimeTest {

	private String domainName;
	private static final String DEVICE_MANAGER = "DevMgr_localhost";

	@BeforeClass
	public static void disaableConsoleAutoShow() {
		ConsoleUtils.disableAutoShowConsole();
	}

	@Test
	public void namespacedWaveformTest() {
		final String waveformName = "a.b.c.d.waveform";

		// Launch the domain
		domainName = "CorbaNameBrowserTest_" + (int) (1000.0 * Math.random());
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);

		// Launch the namespaced waveform
		ScaExplorerTestUtils.launchWaveformFromDomain(bot, domainName, waveformName);
		String fullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, new String[] { domainName, "Waveforms" }, waveformName);

		// Find the namespaced waveform node & a child component
		SWTBotView view = openAndConnect();
		fullName = fullName.replace(".", "\\.");
		List<String> pathList = Arrays.asList("127.0.0.1", domainName, fullName + "_1", "HardLimit_1");
		StandardTestActions.waitForTreeItemToAppear(bot, view.bot().tree(), pathList);
	}

	@Test
	public void listenToEventChannel() {
		// Launch the domain
		domainName = "CorbaNameBrowserTest_" + (int) (1000.0 * Math.random());
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);

		// Listen to the event channel
		SWTBotView view = openAndConnect();
		List<String> pathList = Arrays.asList("127.0.0.1", domainName, "ODM_Channel");
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(bot, view.bot().tree(), pathList);
		treeItem.contextMenu().menu("Listen to Event Channel").click();

		// Wait for the event view, then close it
		SWTBotView eventView = ViewUtils.getEventView(bot);
		try {
			Assert.assertEquals("ODM_Channel", ((IWorkbenchPart2) eventView.getViewReference().getPart(false)).getPartName());
		} finally {
			eventView.close();
		}
	}

	@After
	public void after() throws CoreException {
		SWTBotView view = ViewUtils.getCorbaNameBrowserView(bot);
		if (view.bot().tree().hasItems()) {
			view.bot().tree().select(0).contextMenu("Disconnect").click();
		}
		ViewUtils.getExplorerView(bot).show();

		if (domainName != null) {
			String tmpName = domainName;
			domainName = null;
			ScaExplorerTestUtils.deleteDomainInstance(bot, tmpName);
			StandardTestActions.cleanUpLaunches();
		}

		super.after();
	}

	/**
	 * IDE-1752 Ensure the CORBA name browser displays both id and kind components of a {@link NameComponent}.
	 */
	@Test
	public void idWithKind() throws InvalidName {
		OrbSession session = OrbSession.createSession();
		NamingContext namingContext = null;
		NameComponent[] name = new NameComponent[] { new NameComponent("myid", "mykind") };
		try {
			org.omg.CORBA.Object obj = session.getOrb().string_to_object("corbaname::127.0.0.1");
			namingContext = NamingContextHelper.narrow(obj);
			try {
				namingContext.bind(name, obj);
			} catch (NotFound | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | AlreadyBound e) {
				Assert.fail(e.toString());
			}

			SWTBotView view = openAndConnect();
			StandardTestActions.waitForTreeItemToAppear(bot, view.bot().tree(), Arrays.asList("127.0.0.1", "myid.mykind"));
		} finally {
			if (namingContext != null) {
				try {
					namingContext.unbind(name);
				} catch (NotFound | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName e) {
					e.printStackTrace();
				}
			}
			session.dispose();
		}
	}

	private SWTBotView openAndConnect() {
		SWTBotView view = ViewUtils.getCorbaNameBrowserView(bot);
		view.show();
		view.bot().comboBox().setText("corbaname::127.0.0.1");
		view.bot().buttonWithTooltip("Connect to the specified host").click();
		return view;
	}
}
