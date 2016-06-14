package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.Organisation;
import seng302.group3.model.Person;
import seng302.group3.model.Skill;
import seng302.group3.model.Team;

/**
 * Created by jwc78 on 30/05/15.
 * A testing suite for Team Sheet (the dialog for creating and editing teams)
 */
public class TeamSheetTest {
    private Organisation testOrganisation;
    private TeamSheet teamSheet;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        teamSheet = new TeamSheet();
        testOrganisation = new Organisation("testOrg");
    }

    @After
    public void tearDown() throws Exception{
        testOrganisation = null;
        teamSheet = null;
    }

    @Test
    public void TestNullShortNameTextFieldOnValidate() throws Exception{
        teamSheet.shortNameTextField.setText("");
        teamSheet.longNameTextField.setText("ValidLongName");
        teamSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("As shortName is empty this should validate with a false result"
            , teamSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullLongNameTextFieldOnValidate() throws Exception{
        teamSheet.shortNameTextField.setText("ValidShortName");
        teamSheet.longNameTextField.setText("");
        teamSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("As longName is empty this should validate with a false result"
            , teamSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullDescriptionTextFieldOnValidate() throws Exception{
        teamSheet.shortNameTextField.setText("ValidShortName");
        teamSheet.longNameTextField.setText("ValidLongName");
        teamSheet.descriptionTextField.setText("");

        Assert.assertFalse("As description is empty this should validate with a false result"
            , teamSheet.validate(testOrganisation));
    }

    @Test
    public void TestNoNullTextFieldsOnValidate() throws Exception{
        teamSheet.shortNameTextField.setText("ValidShortName");
        teamSheet.longNameTextField.setText("ValidLongName");
        teamSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertTrue("As there are no empty field should validate",
            teamSheet.validate(testOrganisation));
    }

    @Test
    public void TestNonUniqueNameOnValidate() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);

        teamSheet.shortNameTextField.setText("ShortName");
        teamSheet.longNameTextField.setText("ValidLongName");
        teamSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("Because the name is non-unique in the organisation validates to false",
            teamSheet.validate(testOrganisation));
    }

    @Test
    public void TestUniqueNameOnValidate() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);

        teamSheet.shortNameTextField.setText("ValidShortName");
        teamSheet.longNameTextField.setText("ValidLongName");
        teamSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertTrue("Because the name is unique in the organisation validates to true",
            teamSheet.validate(testOrganisation));
    }

    @Test
    public void TestValidationWhenATeamHasBeenPassedToBeConstructed() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);

        teamSheet.fieldConstruct(firstTeam);

        Assert.assertTrue("The team is a valid team so it should validate"
            , teamSheet.validate(testOrganisation, firstTeam));
    }

    @Test
    public void TestLoadingATeamToBeEditedShortName() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);

        teamSheet.fieldConstruct(firstTeam);

        Assert.assertTrue("The team's short name should be the same as the textfield value",
            teamSheet.shortNameTextField.getText().equals("ShortName"));
    }

    @Test
    public void TestLoadingATeamToBeEditedLongName() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);

        teamSheet.fieldConstruct(firstTeam);

        Assert.assertTrue("The team's long name should be the same as the textfield value",
            teamSheet.longNameTextField.getText().equals("LongName"));
    }

    @Test
    public void TestLoadingATeamToBeEditedDescription() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);

        teamSheet.fieldConstruct(firstTeam);

        Assert.assertTrue("The team's description should be the same as the textfield value",
            teamSheet.descriptionTextField.getText().equals("Description"));
    }

    @Test
    public void TestAvailablePersonShown() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        Assert.assertTrue("The observable list for available people should contain one person",
            teamSheet.allAvailablePeopleList.size() == 1);
    }

    @Test
    public void TestApplyForAddingAPersonToTeamWhenThePersonHasNoCurrentTeam() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().select(firstPerson);
        teamSheet.buttonAddPerson.fire();

        Assert.assertTrue("Person should be moved from available to current",
            teamSheet.allCurrentPeopleList.size() == 1);
        Assert.assertTrue("Available list should be empty",
            teamSheet.allAvailablePeopleList.size() == 0);
    }

    @Test
    public void TestAddingPersonAssignedToAnotherTeam() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Team secondTeam = new Team("ShortName2", "LongName2", "Description2");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addTeam(secondTeam);
        testOrganisation.addPerson(firstPerson);
        firstPerson.setTeam(firstTeam);
        firstTeam.addPerson(firstPerson);

        teamSheet.fieldConstruct(secondTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().select(firstPerson);
        teamSheet.buttonAddPerson.fire();


        Assert.assertTrue("Person shouldn't be moved from available to current",
            teamSheet.allCurrentPeopleList.size() == 0);
        Assert.assertTrue("Available list should still contain the person",
            teamSheet.allAvailablePeopleList.size() == 1);
    }

    @Test
    public void TestRemovePersonFromTeam() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        firstPerson.setTeam(firstTeam);
        firstTeam.addPerson(firstPerson);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allCurrentPeopleListView.getSelectionModel().select(firstPerson);
        teamSheet.buttonRemovePerson.fire();


        Assert.assertTrue("Person should be moved from current to available",
            teamSheet.allCurrentPeopleList.size() == 0);
        Assert.assertTrue("Available list should contain the removed person",
            teamSheet.allAvailablePeopleList.size() == 1);
    }

    @Test
    public void TestRemoveMultiplePeopleFromTeam() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Person secondPerson = new Person("ShortName2", "LongName2", "Description2");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addPerson(secondPerson);
        firstPerson.setTeam(firstTeam);
        firstTeam.addPerson(firstPerson);
        firstTeam.addPerson(secondPerson);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allCurrentPeopleListView.getSelectionModel().selectAll();
        teamSheet.buttonRemovePerson.fire();


        Assert.assertTrue("People should be moved from current to available",
            teamSheet.allCurrentPeopleList.size() == 0);
        Assert.assertTrue("Available list should contain the removed people",
            teamSheet.allAvailablePeopleList.size() == 2);
    }

    @Test
    public void TestAddingPersonWithPOSkillGettingPORoleWithoutExistingPO() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Skill productOwner = new Skill("PO", "Product Owner");
        Skill scrumMaster = new Skill("SM", "Scrum Master");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addSkill(productOwner);
        testOrganisation.addSkill(scrumMaster);
        firstPerson.addSkill(productOwner);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().select(firstPerson);
        teamSheet.buttonAddPerson.fire();
        teamSheet.apply(firstTeam);

        Assert.assertTrue("Person 1 should be made productowner",
            firstTeam.getProductOwner() == firstPerson);
    }

    @Test
    public void TestAddingPersonWithPOSkillNotGivenPORoleWithExistingPO() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Person secondPerson = new Person("ShortName2", "LongName2", "Description2");
        Skill productOwner = new Skill("PO", "Product Owner");
        Skill scrumMaster = new Skill("SM", "Scrum Master");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addPerson(secondPerson);
        testOrganisation.addSkill(productOwner);
        testOrganisation.addSkill(scrumMaster);
        firstPerson.addSkill(productOwner);
        secondPerson.addSkill(productOwner);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().select(firstPerson);
        teamSheet.buttonAddPerson.fire();
        teamSheet.allAvailablePeopleListView.getSelectionModel().select(secondPerson);
        teamSheet.apply(firstTeam);

        Assert.assertTrue("Person 2 should not have been made productowner",
            firstTeam.getProductOwner() != secondPerson);
    }


    @Test
    public void TestAddingPersonWithSMSkillGettingSMRoleWithoutExistingSM() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Skill productOwner = new Skill("PO", "Product Owner");
        Skill scrumMaster = new Skill("SM", "Scrum Master");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addSkill(productOwner);
        testOrganisation.addSkill(scrumMaster);
        firstPerson.addSkill(scrumMaster);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().select(firstPerson);
        teamSheet.buttonAddPerson.fire();
        teamSheet.apply(firstTeam);

        Assert.assertTrue("Person 1 should be made scrummaster",
            firstTeam.getScrumMaster() == firstPerson);
    }

    @Test
    public void TestAddingPersonWithSMSkillNotGivenSMRoleWithExistingSM() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Person secondPerson = new Person("ShortName2", "LongName2", "Description2");
        Skill productOwner = new Skill("PO", "Product Owner");
        Skill scrumMaster = new Skill("SM", "Scrum Master");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addPerson(secondPerson);
        testOrganisation.addSkill(productOwner);
        testOrganisation.addSkill(scrumMaster);
        firstPerson.addSkill(scrumMaster);
        secondPerson.addSkill(scrumMaster);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().select(firstPerson);
        teamSheet.buttonAddPerson.fire();
        teamSheet.allAvailablePeopleListView.getSelectionModel().select(secondPerson);
        teamSheet.apply(firstTeam);

        Assert.assertTrue("Person 2 should not have been made scrummaster",
            firstTeam.getScrumMaster() != secondPerson);
    }

    @Test
    public void TestAddingMultipleValidPeopleToTeam() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Person secondPerson = new Person("ShortName2", "LongName2", "Description2");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addPerson(secondPerson);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().selectAll();
        teamSheet.buttonAddPerson.fire();


        Assert.assertTrue("People should be moved from available to current",
            teamSheet.allAvailablePeopleList.size() == 0);
        Assert.assertTrue("Current list should contain the added people",
            teamSheet.allCurrentPeopleList.size() == 2);
    }

    @Test
    public void TestAddingMultipleInvalidPeopleToTeam() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Team secondTeam = new Team("ShortName2", "LongName2", "Description2");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Person secondPerson = new Person("ShortName2", "LongName2", "Description2");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addPerson(secondPerson);
        secondTeam.addPerson(firstPerson);
        secondTeam.addPerson(secondPerson);
        firstPerson.setTeam(secondTeam);
        secondPerson.setTeam(secondTeam);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().selectAll();
        teamSheet.buttonAddPerson.fire();


        Assert.assertTrue("People should not be moved from available to current",
            teamSheet.allAvailablePeopleList.size() == 2);
        Assert.assertTrue("Current list should not contain any people",
            teamSheet.allCurrentPeopleList.size() == 0);
    }

    @Test
    public void TestAddingMultipleValidAndInvalidPeopleToTeam() throws Exception{
        Team firstTeam = new Team("ShortName", "LongName", "Description");
        Team secondTeam = new Team("ShortName2", "LongName2", "Description2");
        Person firstPerson = new Person("ShortName", "LongName", "Description");
        Person secondPerson = new Person("ShortName2", "LongName2", "Description2");
        testOrganisation.addTeam(firstTeam);
        testOrganisation.addPerson(firstPerson);
        testOrganisation.addPerson(secondPerson);
        secondTeam.addPerson(firstPerson);
        firstPerson.setTeam(secondTeam);

        teamSheet.fieldConstruct(firstTeam);
        teamSheet.peopleConstruct(testOrganisation);

        teamSheet.allAvailablePeopleListView.getSelectionModel().selectAll();
        teamSheet.buttonAddPerson.fire();


        Assert.assertTrue("People should not be moved from available to current",
            teamSheet.allAvailablePeopleList.size() == 2);
        Assert.assertTrue("Current list should not contain any people",
            teamSheet.allCurrentPeopleList.size() == 0);
    }

}
