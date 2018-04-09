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

public class SadPropertiesTabSimpleTest extends SadAbstractPropertiesTabTest {

	private static final String ALLPROPS = "AllPropertyTypesComponent2";
	private static final String ALLPROPS_1 = "AllPropertyTypesComponent2_1";

	private static final String DATA_CONVERTER = "rh.DataConverter";
	private static final String DATA_CONVERTER_1 = "DataConverter_1";
	private static final String DATA_CONVERTER_ENUM = "outputType";

	@Override
	protected void addComponents(RHBotGefEditor editor) {
		DiagramTestUtils.addFromPaletteToDiagram(editor, ALLPROPS, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DATA_CONVERTER, 150, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, ALLPROPS_1);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DATA_CONVERTER_1);
	}

	@Override
	public void componentsPresent() {
		SWTBotTreeItem[] topLevelItems = getEditorBot().tree().getAllItems();
		Assert.assertEquals(2, topLevelItems.length);
		Assert.assertEquals(ALLPROPS_1, topLevelItems[0].cell(COLUMN_NAME));
		Assert.assertEquals(DATA_CONVERTER_1, topLevelItems[1].cell(COLUMN_NAME));
	}

	@Override
	protected List<String> getBooleanPath() {
		return Arrays.asList(ALLPROPS_1, "simple_boolean");
	}

	@Override
	protected List<String> getCharPath() {
		return Arrays.asList(ALLPROPS_1, "simple_char");
	}

	@Override
	protected List<String> getDoublePath() {
		return Arrays.asList(ALLPROPS_1, "simple_double");
	}

	@Override
	protected List<String> getEnumPath() {
		return Arrays.asList(DATA_CONVERTER_1, DATA_CONVERTER_ENUM);
	}

	@Override
	protected List<String> getEnumValues() {
		return Arrays.asList("Complex", "Real", "PassThrough");
	}

	@Override
	protected List<String> getFloatPath() {
		return Arrays.asList(ALLPROPS_1, "simple_float");
	}

	@Override
	protected List<String> getLongPath() {
		return Arrays.asList(ALLPROPS_1, "simple_long");
	}

	@Override
	protected List<String> getLongLongPath() {
		return Arrays.asList(ALLPROPS_1, "simple_longlong");
	}

	@Override
	protected List<String> getStringPath() {
		return Arrays.asList(ALLPROPS_1, "simple_string");
	}

	@Override
	protected List<String> getExternalPath() {
		return Arrays.asList(DATA_CONVERTER_1, "outputType");
	}
}
