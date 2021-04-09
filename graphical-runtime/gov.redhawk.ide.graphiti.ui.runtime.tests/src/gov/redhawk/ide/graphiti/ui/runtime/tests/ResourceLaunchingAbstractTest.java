/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.ui.runtime.tests;


public abstract class ResourceLaunchingAbstractTest extends LocalLaunchingAbstractTest {

	private ComponentDescription slowComp = getSlowComponentDescription();

	/**
	 * Must have ports such that Slow_out[0] -> Fast_in[0] is possible, and Fast_out[0] -> Slow_in[0]
	 */
	protected abstract ComponentDescription getFastComponentDescription();

	private ComponentDescription fastComp = getFastComponentDescription();

}
