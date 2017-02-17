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

import gov.redhawk.ide.graphiti.ui.tests.AbstractRequirementsPropertiesTest;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.Requirements;

/**
 * 
 */
public class RequirementsPropertiesTest extends AbstractRequirementsPropertiesTest {

	private static final String DOMAIN = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String GPP_1 = "GPP_1";
	private DcdComponentInstantiation compInst;

	@Override
	protected void createProject() {
		final String projectName = "RequirementsNode";
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN);
		final RHBotGefEditor editor = gefBot.rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);

		editor.getEditPart(GPP_1).select();
		this.compInst = DiagramTestUtils.getDeviceObject(editor, GPP_1);
	}

	@Override
	protected void openTargetSdrProject() {
		final String nodeName = "RequirementsNode";
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, new String[] { "Target SDR", "Nodes" }, nodeName, DiagramType.GRAPHITI_NODE_EDITOR);

		RHBotGefEditor editor = gefBot.rhGefEditor(nodeName);
		editor.getEditPart(GPP_1).select();
	}

	@Override
	protected Requirements getRequirements() {
		return compInst.getDeployerRequires();
	}

}
