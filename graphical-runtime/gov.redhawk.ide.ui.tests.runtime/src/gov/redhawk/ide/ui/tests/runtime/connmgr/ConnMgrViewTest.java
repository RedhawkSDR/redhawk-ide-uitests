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
package gov.redhawk.ide.ui.tests.runtime.connmgr;

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
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

import CF.ConnectionManager;
import CF.ConnectionManagerPOATie;
import CF.DomainManager;
import CF.DomainManagerPOATie;
import CF.ConnectionManagerPackage.ConnectionStatusType;
import CF.ConnectionManagerPackage.EndpointStatusType;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.ui.tests.runtime.stubs.DomMgrStub;
import gov.redhawk.model.sca.DomainConnectionState;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.util.OrbSession;

public class ConnMgrViewTest extends UITest {

	private static final String DOMAIN = ConnMgrViewTest.class.getSimpleName();
	private static final String FM_RDS_1 = "ConnMgrNode:FmRdsSimulator_1";
	private static final String FM_RDS_OUT_PORT = "dataFloat_out";
	private static final String WAVE_IN_PORT = "dataFloat";
	private static final String WAVE_OUT_PORT = "dataFloat_out";

	private OrbSession session;
	private ConnectionManagerStub connMgrStub;

	@Before
	public void setupDomain() throws CoreException {
		session = OrbSession.createSession(ConnMgrViewTest.class.getSimpleName());
		ORB orb = session.getOrb();
		POA poa = session.getPOA();

		// Create a fake dom mgr and conn mgr within the IDE's model
		ScaDomainManager scaDomMgr = ScaFactory.eINSTANCE.createScaDomainManager();
		DomMgrStub domMgrStub = new DomMgrStub(DOMAIN);
		DomainManager domMgr = new DomainManagerPOATie(domMgrStub, poa)._this(orb);
		scaDomMgr.setDataProvidersEnabled(false);
		scaDomMgr.setLocalName(DOMAIN);
		scaDomMgr.setName(DOMAIN);
		scaDomMgr.setObj(domMgr);
		scaDomMgr.setState(DomainConnectionState.CONNECTED);

		connMgrStub = new ConnectionManagerStub();
		ConnectionManager connMgr = new ConnectionManagerPOATie(connMgrStub, poa)._this(orb);
		domMgrStub.stub_setConnectionMgr(connMgr);

		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaModelCommand.execute(registry, () -> {
			registry.getDomains().add(scaDomMgr);
		});

		// Give the connection manager some fake entries
		List<ConnectionStatusType> statuses = new ArrayList<>();
		EndpointStatusType sourceEndpoint = new EndpointStatusType(domMgr, FM_RDS_OUT_PORT, "IDL:BULKIO/internal/UsesPortStatisticsProviderExt:1.0", FM_RDS_1);
		EndpointStatusType targetEndpoint = new EndpointStatusType(domMgr, WAVE_IN_PORT, "IDL:BULKIO/internal/dataFloatExt:1.0",
			"DCE:49d81b1f-8275-4be3-b7c8-86b87e965c38:wave1_1");
		String connectionID = "connection_abc";
		String requestID = "request_abc";
		boolean connected = true;
		statuses.add(new ConnectionStatusType(targetEndpoint, sourceEndpoint, connectionID, requestID, requestID + "_" + connectionID, connected));

		sourceEndpoint = new EndpointStatusType(domMgr, WAVE_OUT_PORT, "IDL:BULKIO/internal/UsesPortStatisticsProviderExt:1.0",
			"DCE:49d81b1f-8275-4be3-b7c8-86b87e965c38:wave1_1");
		targetEndpoint = new EndpointStatusType(domMgr, WAVE_IN_PORT, "IDL:BULKIO/internal/dataFloatExt:1.0",
			"DCE:49d81b1f-8275-4be3-b7c8-86b87e965c38:wave2_1");
		connectionID = "connection_def";
		requestID = "request_def";
		connected = true;
		statuses.add(new ConnectionStatusType(targetEndpoint, sourceEndpoint, connectionID, requestID, requestID + "_" + connectionID, connected));

		connMgrStub.stub_setConnectionStatuses(statuses.toArray(new ConnectionStatusType[statuses.size()]));

		StandardTestActions.setRefreshInterval(1000);
	}

