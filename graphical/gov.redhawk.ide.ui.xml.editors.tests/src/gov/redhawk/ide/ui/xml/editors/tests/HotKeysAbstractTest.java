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
package gov.redhawk.ide.ui.xml.editors.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.UITest;

public abstract class HotKeysAbstractTest extends UITest {

	protected SWTBotEditor editor;
	
	/**
	 * Imports a project, and opens the appropriate editor to the correct tab. Should set {@link #editor}.
	 * @throws CoreException 
	 */
	protected abstract void importProjectAndOpen() throws CoreException;
	
	/**
	 * IDE-1342
	 * @throws CoreException
	 */
	@Test
	public void pressDeleteInSpdXml() throws CoreException {
		importProjectAndOpen();
		Assert.assertFalse(editor.isDirty());
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.DELETE);
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return editor.isDirty();
			}
			
			@Override
			public String getFailureMessage() {
				return "Editor did not dirty";
			}
		});
	}
	
}
