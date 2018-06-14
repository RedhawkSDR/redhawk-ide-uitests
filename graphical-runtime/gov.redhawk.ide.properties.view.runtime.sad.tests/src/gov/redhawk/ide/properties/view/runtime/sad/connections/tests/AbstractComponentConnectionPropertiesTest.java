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
package gov.redhawk.ide.properties.view.runtime.sad.connections.tests;

import org.junit.Test;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractConnectionPropertiesTest;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportProperty;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportType;

public abstract class AbstractComponentConnectionPropertiesTest extends AbstractConnectionPropertiesTest {

	/**
	 * This method should launch the negotiator components, set everything up, and select the connection.
	 */
	protected abstract void prepareNegotiatorComponentConnection();

	/**
	 * Tests the advanced properties of a connection using two "Negotiator" components to simulate a negotiated
	 * connection.
	 */
	@Test
	public void connectionAdvancedSimulated() {
		prepareNegotiatorComponentConnection();
		common(new TransportTypeAndProps(TransportType.NEGOTIATOR_SIM, new TransportProperty("first", "1"), new TransportProperty("second", "two")));
	}

}
