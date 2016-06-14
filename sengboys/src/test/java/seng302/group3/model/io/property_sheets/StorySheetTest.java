package seng302.group3.model.io.property_sheets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.*;
import seng302.group3.model.io.Editor;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by ntr24 on 30/05/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class StorySheetTest {

    Organisation organisation;
    Person person;

    @Mock
    private Editor mockEditor;

    private StorySheet storySheet;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setup(){
        organisation = new Organisation("Test Org");
        storySheet = spy(new StorySheet(organisation));
        Skill PO = new Skill("PO", "PO");
        organisation.addSkill(PO);
        person = new Person("p","p1","p2");
        person.addSkill(PO);


        storySheet.shortNameTextField.setText("Valid String");
        storySheet.fullNameTextField.setText("Valid String");
        storySheet.descriptionTextField.setText("Valid String");
    }


    @Test
    public void testNonNullShortNameTextFieldOnValidate(){
        storySheet.shortNameTextField.setText("");

        Assert.assertFalse("Validation should not allow a null value short name", storySheet.validate());
    }

    @Test
    public void testNonNullFullNameTextFieldOnValidate(){
        storySheet.fullNameTextField.setText("");

        Assert.assertFalse("Validation should not allow a null value full name", storySheet.validate());
    }

    @Test
    public void testNonNullDescriptionTextFieldOnValidate(){
        storySheet.descriptionTextField.setText("");

        Assert.assertFalse("Validation should not allow a null value description", storySheet.validate());
    }

    @Test
    public void testMultipleNullTextFieldsOnValidate(){
        storySheet.shortNameTextField.setText("");
        storySheet.fullNameTextField.setText("");
        storySheet.descriptionTextField.setText("");

        Assert.assertFalse("Validation should not allow multiple null values", storySheet.validate());
    }

    @Test
    public void testNullCreatorPersonOnValidate(){

        Assert.assertFalse("Validation should not allow a null creator", storySheet.validate());
    }

    @Test
    public void testNonNullCreatorPersonOnValidate(){

        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));

        Assert.assertTrue("Validation should pass with all text fields set and creator selected",
                storySheet.validate());
    }

    @Test
    public void testNonUniqueShortNameOnValidate(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));

        storySheet.shortNameTextField.setText("non unique name");

        organisation.addStory(new Story("non unique name", "full name", "description"));

        Assert.assertFalse("Validation should not allow a non unique short name", storySheet.validate());
    }

    @Test
    public void testUniqueShortNameOnValidate(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));

        storySheet.shortNameTextField.setText("unique name");

        organisation.addStory(new Story("unique name 2", "full name", "description"));

        Assert.assertTrue("Validation should allow a unique short name", storySheet.validate());
    }

    @Test
    public void testAdditionalUniquenessCheckerStoriesForNonUniqueStoryOnValidate(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));

        Story story1 = new Story("non unique name", "full name", "description");
        Collection<Story> additionalStories = new ArrayList<>();
        additionalStories.add(story1);

        storySheet.setAdditionalUniqueCheck(additionalStories);

        storySheet.shortNameTextField.setText("non unique name");

        Assert.assertFalse("Validation should not allow a non unique short name with additional checker stories",
                storySheet.validate());
    }

    @Test
    public void testAdditionalUniquenessCheckerStoriesForUniqueStoryOnValidate(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));

        Story story1 = new Story("unique name", "full name", "description");
        Collection<Story> additionalStories = new ArrayList<>();
        additionalStories.add(story1);

        storySheet.setAdditionalUniqueCheck(additionalStories);

        storySheet.shortNameTextField.setText("unique name 2");

        Assert.assertTrue("Validation should allow a unique short name with additional checker stories",
                storySheet.validate());
    }

    @Test
    public void testConstructStoryForEditingShortNameIsSetCorrectly(){
        Story story = new Story("Story short name", "Story full name", "Story Description");

        storySheet.fieldConstruct(story);

        Assert.assertEquals("The Short name text field should be set to that of the given stories",
                story.getShortName(), storySheet.shortNameTextField.getText());
    }

    @Test
    public void testConstructStoryForEditingFullNameIsSetCorrectly(){
        Story story = new Story("Story short name", "Story full name", "Story Description");

        storySheet.fieldConstruct(story);

        Assert.assertEquals("The Full name text field should be set to that of the given stories",
                story.getFullName(), storySheet.fullNameTextField.getText());
    }

    @Test
    public void testConstructStoryForEditingDescriptionIsSetCorrectly(){
        Story story = new Story("Story short name", "Story full name", "Story Description");

        storySheet.fieldConstruct(story);

        Assert.assertEquals("The Description text field should be set to that of the given stories",
                story.getDescription(), storySheet.descriptionTextField.getText());
    }

    @Test
    public void testConstructStoryForEditingCreatorIsSetCorrectly(){
        Story story = new Story("Story short name", "Story full name", "Story Description");
        Person creator = new Person("Creator Person");

        story.setCreatorPerson(creator);

        storySheet.fieldConstruct(story);

        Assert.assertEquals("The Creator Person should be set to that of the given stories",
                story.getCreatorPerson(), storySheet.personComboBox.getSelectionModel().getSelectedItem());
    }

    @Test
    public void testConstructStoryForEditingNullCreatorIsSetCorrectly(){
        Story story = new Story("Story short name", "Story full name", "Story Description");

        storySheet.fieldConstruct(story);

        Assert.assertEquals("The Creator Person should be set to 'None' if the story doesn't have one",
                "None", storySheet.personComboBox.getSelectionModel().getSelectedItem());
    }

    @Test
    public void testConstructStoryForEditingBacklogIsSetCorrectly(){
        Story story = new Story("Story short name", "Story full name", "Story Description");
        Backlog backlog = new Backlog("Backlog");

        story.setBacklog(backlog);

        storySheet.fieldConstruct(story);

        Assert.assertEquals("The Backlog should be set to that of the given stories",
                story.getBacklog(), storySheet.backlogComboBox.getSelectionModel().getSelectedItem());
    }

    @Test
    public void testConstructStoryForEditingNullBacklogIsSetCorrectly(){
        Story story = new Story("Story short name", "Story full name", "Story Description");

        storySheet.fieldConstruct(story);

        Assert.assertEquals("The Backlog should be set to 'None' if the story doesnt have one",
                "None", storySheet.backlogComboBox.getSelectionModel().getSelectedItem());
    }

    @Test
    public void testStoryShortNameShouldNotConflictWithItselfWhenEditingOnValidate(){
        Story story = new Story("Story short name", "Story full name", "Story Description");
        story.setCreatorPerson(new Person("Person"));

        organisation.addStory(story);
        storySheet.fieldConstruct(story);

        storySheet.shortNameTextField.setText("Changed String");
        storySheet.shortNameTextField.setText("Story short name");

        Assert.assertTrue("Editing a Skill not changing the short name should not conflict with a uniqueness check",
                storySheet.validate());
    }

    @Test
    public void testApplyIsCorrectlySettingTheStoryShortName(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));
        storySheet.shortNameTextField.setText("New Short Name");

        Story story = new Story();

        storySheet.apply(story);

        Assert.assertEquals("The Story short name should be set to content of the short name text field ",
                storySheet.shortNameTextField.getText() , story.getShortName());
    }

    @Test
    public void testApplyIsCorrectlySettingTheStoryFullName(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));
        storySheet.fullNameTextField.setText("New Full Name");

        Story story = new Story();

        storySheet.apply(story);

        Assert.assertEquals("The Story Full name should be set to content of the full name text field ",
                storySheet.fullNameTextField.getText() , story.getFullName());
    }

    @Test
    public void testApplyIsCorrectlySettingTheStoryDescription(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));
        storySheet.descriptionTextField.setText("New Description");

        Story story = new Story();

        storySheet.apply(story);

        Assert.assertEquals("The Story description should be set to content of the description text field ",
                storySheet.descriptionTextField.getText() , story.getDescription());
    }

    @Test
    public void testApplyIsCorrectlySettingTheStoryCreator(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));

        Story story = new Story();

        storySheet.apply(story);

        Assert.assertEquals("The Story creator should be set to combo box selection",
                storySheet.personComboBox.getSelectionModel().getSelectedItem() , story.getCreatorPerson());
    }

    @Test
    public void testApplyIsCorrectlySettingTheStoryBacklog(){
        storySheet.personComboBox.getSelectionModel().select(new Person("Person"));
        storySheet.backlogComboBox.getSelectionModel().select(new Backlog("Backlog"));

        Story story = new Story();

        storySheet.apply(story);

        Assert.assertEquals("The Story backlog should be set to combo box selection",
                storySheet.backlogComboBox.getSelectionModel().getSelectedItem() , story.getBacklog());
    }

    @Test
    public void testApplyIsCorrectlyUpdatingTheStoryShortName(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));

        storySheet.fieldConstruct(story);

        storySheet.shortNameTextField.setText("New short name");

        storySheet.apply(story);

        Assert.assertEquals("The new short name should be set to the changed short name text field ",
                storySheet.shortNameTextField.getText() , story.getShortName());
    }

    @Test
    public void testApplyIsCorrectlyUpdatingTheStoryFullName(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));

        storySheet.fieldConstruct(story);

        storySheet.fullNameTextField.setText("New Full name");

        storySheet.apply(story);

        Assert.assertEquals("The new full name should be set to the changed full name text field ",
                storySheet.fullNameTextField.getText() , story.getFullName());
    }

    @Test
    public void testApplyIsCorrectlyUpdatingTheStoryDescription(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));

        storySheet.fieldConstruct(story);

        storySheet.descriptionTextField.setText("New Description");

        storySheet.apply(story);

        Assert.assertEquals("The new description should be set to the changed description text field ",
                storySheet.descriptionTextField.getText() , story.getDescription());
    }

    @Test
    public void testApplyIsCorrectlyUpdatingTheStoryBacklog(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Old Backlog"));

        storySheet.fieldConstruct(story);

        storySheet.backlogComboBox.getSelectionModel().select(new Backlog("New Backlog"));

        storySheet.apply(story);

        Assert.assertEquals("The new backlog should be set to the changed backlog combo box selection",
                storySheet.backlogComboBox.getSelectionModel().getSelectedItem(), story.getBacklog());
    }

    @Test
    public void testViewAcceptanceCriteria(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Old Backlog"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);

        storySheet.apply(story);

        Assert.assertEquals("The acceptance criteria should be shown in the list view",
            storySheet.acceptanceCriteriaListView.getItems(), story.getAcceptanceCriteria());
    }

    @Test
    public void testDeleteAcceptanceCriteria() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Old Backlog"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);

        storySheet.apply(story);

        storySheet.acceptanceCriteriaListView.getSelectionModel().select(0);

        storySheet.buttonDeleteAC.fire();

        Assert.assertEquals("There should be no AC's in the list",
            storySheet.acceptanceCriteriaListView.getItems().size(), 0);
    }

    @Test
    public void testAddEstimateToExistingStory() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        Backlog backlog = new Backlog("Old Backlog");
        backlog.setScale(BacklogSheet.TSHIRTS);
        story.setBacklog(backlog);
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);

        storySheet.estimateComboBox.getSelectionModel().select("M");

        storySheet.apply(story);

        Assert.assertEquals(
            "Expected estimate value of story to be the same as the selected estimate value",
            story.getEstimate(), BacklogSheet.tShirts.get("M").doubleValue(), 0.0);
    }

    @Test
    public void testEstimateDisabledNoBacklog() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);

        Assert.assertTrue("Estimate combobox should be disabled if there is no backlog selected",
            storySheet.estimateComboBox.isDisabled());
    }

    @Test
    public void testEstimateDisabledNoACs() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Backlog 1"));

        storySheet.fieldConstruct(story);

        Assert.assertTrue("Estimate combobox should be disabled if there are no ACs", storySheet.estimateComboBox.isDisabled());
    }

    @Test
    public void testEstimateDisabledNoBacklogNoACs() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));

        storySheet.fieldConstruct(story);

        Assert.assertTrue("Estimate combobox should be disabled if there are no ACs and no Backlog", storySheet.estimateComboBox.isDisabled());
    }

    @Test
    public void testEstimateEnabledWithBacklogAndACs() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Backlog 1"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);

        Assert.assertFalse(
            "Estimate combobox should be enabled if there is a backlog selected and ACs",
            storySheet.estimateComboBox.isDisabled());
    }

    @Test
    public void testReadyCheckboxDisabledWithNoShortName() {
        Story story = new Story("", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Backlog 1"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);

        Assert.assertTrue("Estimate checkbox should be disabled if there is no ShortName",
            storySheet.readyCheckBox.isDisabled());
    }


    @Test
    public void testReadyCheckboxDisabledWithNoBacklog() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);

        Assert.assertTrue(
            "Estimate checkbox should be disabled if there is no Backlog",
            storySheet.readyCheckBox.isDisabled());
    }


    @Test
    public void testReadyCheckboxDisabledWithNoACs() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Backlog 1"));

        storySheet.fieldConstruct(story);

        Assert.assertTrue(
            "Estimate checkbox should be disabled if there are no ACs",
            storySheet.readyCheckBox.isDisabled());
    }


    @Test
    public void testReadyCheckboxDisabledWithNoBacklogAndNoACs() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));

        storySheet.fieldConstruct(story);

        Assert.assertTrue(
            "Estimate checkbox should be disabled if there is no Backlog and no ACs",
            storySheet.readyCheckBox.isDisabled());
    }


    @Test
    public void testReadyCheckboxEnabledWithBacklogACsEstimate() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Backlog 1"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));

        storySheet.fieldConstruct(story);
        storySheet.estimateComboBox.getSelectionModel().select(3);

        Assert.assertFalse(
            "Estimate checkbox should be enabled if there is Backlog, AC and estimate",
            storySheet.readyCheckBox.isDisabled());
    }

    @Test
    public void testReadyCheckboxEnabledAndCheckedIfAlreadyReady() {
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Backlog 1"));
        story.addAcceptanceCriteria(new AcceptanceCriteria("Acceptance Criteria 1"));
        story.setEstimate(3);
        story.setReady(true);

        storySheet.fieldConstruct(story);

        Assert.assertFalse(
            "Estimate checkbox should be enabled if there is a Backlog and ACs and checked if the story was already marked as ready",
            storySheet.readyCheckBox.isDisabled());
    }

    @Test
    public void hasChangedOnAddNoChanges(){
        storySheet = new StorySheet(organisation);
        Assert.assertFalse("A blank story should have no changes", storySheet.hasChangedOnAdd());
    }

    @Test
    public void hasChangedOnEditNoChanges(){
        storySheet = new StorySheet(organisation);
        Story story = new Story("s","s2", "s3",person);
        storySheet.fieldConstruct(story);
        Assert.assertFalse("A blank story should have no changes", storySheet.hasChangedOnEdit());
    }

    @Test
    public void testTasksListPopulates(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Old Backlog"));
        story.addTask(new Task("testTask", "testDesc"));

        storySheet.fieldConstruct(story);

        storySheet.apply(story);

        Assert.assertEquals("The task should be shown in the list view",
                storySheet.storyTasksListView.getItems(), story.getTasks());
    }

    @Test
    public void testTasksDelete(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Old Backlog"));
        story.addTask(new Task("testTask", "testDesc"));

        storySheet.fieldConstruct(story);

        storySheet.apply(story);

        storySheet.storyTasksListView.getSelectionModel().select(0);

        storySheet.buttonDeleteTask.fire();

        Assert.assertEquals("There should be no Task's in the list",
                storySheet.storyTasksListView.getItems().size(), 0);
    }

    @Test
    public void testCreateTask(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Old Backlog"));
        //Task t = new Task("testTask", "testDesc");
        doReturn(mockEditor).when(storySheet).makeEditor();
        when(mockEditor.CreateTask(any(), any())).thenReturn(true);

        storySheet.fieldConstruct(story);

        storySheet.apply(story);

        storySheet.buttonNewTask(new javafx.event.ActionEvent());

        Assert.assertEquals("The task should be shown in the list view",
                storySheet.storyTasksListView.getItems().size(), 1);
    }

    @Test
    public void testCreateAC(){
        Story story = new Story("Original Short Name", "Original Full Name", "Original Description");
        story.setCreatorPerson(new Person("Person"));
        story.setBacklog(new Backlog("Old Backlog"));
        doReturn(mockEditor).when(storySheet).makeEditor();
        when(mockEditor.CreateACFromStory(any())).thenReturn(true);

        storySheet.fieldConstruct(story);

        storySheet.apply(story);

        storySheet.buttonNewAC(new javafx.event.ActionEvent());

        Assert.assertEquals("The task should be shown in the list view",
                storySheet.acceptanceCriteriaListView.getItems().size(), 1);
    }
}
