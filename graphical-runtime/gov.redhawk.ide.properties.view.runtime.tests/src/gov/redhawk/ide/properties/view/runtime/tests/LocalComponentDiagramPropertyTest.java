package gov.redhawk.ide.properties.view.runtime.tests;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

/**
 * Tests properties of a domain launched component selected in the Chalkboard Diagram
 */
public class LocalComponentDiagramPropertyTest extends LocalComponentPropertyTest {

	@Override
	protected void prepareObject() {
		RHBotGefEditor editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, COMP_NAME, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, COMP_NAME + "_1");
		DiagramTestUtils.waitForComponentState(bot, editor, COMP_NAME + "_1", ComponentState.STOPPED);

		ViewUtils.disableConsoleView(gefBot);

		editor.click(COMP_NAME);
	}

	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Component Properties";
	}
}
