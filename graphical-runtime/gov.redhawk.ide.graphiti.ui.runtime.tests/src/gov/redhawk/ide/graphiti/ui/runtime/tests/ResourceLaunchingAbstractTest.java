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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWTException;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.AssertionFailedException;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.logging.ui.LogLevels;

public abstract class ResourceLaunchingAbstractTest extends UIRuntimeTest {

	private boolean assertionError;

	/**
	 * Must have ports such that Slow_out[0] -> Fast_in[0] is possible, and Fast_out[0] -> Slow_in[0]
	 */
	protected abstract ComponentDescription getSlowComponentDescription();

	private ComponentDescription slowComp = getSlowComponentDescription();

	/**
	 * See above
	 */
	protected abstract ComponentDescription getFastComponentDescription();

	private ComponentDescription fastComp = getFastComponentDescription();
	private ILogListener listener;

	/**
	 * IDE-1045, IDE-1384
	 * Ensure connections cannot be made while a resource is starting up
	 */
	@Test
	public void noConnectionsDuringStartup() {
		RHBotGefEditor editor = openDiagram();

		DiagramTestUtils.addFromPaletteToDiagram(editor, slowComp.getFullName(), 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, fastComp.getFullName(), 150, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, slowComp.getShortName(1), ComponentState.LAUNCHING); // IDE-1384
		DiagramTestUtils.waitForComponentState(bot, editor, fastComp.getShortName(1), ComponentState.STOPPED);

		// Draw both possible port connections, in both forward and reverse directions
		SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, slowComp.getShortName(1), slowComp.getOutPort(0));
		SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, fastComp.getShortName(1), fastComp.getInPort(0));
		DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, provides, uses);
		uses = DiagramTestUtils.getDiagramUsesPort(editor, fastComp.getShortName(1), fastComp.getOutPort(0));
		provides = DiagramTestUtils.getDiagramProvidesPort(editor, slowComp.getShortName(1), slowComp.getInPort(0));
		DiagramTestUtils.drawConnectionBetweenPorts(editor, uses, provides);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, provides, uses);

		// There should be no connections
		uses = DiagramTestUtils.getDiagramUsesPort(editor, slowComp.getShortName(1), slowComp.getOutPort(0));
		Assert.assertEquals(0, DiagramTestUtils.getSourceConnectionsFromPort(editor, uses).size());
		uses = DiagramTestUtils.getDiagramUsesPort(editor, fastComp.getShortName(1), fastComp.getOutPort(0));
		Assert.assertEquals(0, DiagramTestUtils.getSourceConnectionsFromPort(editor, uses).size());
	}

	/**
	 * IDE-1384 Show resources as disabled/gray while they're launching
	 * Ensure some context menus are not present / disabled while the resource is starting up
	 */
	@Test
	public void contextMenusDisableDuringStartup() {
		RHBotGefEditor editor = openDiagram();

		DiagramTestUtils.addFromPaletteToDiagram(editor, slowComp.getFullName(), 0, 0);
		DiagramTestUtils.waitForComponentState(bot, editor, slowComp.getShortName(1), ComponentState.LAUNCHING); // IDE-1384
		SWTBotGefEditPart editPart = editor.getEditPart(slowComp.getShortName(1)).select();

		// We have to listen for log entries
		addAssertionListner();

		assertionError = false;
		DiagramTestUtils.releaseFromDiagram(editor, editPart);
		waitForAssertionInLog();

		try {
			DiagramTestUtils.startComponentFromDiagram(editor, slowComp.getShortName(1));
			Assert.fail();
		} catch (WidgetNotFoundException ex) {
			// PASS
		}

		try {
			DiagramTestUtils.changeLogLevelFromDiagram(editor, slowComp.getShortName(1), LogLevels.DEBUG);
			Assert.fail();
		} catch (WidgetNotFoundException ex) {
			// PASS
		}
	}

	private void addAssertionListner() {
		ILog log = Platform.getLog(Platform.getBundle("org.eclipse.ui.workbench"));
		this.listener = new ILogListener() {
			@Override
			public void logging(IStatus status, String plugin) {
				if (!"org.eclipse.ui".equals(status.getPlugin())) {
					return;
				}
				if (status.getException() instanceof SWTException) {
					if (((SWTException) status.getException()).throwable instanceof AssertionFailedException) {
						assertionError = true;
					}
				}
			}
		};
		log.addLogListener(this.listener);
	}

	@After
	public void after_launching() {
		if (this.listener != null) {
			ILog log = Platform.getLog(Platform.getBundle("org.eclipse.ui.workbench"));
			log.removeLogListener(listener);
			this.listener = null;
		}
		bot.waitUntil(new WaitForLaunchTermination(true));
	}

	private void waitForAssertionInLog() {
		bot.waitUntil(new DefaultCondition() {
			public boolean test() throws Exception {
				return assertionError;
			};

			public String getFailureMessage() {
				return "Assertion error was not logged by org.eclipse.ui.workbench for org.eclipse.ui";
			};
		});
	}

	/**
	 * Open the appropriate diagram
	 */
	protected abstract RHBotGefEditor openDiagram();
}
