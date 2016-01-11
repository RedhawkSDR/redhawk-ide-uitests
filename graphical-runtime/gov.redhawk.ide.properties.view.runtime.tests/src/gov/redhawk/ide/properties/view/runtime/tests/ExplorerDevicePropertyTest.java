package gov.redhawk.ide.properties.view.runtime.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class ExplorerDevicePropertyTest extends DomainDevicePropertyTest {
	
	@Override
	protected void prepareObject() {
		super.prepareObject();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[]{DOMAIN, "Device Managers"}, DEVICE_MANAGER, DiagramType.GRAPHITI_NODE_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(DEVICE_MANAGER);
		editor.click(DEVICE_NUM);
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Device Properties";
	}
}
