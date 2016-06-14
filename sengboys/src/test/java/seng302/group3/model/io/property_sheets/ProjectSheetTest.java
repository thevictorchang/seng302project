package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.Organisation;
import seng302.group3.model.Project;

/**
 * Created by jwc78 on 30/05/15.
 * A testing suite for Project Sheet (the dialog for creating and editing projects)
 */
public class ProjectSheetTest {
    private Organisation testOrganisation;
    private ProjectSheet projectSheet;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        projectSheet = new ProjectSheet();
        testOrganisation = new Organisation("testOrg");
    }

    @After
    public void tearDown() throws Exception{
        testOrganisation = null;
        projectSheet = null;
    }

    @Test
    public void TestNullShortNameTextFieldOnValidate() throws Exception{
        projectSheet.shortNameTextField.setText("");
        projectSheet.longNameTextField.setText("ValidLongName");
        projectSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("As shortName is empty this should validate with a false result"
                , projectSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullLongNameTextFieldOnValidate() throws Exception{
        projectSheet.shortNameTextField.setText("ValidShortName");
        projectSheet.longNameTextField.setText("");
        projectSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("As longName is empty this should validate with a false result"
            , projectSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullDescriptionTextFieldOnValidate() throws Exception{
        projectSheet.shortNameTextField.setText("ValidShortName");
        projectSheet.longNameTextField.setText("ValidLongName");
        projectSheet.descriptionTextField.setText("");

        Assert.assertFalse("As description is empty this should validate with a false result"
            , projectSheet.validate(testOrganisation));
    }

    @Test
    public void TestNoNullTextFieldsOnValidate() throws Exception{
        projectSheet.shortNameTextField.setText("ValidShortName");
        projectSheet.longNameTextField.setText("ValidLongName");
        projectSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertTrue("As there are no empty field should validate",
            projectSheet.validate(testOrganisation));
    }

    @Test
    public void TestNonUniqueNameOnValidate() throws Exception{
        Project firstProject = new Project("ShortName", "LongName", "Description");
        testOrganisation.addProject(firstProject);

        projectSheet.shortNameTextField.setText("ShortName");
        projectSheet.longNameTextField.setText("ValidLongName");
        projectSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("Because the name is non-unique in the organisation validates to false"
            , projectSheet.validate(testOrganisation));
    }

    @Test
    public void TestUniqueNameOnValidate() throws Exception{
        Project firstProject = new Project("ShortName", "LongName", "Description");
        testOrganisation.addProject(firstProject);

        projectSheet.shortNameTextField.setText("ValidShortName");
        projectSheet.longNameTextField.setText("ValidLongName");
        projectSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertTrue("Because the name is unique in the organisation validates to true",
            projectSheet.validate(testOrganisation));
    }

    @Test
    public void TestValidationWhenAProjectHasBeenPassedToBeConstructed() throws Exception{
        Project firstProject = new Project("ShortName", "LongName", "Description");
        testOrganisation.addProject(firstProject);

        projectSheet.fieldConstruct(firstProject);

        Assert.assertTrue("The project is a valid project so it should validate"
            , projectSheet.validate(testOrganisation, firstProject));
    }

    @Test
    public void TestLoadingAProjectToBeEditedShortName() throws Exception{
        Project firstProject = new Project("ShortName", "LongName", "Description");
        testOrganisation.addProject(firstProject);

        projectSheet.fieldConstruct(firstProject);

        Assert.assertTrue("The project's short name should be the same as the textfield value",
            projectSheet.shortNameTextField.getText().equals("ShortName"));
    }

    @Test
    public void TestLoadingAProjectToBeEditedLongName() throws Exception{
        Project firstProject = new Project("ShortName", "LongName", "Description");
        testOrganisation.addProject(firstProject);

        projectSheet.fieldConstruct(firstProject);

        Assert.assertTrue("The project's long name should be the same as the textfield value",
            projectSheet.longNameTextField.getText().equals("LongName"));
    }

    @Test
    public void TestLoadingAProjectToBeEditedDescription() throws Exception{
        Project firstProject = new Project("ShortName", "LongName", "Description");
        testOrganisation.addProject(firstProject);

        projectSheet.fieldConstruct(firstProject);

        Assert.assertTrue("The project's description should be the same as the textfield value",
            projectSheet.descriptionTextField.getText().equals("Description"));
    }

}
