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

import CF.DataType;
import CF.PropertiesHolder;
import CF.PropertyEmitterOperations;
import CF.UnknownProperties;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;

public abstract class PropertyEmitterStub implements PropertyEmitterOperations {

	@Override
	public void initializeProperties(CF.DataType[] initialProperties)
		throws CF.PropertyEmitterPackage.AlreadyInitialized, CF.PropertySetPackage.InvalidConfiguration, CF.PropertySetPackage.PartialConfiguration {
	}

	@Override
	public String registerPropertyListener(org.omg.CORBA.Object obj, String[] propIds, float interval) throws CF.UnknownProperties, CF.InvalidObjectReference {
		return null;
	}

	@Override
	public void unregisterPropertyListener(String id) throws CF.InvalidIdentifier {
	}

	@Override
	public void configure(DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
	}

	@Override
	public void query(PropertiesHolder configProperties) throws UnknownProperties {
	}
}
