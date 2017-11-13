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
package gov.redhawk.ide.ui.tests.runtime.allocmgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StringSeqHelper;
import org.omg.PortableServer.POA;

import CF.AllocationManager;
import CF.AllocationManagerPOATie;
import CF.DataType;
import CF.Device;
import CF.DeviceManager;
import CF.DeviceManagerPOATie;
import CF.DevicePOATie;
import CF.DomainManager;
import CF.DomainManagerPOATie;
import CF.PropertiesHelper;
import CF.AllocationManagerPackage.AllocationStatusType;
import CF.AllocationManagerPackage.InvalidAllocationId;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.DomainConnectionState;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.util.OrbSession;

public class AllocMgrViewTest extends UITest {

	private static final String DOMAIN_1 = AllocMgrViewTest.class.getSimpleName();
	private static final String DOMAIN_2 = AllocMgrViewTest.class.getSimpleName() + "2";
	private static final String DEV_MGR_1 = AllocMgrViewTest.class.getSimpleName() + "_devmgr";
	private static final String DEV_MGR_2 = AllocMgrViewTest.class.getSimpleName() + "_devmgr2";
	private static final String DEV_1 = AllocMgrViewTest.class.getSimpleName() + "_dev";
	private static final String DEV_2 = AllocMgrViewTest.class.getSimpleName() + "_dev2";

	private OrbSession session;
	private AllocationManagerStub allocMgrStub;

	@Before
	public void setupDomain() throws CoreException {
		session = OrbSession.createSession(AllocMgrViewTest.class.getSimpleName());
		ORB orb = session.getOrb();
		POA poa = session.getPOA();

		// Create a fake dom mgr, alloc mgr, dev mgr, device within the IDE's model
		ScaDomainManager scaDomMgr = ScaFactory.eINSTANCE.createScaDomainManager();
		DomMgrStub domMgrStub = new DomMgrStub(DOMAIN_1);
		DomainManager domMgr = new DomainManagerPOATie(domMgrStub, poa)._this(orb);
		scaDomMgr.setDataProvidersEnabled(false);
		scaDomMgr.setLocalName(DOMAIN_1);
		scaDomMgr.setName(DOMAIN_1);
		scaDomMgr.setObj(domMgr);
		scaDomMgr.setState(DomainConnectionState.CONNECTED);

		allocMgrStub = new AllocationManagerStub();
		AllocationManager allocMgr = new AllocationManagerPOATie(allocMgrStub, poa)._this(orb);
		domMgrStub.stub_setAllocationMgr(allocMgr);

		ScaDeviceManager scaDevMgr = ScaFactory.eINSTANCE.createScaDeviceManager();
		DevMgrStub devMgrStub = new DevMgrStub(DEV_MGR_1);
		DeviceManager devMgr = new DeviceManagerPOATie(devMgrStub, poa)._this(orb);
		scaDevMgr.setDataProvidersEnabled(false);
		scaDevMgr.setCorbaObj(devMgr);
		scaDevMgr.fetchLabel(null);
		scaDomMgr.getDeviceManagers().add(scaDevMgr);

		ScaDevice<Device> scaDevice = ScaFactory.eINSTANCE.createScaDevice();
		DeviceStub devStub = new DeviceStub(DEV_1);
		Device dev = new DevicePOATie(devStub, poa)._this(orb);
		scaDevice.setDataProvidersEnabled(false);
		scaDevice.setCorbaObj(dev);
		scaDevice.fetchLabel(null);
		devMgrStub.stub_setRegisteredDevices(new Device[] { dev });

		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaModelCommand.execute(registry, () -> {
			registry.getDomains().add(scaDomMgr);
		});

		// Create a second fake device manager and device which are not in the IDE's model
		DevMgrStub devMgrStub2 = new DevMgrStub(DEV_MGR_2);
		DeviceManager devMgr2 = new DeviceManagerPOATie(devMgrStub2, poa)._this(orb);
		DeviceStub deviceStub2 = new DeviceStub(DEV_2);
		Device dev2 = new DevicePOATie(deviceStub2, poa)._this(orb);

		// Give the allocation manager some fake entries using both domains' objects
		List<AllocationStatusType> statuses = new ArrayList<>();
		Any any1 = orb.create_any();
		any1.insert_double(1.23);
		Any any2 = orb.create_any();
		Any any2a = orb.create_any();
		StringSeqHelper.insert(any2a, new String[] { "a", "b", "c" });
		Any any2b = orb.create_any();
		any2b.insert_double(4.56);
		PropertiesHelper.insert(any2, new DataType[] { new CF.DataType("a", any2a), new CF.DataType("b", any2b) });
		CF.DataType[] abcProps = new CF.DataType[] { //
			new CF.DataType("double", any1),
			new CF.DataType("struct", any2),
		};
		statuses.add(new AllocationStatusType("allocid_abc", DOMAIN_1, abcProps, dev, devMgr, "source_abc"));
		statuses.add(new AllocationStatusType("allocid_def", DOMAIN_2, new DataType[0], dev, devMgr, "source_def"));
		statuses.add(new AllocationStatusType("allocid_ghi", DOMAIN_1, new DataType[0], dev2, devMgr2, "source_ghi"));
		allocMgrStub.stub_setAllocationStatuses(statuses.toArray(new AllocationStatusType[statuses.size()]));

		StandardTestActions.setRefreshInterval(1000);
	}

