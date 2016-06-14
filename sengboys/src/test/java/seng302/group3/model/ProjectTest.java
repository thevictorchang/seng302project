package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by Eddy on 03-May-15.
 */
public class ProjectTest {
    private Project testProject;
    private Team testTeam;

    @Before
    public void setUp() throws Exception{
        testProject = new Project("nameShort", "longName", "description");
    }

    @After
    public void tearDown() throws Exception {
        testProject = null;
    }

    @Test
    public void testCopyData(){
        Project copyProject = new Project(testProject);
        Assert.assertTrue("A project made from another should have the same values ",
            copyProject.equals(testProject));
    }

    @Test
    public void TestAddTimePeriodWhenProjectAlreadyContainsTeam(){
        Team testTeam = new Team("test","testLN","testD");
        TimePeriod testTimePeriod = new TimePeriod(LocalDate.of(2015,1,1),
            LocalDate.of(2015,2,5),testTeam);
        testProject.getTeams().add(testTeam);
        testProject.addTimePeriod(testTimePeriod);

        Assert.assertFalse("The team in testproject should not be there twice",testProject.getTeams().size() == 2);
    }

    @Test
    public void TestCheckLastTeamWhenTimePeriodIsLastConnectionToTeam(){
        Team testTeam = new Team("test","testLN","testD");
        TimePeriod testTimePeriod = new TimePeriod(LocalDate.of(2015,1,1),
            LocalDate.of(2015,2,5),testTeam);
        testProject.getTimePeriods().add(testTimePeriod);
        testProject.getTeams().add(testTeam);

        testProject.checkLastTeam(testTimePeriod);
        Assert.assertTrue("The team should still be in the project",
            testProject.getTeams().contains(testTeam));
    }

    @Test
    public void TestCheckNewTeamWhenTimePeriodIsFirstConnectionToTeam(){
        Team testTeam = new Team("test","testLN","testD");
        TimePeriod testTimePeriod = new TimePeriod(LocalDate.of(2015,1,1),
            LocalDate.of(2015,2,5),testTeam);
        testProject.checkNewTeam(testTimePeriod);
        Assert.assertTrue("Check new team should have added the team to the project",
            testProject.getTeams().contains(testTeam));
    }
}