	@After
	public void teardownDomain() {
		StandardTestActions.resetRefreshInterval();

		// Close the view
		ViewUtils.getConnectionManagerView(bot).close();

		// Remove the fake domain from the model
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaModelCommand.execute(registry, () -> {
			ScaDomainManager domMgr = registry.findDomain(DOMAIN);
			if (domMgr != null) {
				registry.getDomains().remove(domMgr);
			}
		});

		// Dispose CORBA objects
		connMgrStub = null;
		if (session != null) {
			session.dispose();
			session = null;
		}
	}

	@Test
	public void connMgrView() {
		// Open the allocation manager, find the tree
		SWTBotTreeItem domMgr = ScaExplorerTestUtils.getDomain(bot, DOMAIN);
		domMgr.contextMenu().menu("Connection Manager").click();
		SWTBotView view = ViewUtils.getConnectionManagerView(bot);
		SWTBotTree tree = view.bot().tree();

		// Verify the tree's contents
		String[][] table = new String[][] { //
			new String[] { "connection_abc", "request_abc", "", FM_RDS_1, FM_RDS_OUT_PORT, "wave1_1", WAVE_IN_PORT }, //
			new String[] { "connection_def", "request_def", "", "wave1_1", WAVE_OUT_PORT, "wave2_1", WAVE_IN_PORT } //
		};
		bot.waitUntil(Conditions.treeHasRows(tree, table.length));
		for (int row = 0; row < table.length; row++) {
			for (int column = 0; column < 5; column++) {
				Assert.assertEquals("Failed row " + row + " column " + column, table[row][column], tree.cell(row, column));
			}
		}

		// Remove a connection status in the conn mgr; when it gets polled the row should disappear
		ConnectionStatusType[] statuses = connMgrStub.connections();
		connMgrStub.stub_setConnectionStatuses(Arrays.copyOf(statuses, statuses.length - 1));
		bot.waitUntil(Conditions.treeHasRows(tree, statuses.length - 1));
	}

	@Test
	public void disconnect() {
		// Open the connection manager, find the tree
		SWTBotTreeItem domMgr = ScaExplorerTestUtils.getDomain(bot, DOMAIN);
		domMgr.contextMenu().menu("Connection Manager").click();
		SWTBotView view = ViewUtils.getConnectionManagerView(bot);
		SWTBotTree tree = view.bot().tree();
		bot.waitUntil(Conditions.treeHasRows(tree, 2));

		// Use the context menu to disconnect one of the connections
		tree.select(0).contextMenu().menu("Disconnect").click();
		bot.waitUntil(Conditions.treeHasRows(tree, 1));
	}

	@Test
	public void propertiesView() {
		// Open the allocation manager, find the tree
		SWTBotTreeItem domMgr = ScaExplorerTestUtils.getDomain(bot, DOMAIN);
		domMgr.contextMenu().menu("Connection Manager").click();
		SWTBotView view = ViewUtils.getConnectionManagerView(bot);
		SWTBotTree tree = view.bot().tree();
		bot.waitUntil(Conditions.treeHasRows(tree, 2));

		// Verify we can open the properties view (via double-click or toolbar button)
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).close();
		view.toolbarButton("Show Details").click();
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID).close();
		tree.getTreeItem("connection_abc").doubleClick();
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, "Advanced").tree();

		// Check the advanced props
		bot.waitUntil(Conditions.treeHasRows(propTree, 12));
		for (String item : new String[] { "Connected", "Connection ID", "Connection Record ID", "Requester ID", "Source Entity Name", "Source IOR",
			"Source Port Name", "Source Repo ID", "Target Entity Name", "Target IOR", "Target Port Name", "Target Repo ID" }) {
			propTree.getTreeItem(item);
		}
	}
}
