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
package gov.redhawk.ide.graphiti.sad.ui.tests.properties;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;

public class SadPropertiesTabStructTest extends SadAbstractPropertiesTabTest {

	private static final String DATA_CONVERTER_STRUCT1 = "scaleOutput";
	private static final String DATA_CONVERTER_STRUCT1_BOOLEAN = "charPort";
	private static final String DATA_CONVERTER_STRUCT2 = "floatingPointRange";
	private static final String DATA_CONVERTER_STRUCT2_DOUBLE = "minimum";
	private static final String DATA_CONVERTER_STRUCT3 = "transformProperties";
	private static final String DATA_CONVERTER_STRUCT3_LONG = "fftSize";

	@Override
	protected List<String> getBooleanPath() {
		return Arrays.asList(DATA_CONVERTER_1, DATA_CONVERTER_STRUCT1, DATA_CONVERTER_STRUCT1_BOOLEAN);
	}

	@Override
	protected List<String> getDoublePath() {
		return Arrays.asList(DATA_CONVERTER_1, DATA_CONVERTER_STRUCT2, DATA_CONVERTER_STRUCT2_DOUBLE);
	}

	@Override
	@Ignore
	public void setEnum() {
		// TODO: Remove method
	}

	@Override
	protected List<String> getEnumPath() {
		// TODO
		return null;
	}

	@Override
	protected List<String> getLongPath() {
		return Arrays.asList(DATA_CONVERTER_1, DATA_CONVERTER_STRUCT3, DATA_CONVERTER_STRUCT3_LONG);
	}

	@Override
	@Ignore
	public void setString() {
		// TODO: Remove method
	}

	@Override
	protected List<String> getStringPath() {
		// TODO
		return null;
	}

}
