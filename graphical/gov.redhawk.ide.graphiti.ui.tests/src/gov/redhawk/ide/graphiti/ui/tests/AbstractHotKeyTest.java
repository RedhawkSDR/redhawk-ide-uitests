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
package gov.redhawk.ide.graphiti.ui.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public abstract class AbstractHotKeyTest extends AbstractGraphitiTest {

	private ComponentDescription component = getComponent();

	protected abstract ComponentDescription getComponent();

	/**
	 * @param projectName The name of the project to create
	 * @return The file name that will be opened in an editor
	 */
	protected abstract String createNewProject(String projectName);

	/**
	 * IDE-85 Users should be able to delete a component from a waveform using the delete key on the keyboard
	 */
	@Test
	public void deleteHotKeyTest() {
		String projectName = "Delete_HotKey";

		// Create a new empty diagram
		String editorName = createNewProject(projectName);
		RHBotGefEditor editor = gefBot.rhGefEditor(editorName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, component.getFullName(), 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, component.getShortName(1));

		// Select component and delete with hotkey
		editor.select(component.getShortName(1));
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.DELETE);

		// Confirm component was removed from the waveform
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, component.getShortName(1));
	}

	/**
	 * IDE-1865 Users should be able to delete multiple components from a waveform using the delete key on the keyboard
	 */
	@Test
	public void deleteMultipleHotKeyTest() {
		String projectName = "Delete_HotKey";

		// Create a new empty diagram
		String editorName = createNewProject(projectName);
		RHBotGefEditor editor = gefBot.rhGefEditor(editorName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, component.getFullName(), 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, component.getFullName(), 300, 300);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, component.getShortName(1));
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, component.getShortName(2));

		// Select components and delete with hotkey
		editor.select(editor.getEditPart(component.getShortName(1)), editor.getEditPart(component.getShortName(2)));
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.DELETE);

		// Confirm components were removed from the waveform
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, component.getShortName(1));
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, component.getShortName(2));
	}

	/**
	 * IDE-678 Undo/Redo key bindings
	 */
	@Test
	public void undoRedoHotkeyTest() {
		String projectName = "UndoRedo_Hotkey";

		// Create a new empty diagram
		String editorName = createNewProject(projectName);
		RHBotGefEditor editor = gefBot.rhGefEditor(editorName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, component.getFullName(), 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, component.getShortName(1));

		// Undo
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.CTRL, 'z');
		DiagramTestUtils.waitUntilComponentDisappearsInDiagram(bot, editor, component.getShortName(1));

		// Redo
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.CTRL | SWT.SHIFT, 'z');
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, component.getShortName(1));
	}

}
