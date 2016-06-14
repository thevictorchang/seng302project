package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.*;

import java.time.LocalDate;

/**
 * Created by Eddy on 29-May-15.
 * A testing suite for Person Sheet (the dialog for creating and editing people)
 */
public class PersonSheetTest {
    private Organisation testOrganisation;
    private PersonSheet personSheet;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        personSheet = new PersonSheet();
        testOrganisation = new Organisation("testOrg");
    }

    @After
    public void tearDown() throws Exception{
        testOrganisation = null;
        personSheet = null;
    }

    @Test
    public void TestNullShortNameTextFieldOnValidate() throws Exception{
        personSheet.shortNameTextField.setText("");
        personSheet.fullNameTextField.setText("ValidLongName");
        personSheet.userIDTextField.setText("ValidUserID");

        Assert.assertFalse("As shortName is empty this should validate with a false result"
            , personSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullFullNameTextFieldOnValidate() throws Exception{
        personSheet.shortNameTextField.setText("ValidShortName");
        personSheet.fullNameTextField.setText("");
        personSheet.userIDTextField.setText("ValidUserID");

        Assert.assertFalse("As longName is empty this should validate with a false result"
            , personSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullUserIDTextFieldOnValidate() throws Exception{
        personSheet.shortNameTextField.setText("ValidShortName");
        personSheet.fullNameTextField.setText("ValidLongName");
        personSheet.userIDTextField.setText("");

        Assert.assertFalse("As userID is empty this should validate with a false result"
            , personSheet.validate(testOrganisation));
    }

    @Test
    public void TestNoNullTextFieldsOnValidate() throws Exception{
        personSheet.shortNameTextField.setText("ValidShortName");
        personSheet.fullNameTextField.setText("ValidLongName");
        personSheet.userIDTextField.setText("ValidUserID");

        Assert.assertTrue("As there are no empty field should validate",
            personSheet.validate(testOrganisation));
    }

    @Test
    public void TestNonUniqueNameOnValidate() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        testOrganisation.addPerson(firstPerson);

        personSheet.shortNameTextField.setText("ShortName");
        personSheet.fullNameTextField.setText("ValidLongName");
        personSheet.userIDTextField.setText("ValidUserID");

        Assert.assertFalse("Because the name is non-unique in the organisation validates to false"
            , personSheet.validate(testOrganisation));
    }

    @Test
    public void TestUniqueNameOnValidate() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        testOrganisation.addPerson(firstPerson);

        personSheet.shortNameTextField.setText("ValidShortName");
        personSheet.fullNameTextField.setText("ValidLongName");
        personSheet.userIDTextField.setText("ValidUserID");

        Assert.assertTrue("Because the name is unique in the organisation validates to true",
            personSheet.validate(testOrganisation));
    }

    @Test
    public void TestValidationWhenAPersonHasBeenPassedToBeConstructed() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        testOrganisation.addPerson(firstPerson);

        personSheet.fieldConstruct(firstPerson);

        Assert.assertTrue("The person is a valid person so it should validate"
            , personSheet.validate(testOrganisation, firstPerson));
    }

    @Test
    public void TestLoadingAPersonToBeEditedShortName() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        testOrganisation.addPerson(firstPerson);

        personSheet.fieldConstruct(firstPerson);

        Assert.assertEquals("The person's short name should be the same as the textfield value",
            personSheet.shortNameTextField.getText(), ("ShortName"));
    }

    @Test
    public void TestLoadingAPersonToBeEditedLongName() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        testOrganisation.addPerson(firstPerson);

        personSheet.fieldConstruct(firstPerson);

        Assert.assertEquals("The person's long name should be the same as the textfield value",
            personSheet.fullNameTextField.getText(), ("LongName"));
    }

    @Test
    public void TestLoadingAPersonToBeEditedUserID() throws Exception{
        Person firstPerson = new Person("ShortName", "LongName", "UserID");
        testOrganisation.addPerson(firstPerson);

        personSheet.fieldConstruct(firstPerson);

        Assert.assertEquals("The person's userID should be the same as the textfield value",
            personSheet.userIDTextField.getText(), ("UserID"));
    }

    @Test
    public void TestAddSkillButtonWithOneSkill() throws Exception{
        Skill moveSkill = new Skill("ValidSkillName", "ValidSkillDescription");
        testOrganisation.addSkill(moveSkill);
        personSheet.skillsConstruct(testOrganisation);

        personSheet.allAvailableSkillsListView.getSelectionModel().select(moveSkill);
        personSheet.buttonAddSkill.fire();

        Assert.assertTrue("The button add skill should move the skill to the current list"
                , personSheet.allCurrentSkillsList.contains(moveSkill));
    }

    @Test
    public void TestAddSkillButtonWithMultipleSkills() throws Exception{
        Skill moveSkill = new Skill("ValidSkillName", "ValidSkillDescription");
        Skill moveSkill2 = new Skill("ValidSkillName", "ValidSkillDescription");
        testOrganisation.addSkill(moveSkill);
        testOrganisation.addSkill(moveSkill2);
        personSheet.skillsConstruct(testOrganisation);

        personSheet.allAvailableSkillsListView.getSelectionModel().selectAll();
        personSheet.buttonAddSkill.fire();

        Assert.assertTrue("The button add skill should move both skills to the current list",
                personSheet.allCurrentSkillsList.contains(moveSkill)
                        && personSheet.allCurrentSkillsList.contains(moveSkill2));
    }

    @Test
    public void TestRemoveSkillWithOneSkill() throws Exception{
        Person person = new Person("Name", "LN", "UID");
        Skill skill = new Skill("Skill");
        person.addSkill(skill);

        personSheet.fieldConstruct(person);

        personSheet.allCurrentSkillsListView.getSelectionModel().select(skill);
        personSheet.buttonRemoveSkill.fire();

        Assert.assertTrue("The skill should be in the available list now"
                , personSheet.allAvailableSkillsList.contains(skill));
    }

    @Test
    public void TestRemoveSkillWithMultipleSkills() throws Exception{
        Person person = new Person("Name", "LN", "UID");
        Skill skill = new Skill("Skill");
        Skill skill2 = new Skill("Skill2");
        person.addSkill(skill);
        person.addSkill(skill2);

        personSheet.fieldConstruct(person);

        personSheet.allCurrentSkillsListView.getSelectionModel().selectAll();
        personSheet.buttonRemoveSkill.fire();

        Assert.assertTrue("The skills should both be in the available list now"
                , personSheet.allAvailableSkillsList.contains(skill)
                && personSheet.allAvailableSkillsList.contains(skill2));
    }

    @Test
    public void TestRemoveSkillWhenTheCurrentPersonIsAProductOwner() throws Exception{
        Person person = new Person("SN","LN","UID");
        Skill productOwner = new Skill("PO");
        person.addSkill(productOwner);
        Team team = new Team("SNTeam");
        team.addPerson(person);
        team.setProductOwner(person);
        person.setTeam(team);

        personSheet.fieldConstruct(person);

        personSheet.allCurrentSkillsListView.getSelectionModel().select(productOwner);
        personSheet.buttonRemoveSkill.fire();

        Assert.assertFalse("Because the person is a PO in a team and the skill to be removed is PO, skill will" +
                "not be removed", personSheet.allAvailableSkillsList.contains(productOwner));
    }

    @Test
    public void TestRemoveSkillWhenTheCurrentPersonIsAScrumMaster() throws Exception{
        Person person = new Person("SN","LN","UID");
        Skill scrumMaster = new Skill("SM");
        person.addSkill(scrumMaster);
        Team team = new Team("SNTeam");
        team.addPerson(person);
        team.setScrumMaster(person);
        person.setTeam(team);

        personSheet.fieldConstruct(person);

        personSheet.allCurrentSkillsListView.getSelectionModel().select(scrumMaster);
        personSheet.buttonRemoveSkill.fire();

        Assert.assertFalse("Because the person is a SM in a team and the skill to be removed is SM, skill will" +
                "not be removed", personSheet.allAvailableSkillsList.contains(scrumMaster));
    }

    @Test
    public void TestListenerForConstructionWhenAPersonIsPassed() throws Exception{
        Person person = new Person("SN","LN","UID");
        Team team = new Team("SNTeam");
        testOrganisation.addPerson(person);
        testOrganisation.addTeam(team);

        personSheet.skillsConstruct(testOrganisation);

        personSheet.teamChoice.getSelectionModel().select(team);

        Assert.assertTrue("Selecting a team should fire the teamChoice listener which will disable projectChoice"
                ,personSheet.projectChoice.isDisabled());
    }

    @Test
    public void TestInitialisationOfSkillsToAvailableListWhenMakingANewPerson() throws Exception{
        Skill moveSkill = new Skill("ValidSkillName", "ValidSkillDescription");
        testOrganisation.addSkill(moveSkill);
        personSheet.skillsConstruct(testOrganisation);

        Assert.assertTrue("The skills construct should have added the skill to the available skills list"
                ,personSheet.allAvailableSkillsList.contains(moveSkill));
    }

    @Test
    public void TestInitialisationOfSkillsToCurrentListWhenEditingAPerson() throws Exception{
        Person person = new Person("SN","LN","UID");
        Skill skill = new Skill("SM");
        person.addSkill(skill);

        personSheet.fieldConstruct(person);

        Assert.assertTrue("The skills construct should have added the skill to the current skills list"
                , personSheet.allCurrentSkillsList.contains(skill));
    }

    @Test
    public void TestInitialisationOfNonNullCurrentTeamOnAnEditOperation() throws Exception{
        Person person = new Person("SN","LN","UID");
        Team team = new Team("SNTeam");
        team.addPerson(person);
        person.setTeam(team);

        personSheet.fieldConstruct(person);

        Assert.assertEquals("The persons team should have been set as the default choice"
                , personSheet.teamChoice.getValue(), team);
    }

    @Test
    public void TestInitialisationOfNullCurrentTeamOnAnEditOperation() throws Exception{
        Person person = new Person("SN","LN","UID");
        personSheet.fieldConstruct(person);

        Assert.assertEquals("The persons team should have been set as the default choice"
                ,personSheet.teamChoice.getValue(),"None");
    }

    @Test
    public void TestInitialisationOfNonNullCurrentProjectOnAnEditOperation() throws Exception{
        Person person = new Person("SN","LN","UID");
        Project project = new Project("SNProject");
        project.addPerson(person);
        person.setProject(project);

        personSheet.fieldConstruct(person);

        Assert.assertEquals("The persons project should have been set as the default choice"
                , personSheet.projectChoice.getValue(), project);
    }

    @Test
    public void TestInitialisationOfNullCurrentProjectOnAnEditOperation() throws Exception{
        Person person = new Person("SN","LN","UID");
        personSheet.fieldConstruct(person);

        Assert.assertEquals("The persons project should have been set as the default choice"
                ,personSheet.projectChoice.getValue(),"None");
    }

    @Test
    public void TestInitialisationOfProjectComboBoxWhenPersonBeingEditedIsASM() throws Exception{
        Person person = new Person("SN","LN","UID");
        Team team = new Team("SNTeam");
        team.addPerson(person);
        team.setScrumMaster(person);
        person.setTeam(team);
        team.addTimePeriod(new TimePeriod(LocalDate.now(),LocalDate.now()));

        personSheet.fieldConstruct(person);

        Assert.assertEquals("The persons project should have been set to 'PO or SM'"
                ,personSheet.projectChoice.getValue(),"PO or SM");
    }

    @Test
    public void TestInitialisationOfTeamComboBoxWhenPersonBeingEditedIsAPO() throws Exception{
        Person person = new Person("SN","LN","UID");
        Team team = new Team("SNTeam");
        team.addPerson(person);
        team.setProductOwner(person);
        person.setTeam(team);
        team.addTimePeriod(new TimePeriod(LocalDate.now(),LocalDate.now()));

        personSheet.fieldConstruct(person);

        Assert.assertEquals("The persons project should have been set to 'PO or SM'"
                ,personSheet.teamChoice.getValue(),"PO or SM");
    }

    @Test
    public void TestApplyForShortName() throws Exception{
        Person person = new Person("");
        personSheet.shortNameTextField.setText("ValidShortName");
        personSheet.apply(person);

        Assert.assertEquals("The text in the short name field should have been applied"
                ,person.getShortName(),"ValidShortName");
    }

    @Test
    public void TestApplyForLongName() throws Exception{
        Person person = new Person("","","");
        personSheet.fullNameTextField.setText("ValidFullName");
        personSheet.apply(person);

        Assert.assertEquals("The text in the full name field should have been applied"
                ,person.getFullName(),"ValidFullName");
    }

    @Test
    public void TestApplyForUserID() throws Exception{
        Person person = new Person("","","");
        personSheet.userIDTextField.setText("ValidUserID");
        personSheet.apply(person);

        Assert.assertEquals("The text in the userID field should have been applied"
                ,person.getUserID(),"ValidUserID");
    }

    @Test
    public void TestApplyForSkillsAdded() throws Exception{
        Person person = new Person("","","");
        testOrganisation.addSkill(new Skill("skill1"));
        testOrganisation.addSkill(new Skill("skill2"));
        personSheet.skillsConstruct(testOrganisation);

        personSheet.allAvailableSkillsListView.getSelectionModel().selectAll();
        personSheet.buttonAddSkill.fire();

        personSheet.apply(person);

        Assert.assertTrue("The person should now have 2 skills applied",person.getSkills().size() == 2);
    }

    @Test
    public void TestApplyForSkillsRemoved() throws Exception{
        Person person = new Person("");
        person.addSkill(new Skill("skill"));
        person.addSkill(new Skill("skill2"));

        personSheet.fieldConstruct(person);

        personSheet.allCurrentSkillsListView.getSelectionModel().selectAll();
        personSheet.buttonRemoveSkill.fire();
        personSheet.apply(person);

        Assert.assertTrue("Person should have no skills",person.getSkills().size() == 0);
    }

    @Test
    public void TestApplyForAddingAPersonToAnExistingTeamWhenThePersonHasNoCurrentTeam() throws Exception{
        Person person = new Person("");
        Team team = new Team("");
        testOrganisation.addTeam(team);

        personSheet.skillsConstruct(testOrganisation);
        personSheet.teamChoice.getSelectionModel().select(team);

        personSheet.apply(person);
        Assert.assertTrue("The person should be part of the team and the team should contain the person"
                ,person.getTeam() == team && team.getPeople().contains(person));
    }

    @Test
    public void TestApplyForAddingAPersonToAnExistingProjectWhenThePersonHasNoCurrentProject() throws Exception{
        Person person = new Person("");
        Project project = new Project("");
        testOrganisation.addProject(project);

        personSheet.skillsConstruct(testOrganisation);
        personSheet.projectChoice.getSelectionModel().select(project);

        personSheet.apply(person);
        Assert.assertTrue("The person should be part of the project and the project should contain the person"
                ,person.getProject() == project && project.getPeople().contains(person));
    }

    @Test
    public void TestPersonInNewProjectOnApplyWhenProjectChoiceChangedToNewProject() throws Exception{
        Person person = new Person("");
        Project project = new Project("");
        project.addPerson(person);
        person.setProject(project);
        Project newProject = new Project("newProject");
        testOrganisation.addProject(project);
        testOrganisation.addProject(newProject);

        personSheet.skillsConstruct(testOrganisation);
        personSheet.projectChoice.getSelectionModel().select(newProject);

        personSheet.apply(person);
        Assert.assertTrue("The person should be part of the project and the project should contain the person"
                ,person.getProject() == newProject && newProject.getPeople().contains(person));
    }

    @Test
    public void TestPersonNoLongerInOldProjectOnApplyWhenProjectChoiceChangedToNewProject() throws Exception{
        Person person = new Person("");
        Project project = new Project("");
        project.addPerson(person);
        person.setProject(project);
        Project newProject = new Project("newProject");
        testOrganisation.addProject(project);
        testOrganisation.addProject(newProject);

        personSheet.skillsConstruct(testOrganisation);
        personSheet.projectChoice.getSelectionModel().select(newProject);

        personSheet.apply(person);
        Assert.assertTrue("The person shouldn't be part of the old project and the old project shouldn't contain the person"
                ,person.getProject() != project && (!(project.getPeople().contains(person))));
    }

    @Test
    public void TestPersonNoLongerHasCurrentProjectOnApplyWithProjectChoiceChangedFromOldProjectToNone() throws Exception{
        Person person = new Person("");
        Project project = new Project("");
        project.addPerson(person);
        person.setProject(project);
        testOrganisation.addProject(project);


        personSheet.skillsConstruct(testOrganisation);
        personSheet.projectChoice.getSelectionModel().select("None");

        personSheet.apply(person);
        Assert.assertTrue("The person shouldn't have a current project and the old project should not contain person"
                ,person.getProject() == null && !project.getPeople().contains(person));
    }

    @Test
    public void TestApplyWhenPersonHasNewSkillsAddedAndCurrentProjectIsNotChanged() throws Exception{
        Person person = new Person("");
        Project project = new Project("");
        project.addPerson(person);
        person.setProject(project);
        Skill skill = new Skill("skill");
        testOrganisation.addProject(project);
        testOrganisation.addSkill(skill);

        personSheet.skillsConstruct(testOrganisation);
        personSheet.fieldConstruct(person);
        personSheet.allAvailableSkillsListView.getSelectionModel().select(skill);
        personSheet.buttonAddSkill.fire();
        personSheet.apply(person);

        Assert.assertTrue("Because the person has been added to the project the project should have the persons skills"
                ,project.getSkills().contains(skill));
    }

    @Test
    public void TestPersonNoLongerInOldTeamOnApplyWhenTeamChoiceChangedToNewTeam() throws Exception{
        Person person = new Person("");
        Team team = new Team("");
        team.addPerson(person);
        person.setTeam(team);
        Team team1 = new Team("newProject");
        testOrganisation.addTeam(team);
        testOrganisation.addTeam(team1);

        personSheet.skillsConstruct(testOrganisation);
        personSheet.teamChoice.getSelectionModel().select(team1);

        personSheet.apply(person);
        Assert.assertTrue("The person shouldn't be part of the old team and the old team shouldn't contain the person"
            ,person.getTeam() != team && (!(team.getPeople().contains(person))));
    }

    @Test
    public void TestPersonInNewTeamOnApplyWhenTeamChoiceChangedToNewTeam() throws Exception{
        Person person = new Person("");
        Team team = new Team("");
        team.addPerson(person);
        person.setTeam(team);
        Team team1 = new Team("newProject");
        testOrganisation.addTeam(team);
        testOrganisation.addTeam(team1);

        personSheet.skillsConstruct(testOrganisation);
        personSheet.teamChoice.getSelectionModel().select(team1);

        personSheet.apply(person);
        Assert.assertTrue("The person should be part of the team and the team should contain the person"
            ,person.getTeam() == team1 && team1.getPeople().contains(person));
    }

    @Test
    public void TestApplySelectingNoneForTeamWhenThePersonHasAnAlreadyExistingTeam() throws Exception{

    }

    @Test
    public void TestApplyWhenPersonHasNewSkillsAddedAndCurrentTeamIsNotChanged() throws Exception{

    }
}
