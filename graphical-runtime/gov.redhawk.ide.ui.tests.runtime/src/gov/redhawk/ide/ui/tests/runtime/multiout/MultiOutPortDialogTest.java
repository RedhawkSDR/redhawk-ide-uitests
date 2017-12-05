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
package gov.redhawk.ide.ui.tests.runtime.multiout;

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;

public class MultiOutPortDialogTest extends UIRuntimeTest {

	@Test
	public void multiOutConnectionDialogTest() {
		Assert.fail();
		/**
		 * - Multi-out port dialog test class
		 *   -- Make sure that the dialog can't be completed with the Text box empty and enabled
		 *   -- Make sure the dialog displays 'IN USE' connection IDs
		 *   -- Make sure the dialog can't be completed when selecting an 'IN USE' connection ID
		 *   -- Make sure radio buttons enable/disable widgets as appropriate
		 *   -- Make sure selecting an existing ID creates the correct connection node
		 *   -- Make sure entering an ID manually creates the correct connection node
		 *   -- Make sure canceling the dialog does NOT create a connection
		 * 
		 * 
		 * Test using the list, using a manual id for an invalid connection, using a manual id for a valid connection, 
		 * using the list to try and select an IN USE ID, selecting the radio buttons and having the appropriate widgets disable/enable
		 */
	}
}
