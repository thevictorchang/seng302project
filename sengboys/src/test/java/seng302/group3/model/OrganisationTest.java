package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class OrganisationTest {


    private Organisation testOrganisation; // test Organisation for testing the Organisation() method
    private Organisation testingCopyOrganisation;

    @Before
    public void setUp() throws Exception {
        //make a test Organisation
        testOrganisation = new Organisation("shortName text");
        testingCopyOrganisation = new Organisation("Organisation for copying");


        Project project1 = new Project("project 1 short name", "project 1 long name", "project 1 desc");
        testingCopyOrganisation.addProject(project1);
        Project project2 = new Project("project 2 short name", "project 2 long name", "project 2 desc");
        testingCopyOrganisation.addProject(project2);

        Team team1 = new Team("team 1 short name", "team 1 long name", "team 1 desc");
        testingCopyOrganisation.addTeam(team1);
        Team team2 = new Team("team 2 short name", "team 2 long name", "team 2 desc");
        testingCopyOrganisation.addTeam(team2);
        Team team3 = new Team("team 3 short name", "team 3 long name", "team 3 desc");
        testingCopyOrganisation.addTeam(team3);

        Person person1 = new Person("Person 1 name", "Person 1 full name", "Person 1 userid");
        testingCopyOrganisation.addPerson(person1);
        Person person2 = new Person("Person 2 name", "Person 2 full name", "Person 2 userid");
        testingCopyOrganisation.addPerson(person2);
        Person person3 = new Person("Person 3 name", "Person 3 full name", "Person 3 userid");
        testingCopyOrganisation.addPerson(person3);

        Skill skill1 = new Skill("Skill 1 name", "Skill 1 desc");
        testingCopyOrganisation.addSkill(skill1);
        Skill skill2 = new Skill("Skill 2 name", "Skill 2 desc");
        testingCopyOrganisation.addSkill(skill2);
        Skill skill3 = new Skill("Skill 3 name", "Skill 3 desc");
        testingCopyOrganisation.addSkill(skill3);

        person1.addSkill(skill1);
        person1.addSkill(skill2);
        person1.addSkill(skill3);
        person2.addSkill(skill2);

        team1.addPerson(person1);
        person1.setTeam(team1);
        team1.addPerson(person3);
        person3.setTeam(team1);
        team2.addPerson(person2);
        person2.setTeam(team2);

        TimePeriod time1 = new TimePeriod(LocalDate.of(2000,1,1),LocalDate.of(2001,1,1), team1);
        TimePeriod time2 = new TimePeriod(LocalDate.of(2002,1,1),LocalDate.of(2003,1,1), team3);
        TimePeriod time3 = new TimePeriod(LocalDate.of(2004,1,1),LocalDate.of(2005,1,1), team2);

        project1.addTimePeriod(time1);
        project1.addTimePeriod(time2);
        project2.addTimePeriod(time3);

    }

    @After
    public void tearDown() throws Exception {
        //destroys the test Organisation after test
        testOrganisation = null;
    }

    @Test
    public void testConstructors() {
        Organisation zeroParamOrganisation = new Organisation();
        Organisation fullParamOrganisation = new Organisation("fullParamOrganisation");
        Assert.assertNotNull("zeroParamOrganisation should exist", zeroParamOrganisation);
        Assert.assertNotNull("fullParamOrganisation should exist", fullParamOrganisation);
    }

    @Test
    public void testGetNameShort() throws Exception {
        Assert.assertEquals("Organisation's nameShort should be 'shortName text'"
            , "shortName text", testOrganisation.getShortName());
    }

    @Test
    public void testSetNameShort() throws Exception {
        Assert.assertTrue("The nameShort should be 'shortName text'"
            , testOrganisation.getShortName().equals("shortName text"));
        testOrganisation.setShortName("testNameShort");
        Assert.assertTrue("The nameShort should be 'testNameShort'"
            , testOrganisation.getShortName().equals("testNameShort"));
    }


    /*
    public void testGetPastHistory() throws Exception {
        fail("Not yet implemented");
    }
    */

    @Test
    public void testGetCurrentChange() throws Exception {
        //Assert.assertNull("The current change should be null by default", testOrganisation.getCurrentChange());
        //Change testChange = new Change(Change.changeType.EDIT_ORGANISATION, testOrganisation);
        //testOrganisation.addChange(testChange);
        //Assert.assertNotNull("Current change should not be null when a change is added", testOrganisation.getCurrentChange());
    }
    
    /*
    public void testGetFutureHistory() throws Exception {
        fail("Not yet implemented");
    }
    */

    @Test
    public void testGetFilename() throws Exception {
        Assert.assertEquals("Filename should be 'organisation1.org'", testOrganisation.getFilename(), "organisation1.org");
    }

    @Test
    public void testSetFilename() throws Exception {
        testOrganisation.setFilename("project2.proj");
        Assert.assertEquals("Filename should be 'project2.proj'", testOrganisation.getFilename(), "project2.proj");
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertTrue((testOrganisation.getShortName()).equals(testOrganisation.toString()));
    }

    @Test
    public void testAddChange() throws Exception {
        //Assert.assertNull("The current change should be null by default", testOrganisation.getCurrentChange());
        //Change testChange = new Change(Change.changeType.EDIT_ORGANISATION, testOrganisation);
        //testOrganisation.addChange(testChange);
        //Assert.assertNotNull("Current change should not be null when a change is added", testOrganisation.getCurrentChange());
    }

    @Test
    public void testSetCurrentChange() throws Exception {
        //Change testChange = new Change(Change.changeType.EDIT_ORGANISATION, testOrganisation);
        //testOrganisation.setCurrentChange(testChange);
        //Assert.assertNotNull("Current change should not be null when a change is added", testOrganisation.getCurrentChange());
    }

    @Test
    public void testSetPeople() throws Exception {
        Collection<Person> testPeople = new ArrayList<>();
        Person johnDoe = new Person("johnDoe", "John Doe", "JD1");
        Person janeDoe = new Person("janeDoe", "Jane Doe", "JD2");
        testPeople.add(johnDoe);
        testPeople.add(janeDoe);
        testOrganisation.setPeople(testPeople);
        Assert.assertTrue("testOrganisation's people collection should be able to set members", testOrganisation.getPeople().equals(testPeople));
    }

    @Test
    public void testRemovePerson() throws Exception {
        Person testPerson = new Person("test person");
        testOrganisation.addPerson(testPerson); // add a person
        Assert.assertTrue("The collection People should contain testPerson", (testOrganisation.getPeople().contains(testPerson)));
        testOrganisation.removePerson(testPerson);
        Assert.assertTrue("The collection People should be empty", testOrganisation.getPeople().size() == 0);
    }

    @Test
    public void testAddPerson() throws Exception {
        Person testPerson = new Person("test person");
        //check if the collection of people is empty first
        Assert.assertTrue("The collection People should be empty", testOrganisation.getPeople().size() == 0);
        testOrganisation.addPerson(testPerson); // add a person
        Assert.assertTrue("The collection People should contain testPerson", testOrganisation.getPeople().contains(testPerson));

    }

    @Test
    public void testGetPeople() throws Exception {
        //check if the collection of people is empty
        Assert.assertTrue("The collection People should be empty", testOrganisation.getPeople().isEmpty());


    }

    @Test
    public void testAddSkill() throws Exception {
        Skill testSkill = new Skill("Test Skill");
        Assert.assertTrue("The collection skills should be empty", testOrganisation.getSkills().size() == 0);
        testOrganisation.addSkill(testSkill);
        Assert.assertTrue("The collection skills should contain testPerson", testOrganisation.getSkills().contains(testSkill));
    }

    @Test
    public void testRemoveSkill() throws Exception {
        Skill testSkill = new Skill("test Skill");
        testOrganisation.addSkill(testSkill);
        Assert.assertTrue("The collection skills should contain testSkill", (testOrganisation.getSkills().contains(testSkill)));
        testOrganisation.removeSkill(testSkill);
        Assert.assertTrue("The collection skills should be empty", testOrganisation.getSkills().size() == 0);
    }

    @Test
    public void testAddProject() throws Exception {
        Project testProject = new Project("Test Project");
        Assert.assertTrue("The collection projects should be empty", testOrganisation.getProjects().size() == 0);
        testOrganisation.addProject(testProject);
        Assert.assertTrue("The collection projects should contain testProject", testOrganisation.getProjects().contains(testProject));
    }

    @Test
    public void testRemoveProject() throws Exception {
        Project testProject = new Project("Test Project");
        testOrganisation.addProject(testProject);
        Assert.assertTrue("The collection Projects should contain testProject", (testOrganisation.getProjects().contains(testProject)));
        testOrganisation.removeProject(testProject);
        Assert.assertTrue("The collection Projects should be empty", testOrganisation.getProjects().size() == 0);
    }

    @Test
    public void testAddTeam() throws Exception {
        Team testTeam = new Team("Test Team");
        Assert.assertTrue("The collection Teams should be empty", testOrganisation.getTeams().size() == 0);
        testOrganisation.addTeam(testTeam);
        Assert.assertTrue("The collection Teams should contain testTeam", testOrganisation.getTeams().contains(testTeam));
    }

    @Test
    public void testRemoveTeam() throws Exception {
        Team testTeam = new Team("Test Team");;
        testOrganisation.addTeam(testTeam);
        Assert.assertTrue("The collection Teams should contain testTeam", (testOrganisation.getTeams().contains(testTeam)));
        testOrganisation.removeTeam(testTeam);
        Assert.assertTrue("The collection Teams should be empty", testOrganisation.getTeams().size() == 0);
    }

    @Test
    public void testUnique() throws Exception {
        Person testPerson = new Person("Testperson");
        Project testProject = new Project("shortName","longName", "description");
        Assert.assertTrue("There should be no Person with Testperson as a shortMa,e", testOrganisation.uniqueName("Testperson", Organisation.uniqueType.UNIQUE_PERSON));
        testOrganisation.addPerson(testPerson);
        Assert.assertFalse("Testing with the Testperson shortname should ne non-unique now", testOrganisation.uniqueName("Testperson", Organisation.uniqueType.UNIQUE_PERSON));
        testOrganisation.removePerson(testPerson);
        testOrganisation.addProject(testProject);
        Assert.assertTrue("Only the shortname needs to be unique", testOrganisation.uniqueName("wrongshortname", Organisation.uniqueType.UNIQUE_PROJECT));
    }


    /**
     * Testing the serializedCopy() method within organisation. Expected result is for an exact copy of the given
     * organisation within a new object.
     * @throws Exception
     */
    @Test
    public void testSerializedCopy() throws Exception{
        Organisation copyOrganisation = testingCopyOrganisation.serializedCopy();
        Assert.assertEquals("There should be two projects copied by the method", 2,
            copyOrganisation.getProjects().size());
        Assert.assertEquals("These should be three teams copied by the method", 3,
            copyOrganisation.getTeams().size());
        Assert.assertEquals("These should be three People copied by the method", 3,
            copyOrganisation.getPeople().size());
        Assert.assertEquals("These should be three Skills copied by the method", 3,
            copyOrganisation.getSkills().size());

        ArrayList<Project> projects = (ArrayList<Project>) copyOrganisation.getProjects();
        Project project1 = projects.get(0);
        Project project2 = projects.get(1);

        Assert.assertEquals("Project 1 short name should be the same as the original", project1.getShortName(), "project 1 short name");
        Assert.assertEquals("Project 1 long name should be the same as the original", project1.getNameLong(), "project 1 long name");
        Assert.assertEquals("Project 1 description should be the same as the original", project1.getDescription(), "project 1 desc");

        Assert.assertEquals("Project 2 short name should be the same as the original", project2.getShortName(), "project 2 short name");
        Assert.assertEquals("Project 2 long name should be the same as the original", project2.getNameLong(), "project 2 long name");
        Assert.assertEquals("Project 2 description should be the same as the original", project2.getDescription(), "project 2 desc");

        Assert.assertEquals("Project 1 should have two teams", 2, project1.getTeams().size());
        Assert.assertEquals("Project 1 should have two people", 2, project1.getPeople().size());
        Assert.assertEquals("Project 1 should have three skills", 3, project1.getSkills().size());

        Assert.assertEquals("Project 2 should have one team", 1, project2.getTeams().size());
        Assert.assertEquals("Project 2 should have one person", 1, project2.getPeople().size());
        Assert.assertEquals("Project 2 should have one skill", 1, project2.getSkills().size());

        ArrayList<Project> originalProjects = (ArrayList<Project>) testingCopyOrganisation.getProjects();
        Project originalProject1 = originalProjects.get(0);
        Project originalProject2 = originalProjects.get(1);

        Assert.assertNotSame("The copied project one should be a different to the original" , project1, originalProject1);
        Assert.assertNotSame("The copied project two should be a different to the original" , project2, originalProject2);

        ArrayList<Team> teams = (ArrayList<Team>) copyOrganisation.getTeams();
        Team team1 = teams.get(0);
        Team team2 = teams.get(1);
        Team team3 = teams.get(2);

        Assert.assertEquals("Team 1 short name should be the same as the original", team1.getShortName(), "team 1 short name");
        Assert.assertEquals("Team 1 long name should be the same as the original", team1.getNameLong(), "team 1 long name");
        Assert.assertEquals("Team 1 description should be the same as the original", team1.getDescription(), "team 1 desc");

        Assert.assertEquals("Team 2 short name should be the same as the original", team2.getShortName(), "team 2 short name");
        Assert.assertEquals("Team 2 long name should be the same as the original", team2.getNameLong(), "team 2 long name");
        Assert.assertEquals("Team 2 description should be the same as the original", team2.getDescription(), "team 2 desc");

        Assert.assertEquals("Team 3 short name should be the same as the original", team3.getShortName(), "team 3 short name");
        Assert.assertEquals("Team 3 long name should be the same as the original", team3.getNameLong(), "team 3 long name");
        Assert.assertEquals("Team 3 description should be the same as the original", team3.getDescription(), "team 3 desc");

        Assert.assertEquals("Team 1 should have two people", 2, team1.getPeople().size());
        Assert.assertEquals("Team 1 should have three skills", 3, team1.getSkills().size());

        Assert.assertEquals("Team 2 should have one person", 1, team2.getPeople().size());
        Assert.assertEquals("Team 2 should have one skill", 1, team2.getSkills().size());

        Assert.assertEquals("Team 3 should have no people", 0, team3.getPeople().size());
        Assert.assertEquals("Team 3 should have no skills", 0, team3.getSkills().size());

        ArrayList<Team> originalTeams = (ArrayList<Team>) testingCopyOrganisation.getTeams();
        Team originalTeam1 = originalTeams.get(0);
        Team originalTeam2 = originalTeams.get(1);
        Team originalTeam3 = originalTeams.get(2);

        Assert.assertNotSame("The copied team one should be a different to the original" , team1, originalTeam1);
        Assert.assertNotSame("The copied team two should be a different to the original" , team2, originalTeam2);
        Assert.assertNotSame("The copied team three should be a different to the original" , team3, originalTeam3);

        ArrayList<Person> people = (ArrayList<Person>) copyOrganisation.getPeople();
        Person person1 = people.get(0);
        Person person2 = people.get(1);
        Person person3 = people.get(2);

        Assert.assertEquals("Person 1 short name should be the same as the original", person1.getShortName(), "Person 1 name");
        Assert.assertEquals("Person 1 full name should be the same as the original", person1.getFullName(), "Person 1 full name");
        Assert.assertEquals("Person 1 userid should be the same as the original", person1.getUserID(), "Person 1 userid");

        Assert.assertEquals("Person 2 short name should be the same as the original", person2.getShortName(), "Person 2 name");
        Assert.assertEquals("Person 2 full name should be the same as the original", person2.getFullName(), "Person 2 full name");
        Assert.assertEquals("Person 2 userid should be the same as the original", person2.getUserID(), "Person 2 userid");

        Assert.assertEquals("Person 3 short name should be the same as the original", person3.getShortName(), "Person 3 name");
        Assert.assertEquals("Person 3 full name should be the same as the original", person3.getFullName(), "Person 3 full name");
        Assert.assertEquals("Person 3 userid should be the same as the original", person3.getUserID(), "Person 3 userid");

        Assert.assertEquals("Person 1 should have three skills", 3, person1.getSkills().size());
        Assert.assertEquals("Person 2 should have one skill", 1, person2.getSkills().size());
        Assert.assertEquals("Person 3 should have no skills", 0, person3.getSkills().size());

        ArrayList<Person> originalPeople = (ArrayList<Person>) testingCopyOrganisation.getPeople();
        Person originalPerson1 = originalPeople.get(0);
        Person originalPerson2 = originalPeople.get(1);
        Person originalPerson3 = originalPeople.get(2);

        Assert.assertNotSame("The copied person one should be a different to the original" , person1, originalPerson1);
        Assert.assertNotSame("The copied person two should be a different to the original" , person2, originalPerson2);
        Assert.assertNotSame("The copied person three should be a different to the original" , person3, originalPerson3);

        ArrayList<Skill> skills = (ArrayList<Skill>) copyOrganisation.getSkills();
        Skill skill1 = skills.get(0);
        Skill skill2 = skills.get(1);
        Skill skill3 = skills.get(2);

        Assert.assertEquals("Skill 1 name should be the same as the original", skill1.getShortName(), "Skill 1 name");
        Assert.assertEquals("Skill 1 description should be the same as the original", skill1.getDescription(), "Skill 1 desc");

        Assert.assertEquals("Skill 2 name should be the same as the original", skill2.getShortName(), "Skill 2 name");
        Assert.assertEquals("Skill 2 description should be the same as the original", skill2.getDescription(), "Skill 2 desc");

        Assert.assertEquals("Skill 3 name should be the same as the original", skill3.getShortName(), "Skill 3 name");
        Assert.assertEquals("Skill 3 description should be the same as the original", skill3.getDescription(), "Skill 3 desc");

        ArrayList<Skill> originalSkills = (ArrayList<Skill>) testingCopyOrganisation.getSkills();
        Skill originalSkill1 = originalSkills.get(0);
        Skill originalSkill2 = originalSkills.get(1);
        Skill originalSkill3 = originalSkills.get(2);

        Assert.assertNotSame("The copied skill one should be a different to the original" , skill1, originalSkill1);
        Assert.assertNotSame("The copied skill two should be a different to the original" , skill2, originalSkill2);
        Assert.assertNotSame("The copied skill three should be a different to the original" , skill3, originalSkill3);

    }

}

