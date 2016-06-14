package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

/**
 * Created by epa31 on 9/08/15.
 */
public class LoggedTimeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void TestGetLoggedTimeInHours() throws Exception {
        LoggedTime loggedTime = new LoggedTime(LocalDateTime.now().plusMinutes(126),LocalDateTime.now());
        Assert.assertEquals(loggedTime.getLoggedTimeInHours(), (Double) 2.1);
    }

    @Test
    public void TestGetLoggedTime() throws Exception {
        LoggedTime loggedTime = new LoggedTime(LocalDateTime.now().plusMinutes(20).plusSeconds(15),LocalDateTime.now());
        Assert.assertEquals(loggedTime.getLoggedTime(), (Long) 20L);
    }
}
