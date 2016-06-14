package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by cjm328 on 7/05/15.
 */
public class DeleteTest {
    private Organisation testOrganisation;
    private Person testPerson;
    private Skill testSkill;
    private Release testRelease;
    private Project testProject;
    private Team testTeam;

    @Before
    public void setUp() throws Exception {

        //Set up a test structure for deletion
        //make a test Organisation
        testOrganisation = new Organisation("shortName text");
        //make a test project
        testProject = new Project("testproj");
        testOrganisation.addProject(testProject);

        testTeam = new Team("testteam");

        TimePeriod testTimePeriod = new TimePeriod(LocalDate.of(2015,1,1), LocalDate.of(2015,12,1),testProject,testTeam);
        testOrganisation.addTeam(testTeam);
        //testProject.checkNewTeam(testTimePeriod);
        testProject.addTimePeriod(testTimePeriod);
        testTeam.addTimePeriod(testTimePeriod);

        testPerson = new Person("testperson");
        testOrganisation.addPerson(testPerson);
        testTeam.addPerson(testPerson);
        testProject.addPerson(testPerson);
        testPerson.setProject(testProject);
        testPerson.setTeam(testTeam);

        testSkill = new Skill("testLanguage");
        testPerson.addSkill(testSkill);

        testRelease = new Release("testrelease", LocalDate.now(), testProject);
        testOrganisation.addRelease(testRelease);
        testProject.addRelease(testRelease);


    }

    @After
    public void tearDown() throws Exception {
        //destroys the test Organisation after test
        testOrganisation = null;
        testProject = null;
        testTeam = null;
        testPerson = null;
        testSkill = null;
        testRelease = null;
    }

    @Test
    public void testOrganisationHasNoSkillsAfterSkillDelete() throws Exception {
        new Delete(testSkill, testOrganisation, true);
        Assert.assertTrue("Organisation has no skills", testOrganisation.getSkills().isEmpty());
    }

    @Test
    public void testProjectHasNoSkillsAfterSkillDelete() throws Exception {
        new Delete(testSkill, testOrganisation, true);
        Assert.assertTrue("Project has no skills", testProject.getSkills().isEmpty());
    }

    @Test
    public void testTeamHasNoSkillsAfterSkillDelete() throws Exception {
        new Delete(testSkill, testOrganisation, true);
        Assert.assertTrue("Team has no skills", testTeam.getSkills().isEmpty());
    }

    @Test
    public void testPersonHasNoSkillsAfterSkillDelete() throws Exception {
        new Delete(testSkill, testOrganisation, true);
        Assert.assertTrue("Person has no skills", testPerson.getSkills().isEmpty());
    }

    @Test
    // Tests for deleting people
    public void testOrganisationHasNoPeopleAfterPersonDelete() throws Exception {
        new Delete(testPerson, testOrganisation, true);
        Assert.assertTrue("Organisation has no people", testOrganisation.getPeople().isEmpty());
    }

    @Test
    public void testProjectHasNoPeopleAfterPersonDelete() throws Exception {
        new Delete(testPerson, testOrganisation, true);
        Assert.assertTrue("Project has no people", testProject.getPeople().isEmpty());
    }

    @Test
    public void testTeamHasNoPeopleAfterPersonDelete() throws Exception {
        new Delete(testPerson, testOrganisation, true);
        Assert.assertTrue("Team has no people", testTeam.getPeople().isEmpty());
    }

    @Test
    // Tests for deleting releases
    public void testOrganisationHasNoReleasesAfterReleaseDelete() throws Exception {
        new Delete(testRelease, testOrganisation, true);
        Assert.assertTrue("Organisation has no releases", testOrganisation.getReleases().isEmpty());
    }

    @Test
    public void testProjectHasNoReleasesAfterReleaseDelete() throws Exception {
        new Delete(testRelease, testOrganisation, true);
        Assert.assertTrue("Project has no releases", testProject.getReleases().isEmpty());
    }

