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

public class SadPropertiesTabSimpleTest extends SadAbstractPropertiesTabTest {

	private static final String SIG_GEN_DOUBLE = "frequency";
	private static final String SIG_GEN_STRING = "stream_id";
	private static final String SIG_GEN_BOOLEAN = "throttle";
	private static final String SIG_GEN_LONG = "xfer_len";
	private static final String DATA_CONVERTER_ENUM = "outputType";

	@Override
	protected List<String> getBooleanPath() {
		return Arrays.asList(SIG_GEN_1, SIG_GEN_BOOLEAN);
	}

	@Override
	protected List<String> getDoublePath() {
		return Arrays.asList(SIG_GEN_1, SIG_GEN_DOUBLE);
	}

	@Override
	protected List<String> getEnumPath() {
		return Arrays.asList(DATA_CONVERTER_1, DATA_CONVERTER_ENUM);
	}

	@Override
	protected List<String> getLongPath() {
		return Arrays.asList(SIG_GEN_1, SIG_GEN_LONG);
	}

	@Override
	protected List<String> getStringPath() {
		return Arrays.asList(SIG_GEN_1, SIG_GEN_STRING);
	}

}
