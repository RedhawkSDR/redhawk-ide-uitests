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

import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

public class LabelResolvesConditions extends DefaultCondition {

	private SWTBotTree tree;
	private int row;
	private int column;

	public LabelResolvesConditions(SWTBotTree tree, int row, int column) {
		this.tree = tree;
		this.row = row;
		this.column = column;
	}

	@Override
	public boolean test() throws Exception {
		return !tree.cell(row, column).startsWith("IOR:");
	}

	@Override
	public String getFailureMessage() {
		return "Cell in row " + row + " column " + column + " did not resolve from an IOR to a label";
	}

}
