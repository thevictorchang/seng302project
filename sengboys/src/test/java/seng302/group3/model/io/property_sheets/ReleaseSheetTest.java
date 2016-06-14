package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.Organisation;
import seng302.group3.model.Project;
import seng302.group3.model.Release;

import java.time.LocalDate;

/**
 * Created by ntr24 on 30/05/15.
 */
public class ReleaseSheetTest {
    Organisation organisation;
    ReleaseSheet releaseSheet;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setup(){
        organisation = new Organisation("Testing Organisation");
        releaseSheet = new ReleaseSheet();

        releaseSheet.shortNameTextField.setText("Valid Text");
        releaseSheet.descriptionTextField.setText("Valid Text");
        releaseSheet.releaseDatePicker.setValue(LocalDate.now());
        releaseSheet.projectChoice.getItems().add(new Project("Project"));
        releaseSheet.projectChoice.getSelectionModel().selectFirst();
    }

    @After
    public void tearDown(){
        organisation = null;
        releaseSheet = null;
    }

    @Test
    public void testShortNameNonNullOnValidate(){
        releaseSheet.shortNameTextField.setText("");

        Assert.assertFalse("An empty short name should not validate"
                , releaseSheet.validate(organisation));
    }

    @Test
    public void testDescriptionNonNullOnValidate(){
        releaseSheet.descriptionTextField.setText("");

        Assert.assertFalse("An empty description should not validate"
                , releaseSheet.validate(organisation));
    }

    @Test
    public void testDatePickerNonNullOnValidate(){
        releaseSheet.releaseDatePicker.setValue(null);

        Assert.assertFalse("An empty date should not validate"
                , releaseSheet.validate(organisation));
    }

    @Test
    public void testProjectChoiceNonNullOnValidate(){
        releaseSheet.projectChoice.getSelectionModel().select(null);

        Assert.assertFalse("An empty date should not validate"
                , releaseSheet.validate(organisation));
    }

    @Test
    public void testNonUniqueShortNameOnValidate(){
        Release release = new Release("Non Unique Name", LocalDate.now(), new Project("New Proj"));
        organisation.addRelease(release);
        releaseSheet.shortNameTextField.setText("Non Unique Name");

        Assert.assertFalse("An non unique short name should not validate"
                , releaseSheet.validate(organisation));
    }

    @Test
    public void testUniqueShortNameOnValidate(){
        releaseSheet.shortNameTextField.setText("Unique Name 2");

        Assert.assertTrue("A unique short name should validate"
                , releaseSheet.validate(organisation));
    }

    @Test
    public void testEditShouldValidateWithNoChanges(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        Assert.assertTrue("Nothing has changed in the edit so should validate"
                , releaseSheet.validate(organisation));
    }

    @Test
    public void testFieldConstructSetsShortName(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        Assert.assertEquals("Field Construct correctly sets the short name text field",
                releaseSheet.shortNameTextField.getText(), release.getShortName());
    }

    @Test
    public void testFieldConstructSetsDescription(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        Assert.assertEquals("Field Construct correctly sets the description text field",
                releaseSheet.descriptionTextField.getText(), release.getDescription());
    }

    @Test
    public void testFieldConstructSetsReleaseDate(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        Assert.assertEquals("Field Construct correctly sets the release date field",
                releaseSheet.releaseDatePicker.getValue(), release.getReleaseDate());
    }

    @Test
    public void testFieldConstructSetsProjectField(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        Assert.assertEquals("Field Construct correctly sets the project field",
                releaseSheet.projectChoice.getSelectionModel().getSelectedItem(), release.getProject());
    }

    @Test
    public void testApplySetsTheReleasesShortName(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.apply(release);

        Assert.assertEquals("Apply correctly sets the releases short name",
                releaseSheet.shortNameTextField.getText(), release.getShortName());
    }

    @Test
    public void testApplySetsTheReleasesDescription(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.apply(release);

        Assert.assertEquals("Apply correctly sets the releases description",
                releaseSheet.descriptionTextField.getText(), release.getDescription());
    }

    @Test
    public void testApplySetsTheReleasesReleaseDate(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.apply(release);

        Assert.assertEquals("Apply correctly sets the releases date",
                releaseSheet.releaseDatePicker.getValue(), release.getReleaseDate());
    }

    @Test
    public void testApplySetsTheReleasesProject(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("New Proj"));
        release.setDescription("Description");

        releaseSheet.apply(release);

        Assert.assertEquals("Apply correctly sets the releases project",
                releaseSheet.projectChoice.getSelectionModel().getSelectedItem(), release.getProject());
    }

    @Test
    public void testEditingReleaseApplyUpdatesToTheNewShortName(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("Original Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        releaseSheet.shortNameTextField.setText("New Short Name");

        releaseSheet.apply(release);

        Assert.assertEquals("Apply when editing updates to the new value for the short name",
                releaseSheet.shortNameTextField.getText(), release.getShortName());
    }

    @Test
    public void testEditingReleasesApplyUpdatesToTheNewDescription(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("Original Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        releaseSheet.descriptionTextField.setText("New Description");

        releaseSheet.apply(release);

        Assert.assertEquals("Apply when editing updates to the new value for the description",
                releaseSheet.descriptionTextField.getText(), release.getDescription());
    }

    @Test
    public void testEditingReleasesApplyUpdatesToTheNewReleaseDate(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("Original Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        releaseSheet.releaseDatePicker.setValue(LocalDate.now());

        releaseSheet.apply(release);

        Assert.assertEquals("Apply when editing updates to the new value for the release date",
                releaseSheet.releaseDatePicker.getValue(), release.getReleaseDate());
    }

    @Test
    public void testEditingReleasesApplyUpdatesToTheNewProject(){
        Release release = new Release("Original Name", LocalDate.now(), new Project("Original Proj"));
        release.setDescription("Description");

        releaseSheet.fieldConstruct(release);

        releaseSheet.projectChoice.getSelectionModel().select(new Project("New Proj"));

        releaseSheet.apply(release);

        Assert.assertEquals("Apply when editing updates to the new value for the project",
                releaseSheet.projectChoice.getSelectionModel().getSelectedItem(), release.getProject());
    }
}
