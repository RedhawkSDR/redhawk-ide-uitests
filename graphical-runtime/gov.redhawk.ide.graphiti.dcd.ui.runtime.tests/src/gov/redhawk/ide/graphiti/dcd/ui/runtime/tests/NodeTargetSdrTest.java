/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.dcd.ui.runtime.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

public class NodeTargetSdrTest extends UIRuntimeTest {

	private RHSWTGefBot gefBot;

	@Before
	public void beforeTest() throws Exception {
		super.before();
		gefBot = new RHSWTGefBot();
	}

	/**
	 * IDE-1530 NPE When trying to open a Node Diagram containing a device/service not in the SDR</br>
	 * If a device/service is not found in the Target SDR, it should still be drawn on the diagram,
	 * with an appropriate error marker.
	 */
	@Test
	public void deviceNotInSdrTest() {
		final String projectLanguage = "Python";

		// Create, generate, and export test device
		final String deviceName = "TestDevice";
		final String[] devParentPath = { "Target SDR", "Devices" };
		DeviceUtils.createDeviceProject(bot, deviceName, projectLanguage);
		generatedAndExportProject(deviceName);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, devParentPath, deviceName);

		// Create, generate, and export test service
		final String serviceName = "TestService";
		final String serviceIdl = "IDL:CF/AggregateExecutableDevice:1.0";
		final String[] servParentPath = { "Target SDR", "Services" };
		ServiceUtils.createServiceProject(bot, serviceName, serviceIdl, projectLanguage);
		generatedAndExportProject(serviceName);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, servParentPath, serviceName);

		// Create and export test node
		final String nodeName = "TestNode";
		final String domainName = "REDHAWK_DEV";
		final String[] nodeParentPath = { "Target SDR", "Nodes" };
		NodeUtils.createNewNodeProject(bot, nodeName, domainName);
		RHBotGefEditor editor = gefBot.rhGefEditor(nodeName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, deviceName, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, serviceName, 250, 250);
		editor.save();
		editor.close();
		StandardTestActions.exportProject(nodeName, bot);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, nodeParentPath, nodeName);

		// Delete the device and service projects from the Target SDR
		ScaExplorerTestUtils.deleteFromTargetSdr(gefBot, devParentPath, deviceName);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, servParentPath, serviceName);
		ScaExplorerTestUtils.deleteFromTargetSdr(gefBot, servParentPath, serviceName);

		// Confirm that device and service still show up in the diagram (with expected error messages)
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, nodeParentPath, nodeName);
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, nodeParentPath, nodeName, DiagramType.GRAPHITI_NODE_EDITOR);
		editor = gefBot.rhGefEditor(nodeName);
		final String errorText = "< Component Bad Reference >";
		
		SWTBotGefEditPart devicePart = editor.getEditPart(deviceName + "_1");
		RHContainerShape deviceModel = (RHContainerShape) devicePart.part().getModel();
		Assert.assertEquals("Expected error text not found", errorText, deviceModel.getOuterText().getValue());

		SWTBotGefEditPart servicePart = editor.getEditPart(serviceName + "_1");
		RHContainerShape serviceModel = (RHContainerShape) servicePart.part().getModel();
		Assert.assertEquals("Expected error text not found", errorText, serviceModel.getOuterText().getValue());
	}

	private void generatedAndExportProject(String projectName) {
		SWTBotEditor editor = bot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, editor);
		bot.waitUntil(new WaitForBuild(), 30000);
		bot.closeAllEditors();
		StandardTestActions.exportProject(projectName, bot);
	}
}
