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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

public class DevMgrServiceSyncTest extends AbstractDevMgrSandboxSyncTest {

	@Override
	protected String getType() {
		return "service";
	}

	@Override
	protected String getResourceId() {
		return SERVICE_STUB;
	}

	@Override
	protected String getResourceLaunchId() {
		return SERVICE_STUB_1;
	}

	@Override
	protected String getSecondResourceLaunchId() {
		return SERVICE_STUB_2;
	}
}
