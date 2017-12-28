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
package gov.redhawk.ide.properties.view.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class DesignComponentPropertyTest extends AbstractPropertiesViewDesignTest {

	private static final String WAVEFORM_NAME = "AllPropertyTypesDesignWf";
	private static final String COMPONENT_NAME = "AllPropertyTypesComponent";
	private SoftwareAssembly sad = null;

	@Override
	protected void prepareObject() {
		WaveformUtils.createNewWaveform(bot, WAVEFORM_NAME, COMPONENT_NAME);
		setEditor();
		editor.click(COMPONENT_NAME);
	}

	@Override
	protected void selectObject() {
		editor.click(COMPONENT_NAME);
	}

	@Override
	protected void setEditor() {
		editor = gefBot.gefEditor(WAVEFORM_NAME);
	}

	@Override
	protected ComponentProperties getModelPropertiesFromEditor() throws IOException {
		editor.bot().cTabItem(WAVEFORM_NAME + ".sad.xml").activate();
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		String editorText = editor.toTextEditor().getText();
		Resource resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		ComponentProperties componentProps = sad.getAllComponentInstantiations().get(0).getComponentProperties();
		if (componentProps != null) {
			return componentProps;
		} else {
			return PartitioningFactory.eINSTANCE.createComponentProperties();
		}
	}

	@Override
	protected void writeModelPropertiesToEditor(ComponentProperties componentProps) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		sad.getAllComponentInstantiations().get(0).setComponentProperties(componentProps);
		sad.eResource().save(outputStream, null);
		editor.toTextEditor().setText(outputStream.toString());
	}

	// ### Class Specific Tests ### //
	/**
	 * Open a waveform project so that the initial tab is not the diagram tab. Ensure that the properties view still
	 * works.
	 * 
	 * IDE-1338
	 */
	@Test
	public void overviewTabDefaultTest() {
		prepareObject();
		editor.bot().cTabItem("Overview").activate();
		editor.close();

		ProjectExplorerUtils.openProjectInEditor(bot, WAVEFORM_NAME, WAVEFORM_NAME + ".sad.xml");
		SWTBotEditor editor2 = bot.editorByTitle(WAVEFORM_NAME);
		editor2.bot().cTabItem("Diagram").activate();

		setEditor();
		selectObject();
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, PROP_TAB_NAME);
		Assert.assertNotNull("Property window does not populate", propTree);
		SWTBotTreeItem[] items = propTree.getAllItems();
		Assert.assertTrue("No property values are displayed", items.length > 0);
	}

	/**
	 * IDE-831 - Can't delete sequence values in runtime property dialog when in edit mode
	 */
	@Test
	public void editSimpleSequenceSize() {
		final String simpleSeqDouble = "simpleSeqDouble";

		prepareObject();
		setEditor();
		selectObject();
		SWTBotTree propTree = ViewUtils.selectPropertiesTab(bot, PROP_TAB_NAME);

		SWTBotTreeItem treeItem = propTree.getTreeItem(simpleSeqDouble);
		treeItem.select();
		treeItem.click(1);
		keyboard.pressShortcut(Keystrokes.SPACE);

		SWTBotShell editShell = bot.shell("Edit Property Value");
		editShell.setFocus();
		bot.waitUntil(Conditions.shellIsActive("Edit Property Value"));
		SWTBotTable table = editShell.bot().table();

		List<String> seqValues = new ArrayList<String>();
		int tableSize = table.rowCount();
		for (int i = 0; i < tableSize; ++i) {
			seqValues.add(table.cell(i, 0));
		}

		table.click(0, 0);
		editShell.bot().buttonWithTooltip("Remove").click();

		Assert.assertEquals("Item was not removed from properties table", (tableSize - 1), editShell.bot().table().rowCount());
		editShell.bot().button("Finish").click();

		treeItem = propTree.getTreeItem(simpleSeqDouble);

		// This command creates a String array of simpleSeq values in the properties view
		String[] values = treeItem.cell(1).substring(1, treeItem.cell(1).length() - 1).split(", ");
		Assert.assertEquals("Properties view did not update", tableSize - 1, values.length);

		Assert.assertTrue(true);

	}
}
