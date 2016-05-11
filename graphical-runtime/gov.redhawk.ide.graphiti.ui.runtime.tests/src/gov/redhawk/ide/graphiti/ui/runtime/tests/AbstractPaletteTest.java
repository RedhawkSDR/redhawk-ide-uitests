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

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.runtime.tests.util.FilterInfo;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.PaletteUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;

public abstract class AbstractPaletteTest extends UIRuntimeTest {

	protected RHSWTGefBot gefBot = new RHSWTGefBot(); // SUPPRESS CHECKSTYLE shared variable

	/**
	 * IDE-1112
	 * Test the diagram palette filter
	 */
	@Test
	public void filter() {
		RHBotGefEditor editor = launchDiagram();
		ComponentDescription[] descriptions = getComponentsToFilter();
		FilterInfo[] filterInfos = getFilterInfo();

		for (ComponentDescription description : descriptions) {
			String fullName = description.getFullName();
			Assert.assertTrue("Component not initially present: " + fullName, PaletteUtils.toolIsPresent(editor, fullName));
		}

		for (FilterInfo filterInfo : filterInfos) {
			String filter = filterInfo.getFilter();
			PaletteUtils.setFilter(editor, filter);

			for (int i = 0; i < descriptions.length; i++) {
				String fullName = descriptions[i].getFullName();
				if (filterInfo.isResourceShown(i)) {
					String errorMsg = String.format("Component %s is missing from the palette (filter '%s')", fullName, filter);
					Assert.assertTrue(errorMsg, PaletteUtils.toolIsPresent(editor, fullName));
				} else {
					String errorMsg = String.format("Component %s should be filtered out of the palette (filter '%s')", fullName, filter);
					Assert.assertFalse(errorMsg, PaletteUtils.toolIsPresent(editor, fullName));
				}
			}
		}
	}

	/**
	 * IDE-953
	 * Multiple implementations should not shown for selection at runtime.
	 */
	@Test
	public void multipleImplementations() {
		RHBotGefEditor editor = launchDiagram();
		Assert.assertTrue("Multiple implementations should be available in runtime palette",
			PaletteUtils.hasMultipleImplementations(editor, getMultipleImplComponent().getFullName()));
	}

	protected abstract RHBotGefEditor launchDiagram();

	/**
	 * A list of components to check for after each filter operation.
	 * @return
	 */
	protected abstract ComponentDescription[] getComponentsToFilter();

	/**
	 * Each FilterInfo specified the filter text to try, and which components should be shown.
	 * @return
	 */
	protected abstract FilterInfo[] getFilterInfo();

	/**
	 * A component with multiple implementations
	 * @return
	 */
	protected abstract ComponentDescription getMultipleImplComponent();
}
