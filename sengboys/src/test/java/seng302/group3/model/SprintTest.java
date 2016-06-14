package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SprintTest {


    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void testSevenParamConstructor() throws Exception {
        String shortName = "Short Name";
        String fullName = "Full Name";
        String description = "Description";
        TimePeriod timePeriod = new TimePeriod();
        Team team = new Team();
        Release release =  new Release();
        Backlog backlog = new Backlog();

        Sprint sprint = new Sprint(shortName, fullName, description, timePeriod, team, release, backlog);

        assertNotNull("sprint should exist", sprint);


    }

    @Test
    public void testGetTotalMinutesLoggedForSprint() throws Exception {
        String shortName = "Short Name";
        String fullName = "Full Name";
        String description = "Description";
        TimePeriod timePeriod = new TimePeriod();
        Team team = new Team();
        Release release =  new Release();
        Backlog backlog = new Backlog();

        Sprint sprint = new Sprint(shortName, fullName, description, timePeriod, team, release, backlog);
        Story story = new Story("Story 1", "Story One", "Desc");

        sprint.addStory(story);
        Task task = new Task("Task 1", "Desc");
        story.addTask(task);
        task.updateEstimate(new Estimate(60.0));

        LoggedTime time = new LoggedTime(LocalDateTime.now(), LocalDateTime.now().minusMinutes(40));

        task.addHourLog(time);

        Assert.assertEquals("The sprint should have 40 minutes logged against it", 40L, sprint.getTotalMinutesLogged(), 0.1);
    }


    @Test
    public void testGetTotalHoursLoggedForSprintForDay() throws Exception {
        String shortName = "Short Name";
        String fullName = "Full Name";
        String description = "Description";
        TimePeriod timePeriod = new TimePeriod();
        Team team = new Team();
        Release release =  new Release();
        Backlog backlog = new Backlog();

        Sprint sprint = new Sprint(shortName, fullName, description, timePeriod, team, release, backlog);
        Story story = new Story("Story 1", "Story One", "Desc");

        sprint.addStory(story);
        Task task = new Task("Task 1", "Desc");
        story.addTask(task);
        task.updateEstimate(new Estimate(60.0));

        LoggedTime time = new LoggedTime(LocalDateTime.now(), LocalDateTime.now().minusMinutes(40));

        task.addHourLog(time);

        Assert.assertEquals("The sprint should have 40 minutes logged against it", 40L, sprint.getHoursOnThisDay(LocalDate.now())*60, 0.1);
    }


    @Test
    public void testGetSprintEstimateMinutes() throws Exception {
        String shortName = "Short Name";
        String fullName = "Full Name";
        String description = "Description";
        TimePeriod timePeriod = new TimePeriod();
        Team team = new Team();
        Release release =  new Release();
        Backlog backlog = new Backlog();

        Sprint sprint = new Sprint(shortName, fullName, description, timePeriod, team, release, backlog);
        Story story = new Story("Story 1", "Story One", "Desc");

        sprint.addStory(story);
        Task task = new Task("Task 1", "Desc");
        story.addTask(task);
        task.updateEstimate(new Estimate(60.0));

        LoggedTime time = new LoggedTime(LocalDateTime.now(), LocalDateTime.now().minusMinutes(40));

        task.addHourLog(time);

        Assert.assertEquals("The sprint should have 40 minutes logged against it", 60L, sprint.getSprintEstimateMinutes(), 0.1);
    }
}