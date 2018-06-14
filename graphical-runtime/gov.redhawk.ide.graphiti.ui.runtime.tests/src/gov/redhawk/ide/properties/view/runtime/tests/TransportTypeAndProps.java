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
package gov.redhawk.ide.properties.view.runtime.tests;

import java.util.Arrays;
import java.util.List;

public class TransportTypeAndProps {

	public enum TransportType {
		SHMIPC("shmipc"),
		CORBA("CORBA"),
		NEGOTIATOR_SIM("transport_1");

		private String text;

		TransportType(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	};

	public static class TransportProperty {

		private String propName;
		private String propValue;

		public TransportProperty(String propName, String propValue) {
			this.propName = propName;
			this.propValue = propValue;
		}

		public String getPropName() {
			return propName;
		}

		public String getPropValue() {
			return propValue;
		}

	}

	private TransportType tt;
	private List<TransportProperty> props;

	public TransportTypeAndProps(TransportType tt, TransportProperty... props) {
		this.tt = tt;
		this.props = Arrays.asList(props);
	}

	public TransportType getTransportType() {
		return tt;
	}

	public List<TransportProperty> getProperties() {
		return props;
	}
}
