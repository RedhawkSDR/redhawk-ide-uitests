package gov.redhawk.ide.properties.view.runtime.tests;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Assert;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaWaveform;

/**
 * Tests properties of a component contained within a locally launched waveform selected in the Chalkboard Diagram
 */
public class LocalWaveformComponentDiagramPropertyTest extends LocalComponentDiagramPropertyTest {

	public final static String WAVEFORM_NAME = "AllPropertyTypesWaveform";
	private String waveformFullName;

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, waveformFullName);
	}

	@Override
	protected void prepareObject() {
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, WAVEFORM_NAME);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, new String[] { "Sandbox" }, WAVEFORM_NAME);
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, new String[] { "Sandbox" }, WAVEFORM_NAME, DiagramType.GRAPHITI_CHALKBOARD);
		waveformFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, new String[] { "Sandbox" }, WAVEFORM_NAME);
		Assert.assertNotNull("Waveform full name did not populate", waveformFullName);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformFullName);
		
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, COMP_NAME + "_1");
		DiagramTestUtils.waitForComponentState(bot, editor, COMP_NAME + "_1", ComponentState.STOPPED);

		ViewUtils.disableConsoleView(gefBot);
		
		editor.click(COMP_NAME);
	}

	@Override
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		EList<ScaWaveform> localWaveforms = localSca.getWaveforms();
		for (ScaWaveform waveform : localWaveforms) {
			if (waveformFullName.equals(waveform.getName())) {
				for (ScaComponent c : waveform.getComponents()) {
					if (COMP_NAME.equals(c.getProfileObj().getName())) {
						return c.getProperties();
					}
				}
			}
		}
		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}