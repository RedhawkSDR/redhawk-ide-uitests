package gov.redhawk.ide.properties.view.runtime.dcd.tests;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;

import gov.redhawk.ide.properties.view.runtime.tests.AbstractPropertiesViewRuntimeTest;
import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.sca.ScaPlugin;

/**
 * Tests properties of a domain launched device selected in the REDHAWK Explorer View
 */
public class DomainDevicePropertyTest extends AbstractPropertiesViewRuntimeTest {
	// Pulled this from AbstractGraphitiDomainNodeRuntimeTest -- Should be able to make another abstract test for all
	// domain property tests that is
	// in charge of launch and tearing down the domain
	protected static String DOMAIN = DomainDevicePropertyTest.class.getSimpleName() + "_" + (int) (1000.0 * Math.random());
	protected static final String DEVICE_MANAGER = "AllPropertyTypes_DevMgr";
	protected static final String DEVICE = "AllPropertyTypesDevice";
	protected static final String DEVICE_NUM = DEVICE + "_1";
	protected static final String DEVICE_STARTED = DEVICE_NUM + " STARTED";
	
	protected String[] DEVICE_PARENT_PATH;

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
		DOMAIN = DOMAIN + (int) (1000.0 * Math.random());
		DEVICE_PARENT_PATH = new String[] { DOMAIN, "Device Managers", DEVICE_MANAGER };
		ScaExplorerTestUtils.launchDomain(bot, DOMAIN, DEVICE_MANAGER);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DEVICE_PARENT_PATH, DEVICE_NUM);
		
		ConsoleUtils.disableAutoShowConsole(gefBot);
		treeItem.select();
	}
	
	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Properties";
	}

	@Override
	protected EList<ScaAbstractProperty< ? >> getModelObjectProperties() {
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(Display.getCurrent());
		ScaDomainManager dom = registry.findDomain(DOMAIN);
		EList<ScaDeviceManager> devMgrs = dom.getDeviceManagers();
		EList<ScaDevice< ? >> devs = devMgrs.get(0).getAllDevices();
		for (ScaDevice< ? > dev : devs) {
			if (DEVICE.equals(dev.getProfileObj().getName())) {
				return dev.getProperties();
			}
		}
		return new BasicEList<ScaAbstractProperty< ? >>();
	}
}