	@After
	public void teardownDomain() {
		StandardTestActions.resetRefreshInterval();

		// Remove the fake domain from the model
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaModelCommand.execute(registry, () -> {
			ScaDomainManager domMgr = registry.findDomain(DOMAIN_1);
			if (domMgr != null) {
				registry.getDomains().remove(domMgr);
			}
		});

		// Dispose CORBA objects
		allocMgrStub = null;
		if (session != null) {
			session.dispose();
			session = null;
		}
	}

	@Test
	public void allocMgrView() throws InvalidAllocationId {
		// Open the allocation manager, find the tree
		SWTBotTreeItem domMgr = ScaExplorerTestUtils.getDomain(bot, DOMAIN_1);
		domMgr.contextMenu().menu("Allocation Manager").click();
		SWTBotView view = ViewUtils.getAllocationManagerView(bot);
		SWTBotTree tree = view.bot().tree();

		// Verify the tree's contents
		String[][] table = new String[][] { //
			new String[] { "allocid_abc", DOMAIN_1, DEV_1, DEV_MGR_1, "source_abc" }, //
			new String[] { "allocid_def", DOMAIN_2, DEV_1, DEV_MGR_1, "source_def" }, //
			new String[] { "allocid_ghi", DOMAIN_1, DEV_2, DEV_MGR_2, "source_ghi" } //
		};
		bot.waitUntil(Conditions.treeHasRows(tree, table.length));
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 5; column++) {
				bot.waitUntil(new LabelResolvesConditions(tree, row, column));
				Assert.assertEquals("Failed row " + row + " column " + column, table[row][column], tree.cell(row, column));
			}
		}

		// Remove an allocation status in the alloc mgr; when it gets polled the row should disappear
		AllocationStatusType[] statuses = allocMgrStub.allocations(null);
		allocMgrStub.stub_setAllocationStatuses(Arrays.copyOf(statuses, statuses.length - 1));
		bot.waitUntil(Conditions.treeHasRows(tree, statuses.length - 1));
	}

	@Test
	public void deallocate() {
		// Open the allocation manager, find the tree
		SWTBotTreeItem domMgr = ScaExplorerTestUtils.getDomain(bot, DOMAIN_1);
		domMgr.contextMenu().menu("Allocation Manager").click();
		SWTBotView view = ViewUtils.getAllocationManagerView(bot);
		SWTBotTree tree = view.bot().tree();
		bot.waitUntil(Conditions.treeHasRows(tree, 3));

		// Use the context menu to deallocate one of the allocations
		tree.select(0).contextMenu().menu("Deallocate").click();
		bot.waitUntil(Conditions.treeHasRows(tree, 2));
	}

	@Test
	public void propertiesView() {
		// Open the allocation manager, find the tree
		SWTBotTreeItem domMgr = ScaExplorerTestUtils.getDomain(bot, DOMAIN_1);
		domMgr.contextMenu().menu("Allocation Manager").click();
		SWTBotView view = ViewUtils.getAllocationManagerView(bot);
		SWTBotTree tree = view.bot().tree();
		bot.waitUntil(Conditions.treeHasRows(tree, 3));

		// Verify we can open the properties view (via double-click or toolbar button)
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).close();
		view.toolbarButton("Show Details").click();
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).close();
		tree.getTreeItem("allocid_abc").doubleClick();
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, "Properties");

		// Check that some props are there
		bot.waitUntil(Conditions.treeHasRows(propTree, 2));
		propTree.getTreeItem("double");
		propTree.getTreeItem("struct");
	}
}
