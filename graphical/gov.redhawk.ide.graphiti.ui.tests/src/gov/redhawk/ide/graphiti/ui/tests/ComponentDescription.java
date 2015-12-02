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
package gov.redhawk.ide.graphiti.ui.tests;

import java.util.HashMap;
import java.util.Map;

public class ComponentDescription {

	private String fullName;
	private String shortName;
	private String[] inPorts;
	private String[] outPorts;
	private Map<String, String> keys;

	public ComponentDescription(String fullName, String[] inPorts, String[] outPorts) {
		this.fullName = fullName;
		int index = this.fullName.lastIndexOf('.');
		if (index == -1) {
			this.shortName = this.fullName;
		} else {
			this.shortName = this.fullName.substring(index + 1);
		}
		this.inPorts = inPorts;
		this.outPorts = outPorts;
		this.keys = new HashMap<String, String>();
	}

	public String getFullName() {
		return fullName;
	}

	public String getShortName() {
		return shortName;
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

	public void setKey(String key, String value) {
		this.keys.put(key, value);
	}

	public String getKey(String key) {
		return this.keys.get(key);
	}
}
