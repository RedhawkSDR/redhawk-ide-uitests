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

import gov.redhawk.ide.ui.tests.projectCreation.util.StandardCodegenInfo;

public class ServiceWizardTest extends ComponentWizardTest {
	

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
		wizardBot.textWithLabel("Service Interface").setText("IDL:IDETEST/SampleInterface:1.0");
	}
	
	@Test
	@Override
	public void testPythonCreation() {
		testServiceProjectCreation("ServiceWizardTest01", "Python", "Python Code Generator", "Default Service");
		wizardShell.close();
	}

	@Test
	@Override
	public void testCppCreation() {
		testServiceProjectCreation("ServiceWizardTest01", "C++", "C++ Code Generator", "Default Service");
		wizardShell.close();
	}

	@Test
	@Override
	public void testJavaCreation() {
		testServiceProjectCreation("ServiceWizardTest01", "Java", "Java Code Generator", "Default Service");
		wizardShell.close();
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
	public void testNonDefaultLocation() throws IOException {
		setServiceInWizard();
		super.testNonDefaultLocation();
	}
	
	@Test
	@Override
	public void testUUID() {
		setServiceInWizard();
		super.testUUID();
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
