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
package gov.redhawk.ide.graphiti.sad.ui.tests.formeditor;

import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

/**
 * IDE-1940 & IDE-1941
 * 
 * Test editing a component instantiations details via the Components tab.
 * - Includes testing a placement with multiple instantiations
 * - Includes editing an instantiation in a host collocation
 */
public class WaveformComponentsTabTest extends AbstractWaveformTabTest {

	private static final String PROJECT_NAME = "ComponentTabTestWF";
	private static final String SUFFIX = "_Edited";

	private final String[] components = { "SigGen_1", "SigGen_2", "HardLimit_1" };

	@Override
	protected String getProjectName() {
		return PROJECT_NAME;
	}

	@Override
	protected String getTabName() {
		return DiagramTestUtils.COMPONENTS_TAB;
	}

	/**
	 * IDE-1940
	 * Test changing a component instantiations usage name and naming service name via the Components Tab
	 **/
	@Test
	public void usageName() {
		for (final String componentId : components) {
			editorBot.tree().select(componentId);
			final SadComponentInstantiation ci = sad.getComponentInstantiation(componentId);

			// Sanity check to make sure the usage name is what we think it is
			Assert.assertEquals(componentId, ci.getUsageName());
			Assert.assertEquals(componentId, ci.getFindComponent().getNamingService().getName());

			// Change the value and check that model was updated
			editorBot.textWithLabel("Usage Name:").selectAll().setText(componentId + SUFFIX);
			editorBot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					Assert.assertEquals(componentId + SUFFIX, ci.getUsageName());
					Assert.assertEquals(componentId + SUFFIX, ci.getFindComponent().getNamingService().getName());
					return true;
				}

				@Override
				public String getFailureMessage() {
					return "Model component instantiation usageName does not match Components tab usage name field";
				}
			});

			editorBot.textWithLabel("Usage Name:").selectAll().setText("");
			EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SAD_EDITOR_COMPONENTS_TAB_ID);

			editorBot.textWithLabel("Usage Name:").selectAll().setText(componentId);
			EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_COMPONENTS_TAB_ID);
		}
	}

	/**
	 * IDE-1941
	 * Test Component Tab logging config fields
	 */
	@Test
	public void loggingConfiguration() {
		for (String componentId : components) {
			editorBot.tree().select(componentId);
			final SadComponentInstantiation ci = sad.getComponentInstantiation(componentId);

			// Sanity check to make sure that the box is unchecked is no logging config element exists
			Assert.assertFalse(editorBot.checkBoxWithLabel("Logging Configuration:").isChecked());
			Assert.assertNull(ci.getLoggingConfig());

			// Enable logging config and check that the model element is created
			editorBot.checkBoxWithLabel("Logging Configuration:").click();
			Assert.assertNotNull(ci.getLoggingConfig());

			// Set a log level and make sure it gets updated
			editorBot.comboBoxWithLabel("Log Level:").setSelection("DEBUG");
			Assert.assertEquals("DEBUG", ci.getLoggingConfig().getLevel());
			editorBot.comboBoxWithLabel("Log Level:").setSelection(0);
			Assert.assertNull(ci.getLoggingConfig().getLevel());

			// Set a uri and make sure it gets updated
			final String testUri = "sca://scauri";
			editorBot.textWithLabel("Logging URI:").selectAll().setText(testUri);
			editorBot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					Assert.assertEquals(testUri, ci.getLoggingConfig().getUri());
					return true;
				}

				@Override
				public String getFailureMessage() {
					return "Model URI does not match the Components tab URI field";
				}
			});

			editorBot.textWithLabel("Logging URI:").selectAll().setText("");
			editorBot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					Assert.assertEquals("", ci.getLoggingConfig().getUri());
					return true;
				}

				@Override
				public String getFailureMessage() {
					return "Model URI does not match the Components tab URI field";
				}
			});

			// Disable logging config and check that the model element is removed
			editorBot.checkBoxWithLabel("Logging Configuration:").click();
			Assert.assertNull(ci.getLoggingConfig());
		}
	}
}
