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
package gov.redhawk.ide.graphiti.ui.tests.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFileRef;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public abstract class AbstractXmlToDiagramAddTest extends UITest {

	/**
	 * There must be a port such that A[0] -> B[0] is possible.
	 * Also requires a key path=components, or path=devices, etc.
	 */
	protected abstract ComponentDescription getComponentADescription();

	private ComponentDescription compA = getComponentADescription();

	/**
	 * See above
	 */
	protected abstract ComponentDescription getComponentBDescription();

	private ComponentDescription compB = getComponentBDescription();

	/**
	 * Should create a new project and open the editor.
	 * @return
	 */
	protected abstract RHBotGefEditor createEditor(String name);

	protected enum EditorType {
		SAD,
		DCD
	};

	protected abstract EditorType getEditorType();

	private SoftwareAssembly sad;
	private DeviceConfiguration dcd;

	/**
	 * IDE-847, IDE-994, IDE-1434
	 * Add resources to the XML and have them reflected in the diagram
	 * @throws IOException
	 */
	@Test
	public void addComponentInXml() throws IOException {
		final String TEST_NAME = "addComponentInXml";

		RHBotGefEditor editor = createEditor(TEST_NAME);
		getModelFromEditorXml(editor, TEST_NAME);

		// Add two components
		ComponentFile compFileA = createComponentFile(compA);
		ComponentFile compFileB = createComponentFile(compB);
		if (getEditorType() == EditorType.SAD) {
			if (sad.getComponentFiles() == null) {
				sad.setComponentFiles(PartitioningFactory.eINSTANCE.createComponentFiles());
			}
			List<ComponentFile> componentFiles = sad.getComponentFiles().getComponentFile();
			componentFiles.add(compFileA);
			componentFiles.add(compFileB);
			List<SadComponentPlacement> placements = sad.getPartitioning().getComponentPlacement();
			placements.add(createSadPlacement(compFileA, compA.getShortName(1), 0));
			placements.add(createSadPlacement(compFileB, compB.getShortName(1), 1));
		} else {
			if (dcd.getComponentFiles() == null) {
				dcd.setComponentFiles(PartitioningFactory.eINSTANCE.createComponentFiles());
			}
			List<ComponentFile> componentFiles = dcd.getComponentFiles().getComponentFile();
			componentFiles.add(compFileA);
			componentFiles.add(compFileB);
			List<DcdComponentPlacement> placements = dcd.getPartitioning().getComponentPlacement();
			placements.add(createDcdPlacement(compFileA, compA.getShortName(1)));
			placements.add(createDcdPlacement(compFileB, compB.getShortName(1)));
		}
		writeModelToXmlEditor(editor, TEST_NAME);
		MenuUtils.save(editor);

		// Confirm shapes in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		RHContainerShapeImpl shapeA = (RHContainerShapeImpl) DiagramTestUtils.getRHContainerShape(editor, compA.getShortName(1));
		Assert.assertNotNull("Missing new resource " + compA.getFullName(), shapeA);
		Assert.assertEquals(compA.getFullName(), shapeA.getOuterText().getValue());
		Assert.assertEquals(compA.getShortName(1), shapeA.getInnerText().getValue());
		Assert.assertTrue(shapeA.getUsesPortsContainerShape().getChildren().size() > 1);
		RHContainerShapeImpl shapeB = (RHContainerShapeImpl) DiagramTestUtils.getRHContainerShape(editor, compB.getShortName(1));
		Assert.assertNotNull("Missing new resource " + compB.getFullName(), shapeB);
		Assert.assertEquals(compB.getFullName(), shapeB.getOuterText().getValue());
		Assert.assertEquals(compB.getShortName(1), shapeB.getInnerText().getValue());
		Assert.assertTrue(shapeB.getProvidesPortsContainerShape().getChildren().size() > 1);
		if (getEditorType() == EditorType.SAD) {
			ComponentShapeImpl componentShapeA = (ComponentShapeImpl) shapeA;
			Assert.assertEquals("0", componentShapeA.getStartOrderText().getValue());
			ComponentShapeImpl componentShapeB = (ComponentShapeImpl) shapeB;
			Assert.assertEquals("1", componentShapeB.getStartOrderText().getValue());
		}
	}

	/**
	 * IDE-848, IDE-994
	 * Add a connection to XML and have it reflected in the diagram.
	 * @throws IOException
	 */
	@Test
	public void addConnectionInXml() throws IOException {
		final String TEST_NAME = "addComponentInXml";

		RHBotGefEditor editor = createEditor(TEST_NAME);
		DiagramTestUtils.addFromPaletteToDiagram(editor, compA.getFullName(), 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, compB.getFullName(), 300, 0);

		getModelFromEditorXml(editor, TEST_NAME);
		if (getEditorType() == EditorType.SAD) {
			if (sad.getConnections() == null) {
				sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
			}
			List<SadConnectInterface> connections = sad.getConnections().getConnectInterface();
			SadConnectInterface connection = SadFactory.eINSTANCE.createSadConnectInterface("connection_1", compA.getOutPort(0), compA.getShortName(1),
				compB.getInPort(0), compB.getShortName(1));
			connections.add(connection);
		} else {
			if (dcd.getConnections() == null) {
				dcd.setConnections(DcdFactory.eINSTANCE.createDcdConnections());
			}
			List<DcdConnectInterface> connections = dcd.getConnections().getConnectInterface();
			DcdConnectInterface connection = DcdFactory.eINSTANCE.createDcdConnectInterface("connection_1", compA.getOutPort(0), compA.getShortName(1),
				compB.getInPort(0), compB.getShortName(1));
			connections.add(connection);
		}
		writeModelToXmlEditor(editor, TEST_NAME);
		MenuUtils.save(editor);

		// Confirm connections in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, compA.getShortName(1), compA.getOutPort(0));
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, compB.getShortName(1), compB.getInPort(0));
		List<SWTBotGefConnectionEditPart> usesConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		List<SWTBotGefConnectionEditPart> providesConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, providesEditPart);
		Assert.assertEquals(1, usesConnections.size());
		Assert.assertEquals(1, providesConnections.size());
		Assert.assertEquals(usesConnections.get(0).part(), providesConnections.get(0).part());
	}

	private void getModelFromEditorXml(SWTBotEditor editor, String testName) throws IOException {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		if (getEditorType() == EditorType.SAD) {
			DiagramTestUtils.openTabInEditor(editor, testName + ".sad.xml");
			String editorText = editor.toTextEditor().getText();
			Resource resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
			resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
			sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		} else {
			DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
			String editorText = editor.toTextEditor().getText();
			Resource resource = resourceSet.createResource(URI.createURI("mem://DeviceManager.dcd.xml"), DcdPackage.eCONTENT_TYPE);
			resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
			dcd = DeviceConfiguration.Util.getDeviceConfiguration(resource);
		}
	}

	private void writeModelToXmlEditor(SWTBotEditor editor, String testName) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (getEditorType() == EditorType.SAD) {
			DiagramTestUtils.openTabInEditor(editor, testName + ".sad.xml");
			sad.eResource().save(outputStream, null);
		} else {
			DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
			dcd.eResource().save(outputStream, null);
		}
		editor.toTextEditor().setText(outputStream.toString());
	}

	private ComponentFile createComponentFile(ComponentDescription description) {
		if (getEditorType() == EditorType.SAD) {
			return SadFactory.eINSTANCE.createComponentFile(description.getShortName(), getPath(description));
		} else {
			return DcdFactory.eINSTANCE.createComponentFile(description.getShortName(), getPath(description));
		}
	}

	private SadComponentPlacement createSadPlacement(ComponentFile compFile, String id, int start) {
		ComponentFileRef ref = PartitioningFactory.eINSTANCE.createComponentFileRef(compFile.getId());
		SadComponentInstantiation instance = SadFactory.eINSTANCE.createSadComponentInstantiation(id, BigInteger.valueOf(start), id, id);
		SadComponentPlacement sadPlacement = SadFactory.eINSTANCE.createSadComponentPlacement(ref, Arrays.asList(instance));
		return sadPlacement;
	}

	private DcdComponentPlacement createDcdPlacement(ComponentFile compFile, String id) {
		ComponentFileRef ref = PartitioningFactory.eINSTANCE.createComponentFileRef(compFile.getId());
		DcdComponentInstantiation instance = DcdFactory.eINSTANCE.createDcdComponentInstantiation(id, id);
		DcdComponentPlacement dcdPlacement = DcdFactory.eINSTANCE.createDcdComponentPlacement(ref, Arrays.asList(instance));
		return dcdPlacement;
	}

	private String getPath(ComponentDescription description) {
		String path = description.getKey("path");
		Assert.assertNotNull("Internal test error (need path key)", path);

		StringBuilder sb = new StringBuilder();
		sb.append('/');
		sb.append(path);
		sb.append('/');
		sb.append(description.getFullName().replace('.', '/'));
		sb.append('/');
		sb.append(description.getShortName());
		sb.append(".spd.xml");
		return sb.toString();
	}
}
