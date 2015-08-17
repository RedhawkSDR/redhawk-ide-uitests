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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.PaletteUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DiagramPaletteFilterTest extends AbstractGraphitiTest {

	private RHBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";

	/**
	 * Test filtering of the device/service list
	 * IDE-1112 Test presence of namespaced device in palette.
	 */
	@Test
	public void testFilter() {
		projectName = "Filter-Test";

		final String device1 = GPP;
		final String device2 = DEVICE_STUB;
		final String device3 = "name.space.device";

		final String errorMissing1 = "Device " + device1 + " is missing from the palette";
		final String errorMissing2 = "Device " + device2 + " is missing from the palette";
		final String errorMissing3 = "Device " + device3 + " is missing from the palette";
		final String errorShown1 = "Device " + device1 + " should be filtered out of the palette";
		final String errorShown2 = "Device " + device2 + " should be filtered out of the palette";
		final String errorShown3 = "Device " + device3 + " should be filtered out of the palette";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, device1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, device2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, device3));

		PaletteUtils.setFilter(editor, "g");

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, device1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, device2));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, device3));

		PaletteUtils.setFilter(editor, "sh");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, device1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, device2));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, device3));

		PaletteUtils.setFilter(editor, "d");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, device1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, device2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, device3));

		PaletteUtils.setFilter(editor, ".");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, device1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, device2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, device3));

		PaletteUtils.setFilter(editor, "");

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, device1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, device2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, device3));
	}

}
