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
import java.util.List;

import CF.ConnectionManagerOperations;
import CF.ConnectionStatusIteratorHolder;
import CF.ConnectionManagerPackage.ConnectionStatusSequenceHolder;
import CF.ConnectionManagerPackage.ConnectionStatusType;
import CF.ConnectionManagerPackage.EndpointRequest;
import CF.PortPackage.InvalidPort;

public class ConnectionManagerStub implements ConnectionManagerOperations {

	private ConnectionStatusType[] statuses = new ConnectionStatusType[0];

	@Override
	public String connect(EndpointRequest usesEndpoint, EndpointRequest providesEndpoint, String requesterId, String connectionId) throws InvalidPort {
		return null;
	}

	@Override
	public void disconnect(String connectionRecordId) throws InvalidPort {
		List<ConnectionStatusType> newStatuses = new ArrayList<>(statuses.length - 1);
		for (ConnectionStatusType status : statuses) {
			if (connectionRecordId.equals(status.connectionRecordId)) {
				continue;
			}
			newStatuses.add(status);
		}
		statuses = newStatuses.toArray(new ConnectionStatusType[newStatuses.size()]);
	}

	public void stub_setConnectionStatuses(ConnectionStatusType[] statuses) {
		this.statuses = statuses;
	}

	@Override
	public ConnectionStatusType[] connections() {
		return statuses;
	}

	@Override
	public void listConnections(int howMany, ConnectionStatusSequenceHolder connections, ConnectionStatusIteratorHolder iter) {
	}

}
