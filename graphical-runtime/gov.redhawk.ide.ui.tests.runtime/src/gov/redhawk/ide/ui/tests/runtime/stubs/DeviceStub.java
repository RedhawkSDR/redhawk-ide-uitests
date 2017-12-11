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
package gov.redhawk.ide.ui.tests.runtime.stubs;

import org.omg.CORBA.Object;

import CF.AggregateDevice;
import CF.DataType;
import CF.DeviceOperations;
import CF.PropertiesHolder;
import CF.UnknownProperties;
import CF.DevicePackage.AdminType;
import CF.DevicePackage.InsufficientCapacity;
import CF.DevicePackage.InvalidCapacity;
import CF.DevicePackage.InvalidState;
import CF.DevicePackage.OperationalType;
import CF.DevicePackage.UsageType;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortSetPackage.PortInfoType;
import CF.PortSupplierPackage.UnknownPort;
import CF.ResourcePackage.StartError;
import CF.ResourcePackage.StopError;
import CF.TestableObjectPackage.UnknownTest;

public class DeviceStub extends LoggingStub implements DeviceOperations {

	private String id;
	private String label;
	private boolean started = false;

	public DeviceStub(String id, String label) {
		this.id = id;
		this.label = label;
	}

	@Override
	public UsageType usageState() {
		return UsageType.ACTIVE;
	}

	@Override
	public AdminType adminState() {
		return AdminType.UNLOCKED;
	}

	@Override
	public void adminState(AdminType newAdminState) {
	}

	@Override
	public OperationalType operationalState() {
		return OperationalType.ENABLED;
	}

	@Override
	public String label() {
		return label;
	}

	@Override
	public AggregateDevice compositeDevice() {
		return null;
	}

	@Override
	public boolean allocateCapacity(DataType[] capacities) throws InvalidCapacity, InvalidState, InsufficientCapacity {
		return false;
	}

	@Override
	public void deallocateCapacity(DataType[] capacities) throws InvalidCapacity, InvalidState {
	}

	@Override
	public String identifier() {
		return id;
	}

	@Override
	public boolean started() {
		return started;
	}

	@Override
	public String softwareProfile() {
		return null;
	}

	@Override
	public void start() throws StartError {
		this.started = true;
	}

	@Override
	public void stop() throws StopError {
		this.started = false;
	}

	@Override
	public void initialize() throws InitializeError {
	}

	@Override
	public void releaseObject() throws ReleaseError {
	}

	@Override
	public void runTest(int testid, PropertiesHolder testValues) throws UnknownTest, UnknownProperties {
	}

	@Override
	public PortInfoType[] getPortSet() {
		return new PortInfoType[0];
	}

	@Override
	public Object getPort(String name) throws UnknownPort {
		throw new UnknownPort();
	}
}
