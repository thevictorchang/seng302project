package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.*;
import seng302.group3.model.search.FilterUtil;

/**
 * Created by cjm328 on 30/05/15.
 * A testing suite for Backlog Sheet (the dialog for creating and editing backlogs)
 */
public class BacklogSheetTest {



    private Organisation testOrganisation;
    private BacklogSheet backlogSheet;
    private Person firstPerson;
    private Story aStory;
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception{

        testOrganisation = new Organisation("testOrg");
        aStory = new Story("ValidStoryName", "ValidLongStoryName", "ValidStoryDescription");
        testOrganisation.addStory(aStory);
        firstPerson = new Person("ShortName", "LongName", "UserID");
        Skill PO = new Skill("PO", "ValidDescription");
        firstPerson.addSkill(PO);
        testOrganisation.addPerson(firstPerson);
        backlogSheet = new BacklogSheet(testOrganisation);
    }

    @After
    public void tearDown() throws Exception{
        testOrganisation = null;
        aStory = null;
        backlogSheet = null;
        firstPerson = null;
    }


    @Test
    public void TestNullNameTextFieldOnValidate() throws Exception{
        backlogSheet.shortNameTextField.setText("");
        backlogSheet.descriptionTextField.setText("ValidDescription");
        backlogSheet.POChoice.getSelectionModel().selectFirst();

        Assert.assertFalse("The validate should fail with an empty sting"
                , backlogSheet.validate(testOrganisation));
    }

    @Test
    public void TestNonNullNameTextFieldOnValidate() throws Exception{
        backlogSheet.shortNameTextField.setText("ValidName1");
        backlogSheet.descriptionTextField.setText("ValidDescription1");
        backlogSheet.POChoice.getSelectionModel().selectFirst();

        Assert.assertTrue("The validate should pass with both non-null stings"
                , backlogSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullDescriptionTextFieldOnValidate() throws Exception{
        backlogSheet.shortNameTextField.setText("ValidName");
        backlogSheet.descriptionTextField.setText("");
        backlogSheet.POChoice.getSelectionModel().selectFirst();

        Assert.assertFalse("The validate should fail with an empty sting"
                , backlogSheet.validate(testOrganisation));
    }

    @Test
    public void TestNoPOSelected() throws Exception{
        backlogSheet.shortNameTextField.setText("ValidName");
        backlogSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("The validate should fail with no PO selected"
                , backlogSheet.validate(testOrganisation));
    }

    @Test
    public void TestLoadingABacklogToBeEditedShortName() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        Backlog testBacklog = new Backlog("ShortName", "LongName", firstPerson);
        testOrganisation.addBacklog(testBacklog);

        backlogSheet.fieldConstruct(testBacklog);

        Assert.assertTrue("The backlog's short name should be the same as the textfield value",
                backlogSheet.shortNameTextField.getText().equals("ShortName"));
    }

    @Test
    public void TestLoadingABacklogToBeEditedDescription() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        Backlog testBacklog = new Backlog("ShortName", "LongName", firstPerson);
        testOrganisation.addBacklog(testBacklog);

        backlogSheet.fieldConstruct(testBacklog);

        Assert.assertTrue("The person's long name should be the same as the textfield value",
                backlogSheet.descriptionTextField.getText().equals("LongName"));
    }


    @Test
    public void TestLoadingABacklogToBeEditedPO() throws Exception{
        //Person firstPerson = new Person("ShortName", "LongName", "UserID");
        Backlog testBacklog = new Backlog("ShortName", "LongName", firstPerson);
        testOrganisation.addBacklog(testBacklog);
        //testOrganisation.addPerson(firstPerson);
        backlogSheet.fieldConstruct(testBacklog);
        Assert.assertTrue("The person's long name should be the same as the textfield value",
                FilterUtil.getComboBoxValue(backlogSheet.POChoice).equals(firstPerson));
    }

    @Test
    public void TestStoryAppearsInAvailableStories() throws Exception{
        Assert.assertTrue("The sheet's all available stories list contains the story previously added to the test organisation.",
                backlogSheet.allAvailableStoriesList.contains(aStory));
    }

    @Test
    public void TestStoryAppearsInCurrentStories() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        Backlog testBacklog = new Backlog("ShortName", "LongName", firstPerson);
        testBacklog.addStory(aStory);
        testOrganisation.addBacklog(testBacklog);

