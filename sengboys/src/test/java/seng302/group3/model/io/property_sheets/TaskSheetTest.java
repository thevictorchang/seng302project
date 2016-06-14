package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.Organisation;
import seng302.group3.model.Task;

/**
 * Created by Eddy on 02-Aug-15.
 */
public class TaskSheetTest {
    private Organisation testOrganisation;
    private Task testTask;
    private TaskSheet testTaskSheet;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        testTaskSheet = new TaskSheet();
        testOrganisation = new Organisation("Org");
        testTask = new Task("Task","1",1);
    }

    @After
    public void tearDown() throws Exception {
        testTask = null;
        testTaskSheet = null;
        testOrganisation = null;
    }

    @Test
    public void test() throws Exception {

    }

    @Test
    public void testFieldConstructShortName() throws Exception {
        testTaskSheet.fieldConstruct(testTask);
        Assert.assertTrue("The field construct should set the field to be the short name of the given task",
                testTaskSheet.shortNameTextField.getText().equals(testTask.getShortName()));
    }

    @Test
    public void testFieldConstructDescription() throws Exception {
        testTaskSheet.fieldConstruct(testTask);
        Assert.assertTrue("The field construct should set the field to be the description of the given task",
                testTaskSheet.descriptionTextField.getText().equals(testTask.getDescription()));
    }

    @Test
    public void testValidateDescriptionTextFieldEmpty() throws Exception {
        testTaskSheet.descriptionTextField.setText("");
        testTaskSheet.shortNameTextField.setText("NonEmpty");
        testTaskSheet.estimateField.setText("2");
        Assert.assertFalse("The validate will show the field as being empty",testTaskSheet.validate());
    }

    @Test
    public void testValidateDescriptionTextFieldNonEmpty() throws Exception {
        testTaskSheet.descriptionTextField.setText("NonEmpty");
        testTaskSheet.shortNameTextField.setText("NonEmpty");
        testTaskSheet.estimateField.setText("2");
        Assert.assertTrue("The validate will show the field as being non-empty", testTaskSheet.validate());
    }


    @Test
    public void testValidateEstimateTextFieldEmpty() throws Exception {
        testTaskSheet.estimateField.setText("");
        testTaskSheet.shortNameTextField.setText("NonEmpty");
        testTaskSheet.descriptionTextField.setText("NonEmpty");
        Assert.assertFalse("The validate will show the field as being empty",testTaskSheet.validate());
    }

    @Test
    public void testValidateEstimateTextFieldNonEmpty() throws Exception {
        testTaskSheet.estimateField.setText("2");
        testTaskSheet.shortNameTextField.setText("NonEmpty");
        testTaskSheet.descriptionTextField.setText("NonEmpty");
        Assert.assertTrue("The validate will show the field as being non-empty",testTaskSheet.validate());
    }

    @Test
    public void testValidateShortNameTextFieldEmpty() throws Exception {
        testTaskSheet.shortNameTextField.setText("");
        testTaskSheet.descriptionTextField.setText("NonEmpty");
        testTaskSheet.estimateField.setText("2");
        Assert.assertFalse("The validate will show the field as being empty",testTaskSheet.validate());
    }

    @Test
    public void testValidateShortNameTextFieldNonEmpty() throws Exception {
        testTaskSheet.shortNameTextField.setText("NonEmpty");
        testTaskSheet.descriptionTextField.setText("NonEmpty");
        testTaskSheet.estimateField.setText("2");
        Assert.assertTrue("The validate will show the field as being non-empty",testTaskSheet.validate());
    }
}
