/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.projectCreation;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.ui.tests.projectCreation.util.StandardCodegenInfo;

public class ServiceWizardTest extends ComponentWizardTest {
	
	// The default service interface
	private static final String SERVICE_INTERFACE = "IDL:CF/Logging:1.0";

	/**
	 * Abstracting this out to let individual test methods specify their desired IDL interface
	 */
	private String interfaceId;

	@Before
	@Override
	public void before() throws Exception {
		super.before();
		setInterfaceId(SERVICE_INTERFACE);
	}

	@Override
	protected String getProjectType() {
		return "REDHAWK Service Project";
	}

	protected void testServiceProjectCreation(String name, String lang, String generator, String template) {
		setServiceInWizard();
		super.testProjectCreation(name, lang, generator, new StandardCodegenInfo(template));

		@SuppressWarnings("unchecked")
		Matcher<Widget> matcher = allOf(widgetOfType(CTabItem.class), withMnemonic("Ports"));
		List<Widget> cTabs = bot.getFinder().findControls(matcher);
		if (!cTabs.isEmpty()) {
			Assert.fail("Ports tab should not be displayed");
		}
	}

	public void setServiceInWizard() {
		ServiceUtils.setServiceIdl(bot, getInterfaceId());
	}

	@Test
	@Override
	public void testPythonCreation() {
		testServiceProjectCreation("ServiceWizardTest01", "Python", "Python Code Generator", "Default Service");
		getWizardShell().close();
	}

	@Test
	@Override
	public void testCppCreation() {
		String projectName = "ServiceWizardTest01";
		testServiceProjectCreation(projectName, "C++", "C++ Code Generator", "Default Service");
		checkCodeElementValues(projectName, false);
		getWizardShell().close();
	}

	@Test
	@Override
	public void testJavaCreation() {
		testServiceProjectCreation("ServiceWizardTest01", "Java", "Java Code Generator", "Default Service");
		getWizardShell().close();
	}

	@Test
	@Override
	public void testBackNext() {
		setServiceInWizard();
		super.testBackNext();
	}

	@Test
	@Override
	public void testContributedPropertiesUI() {
		setServiceInWizard();
		super.testContributedPropertiesUI();
	}

	@Test
	@Override
	public void nonDefaultLocation() throws IOException {
		setServiceInWizard();

		super.nonDefaultLocation();
	}

	@Test
	@Override
	public void uuid() {
		setServiceInWizard();

		super.uuid();
	}

	/**
	 * IDE-1919 - Test filter functionality of the IDL Selection Wizard
	 */
	@Test
	public void testIdlFilter() {
		bot.button("Browse...", 1).click();
		SWTBotShell idlShell = bot.shell("Select an interface");
		final SWTBot idlBot = idlShell.bot();

		// Check that non-standard interfaces are hidden
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "PortSupplier" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "Resource" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CORBA_InitialReferences" }, false);
		idlBot.text().setText(SERVICE_INTERFACE);
		idlBot.sleep(250); // Wait a moment for the filter to update

		// Check that filter works as expected
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "PortSupplier" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "Resource" }, false);
		assertIdlEntry(idlBot.tree(), new String[] { "CORBA_InitialReferences" }, false);

		// Clear filter entry box and check that default state has returned
		idlBot.toolbarButtonWithTooltip("Clear").click();
		idlBot.sleep(250); // Wait a moment for the filter to update
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "PortSupplier" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "Resource" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CORBA_InitialReferences" }, false);

		// Click "Show all interfaces" and check that everything is visible
		idlBot.checkBox().click();
		idlBot.sleep(250); // Wait a moment for the filter to update
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "PortSupplier" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "Resource" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CORBA_InitialReferences" }, true);

		// Re-enter the filter text and test accordingly
		idlBot.text().setText(SERVICE_INTERFACE);
		idlBot.sleep(250); // Wait a moment for the filter to update
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "PortSupplier" }, true);
		assertIdlEntry(idlBot.tree(), new String[] { "CF", "Resource" }, false);
		assertIdlEntry(idlBot.tree(), new String[] { "CORBA_InitialReferences" }, false);

		idlBot.button("Cancel").click();
		getWizardBot().button("Cancel").click();
	}

	private void assertIdlEntry(SWTBotTree tree, String[] entryPath, boolean shouldAppear) {
		try {
			tree.expandNode(entryPath);
			if (!shouldAppear) {
				Assert.fail("Interface " + entryPath[entryPath.length - 1] + " should not be visible");
			}
		} catch (WidgetNotFoundException e) {
			if (shouldAppear) {
				Assert.fail("Expected interface not found: " + entryPath[entryPath.length - 1]);
			}
		}
	}

	/**
	 * IDE-1111: Test creation of service with dots in the name
	 */
	@Test
	@Override
	public void testNamespacedObjectCreation() {
		testServiceProjectCreation("namespaced.service.IDE1111", "Python", null, null);
		Assert.assertEquals("code.entrypoint", "python/IDE1111.py", bot.activeEditor().bot().textWithLabel("Entry Point:").getText());
		verifyEditorTabPresent("IDE1111.spd.xml");
		verifyEditorTabPresent("IDE1111.scd.xml");
	}

	/**
	 * IDE-1901 - Services whose IDL interface inherits form PropertySet should generate a prf.xml file 
	 */
	@Test
	public void testPropertySetService() {
		String projectName = "PropertySetService";
		setInterfaceId("IDL:CF/PropertyEmitter:1.0");

		testServiceProjectCreation(projectName, "Python", null, null);
		verifyEditorTabPresent(projectName + ".spd.xml");
		verifyEditorTabPresent(projectName + ".prf.xml");
		verifyEditorTabPresent(projectName + ".scd.xml");
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

}
