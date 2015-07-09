/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.diagram.PaletteUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class ChalkboardPaletteTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	@Test
	public void testPaletteFilter() {
		// IDE-1112: test presence of namespaced component in palette
		final String component3 = "name.space.comp";

		final String errorMissing1 = "Component " + SIGGEN + " is missing from the palette";
		final String errorMissing2 = "Component " + HARD_LIMIT + " is missing from the palette";
		final String errorMissing3 = "Component " + component3 + " is missing from the palette";
		final String errorShown1 = "Component " + SIGGEN + " should be filtered out of the palette";
		final String errorShown2 = "Component " + HARD_LIMIT + " should be filtered out of the palette";
		final String errorShown3 = "Component " + component3 + " should be filtered out of the palette";

		editor = openChalkboardDiagram(gefBot);

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, SIGGEN));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, HARD_LIMIT));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteUtils.setFilter(editor, "s");

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, SIGGEN));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, HARD_LIMIT));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));
		
		PaletteUtils.setFilter(editor, "sh");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, SIGGEN));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, HARD_LIMIT));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteUtils.setFilter(editor, "ard");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, SIGGEN));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, HARD_LIMIT));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteUtils.setFilter(editor, ".c");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, SIGGEN));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, HARD_LIMIT));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));

		PaletteUtils.setFilter(editor, "");

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, SIGGEN));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, HARD_LIMIT));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));
	}
	
	@Test
	public void testMultipleImplementations() {
		editor = openChalkboardDiagram(gefBot);
		Assert.assertTrue(PaletteUtils.hasMultipleImplementations(editor, SIGGEN));
		Assert.assertTrue(PaletteUtils.hasMultipleImplementations(editor, HARD_LIMIT));
	}
	
}
