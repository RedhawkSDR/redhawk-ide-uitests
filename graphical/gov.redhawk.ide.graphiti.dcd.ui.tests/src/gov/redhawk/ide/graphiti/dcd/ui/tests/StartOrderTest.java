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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import java.math.BigInteger;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.StartOrderUtils;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

public class StartOrderTest extends AbstractGraphitiTest {

	private static final String GPP = "GPP";
	private static final String GPP_1 = "GPP_1";
	private static final String DEVICE_STUB = "DeviceStub";
	private static final String DEVICE_STUB_1 = "DeviceStub_1";
	private static final String SERVICE_STUB = "ResourceServiceStub";
	private static final String SERVICE_STUB_1 = "ResourceServiceStub_1";

	private static final String DOMAIN = "REDHAWK_DEV";

	private String nodeName;

	/**
	 * IDE-1944
	 * Test ability to set and change start order.
	 */
	@Test
	public void changeStartOrderTest() {
		nodeName = "Change_Start_Order";

		// Create a new empty node
		NodeUtils.createNewNodeProject(bot, nodeName, DOMAIN);
		RHBotGefEditor editor = gefBot.rhGefEditor(nodeName);

		// Add devices to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 100, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 100, 150);

		// Get device objects
		DcdComponentInstantiation gppCompInst = DiagramTestUtils.getDeviceObject(editor, GPP_1);
		DcdComponentInstantiation deviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB_1);

		// Initial assertion
		MenuUtils.save(editor);
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, gppCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, deviceStubCompInst.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, GPP_1, "0", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, DEVICE_STUB_1, "1", false));

		// Increase start order test
		StartOrderUtils.moveStartOrderLater(editor, GPP_1);
		MenuUtils.save(editor);
		gppCompInst = DiagramTestUtils.getDeviceObject(editor, GPP_1);
		deviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB_1);
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, gppCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, deviceStubCompInst.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, GPP_1, "1", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, DEVICE_STUB_1, "0", false));

		// Decrease start order test
		StartOrderUtils.moveStartOrderEarlier(editor, GPP_1);
		MenuUtils.save(editor);
		gppCompInst = DiagramTestUtils.getDeviceObject(editor, GPP_1);
		deviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB_1);
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, gppCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, deviceStubCompInst.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, GPP_1, "0", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, DEVICE_STUB_1, "1", false));
	}

	/**
	 * IDE-1944
	 * Start order should be treated as an optional field, and should not cause errors when null
	 * Similar steps changeStartOrderTest(), but includes a device without a defined start order
	 */
	@Test
	public void changeStartOrderWithNullTest() {
		nodeName = "Null_Start_Order";

		NodeUtils.createNewNodeProject(bot, nodeName, DOMAIN, GPP);
		RHBotGefEditor editor = gefBot.rhGefEditor(nodeName);
		final DcdComponentInstantiation tmpGpp = DiagramTestUtils.getDeviceObject(editor, GPP);

		// GPP will be created with a start order, so set it to null now and refresh the diagram
		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(tmpGpp);
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				tmpGpp.setStartOrder(null);
			}
		});
		editor.saveAndClose();
		ProjectExplorerUtils.openProjectInEditor(bot, nodeName, "DeviceManager.dcd.xml");
		editor = gefBot.rhGefEditor(nodeName);

		// Add an additional device and service to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 10, 150);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SERVICE_STUB, 200, 10);
		MenuUtils.save(editor);

		// Get device/service objects
		DcdComponentInstantiation gppCompInst = DiagramTestUtils.getDeviceObject(editor, GPP);
		DcdComponentInstantiation deviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB);
		DcdComponentInstantiation serviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, SERVICE_STUB);
		Assert.assertNotNull(gppCompInst);
		Assert.assertNotNull(deviceStubCompInst);
		Assert.assertNotNull(serviceStubCompInst);

		// Initial assertion
		Assert.assertNull("Start Order should be null", gppCompInst.getStartOrder());
		Assert.assertEquals("Start order is incorrect", BigInteger.ZERO, deviceStubCompInst.getStartOrder());
		Assert.assertEquals("Start order is incorrect", BigInteger.ONE, serviceStubCompInst.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, GPP, "", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, DEVICE_STUB, "0", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, SERVICE_STUB, "1", false));

		// Increase start order test
		StartOrderUtils.moveStartOrderLater(editor, DEVICE_STUB);
		MenuUtils.save(editor);
		gppCompInst = DiagramTestUtils.getDeviceObject(editor, GPP);
		deviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB);
		serviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, SERVICE_STUB);
		Assert.assertNull("Start Order should be null", gppCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, deviceStubCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, serviceStubCompInst.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, GPP, "", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, DEVICE_STUB, "1", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, SERVICE_STUB, "0", false));

		// Decrease start order test
		StartOrderUtils.moveStartOrderEarlier(editor, DEVICE_STUB);
		MenuUtils.save(editor);
		gppCompInst = DiagramTestUtils.getDeviceObject(editor, GPP);
		deviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB);
		serviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, SERVICE_STUB);
		Assert.assertNull("Start Order should be null", gppCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, deviceStubCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, serviceStubCompInst.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, GPP, "", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, DEVICE_STUB, "0", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, SERVICE_STUB, "1", false));

		// Check that deletion causes start orders to update
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(DEVICE_STUB));
		MenuUtils.save(editor);
		gppCompInst = DiagramTestUtils.getDeviceObject(editor, GPP);
		serviceStubCompInst = DiagramTestUtils.getDeviceObject(editor, SERVICE_STUB);
		Assert.assertNull("Start Order should be null", gppCompInst.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, serviceStubCompInst.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, GPP, "", false));
		Assert.assertTrue("Graphical start order object is incorrect", StartOrderUtils.correctStartOrderStylingAndValue(editor, SERVICE_STUB, "0", false));
	}

	/**
	 * IDE-1944
	 * Checks to confirm that start order matches the order in which resources are dragged from the palette to
	 * the diagram
	 */
	@Test
	public void checkStartOrderSequence() {
		nodeName = "Start_Order_Seq";
		final String[] resources = { GPP, DEVICE_STUB, SERVICE_STUB };
		final String[] resourceInstances = { GPP_1, DEVICE_STUB_1, SERVICE_STUB_1 };

		NodeUtils.createNewNodeProject(gefBot, nodeName, DOMAIN);
		RHBotGefEditor editor = gefBot.rhGefEditor(nodeName);
		// Add and check start order
		int xCoord = 0;
		for (int i = 0; i < resourceInstances.length; i++) {
			DiagramTestUtils.addFromPaletteToDiagram(editor, resources[i], xCoord, 0);
			Assert.assertEquals(i, StartOrderUtils.getStartOrder(editor, resourceInstances[i]));
			xCoord += 250;
		}
	}

}
