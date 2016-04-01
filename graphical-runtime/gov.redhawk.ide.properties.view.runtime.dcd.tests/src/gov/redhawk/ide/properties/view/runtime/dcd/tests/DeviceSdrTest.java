package gov.redhawk.ide.properties.view.runtime.dcd.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewTargetSdrTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class DeviceSdrTest extends AbstractPropertiesViewTargetSdrTest {
	final private String[] NODE_PARENT_PATH = { "Target SDR", "Nodes" };
	final private String NODE_NAME = "AllPropertyTypes_DevMgr";
	final private String DEVICE_NAME = "AllPropertyTypesDevice";
	
	@Override
	protected void selectResource() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, NODE_PARENT_PATH, NODE_NAME, DiagramType.GRAPHITI_NODE_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(NODE_NAME);
		editor.select(DEVICE_NAME);
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Device Properties";
	}

}
