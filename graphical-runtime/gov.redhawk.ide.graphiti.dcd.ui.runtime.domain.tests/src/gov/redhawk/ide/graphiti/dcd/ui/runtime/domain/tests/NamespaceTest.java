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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

public class NamespaceTest extends AbstractGraphitiDomainNodeRuntimeTest {

	/**
	 * IDE-1187
	 * Test launching a node that uses namespaces in a running domain.
	 */
	@Test
	public void launchNamespacedNode() {
		launchDomainAndDevMgr(NAMESPACE_DEVICE_MANAGER);
		SWTBotGefEditor editor = gefBot.gefEditor(NAMESPACE_DEVICE_MANAGER);

		Assert.assertNotNull("GPP should be displayed in diagram", editor.getEditPart(GPP_1));
		Assert.assertNotNull("Namespaced device should be displayed in diagram", editor.getEditPart(NAME_SPACE_DEVICE_1));
	}

}
