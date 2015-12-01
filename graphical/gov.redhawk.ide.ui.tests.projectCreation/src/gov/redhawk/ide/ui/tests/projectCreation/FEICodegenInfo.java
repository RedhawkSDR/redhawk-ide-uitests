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
package gov.redhawk.ide.ui.tests.projectCreation;

public class FEICodegenInfo implements ICodegenInfo {

	public enum TunerType {
		RECEIVE,
		TRANSMIT,
		BOTH
	}

	private TunerType tunerType;
	private boolean digitalInput = false;
	private boolean digitalOutput = true;
	private int analogInputPorts = -1;
	private int digitalInputPorts = -1;
	private String inputPortType = null;
	private String outputPortType = null;
	private boolean multiOut = true;
	private boolean ingestsGPS = false;
	private boolean outputsGPS = false;

	public TunerType getType() {
		return tunerType;
	}

	public void setType(TunerType tunerType) {
		this.tunerType = tunerType;
	}

	public boolean isInputDigital() {
		return digitalInput;
	}

	public void setInputDigital(boolean digital) {
		this.digitalInput = digital;
	}

	public boolean isOutputDigital() {
		return digitalOutput;
	}

	public void setOutputDigital(boolean digital) {
		this.digitalOutput = digital;
	}

	public int getAnalogInputPorts() {
		return analogInputPorts;
	}

	public void setAnalogInputPorts(int analogInputPorts) {
		this.analogInputPorts = analogInputPorts;
	}

	public int getDigitalInputPorts() {
		return digitalInputPorts;
	}

	public void setDigitalInputPorts(int digitalInputPorts) {
		this.digitalInputPorts = digitalInputPorts;
	}

	public String getInputPortType() {
		return inputPortType;
	}

	public void setInputPortType(String inputPortType) {
		this.inputPortType = inputPortType;
	}

	public String getOutputPortType() {
		return outputPortType;
	}

	public void setOutputPortType(String outputPortType) {
		this.outputPortType = outputPortType;
	}

	public boolean isMultiOut() {
		return multiOut;
	}

	public void setMultiOut(boolean multiOut) {
		this.multiOut = multiOut;
	}

	public boolean isIngestsGPS() {
		return ingestsGPS;
	}

	public void setIngestsGPS(boolean ingestsGPS) {
		this.ingestsGPS = ingestsGPS;
	}

	public boolean isOutputsGPS() {
		return outputsGPS;
	}

	public void setOutputsGPS(boolean outputsGPS) {
		this.outputsGPS = outputsGPS;
	}

}
