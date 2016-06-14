package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.*;
import seng302.group3.model.gui.TimeLogRow;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Eddy on 02-Aug-15.
 */
public class TimeLogSheetTest  {

    private Organisation testOrganisation;
    private TimeLogSheet timeLogSheet;
    private Task task;
    private Story story;
    private LoggedTime loggedTime;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        testOrganisation = new Organisation("testOrg");
        story = new Story("1","1","1");
        task = new Task("Task","1",1);
        story.addTask(task);
        task.setObjectType(story);
        testOrganisation.addTask(task);
        testOrganisation.addStory(story);
        loggedTime = new LoggedTime(LocalDateTime.now(),LocalDateTime.now());

        Team team2 = new Team("team2", "2", "2");
        testOrganisation.addTeam(team2);
        Project projectTwo = new Project("ProjectTwo", "2", "2");
        testOrganisation.addProject(projectTwo);
        Person PO = new Person("PO", "ProductOwner", "po302");
        testOrganisation.addPerson(PO);
        Skill productOwner = new Skill("PO", "Product Owner");
        testOrganisation.addSkill(productOwner);
        PO.addSkill(productOwner);
        team2.addPerson(PO);
        PO.setTeam(team2);
        Backlog backlog2 = new Backlog("Backlog2", "2", PO);
        testOrganisation.addBacklog(backlog2);
        Release release2 = new Release("Release2", LocalDate.of(2016,11,11),projectTwo);
        testOrganisation.addRelease(release2);
        Sprint sprint = new Sprint("Sprint1","1","1",new TimePeriod(LocalDate.of(2011, 10, 10),
            LocalDate.of(2018,11,10)),team2,release2,backlog2);
        testOrganisation.addSprint(sprint);
        sprint.addStory(story);
    }

    @After
    public void tearDown() throws Exception{
        testOrganisation = null;
        task = null;
        story = null;
        loggedTime = null;

    }

    @Test
    public void TestInitialisationWithNoOtherLoggedTime() throws Exception{
        timeLogSheet = new TimeLogSheet(task,testOrganisation);
        Assert.assertTrue("",timeLogSheet.timeScrollList.getItems().size() == 0);
    }

    @Test
    public void TestInitialisationWithOtherLoggedTime() throws Exception{
        task.addHourLog(loggedTime);
        timeLogSheet = new TimeLogSheet(task,testOrganisation);
        Assert.assertTrue("",timeLogSheet.timeScrollList.getItems().size() == 1);
    }

    @Test
    public void TestGetSprintTeamWithValidTeamInSprint() throws Exception{
        Team team2 = new Team("team2", "2", "2");
        testOrganisation.addTeam(team2);
        Project projectTwo = new Project("ProjectTwo", "2", "2");
        testOrganisation.addProject(projectTwo);
        Person PO = new Person("PO", "ProductOwner", "po302");
        testOrganisation.addPerson(PO);
        Skill productOwner = new Skill("PO", "Product Owner");
        testOrganisation.addSkill(productOwner);
        PO.addSkill(productOwner);
        Backlog backlog2 = new Backlog("Backlog2", "2", PO);
        testOrganisation.addBacklog(backlog2);
        Release release2 = new Release("Release2", LocalDate.of(2016,11,11),projectTwo);
        testOrganisation.addRelease(release2);
        Sprint sprint = new Sprint("Sprint1","1","1",new TimePeriod(LocalDate.of(2011, 10, 10),
            LocalDate.of(2018,11,10)),team2,release2,backlog2);
        testOrganisation.addSprint(sprint);
        sprint.addStory(story);
        timeLogSheet = new TimeLogSheet(task, testOrganisation);
        Assert.assertEquals(team2,timeLogSheet.getSprintTeamFromStory(testOrganisation));
    }

    @Test
    public void TestGetSprintTeamWithoutValidTeam() throws Exception{
        Team team2 = new Team("team2", "2", "2");
        testOrganisation.addTeam(team2);
        Project projectTwo = new Project("ProjectTwo", "2", "2");
        testOrganisation.addProject(projectTwo);
        Person PO = new Person("PO", "ProductOwner", "po302");
        testOrganisation.addPerson(PO);
        Skill productOwner = new Skill("PO", "Product Owner");
        testOrganisation.addSkill(productOwner);
        PO.addSkill(productOwner);
        Backlog backlog2 = new Backlog("Backlog2", "2", PO);
        testOrganisation.addBacklog(backlog2);
        Release release2 = new Release("Release2", LocalDate.of(2016,11,11),projectTwo);
        testOrganisation.addRelease(release2);
        Sprint sprint = new Sprint("Sprint2","1","1",new TimePeriod(LocalDate.of(2011, 10, 10),
            LocalDate.of(2018,11,10)),team2,release2,backlog2);
        testOrganisation.addSprint(sprint);
        timeLogSheet = new TimeLogSheet(task, testOrganisation);
        sprint.setTeam(null);
        sprint.addStory(story);

        Assert.assertEquals(null,timeLogSheet.getSprintTeamFromStory(testOrganisation));
    }

    @Test
    public void TestParseTimeHourMinFormat() throws Exception{
        timeLogSheet = new TimeLogSheet(testOrganisation);
        Assert.assertTrue(timeLogSheet.parseTime("12h32m").equals("752"));
    }

    @Test
    public void TestParseTimeIntFormat() throws Exception{
        timeLogSheet = new TimeLogSheet(testOrganisation);
        Assert.assertTrue(timeLogSheet.parseTime("12").equals("720"));
    }

    @Test
    public void TestParseTimeDoubleFormat() throws Exception{
        timeLogSheet = new TimeLogSheet(testOrganisation);
        Assert.assertTrue(timeLogSheet.parseTime("1.2").equals("72"));
    }

    @Test
    public void TestParseTimeHourFormat() throws Exception{
        timeLogSheet = new TimeLogSheet(testOrganisation);
        Assert.assertTrue(timeLogSheet.parseTime("12h").equals("720"));
    }

    @Test
    public void TestParseTimeMinFormat() throws Exception{
        timeLogSheet = new TimeLogSheet(testOrganisation);
        Assert.assertTrue(timeLogSheet.parseTime("32m").equals("32"));
    }

    @Test
    public void TestValidateAllLogsSaved() throws Exception{
        task.addHourLog(loggedTime);
        timeLogSheet = new TimeLogSheet(task, testOrganisation);
        Assert.assertTrue(timeLogSheet.validate());
    }

    @Test
    public void TestValidateNotAllLogsSaved() throws Exception{
        task.addHourLog(loggedTime);
        timeLogSheet = new TimeLogSheet(task,testOrganisation);
        TimeLogRow timeLogRow = timeLogSheet.makeTimeLogRow(loggedTime);
        timeLogRow.setUnsaved();
        Assert.assertTrue(timeLogSheet.validate());
    }

    @Test
    public void TestMakeTimeLogRowWithNullLoggedTimeIsNotSaved() throws Exception{
        timeLogSheet = new TimeLogSheet(task,testOrganisation);
        TimeLogRow timeLogRow = timeLogSheet.makeTimeLogRow(null);
        Assert.assertFalse(timeLogRow.isSaved());
    }

    @Test
    public void TestMakeTimeLogRowWithLoggedTimeIsSaved() throws Exception{
        task.addHourLog(loggedTime);
        timeLogSheet = new TimeLogSheet(task,testOrganisation);
        TimeLogRow timeLogRow = timeLogSheet.makeTimeLogRow(loggedTime);
        Assert.assertTrue(timeLogRow.isSaved());
    }
}