    @Test
    // Tests for fully deleting a team
    public void testFullDeleteTeam() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Organisation has no teams", testOrganisation.getTeams().isEmpty());
    }

    @Test
    public void testProjectHasNoTeamsAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Project has no teams", testProject.getTeams().isEmpty());
    }

    @Test
    public void testOrganisationHasNoPeopleAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Organisation has no people", testOrganisation.getPeople().isEmpty());
    }

    @Test
    public void testProjectHasNoPeopleAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Project has no people", testProject.getPeople().isEmpty());
    }

    @Test
    public void testPersonHasNoTeamAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Person has no team", testPerson.getTeam() == null);
    }

    @Test
    public void testPersonHasNoProjectAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Person has no project", testPerson.getProject() == null);
    }

    @Test
    public void testTeamHasNoPeopleAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Team has no people", testTeam.getPeople().isEmpty());
    }

    @Test
    public void testTeamHasNoSkillsAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Team has no skills", testTeam.getSkills().isEmpty());
    }

    @Test
    public void testProjectHasNoSkillsAfterFullTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, true);
        Assert.assertTrue("Project has no skills", testProject.getSkills().isEmpty());
    }

    @Test
    // Tests for partially deleting a team
    public void testOrganisationHasNoTeamsAfterPartialTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, false);
        Assert.assertTrue("Organisation has no teams", testOrganisation.getTeams().isEmpty());
    }

    @Test
    public void testProjectHasNoTeamsAfterPartialTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, false);
        Assert.assertTrue("Project has no teams", testProject.getTeams().isEmpty());
    }

    @Test
    public void testOrganisationHasNoPeopleAfterPartialTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, false);
        Assert.assertFalse("Organisation has no people", testOrganisation.getPeople().isEmpty());
    }

    @Test
    public void testPersonHasNoTeamAfterPartialTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, false);
        Assert.assertTrue("Person has no team", testPerson.getTeam() == null);
    }

    @Test
    public void testTeamHasNoPeopleAfterPartialTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, false);
        Assert.assertTrue("Team has no people", testTeam.getPeople().isEmpty());
    }

    @Test
    public void testTeamHasNoSkillsAfterPartialTeamDelete() throws Exception {
        new Delete(testTeam, testOrganisation, false);
        Assert.assertFalse("Team has no skills", testTeam.getSkills().isEmpty());
    }

    @Test
    // Tests for fully deleting a project
    public void testOrganisationHasNoProjectsAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Organisation has no projects", testOrganisation.getProjects().isEmpty());
    }

    @Test
    public void testOrganisationHasNoTeamsAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Organisation has no teams", testOrganisation.getTeams().isEmpty());
    }

    @Test
    public void testProjectHasNoTeamsAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Project has no teams", testProject.getTeams().isEmpty());
    }

    @Test
    public void testOrganisationHasNoPeopleAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Organisation has no people", testOrganisation.getPeople().isEmpty());
    }

    @Test
    public void testProjectHasNoPeopleAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Project has no people", testProject.getPeople().isEmpty());
    }

    @Test
    public void testPersonHasNoTeamAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Person has no team", testPerson.getTeam() == null);
    }

    @Test
    public void testPersonHasNoProjectAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Person has no project", testPerson.getProject() == null);
    }

    @Test
    public void testTeamHasNoPeopleAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Team has no people", testTeam.getPeople().isEmpty());
    }

    @Test
    public void testTeamHasNoSkillsAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Team has no skills", testTeam.getSkills().isEmpty());
    }

    @Test
    public void testProjectHasNoSkillsAfterFullProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, true);
        Assert.assertTrue("Project has no skills", testProject.getSkills().isEmpty());
    }

    @Test
    // Tests for partially deleting a project
    public void testOrganisationHasNoProjectsAfterPartialProjectDeletion() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertTrue("Organisation has no projects", testOrganisation.getProjects().isEmpty());
    }

    @Test
    public void testOrganisationHasNoTeamsAfterPartialProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertFalse("Organisation has no teams", testOrganisation.getTeams().isEmpty());
    }

    @Test
    public void testProjectHasNoTeamsAfterPartialProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertTrue("Project has no teams", testProject.getTeams().isEmpty());
    }

    @Test
    public void testOrganisationHasNoPeopleAfterPartialProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertFalse("Organisation has no people", testOrganisation.getPeople().isEmpty());
    }

    @Test
    public void testProjectHasNoPeopleAfterPartialProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertTrue("Project has no people", testProject.getPeople().isEmpty());
    }

    @Test
    public void testPersonHasNoTeamAfterPartialProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertFalse("Person has no team", testPerson.getTeam() == null);
    }

    @Test
    public void testPersonHasNoProjectAfterPartialProjectDelete() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertTrue("Person has no project", testPerson.getProject() == null);
    }

    @Test
    public void testTeamHasNoPeopleAfterPartialProjectDeletion() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertFalse("Team has no people", testTeam.getPeople().isEmpty());
    }

    @Test
    public void testTeamHasNoSkillsAfterPartialProjectDeletion() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertFalse("Team has no skills", testTeam.getSkills().isEmpty());
    }

    @Test
    public void testProjectHasNoSkillsAfterPartialProjectDeletion() throws Exception {
        new Delete(testProject, testOrganisation, false);
        Assert.assertTrue("Project has no skills", testProject.getSkills().isEmpty());
    }
}
