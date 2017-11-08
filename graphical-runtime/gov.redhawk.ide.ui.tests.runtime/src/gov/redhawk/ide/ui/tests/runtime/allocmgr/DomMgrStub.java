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

import CF.AllocationManager;
import CF.Application;
import CF.ApplicationFactory;
import CF.ConnectionManager;
import CF.DataType;
import CF.Device;
import CF.DeviceAssignmentType;
import CF.DeviceManager;
import CF.DomainManager;
import CF.DomainManagerOperations;
import CF.EventChannelManager;
import CF.FileManager;
import CF.InvalidFileName;
import CF.InvalidObjectReference;
import CF.InvalidProfile;
import CF.ApplicationFactoryPackage.CreateApplicationError;
import CF.ApplicationFactoryPackage.CreateApplicationInsufficientCapacityError;
import CF.ApplicationFactoryPackage.CreateApplicationRequestError;
import CF.ApplicationFactoryPackage.InvalidInitConfiguration;
import CF.DomainManagerPackage.AlreadyConnected;
import CF.DomainManagerPackage.ApplicationAlreadyInstalled;
import CF.DomainManagerPackage.ApplicationInstallationError;
import CF.DomainManagerPackage.ApplicationUninstallationError;
import CF.DomainManagerPackage.DeviceManagerNotRegistered;
import CF.DomainManagerPackage.InvalidEventChannelName;
import CF.DomainManagerPackage.InvalidIdentifier;
import CF.DomainManagerPackage.NotConnected;
import CF.DomainManagerPackage.RegisterError;
import CF.DomainManagerPackage.UnregisterError;

public class DomMgrStub extends LoggingStub implements DomainManagerOperations {

	private String name;
	private DeviceManager[] devMgrs;
	private AllocationManager allocMgr;

	public DomMgrStub(String name) {
		this.name = name;
	}

	@Override
	public String domainManagerProfile() {
		return null;
	}

	public void stub_setDeviceManagers(DeviceManager[] devMgrs) {
		this.devMgrs = devMgrs;
	}

	@Override
	public DeviceManager[] deviceManagers() {
		return devMgrs;
	}

	@Override
	public Application[] applications() {
		return null;
	}

	@Override
	public ApplicationFactory[] applicationFactories() {
		return null;
	}

	@Override
	public FileManager fileMgr() {
		return null;
	}

	public void stub_setAllocationMgr(AllocationManager allocMgr) {
		this.allocMgr = allocMgr;
	}

	@Override
	public AllocationManager allocationMgr() {
		return allocMgr;
	}

	@Override
	public ConnectionManager connectionMgr() {
		return null;
	}

	@Override
	public EventChannelManager eventChannelMgr() {
		return null;
	}

	@Override
	public String identifier() {
		return "DCE:9ae444e0-0bfd-4e3d-b16c-1cffb3dc0f46";
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public DomainManager[] remoteDomainManagers() {
		return null;
	}

	@Override
	public void registerDevice(Device registeringDevice, DeviceManager registeredDeviceMgr)
		throws InvalidObjectReference, InvalidProfile, DeviceManagerNotRegistered, RegisterError {
	}

	@Override
	public void registerDeviceManager(DeviceManager deviceMgr) throws InvalidObjectReference, InvalidProfile, RegisterError {
	}

	@Override
	public void unregisterDeviceManager(DeviceManager deviceMgr) throws InvalidObjectReference, UnregisterError {
	}

	@Override
	public void unregisterDevice(Device unregisteringDevice) throws InvalidObjectReference, UnregisterError {
	}

	@Override
	public Application createApplication(String profileFileName, String name, DataType[] initConfiguration, DeviceAssignmentType[] deviceAssignments)
		throws InvalidProfile, InvalidFileName, ApplicationInstallationError, CreateApplicationError, CreateApplicationRequestError,
		CreateApplicationInsufficientCapacityError, InvalidInitConfiguration {
		return null;
	}

	@Override
	public void installApplication(String profileFileName) throws InvalidProfile, InvalidFileName, ApplicationInstallationError, ApplicationAlreadyInstalled {
	}

	@Override
	public void uninstallApplication(String applicationId) throws InvalidIdentifier, ApplicationUninstallationError {
	}

	@Override
	public void registerService(Object registeringService, DeviceManager registeredDeviceMgr, String name)
		throws InvalidObjectReference, DeviceManagerNotRegistered, RegisterError {
	}

	@Override
	public void unregisterService(Object unregisteringService, String name) throws InvalidObjectReference, UnregisterError {
	}

	@Override
	public void registerWithEventChannel(Object registeringObject, String registeringId, String eventChannelName)
		throws InvalidObjectReference, InvalidEventChannelName, AlreadyConnected {
	}

	@Override
	public void unregisterFromEventChannel(String unregisteringId, String eventChannelName) throws InvalidEventChannelName, NotConnected {
	}

	@Override
	public void registerRemoteDomainManager(DomainManager registeringDomainManager) throws InvalidObjectReference, RegisterError {
	}

	@Override
	public void unregisterRemoteDomainManager(DomainManager unregisteringDomainManager) throws InvalidObjectReference, UnregisterError {
	}

}
