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
package gov.redhawk.ide.graphiti.ui.runtime.tests.util;

public class FilterInfo {

	private String filter;
	private boolean[] resourceShown;

	public FilterInfo(String filter, boolean... resourcesShown) {
		this.filter = filter;
		this.resourceShown = resourcesShown;
	}

	public String getFilter() {
		return filter;
	}

	public boolean isResourceShown(int index) {
		return this.resourceShown[index];
	}
}
