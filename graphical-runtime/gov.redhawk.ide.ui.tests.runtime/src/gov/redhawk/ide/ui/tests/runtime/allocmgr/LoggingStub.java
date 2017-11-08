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
package gov.redhawk.ide.ui.tests.runtime.allocmgr;

import org.omg.CORBA.IntHolder;

import CF.LogEvent;
import CF.LoggingOperations;
import CF.UnknownIdentifier;

public abstract class LoggingStub extends PropertyEmitterStub implements LoggingOperations {

	@Override
	public LogEvent[] retrieve_records(IntHolder howMany, int startingRecord) {
		return null;
	}

	@Override
	public LogEvent[] retrieve_records_by_date(IntHolder howMany, long toTimeStamp) {
		return null;
	}

	@Override
	public LogEvent[] retrieve_records_from_date(IntHolder howMany, long fromTimeStamp) {
		return null;
	}

	@Override
	public int log_level() {
		return 0;
	}

	@Override
	public void log_level(int newLogLevel) {
	}

	@Override
	public void setLogLevel(String loggerId, int newLevel) throws UnknownIdentifier {
	}

	@Override
	public String getLogConfig() {
		return null;
	}

	@Override
	public void setLogConfig(String configContents) {
	}

	@Override
	public void setLogConfigURL(String configUrl) {
	}
}