        backlogSheet.fieldConstruct(testBacklog);
        Assert.assertTrue("The sheet's all current stories list contains the story previously added to the test organisation.",
                backlogSheet.allCurrentStoriesList.contains(aStory));
    }

    @Test
    public void TestStoryCanBeMovedFromAvailableToCurrent() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        Backlog testBacklog = new Backlog("ShortName", "LongName", firstPerson);
        backlogSheet.fieldConstruct(testBacklog);
        Assert.assertTrue("The sheets all available stories list contains the story previously added to the test organisation.",
                backlogSheet.allAvailableStoriesList.contains(aStory));
        Assert.assertFalse("The sheets all current stories list contains the story previously added to the test organisation.",
                backlogSheet.allCurrentStoriesList.contains(aStory));
        backlogSheet.allAvailableStoriesListView.getSelectionModel().selectFirst();
        backlogSheet.buttonAddStory.fire();
        Assert.assertTrue("The sheets all current stories list contains the story previously added to the test organisation.",
                backlogSheet.allCurrentStoriesList.contains(aStory));
        Assert.assertFalse("The sheets all available stories list contains the story previously added to the test organisation.",
                backlogSheet.allAvailableStoriesList.contains(aStory));
    }

    @Test
    public void TestStoryCanBeMovedFromCurrentToAvailable() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        Backlog testBacklog = new Backlog("ShortName", "LongName", firstPerson);
        testBacklog.addStory(aStory);
        aStory.setBacklog(testBacklog);
        testOrganisation.addBacklog(testBacklog);
        BacklogSheet backlogSheet1 = new BacklogSheet(testOrganisation);
        backlogSheet1.fieldConstruct(testBacklog);
        backlogSheet1.storiesConstruct(testOrganisation);
        Assert.assertTrue("The sheets all current stories list contains the story previously added to the test organisation.",
                backlogSheet1.allCurrentStoriesList.contains(aStory));
        Assert.assertFalse("The sheets all available stories list contains the story previously added to the test organisation.",
                backlogSheet1.allAvailableStoriesList.contains(aStory));
        backlogSheet1.allCurrentStoriesListView.getSelectionModel().selectFirst();
        backlogSheet1.buttonRemoveStory.fire();
        Assert.assertTrue("The sheets all available stories list contains the story previously added to the test organisation.",
                backlogSheet1.allAvailableStoriesList.contains(aStory));
        Assert.assertFalse("The sheets all current stories list contains the story previously added to the test organisation.",
                backlogSheet1.allCurrentStoriesList.contains(aStory));
    }

    @Test
    public void TestStoryPriority() throws Exception{

    }
    @Test
    public void ChangedOnAddNoChanges() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Assert.assertFalse("A blank backlog should have no changes", backlogSheet.hasChangedOnAdd());
    }

    @Test
    public void ChangedOnAddShortNameTextFieldFromNull() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        backlogSheet.shortNameTextField.setText("NonNull");
        Assert.assertTrue("A change in shortname should show in changes",
            backlogSheet.hasChangedOnAdd());
    }

    @Test
    public void ChangedOnAddDescriptionTextFieldFromNull() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        backlogSheet.descriptionTextField.setText("NonNull");
        Assert.assertTrue("A change in description should show in changes",backlogSheet.hasChangedOnAdd());
    }

    @Test
    public void ChangedOnAddProductOwnerFromNull() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        backlogSheet.POChoice.getSelectionModel().select(firstPerson);
        Assert.assertTrue("A change in PO should show in changes",backlogSheet.hasChangedOnAdd());
    }

    @Test
    public void ChangedOnAddAddingCurrentStoryFromNone() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        backlogSheet.allCurrentStoriesList.add(aStory);
        Assert.assertTrue("A change in currentStories should show in changes",backlogSheet.hasChangedOnAdd());
    }

    @Test
    public void ChangedOnAddAddingAvailableStoryFromNone() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Story newStory = new Story("NewStory","1","1");
        backlogSheet.allAvailableStoriesList.add(newStory);
        Assert.assertTrue("A change in available stories should show in changes",backlogSheet.hasChangedOnAdd());
    }

    @Test
    public void ChangedOnEditShortNameTextFieldToOtherString() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Backlog backlog = new Backlog("SN","D",firstPerson);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.shortNameTextField.setText("Not SN");
        Assert.assertTrue("A change in shortname should show in changes",
            backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditShortNameTextFieldToNull() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Backlog backlog = new Backlog("SN","D",firstPerson);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.shortNameTextField.setText("");
        Assert.assertTrue("A change in shortname should show in changes",
            backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditDescriptionTextFieldToOtherString() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.descriptionTextField.setText("Not Des");
        Assert.assertTrue("A change in description should show in changes",
            backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditDescriptionTextFieldToNull() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.descriptionTextField.setText("");
        Assert.assertTrue("A change in description should show in changes",
            backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditProductOwnerToNone() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.POChoice.getSelectionModel().select("None");
        Assert.assertTrue("A change in PO should show in changes",backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditProductOwnerToOtherPO() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Person person = new Person(firstPerson);
        testOrganisation.addPerson(person);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.POChoice.getSelectionModel().select(person);
        Assert.assertTrue("A change in PO should show in changes",backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditCurrentStoriesRemoved() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Story story = new Story("s","s2","s3",firstPerson);
        testOrganisation.addStory(story);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlog.addStory(story);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.allCurrentStoriesList.remove(story);
        Assert.assertTrue("A change in Current Stories should show in changes",backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditCurrentStoriesAdded() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Story story = new Story("s","s2","s3",firstPerson);
        testOrganisation.addStory(story);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlog.addStory(story);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.allCurrentStoriesList.add(aStory);
        Assert.assertTrue("A change in Current Stories should show in changes",backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditAvailableStoriesMovedToCurrent() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Story story = new Story("s","s2","s3",firstPerson);
        testOrganisation.addStory(story);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlog.addStory(story);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.allAvailableStoriesListView.getSelectionModel().select(aStory);
        backlogSheet.buttonAddStory.fire();
        Assert.assertTrue("A change in Available Stories should show in changes",backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void ChangedOnEditAvailableStoriesAddedFromCurrent() throws Exception{
        backlogSheet = new BacklogSheet(testOrganisation);
        Story story = new Story("s","s2","s3",firstPerson);
        testOrganisation.addStory(story);
        Backlog backlog = new Backlog("SN","Des",firstPerson);
        backlog.addStory(story);
        backlogSheet.fieldConstruct(backlog);
        backlogSheet.allCurrentStoriesListView.getSelectionModel().select(story);
        backlogSheet.buttonRemoveStory.fire();
        Assert.assertTrue("A change in Available Stories should show in changes",backlogSheet.hasChangedOnEdit());
    }

    @Test
    public void Apply() throws Exception{

    }





}
