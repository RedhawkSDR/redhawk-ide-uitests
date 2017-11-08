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

import org.omg.CORBA.Object;

import CF.Device;
import CF.DeviceManagerOperations;
import CF.DomainManager;
import CF.FileSystem;
import CF.InvalidObjectReference;
import CF.DeviceManagerPackage.ServiceType;
import CF.PortSetPackage.PortInfoType;
import CF.PortSupplierPackage.UnknownPort;
import mil.jpeojtrs.sca.util.DceUuidUtil;

public class DevMgrStub extends PropertyEmitterStub implements DeviceManagerOperations {

	private String id;
	private String label;
	private Device[] devices;

	public DevMgrStub(String label) {
		this.id = DceUuidUtil.createDceUUID();
		this.label = label;
	}

	@Override
	public String deviceConfigurationProfile() {
		return null;
	}

	@Override
	public FileSystem fileSys() {
		return null;
	}

	@Override
	public String identifier() {
		return id;
	}

	@Override
	public String label() {
		return label;
	}

	@Override
	public DomainManager domMgr() {
		return null;
	}

	public void stub_setRegisteredDevices(Device[] devices) {
		this.devices = devices;
	}

	@Override
	public Device[] registeredDevices() {
		return devices;
	}

	@Override
	public ServiceType[] registeredServices() {
		return null;
	}

	@Override
	public void registerDevice(Device registeringDevice) throws InvalidObjectReference {
	}

	@Override
	public void unregisterDevice(Device registeredDevice) throws InvalidObjectReference {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void registerService(Object registeringService, String name) throws InvalidObjectReference {
	}

	@Override
	public void unregisterService(Object unregisteringService, String name) throws InvalidObjectReference {
	}

	@Override
	public String getComponentImplementationId(String componentInstantiationId) {
		return null;
	}

	@Override
	public PortInfoType[] getPortSet() {
		return null;
	}

	@Override
	public Object getPort(String name) throws UnknownPort {
		return null;
	}
}
