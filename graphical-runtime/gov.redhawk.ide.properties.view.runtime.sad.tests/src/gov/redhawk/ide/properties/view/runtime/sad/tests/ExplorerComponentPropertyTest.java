package gov.redhawk.ide.properties.view.runtime.sad.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

/**
 * Tests properties of a domain launched component selected in the Explorer Diagram
 */
public class ExplorerComponentPropertyTest extends DomainComponentPropertyTest {

	@Override
	protected void prepareObject() {
		super.prepareObject();
		gefBot.gefEditor(waveformFullName).close();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, DOMAIN_WAVEFORM_PARENT_PATH, WAVEFORM, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);
		SWTBotGefEditor editor = gefBot.gefEditor(waveformFullName);
		
		// Need to do this, or the Console view keeps popping up
		ViewUtils.disableConsoleView(gefBot);
		
		editor.click(COMPONENT);
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Component Properties";
	}
}
