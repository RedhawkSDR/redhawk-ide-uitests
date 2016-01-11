package gov.redhawk.ide.properties.view.tests;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class DesignServicePropertyTest extends DesignDevicePropertyTest {

	private final String SERVICE_NAME = "AllPropertyTypesService";

	@Override
	protected void prepareObject() {
		NodeUtils.createNewNodeProject(bot, NODE_NAME, DOMAIN_NAME);
		setPropTabName();
		setEditor();
		DiagramTestUtils.addFromPaletteToDiagram((RHBotGefEditor) editor, SERVICE_NAME, 0, 0);
		selectObject();
	}

	@Override
	protected void selectObject() {
		editor.click(SERVICE_NAME);
	}

	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Service Properties";
	}

	@Override
	protected void setEditor() {
		editor = gefBot.rhGefEditor(NODE_NAME);
	}
}
