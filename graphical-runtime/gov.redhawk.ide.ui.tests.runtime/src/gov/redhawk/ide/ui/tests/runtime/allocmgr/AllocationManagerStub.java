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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import CF.AllocationManagerOperations;
import CF.AllocationStatusIteratorHolder;
import CF.DeviceLocationIteratorHolder;
import CF.DomainManager;
import CF.AllocationManagerPackage.AllocationError;
import CF.AllocationManagerPackage.AllocationRequestType;
import CF.AllocationManagerPackage.AllocationResponseType;
import CF.AllocationManagerPackage.AllocationScopeType;
import CF.AllocationManagerPackage.AllocationStatusSequenceHolder;
import CF.AllocationManagerPackage.AllocationStatusType;
import CF.AllocationManagerPackage.DeviceLocationSequenceHolder;
import CF.AllocationManagerPackage.DeviceLocationType;
import CF.AllocationManagerPackage.DeviceScopeType;
import CF.AllocationManagerPackage.InvalidAllocationId;

public class AllocationManagerStub implements AllocationManagerOperations {

	private AllocationStatusType[] statuses = new AllocationStatusType[0];

	@Override
	public DeviceLocationType[] allDevices() {
		return new DeviceLocationType[0];
	}

	@Override
	public DeviceLocationType[] authorizedDevices() {
		return new DeviceLocationType[0];
	}

	@Override
	public DeviceLocationType[] localDevices() {
		return new DeviceLocationType[0];
	}

	@Override
	public void listDevices(DeviceScopeType deviceScope, int count, DeviceLocationSequenceHolder devices, DeviceLocationIteratorHolder dl) {
	}

	@Override
	public DomainManager domainMgr() {
		return null;
	}

	public void stub_setAllocationStatuses(AllocationStatusType[] statuses) {
		this.statuses = statuses;
	}

	@Override
	public AllocationResponseType[] allocate(AllocationRequestType[] requests) throws AllocationError {
		return null;
	}

	@Override
	public AllocationResponseType[] allocateLocal(AllocationRequestType[] requests, String domainName) throws AllocationError {
		return null;
	}

	@Override
	public void deallocate(String[] allocationIDs) throws InvalidAllocationId {
		Set<String> allocationIDSet = new HashSet<>();
		Collections.addAll(allocationIDSet, allocationIDs);
		List<AllocationStatusType> newStatuses = new ArrayList<>(statuses.length);
		for (AllocationStatusType status : statuses) {
			if (!allocationIDSet.contains(status.allocationID)) {
				newStatuses.add(status);
			}
		}
		statuses = newStatuses.toArray(new AllocationStatusType[newStatuses.size()]);
	}

	@Override
	public AllocationStatusType[] allocations(String[] allocationIDs) throws InvalidAllocationId {
		return statuses;
	}

	@Override
	public AllocationStatusType[] localAllocations(String[] allocationIDs) throws InvalidAllocationId {
		return null;
	}

	@Override
	public void listAllocations(AllocationScopeType allocScope, int howMany, AllocationStatusSequenceHolder allocs, AllocationStatusIteratorHolder ai) {
	}

}
