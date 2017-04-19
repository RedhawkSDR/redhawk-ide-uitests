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
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.ui.tests.projectCreation.util.StandardCodegenInfo;

public class ServiceWizardTest extends ComponentWizardTest {
	private static final String SERVICE_INTERFACE = "IDL:CF/PortSupplier:1.0";

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
		ServiceUtils.setServiceIdl(bot, SERVICE_INTERFACE);
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
	
}
