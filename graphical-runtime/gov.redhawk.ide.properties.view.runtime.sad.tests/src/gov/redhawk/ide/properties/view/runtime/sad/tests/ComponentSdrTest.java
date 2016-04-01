package gov.redhawk.ide.properties.view.runtime.sad.tests;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.graphiti.ui.runtime.tests.AbstractPropertiesViewTargetSdrTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class ComponentSdrTest extends AbstractPropertiesViewTargetSdrTest {
	final private String[] WAVEFORM_PARENT_PATH = { "Target SDR", "Waveforms" };
	final private String WAVEFORM_NAME = "AllPropertyTypesWaveform";
	final private String COMPONENT_NAME = "AllPropertyTypesComponent";

	@Override
	protected void selectResource() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, WAVEFORM_PARENT_PATH, WAVEFORM_NAME, DiagramType.GRAPHITI_WAVEFORM_EDITOR);
		SWTBotGefEditor editor = gefBot.gefEditor(WAVEFORM_NAME);
		editor.select(COMPONENT_NAME);
	}

	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Component Properties";
	}

}
