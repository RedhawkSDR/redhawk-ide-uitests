package gov.redhawk.ide.properties.view.runtime.sad.tests;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.sca.ScaPlugin;

/**
 * Tests properties of a domain launched component selected in the REDHAWK Explorer View
 */
public class DomainComponentPropertyTest extends AbstractPropertiesViewRuntimeTest {
	protected static String DOMAIN = DomainComponentPropertyTest.class.getSimpleName() + "_";
	protected static final String DEVICE_MANAGER = "DevMgr_localhost";
	protected static final String WAVEFORM = "AllPropertyTypesWaveform";

	protected static final String COMPONENT = "AllPropertyTypesComponent";
	protected static final String COMPONENT_NUM = COMPONENT + "_1";

	protected String[] DOMAIN_WAVEFORM_PARENT_PATH;
	protected String waveformFullName;
	
	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.deleteDomainInstance(bot, DOMAIN);
		NodeBooterLauncherUtil.getInstance().terminateAll();
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.afterTest();
	}

	@Override
	protected void prepareObject() {
		DOMAIN = DomainComponentPropertyTest.class.getSimpleName() + "_" + (int) (1000.0 * Math.random());
		DOMAIN_WAVEFORM_PARENT_PATH = new String[] { DOMAIN, "Waveforms" };
		ScaExplorerTestUtils.launchDomain(bot, DOMAIN, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);

		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, WAVEFORM);
		waveformFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, WAVEFORM);

		ArrayList<String> componentParentPath = new ArrayList<>(Arrays.asList(DOMAIN_WAVEFORM_PARENT_PATH));
		componentParentPath.add(waveformFullName);
		
		ViewUtils.disableConsoleView(gefBot);
		
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, componentParentPath.toArray(new String[] {}), COMPONENT_NUM);
		treeItem.select();
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Properties";
	}

	@Override
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(Display.getCurrent());
		ScaWaveform wave = null;
		for (ScaWaveform waveform : registry.findDomain(DOMAIN).getWaveforms()) {
			if (waveformFullName.equals(waveform.getName())) {
				wave = waveform;
				break;
			}
		}

		Assert.assertNotNull("Waveform " + waveformFullName + " could not be found in domain: " + DOMAIN, wave);

		for (ScaComponent c : wave.getComponents()) {
			if (COMPONENT_NUM.equals(c.getName())) {
				return c.getProperties();
			}
		}

		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}
