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
package gov.redhawk.ide.codegen.runtime.tests;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

public class PortsTabNPETest extends UIRuntimeTest {

	private boolean jfaceError;

	/**
	 * Test IDE-1340, NPE after codegen when using ports tab
	 */
	@Test
	public void npe_after_codegen_on_ports_tab() {
		final String projectName = "npe_test";

		// Listen for log entries, which could indicate a problem
		jfaceError = false;
		ILog log = Platform.getLog(Platform.getBundle("org.eclipse.jface"));
		log.addLogListener(new ILogListener() {

			@Override
			public void logging(IStatus status, String plugin) {
				jfaceError = true;
			}
		});

		// Reproduce the error
		ComponentUtils.createComponentProject(bot, "npe_test", "Python");
		SWTBotEditor editor = bot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, editor);
		bot.editorById("org.python.pydev.editor.PythonEditor").close();
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PORTS_TAB);
		editor.bot().button("Add").click();
		editor.bot().tableWithLabel("Type:").click(0, 0);

		// Wait to see if it turns up in the log
		try {
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					return jfaceError;
				}

				@Override
				public String getFailureMessage() {
					return null;
				}
			}, 5000);
		} catch (TimeoutException ex) {
			// No error turned up
			return;
		}
		Assert.fail("An error was logged");
	}
}
