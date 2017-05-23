/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.ui.tests.projectCreation;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractCreationWizard2Test extends AbstractCreationWizardTest {

	@Test
	public void testUUID() {
		getWizardBot().textWithLabel("&Project name:").setText("WizardTest02");
		Assert.assertTrue(getWizardBot().button("Next >").isEnabled());

		getWizardBot().radio("Provide an ID").click();
		Assert.assertFalse(getWizardBot().button("Next >").isEnabled());
		getWizardBot().text(" Enter a DCE UUID");

		getWizardBot().textWithLabel("DCE UUID:").setText("187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertFalse(getWizardBot().button("Next >").isEnabled());
		getWizardBot().text(" DCE UUID must start with 'DCE:'");

		getWizardBot().textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(getWizardBot().button("Next >").isEnabled());
		getWizardBot().text(" DCE UUID must start with 'DCE:'");

		getWizardBot().textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595dz");
		Assert.assertFalse(getWizardBot().button("Next >").isEnabled());
		getWizardBot().text(" Enter a valid UUID");

		getWizardBot().textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(getWizardBot().button("Next >").isEnabled());
		getWizardBot().text(" DCE UUID must start with 'DCE:'");

		getWizardBot().radio("Generate an ID").click();
		Assert.assertTrue(getWizardBot().button("Next >").isEnabled());

		getWizardBot().radio("Provide an ID").click();
		Assert.assertFalse(getWizardBot().button("Next >").isEnabled());
		getWizardBot().text(" DCE UUID must start with 'DCE:'");

		getWizardBot().textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertTrue(getWizardBot().button("Next >").isEnabled());

		getWizardShell().close();
	}

}
