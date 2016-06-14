package seng302.group3.model.search;

import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.*;
import seng302.group3.JavaFXThreadingRule;

public class FilterUtilTest {

    ComboBox testCombo;
    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        testCombo = new ComboBox();
        testCombo.getItems().addAll("abc", "bcd", "cde");

    }

    @After
    public void tearDown() throws Exception {
        testCombo = null;
    }

    @Test
    public void testFilterKeepsGoodContainingResult() throws Exception {
        FilterUtil.autoCompleteComboBox(testCombo, FilterUtil.AutoCompleteMode.CONTAINING);
        testCombo.getEditor().setText("b");
        javafx.scene.input.KeyEvent event  = new javafx.scene.input.KeyEvent(KeyEvent.KEY_RELEASED, null, null, KeyCode.A, false, false, false, false);
        testCombo.fireEvent(event);
        Assert.assertTrue("The combobox items contain the string abc still", testCombo.getItems().contains("abc"));
    }

    @Test
    public void testFilterRemovesBadContainingResult() throws Exception {
        FilterUtil.autoCompleteComboBox(testCombo, FilterUtil.AutoCompleteMode.CONTAINING);
        testCombo.getEditor().setText("b");
        javafx.scene.input.KeyEvent event  = new javafx.scene.input.KeyEvent(KeyEvent.KEY_RELEASED, null, null, KeyCode.A, false, false, false, false);
        testCombo.fireEvent(event);
        Assert.assertFalse("The combobox items contain the string cde still", testCombo.getItems().contains("cde"));
    }
    @Test
    public void testFilterKeepsGoodStartsWithResult() throws Exception {
        FilterUtil.autoCompleteComboBox(testCombo, FilterUtil.AutoCompleteMode.STARTS_WITH);
        testCombo.getEditor().setText("a");
        javafx.scene.input.KeyEvent event  = new javafx.scene.input.KeyEvent(KeyEvent.KEY_RELEASED, null, null, KeyCode.A, false, false, false, false);
        testCombo.fireEvent(event);
        Assert.assertTrue("The combobox items contain the string abc still", testCombo.getItems().contains("abc"));
    }

    @Test
    public void testFilterRemovesBadStartsWithResult() throws Exception {
        FilterUtil.autoCompleteComboBox(testCombo, FilterUtil.AutoCompleteMode.STARTS_WITH);
        testCombo.getEditor().setText("a");
        javafx.scene.input.KeyEvent event  = new javafx.scene.input.KeyEvent(KeyEvent.KEY_RELEASED, null, null, KeyCode.A, false, false, false, false);
        testCombo.fireEvent(event);
        Assert.assertFalse("The combobox items contain the string bcd still", testCombo.getItems().contains("bcd"));
    }
}