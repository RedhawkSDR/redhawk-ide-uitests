package gov.redhawk.ide.properties.view.runtime.dcd.tests;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.properties.view.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaDevice;

/**
 * Tests properties of a locally launched device selected in the REDHAWK Explorer View
 */
public class LocalDevicePropertyTest extends AbstractPropertiesViewRuntimeTest {

	final static String DEVICE_NAME = "AllPropertyTypesDevice";
	final static String DEVICE_NAME_NUM = DEVICE_NAME + "_1";
	final static String DEVICE_NAME_STARTED = DEVICE_NAME_NUM + " STARTED";

	@After
	@Override
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, DEVICE_NAME_NUM);
		super.afterTest();
	}
	
	@Override
	protected void prepareObject() {
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, DEVICE_NAME, "python");
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, new String[] { "Sandbox" }, "Device Manager", DEVICE_NAME_NUM);
		ConsoleUtils.disableAutoShowConsole(gefBot);
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { "Sandbox", "Device Manager" }, DEVICE_NAME_NUM);
		treeItem.select();
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Properties";
	}

	@Override
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		for (ScaDevice< ? > dev : localSca.getSandboxDeviceManager().getAllDevices()) {
			if (DEVICE_NAME_NUM.equals(dev.getLabel())) {
				return dev.getProperties();
			}
		}

		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}
