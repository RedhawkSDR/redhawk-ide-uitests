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

public class ComponentDescription {

	private String fullName;
	private String shortName;
	private String[] inPorts;
	private String[] outPorts;

	public ComponentDescription(String fullName, String shortName, String[] inPorts, String[] outPorts) {
		this.fullName = fullName;
		this.shortName = shortName;
		this.inPorts = inPorts;
		this.outPorts = outPorts;
	}

	public String getFullName() {
		return fullName;
	}

	public String getShortName(int number) {
		return String.format("%s_%d", shortName, number);
	}

	public String getInPort(int index) {
		return inPorts[index];
	}

	public String getOutPort(int index) {
		return outPorts[index];
	}
}
