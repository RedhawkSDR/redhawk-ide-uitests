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
package gov.redhawk.ide.properties.view.runtime.sad.ports.tests;

import org.junit.Test;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractPortPropertiesTest;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportProperty;
import gov.redhawk.ide.properties.view.runtime.tests.TransportTypeAndProps.TransportType;

public abstract class AbstractComponentPortPropertiesTest extends AbstractPortPropertiesTest {

	protected final TransportTypeAndProps transport1 = new TransportTypeAndProps(TransportType.TR1, new TransportProperty("first", "1"), new TransportProperty("second", "two"));
	protected final TransportTypeAndProps transport2 = new TransportTypeAndProps(TransportType.TR2);
	protected final TransportTypeAndProps transport3 = new TransportTypeAndProps(TransportType.TR3, new TransportProperty("configured", "true"));

	/**
	 * This method should launch the negotiator component and select the provides port.
	 */
	protected abstract void prepareNegotiatorComponentProvides();

	/**
	 * This method should launch the negotiator component and select the uses port.
	 */
	protected abstract void prepareNegotiatorComponentUses();

	/**
	 * Tests the advanced properties of a provides port of a "Negotiator" components (which has simulated transports)
	 */
	@Test
	public void providesPortAdvancedSimulated() {
		prepareNegotiatorComponentProvides();
		advanced(transport1, transport2, transport3);
	}

	/**
	 * Tests the advanced properties of a uses port of a "Negotiator" components (which has simulated transports)
	 */
	@Test
	public void usesPortAdvancedSimulated() {
		prepareNegotiatorComponentUses();
		advanced(transport1, transport2, transport3);
	}

}
