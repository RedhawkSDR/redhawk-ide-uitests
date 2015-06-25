package gov.redhawk.ide.ui.tests.prf;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests involving the PRF editor and the 'property' property kind.
 */
public class PropertyKindTest extends UITest {
	
	private final String compName = "PropertyKindTest";
	private final String compLanguage = "Python";
	private final String compSpd = compName + ".spd.xml";
	private final String compPrf = compName + ".prf.xml";
	private final String[] BUTTONS = new String[] { "Add Simple", "Add Sequence", "Add Struct", "Add StructSeq" };

	/**
	 * Ensure 'configure' and 'execparam' are present only when there are deprecated properties. Also check that the
	 * default new property kind is 'property'.
	 */
	@Test
	public void checkAvailableKinds() {
		ComponentUtils.createComponentProject(bot, compName, compLanguage);
		SWTBotEditor editor = bot.editorByTitle(compName);

		// Ensure 'configure' and 'execparam' aren't in the list when adding a property
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		for (String buttonText : BUTTONS) {
			editor.bot().button(buttonText).click();
			SWTBotCombo combo = editor.bot().comboBoxWithLabel("Kind:");
			Assert.assertTrue(String.format("After clicking %s, expected a new property of kind 'property'", buttonText), combo.getText().contains("property"));
			for (String item : combo.items()) {
				if (item.contains("configure")) {
					Assert.fail(String.format("After clicking %s, found configure in the list of combo items", buttonText));
				}
				if (item.contains("execparam")) {
					Assert.fail(String.format("After clicking %s, found execparam in the list of combo items", buttonText));
				}
			}
		}

		// Replace the PRF contents
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/PropertyKindTest.prf.xml"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);
		editor.close();

		// Ensure 'configure' and 'execparam' are now present
		ProjectExplorerUtils.openProjectInEditor(bot, compName, compSpd);
		editor = bot.editorByTitle(compName);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		for (int i = 0; i < 4; i++) {
			editor.bot().tree().select(0);
			SWTBotCombo combo = editor.bot().comboBoxWithLabel("Kind:");
			boolean foundConfigure = false, foundExecParam = false;
			for (String item : combo.items()) {
				if (item.contains("configure")) {
					foundConfigure = true;
				}
				if (item.contains("execparam")) {
					foundExecParam = true;
				}
			}
			Assert.assertTrue(String.format("Couldn't find configure in the list of combo items for property #%d", i), foundConfigure);
			Assert.assertTrue(String.format("Couldn't find execparam in the list of combo items for property #%d", i), foundExecParam);
		}
	}
	
	/**
	 * Ensure the editor is okay with multiple kinds.
	 */
	@Test
	public void checkMultiKind() {
		ComponentUtils.createComponentProject(bot, compName, compLanguage);
		SWTBotEditor editor = bot.editorByTitle(compName);
		
		// Replace the PRF contents
		DiagramTestUtils.openTabInEditor(editor, compPrf);
		String prfAsString = FileUtils.read(this.getClass().getResourceAsStream("/testFiles/PropertyKindTest2.prf.xml"));
		editor.bot().styledText().setText(prfAsString);
		MenuUtils.save(editor);
		editor.close();

		// Ensure the editor shows 'configure'
		ProjectExplorerUtils.openProjectInEditor(bot, compName, compSpd);
		editor = bot.editorByTitle(compName);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.PROPERTIES_TAB);
		SWTBotCombo combo = editor.bot().comboBoxWithLabel("Kind:");
		Assert.assertTrue("Expected property to show 'configure' kind in the PRF editor", combo.getText().contains("configure"));
	}
}
