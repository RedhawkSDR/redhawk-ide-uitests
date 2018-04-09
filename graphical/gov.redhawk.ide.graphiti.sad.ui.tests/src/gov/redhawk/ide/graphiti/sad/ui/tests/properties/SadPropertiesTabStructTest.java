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
package gov.redhawk.ide.graphiti.sad.ui.tests.properties;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class SadPropertiesTabStructTest extends SadAbstractPropertiesTabTest {

	private static final String ALLPROPS = "AllPropertyTypesComponent2";
	private static final String ALLPROPS_1 = "AllPropertyTypesComponent2_1";
	private static final String STRUCT = "struct";
	private static final String PREFIX = "struct::simple_";

	private static final String FILE_READER = "rh.FileReader";
	private static final String FILE_READER_1 = "FileReader_1";

	@Override
	protected void addComponents(RHBotGefEditor editor) {
		DiagramTestUtils.addFromPaletteToDiagram(editor, ALLPROPS, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, FILE_READER, 150, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, ALLPROPS_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, FILE_READER_1);
	}

	@Override
	public void componentsPresent() {
		SWTBotTreeItem[] topLevelItems = getEditorBot().tree().getAllItems();
		Assert.assertEquals(2, topLevelItems.length);
		Assert.assertEquals(ALLPROPS_1, topLevelItems[0].cell(COLUMN_NAME));
		Assert.assertEquals(FILE_READER_1, topLevelItems[1].cell(COLUMN_NAME));
	}

	@Override
	protected List<String> getBooleanPath() {
		return Arrays.asList(ALLPROPS_1, STRUCT, PREFIX + "boolean");
	}

	@Override
	protected List<String> getCharPath() {
		return Arrays.asList(ALLPROPS_1, STRUCT, PREFIX + "char");
	}

	@Override
	protected List<String> getDoublePath() {
		return Arrays.asList(ALLPROPS_1, STRUCT, PREFIX + "double");
	}

	@Override
	protected List<String> getEnumPath() {
		return Arrays.asList(FILE_READER_1, "default_timestamp", "tcmode");
	}

	@Override
	protected List<String> getEnumValues() {
		// This is just a subset
		return Arrays.asList("TCM_OFF", "TCM_CPU", "TCM_ZTC");
	}

	@Override
	protected List<String> getFloatPath() {
		return Arrays.asList(ALLPROPS_1, STRUCT, PREFIX + "float");
	}

	@Override
	protected List<String> getLongPath() {
		return Arrays.asList(ALLPROPS_1, STRUCT, PREFIX + "long");
	}

	@Override
	protected List<String> getLongLongPath() {
		return Arrays.asList(ALLPROPS_1, STRUCT, PREFIX + "longlong");
	}

	@Override
	protected List<String> getStringPath() {
		return Arrays.asList(ALLPROPS_1, STRUCT, PREFIX + "string");
	}

	@Override
	protected List<String> getExternalPath() {
		return Arrays.asList(FILE_READER_1, "advanced_properties");
	}
}
