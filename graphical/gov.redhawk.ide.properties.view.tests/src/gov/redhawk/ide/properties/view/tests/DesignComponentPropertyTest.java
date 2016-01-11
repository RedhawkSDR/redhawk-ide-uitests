package gov.redhawk.ide.properties.view.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import gov.redhawk.ide.swtbot.WaveformUtils;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class DesignComponentPropertyTest extends AbstractPropertiesViewDesignTest {

	private final String WAVEFORM_NAME = "AllPropertyTypesDesignWf";
	private final String COMPONENT_NAME = "AllPropertyTypesComponent";
	private SoftwareAssembly sad = null;

	@Override
	protected void prepareObject() {
		WaveformUtils.createNewWaveform(bot, WAVEFORM_NAME, COMPONENT_NAME);
		setPropTabName();
		setEditor();
		editor.click(COMPONENT_NAME);
	}

	@Override
	protected void selectObject() {
		editor.click(COMPONENT_NAME);
	}

	@Override
	protected void setPropTabName() {
		PROP_TAB_NAME = "Component Properties";
	}

	@Override
	protected void setEditor() {
		editor = gefBot.gefEditor(WAVEFORM_NAME);
	}

	@Override
	protected ComponentProperties getModelPropertiesFromEditor() throws IOException {
		editor.bot().cTabItem(WAVEFORM_NAME + ".sad.xml").activate();
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		String editorText = editor.toTextEditor().getText();
		Resource resource = resourceSet.createResource(URI.createURI("mem://temp.sad.xml"), SadPackage.eCONTENT_TYPE);
		resource.load(new ByteArrayInputStream(editorText.getBytes()), null);
		sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		ComponentProperties componentProps = sad.getAllComponentInstantiations().get(0).getComponentProperties();
		if (componentProps != null) {
			return componentProps;
		} else {
			return PartitioningFactory.eINSTANCE.createComponentProperties();
		}
	}

	@Override
	protected void writeModelPropertiesToEditor(ComponentProperties componentProps) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		sad.getAllComponentInstantiations().get(0).setComponentProperties(componentProps);
		sad.eResource().save(outputStream, null);
		editor.toTextEditor().setText(outputStream.toString());
	}
}
