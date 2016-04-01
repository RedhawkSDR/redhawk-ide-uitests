package gov.redhawk.ide.properties.view.runtime.dcd.tests;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

/**
 * Tests properties of a locally launched device selected in the Node Chalkboard Diagram
 */
public class LocalDeviceDiagramPropertyTest extends LocalDevicePropertyTest {

	@Override
	protected void prepareObject() {
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_NAME, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, DEVICE_NAME_NUM);
		DiagramTestUtils.waitForComponentState(bot, editor, DEVICE_NAME_NUM, ComponentState.STOPPED);

		ConsoleUtils.disableAutoShowConsole(gefBot);

		editor.click(DEVICE_NAME_NUM);
	}

	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Device Properties";
	}
}
