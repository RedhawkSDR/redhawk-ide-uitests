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
package gov.redhawk.ide.ui.tests.runtime;

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;

import gov.redhawk.ide.sdr.ui.internal.handlers.LaunchDomainManagerWithOptions;
import gov.redhawk.ide.sdr.ui.util.DebugLevel;
import gov.redhawk.ide.sdr.ui.util.DomainManagerLaunchConfiguration;
import gov.redhawk.ide.swtbot.UIRuntimeTest;

public abstract class AbstractDomainRuntimeTest extends UIRuntimeTest {

	/**
	 * Launches a domain, but doesn't connect to it (i.e. it won't be in the explorer view, just in the console)
	 * @param name
	 * @throws CoreException
	 */
	protected void launchDomainManager(String name) throws CoreException {
		IFileStore store = EFS.getStore(URI.create("sdrdom:///mgr/DomainManager.spd.xml"));
		Assert.assertTrue("The domain manager profile was not found", store.fetchInfo().exists());

		final DomainManagerLaunchConfiguration model = new DomainManagerLaunchConfiguration();
		model.setArguments("");
		model.setDebugLevel(DebugLevel.Error);
		model.setDomainName(name);
		model.setLaunchConfigName(name);
		model.setLocalDomainName(name);
		model.setSpdPath("/mgr/DomainManager.spd.xml");

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				LaunchDomainManagerWithOptions.launchDomainManager(model, null);
			}
		});
	}
}
