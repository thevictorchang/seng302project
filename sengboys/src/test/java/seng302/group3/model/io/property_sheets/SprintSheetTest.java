package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.*;

import java.time.LocalDate;

public class SprintSheetTest {

    private Organisation testOrganisation;
    private SprintSheet sprintSheet;
    private Story aStory;
    private Story aStoryInSprint;
    private Backlog testBacklog;
    private Sprint testSprint;
    private AcceptanceCriteria testAcceptanceCriteria;


    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {

        testOrganisation = new Organisation("testOrg");

        // Acceptance criteria
        testAcceptanceCriteria = new AcceptanceCriteria("testAc");

        // Person and skill
        Person testProductOwner = new Person("ShortName", "LongName", "UserID");
        Skill productOwnerSkill = new Skill("PO", "ValidDescription");
        testProductOwner.addSkill(productOwnerSkill);

        // Backlog
        testBacklog = new Backlog("backlogShortName", "backlogDescription", testProductOwner);

        // Story
        aStory = new Story("aStory", "ValidLongStoryName", "ValidStoryDescription");
        aStory.addAcceptanceCriteria(testAcceptanceCriteria);
        aStory.setReady(true);
        aStory.setPriority(1);
        testBacklog.addStory(aStory);
        aStory.setBacklog(testBacklog);

        aStoryInSprint = new Story("aStoryInSprint", "ValidLongStoryName2", "ValidStoryDescription2");
        aStoryInSprint.addAcceptanceCriteria(testAcceptanceCriteria);
        aStoryInSprint.setReady(true);
        aStoryInSprint.setPriority(2);
        testBacklog.addStory(aStoryInSprint);
        aStoryInSprint.setBacklog(testBacklog);


        // Team
        Team testTeam = new Team("teamShortName", "teamLongName", "teamDescription");

        // Dates
        LocalDate releaseDate = LocalDate.of(2015, 1, 5);
        TimePeriod validTimePeriod = new TimePeriod(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 1, 2));

        // Project and release
        Project testProject = new Project("projectShortName", "projectLongName", "projectDescription");
        Release testRelease = new Release("releaseShortName", releaseDate, testProject);

        // Adding everything to the organisation
        testOrganisation.addStory(aStory);
        testOrganisation.addSkill(productOwnerSkill);
        testOrganisation.addPerson(testProductOwner);
        testOrganisation.addTeam(testTeam);
        testOrganisation.addProject(testProject);
        testOrganisation.addRelease(testRelease);
        testOrganisation.addBacklog(testBacklog);

        TimePeriod teamProjAllocation = new TimePeriod(LocalDate.of(2014, 1, 1), LocalDate.of(2016, 1, 2),testProject,testTeam);
        testProject.addTimePeriod(teamProjAllocation);
        testTeam.addTimePeriod(teamProjAllocation);

        //Add all this into a sprint so fieldConstruct() works in later tests
        testSprint = new Sprint("sprintShortName", "sprintLongName", "sprintDescription", validTimePeriod, testTeam, testRelease, testBacklog);
        testSprint.addStory(aStoryInSprint);

        // Make the sprint sheet
        sprintSheet = new SprintSheet(testOrganisation);

        // Assign all the fields for validation
        // Text fields
        sprintSheet.shortNameTextField.setText("ValidShortName");
        sprintSheet.fullNameTextField.setText("ValidFullName");
        sprintSheet.descriptionTextField.setText("ValidDescription");

        // Time period
        sprintSheet.setDateHolder(validTimePeriod);

