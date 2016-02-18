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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.FindByUtils;

public class FindByDomainMgrTest extends AbstractFindByTest {
	private static final String FIND_BY_TYPE = FindByUtils.FIND_BY_DOMAIN_MANAGER;
	private static final String FIND_BY_NAME = FindByUtils.FIND_BY_DOMAIN_MANAGER;

	@Ignore
	@Test
	@Override
	public void editFindBy() throws IOException {
		// PASS - This test is not applicable to FindByDomainMgr
	}

	@Override
	protected String getFindByType() {
		return FIND_BY_TYPE;
	}

	@Override
	protected String getFindByName() {
		return FIND_BY_NAME;
	}

	@Override
	protected String getEditTextLabel() {
		// PASS - FindByDomainMgr does not have an edit dialog
		return null;
	}

}