        // Combo Boxes
        sprintSheet.teamChoice.setDisable(false);
        sprintSheet.teamChoice.getItems().add(testTeam);
        sprintSheet.teamChoice.getSelectionModel().select(testTeam);
        sprintSheet.releaseChoice.getSelectionModel().selectFirst();
        sprintSheet.backlogChoice.getSelectionModel().selectFirst();

    }

    @After
    public void tearDown() throws Exception {
        testOrganisation = null;
        sprintSheet = null;
    }

    @Test
    public void testAllFieldsWithValidEntries() throws Exception {
        sprintSheet.fieldConstruct(testSprint);
        Assert.assertTrue("The validate should work for all valid entries", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullShortNameTextFieldOnValidate() throws Exception{
        sprintSheet.shortNameTextField.setText("");
        Assert.assertFalse("The validate should fail with an empty short name", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testNullFullNameTextFieldOnValidate() throws Exception {
        sprintSheet.fullNameTextField.setText("");
        Assert.assertFalse("The validate should fail with an empty full name", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testNullDescriptionTextFieldOnValidate() throws Exception {
        sprintSheet.descriptionTextField.setText("");
        Assert.assertFalse("The validate should fail with an empty description", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testNullDateSelectionOnValidate() throws Exception {
        TimePeriod nullTimePeriod = new TimePeriod(null, null);
        sprintSheet.setDateHolder(nullTimePeriod);
        Assert.assertFalse("The validate should fail with no dates selected", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testNullTeamSelectionOnValidate() throws Exception {
        sprintSheet.teamChoice.getSelectionModel().select(null);
        Assert.assertFalse("The validate should fail with no teams selected", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testNullReleaseSelectionOnValidate() throws Exception {
        sprintSheet.releaseChoice.getSelectionModel().selectLast();
        Assert.assertFalse("The validate should fail with no releases selected", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testNullBacklogSelectionOnValidate() throws Exception {
        sprintSheet.backlogChoice.getSelectionModel().selectLast();
        Assert.assertFalse("The validate should fail with no backlogs selected", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testConflictingDatesWithReleaseDateOnValidate() throws Exception {
        TimePeriod invalidTimePeriod = new TimePeriod(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 1, 10));
        sprintSheet.setDateHolder(invalidTimePeriod);
        Assert.assertFalse("The validate should fail with conflicting selected dates and release date", sprintSheet.validate(testOrganisation));
    }

    @Test
    public void testStoryAppearsInAvailableStories() throws Exception {

        sprintSheet.fieldConstruct(testSprint);

        Assert.assertTrue("The sheet's all available stories list contains the story previously added to the test organisation",
                sprintSheet.allAvailableStoriesList.contains(aStory));

        /*
        //sprintSheet.backlogChoice.fireEvent();
        //listener doesn't seem to fire from here, will populate list from here.

        sprintSheet.allAvailableStoriesList.addAll(testBacklog.getStories());

        Assert.assertTrue("The sheet's all available stories list contains the story previously added to the test organisation",
                sprintSheet.allAvailableStoriesList.contains(aStory));
                */
    }

    @Test
    public void testStoryAppearsInCurrentStories() throws Exception {

        sprintSheet.fieldConstruct(testSprint);

        Assert.assertTrue("The sheet's all available stories list contains the story previously added to the test organisation",
                sprintSheet.allCurrentStoriesList.contains(aStoryInSprint));



    }
    @Test
    public void testAddButtonWithOneStory() throws Exception {
        sprintSheet.fieldConstruct(testSprint);

        sprintSheet.allAvailableStoriesListView.getSelectionModel().select(aStory);
        sprintSheet.buttonAddStory.fire();

        Assert.assertTrue("The button add story should move the story to the current list"
                , sprintSheet.allCurrentStoriesList.contains(aStory));


    }

    @Test
    public void testAddButtonWithMultipleStories() throws Exception {

        Story aStory2 = new Story("aStory2", "ValidLongStoryName3", "ValidStoryDescription3");
        aStory2.addAcceptanceCriteria(testAcceptanceCriteria);
        aStory2.setReady(true);
        aStory2.setPriority(3);
        testBacklog.addStory(aStory2);
        aStory2.setBacklog(testBacklog);

        sprintSheet.fieldConstruct(testSprint);

        //testBacklog now has two stories in it
        sprintSheet.allAvailableStoriesListView.getSelectionModel().selectAll();
        sprintSheet.buttonAddStory.fire();

        Assert.assertTrue("The button add story should move two stories to the current list",
                (sprintSheet.allCurrentStoriesList.contains(aStory))
            && (sprintSheet.allCurrentStoriesList.contains(aStory2)));
    }

    @Test
    public void testRemoveButtonWithOneStory() throws Exception {
        sprintSheet.fieldConstruct(testSprint);

        sprintSheet.allCurrentStoriesListView.getSelectionModel().select(aStoryInSprint);
        sprintSheet.buttonRemoveStory.fire();

        Assert.assertTrue("The button remove story should move the story aStoryInSprint to the available list"
                , sprintSheet.allAvailableStoriesList.contains(aStoryInSprint));

    }

    @Test
    public void testRemoveButtonWithMultipleStories() throws Exception {
        Story aStoryInSprint2 = new Story("aStoryInSprint2", "ValidLongStoryName4", "ValidStoryDescription4");
        aStoryInSprint2.addAcceptanceCriteria(testAcceptanceCriteria);
        aStoryInSprint2.setReady(true);
        aStoryInSprint2.setPriority(4);
        testBacklog.addStory(aStoryInSprint2);
        aStoryInSprint2.setBacklog(testBacklog);

        testSprint.addStory(aStoryInSprint2);

        sprintSheet.fieldConstruct(testSprint);

        sprintSheet.allCurrentStoriesListView.getSelectionModel().selectAll();
        sprintSheet.buttonRemoveStory.fire();

        Assert.assertTrue("The button remove story should move two stories to the available list",
                (sprintSheet.allAvailableStoriesList.contains(aStoryInSprint))
                        && (sprintSheet.allAvailableStoriesList.contains(aStoryInSprint2)));



    }
}
