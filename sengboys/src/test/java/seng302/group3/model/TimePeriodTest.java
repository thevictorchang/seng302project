package seng302.group3.model;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.time.LocalDate;

/** 
* TimePeriod Tester. 
* 
* @author <Edward Armstrong>
* @since <pre>May 15, 2015</pre> 
* @version 1.0 
*/ 
public class TimePeriodTest {

    private TimePeriod timePeriod;

    @Before
    public void setUp() throws Exception {
        timePeriod = new TimePeriod(LocalDate.of(2015,1,1), LocalDate.of(2015,2,1));
    }

    @After
    public void tearDown() throws Exception {
        timePeriod = null;
    }

    @Test
    public void testSetStartDate() throws Exception {
        timePeriod.setStartDate(LocalDate.now());
        Assert.assertEquals("Date should be equal from date setter", timePeriod.getStartDate(),
            LocalDate.now());
    }

    @Test
    public void testGetStartDate() throws Exception {
        Assert.assertEquals("Gets the start date", LocalDate.of(2015,1,1), timePeriod.getStartDate());
    }

    @Test
    public void testGetEndDate() throws Exception {
        Assert.assertEquals("Gets the end date", (LocalDate.of(2015,2,1)), timePeriod.getEndDate());
    }

    @Test
    public void testTimePeriodDatesShouldBeEqual() throws Exception {
        timePeriod.setEndDate((LocalDate.now()));
        Assert.assertEquals("Date should be equal", (timePeriod.getEndDate()), LocalDate.now());
    }

    @Test
    public void testTimePeriodsShouldHaveOneDateInOverlap() throws Exception {
        TimePeriod newTimePeriod = new TimePeriod(LocalDate.of(2015,1,5), LocalDate.of(2015,6,16));
        Assert.assertTrue("New time period should overlap with time period",timePeriod.checkDateOverlap(newTimePeriod));
    }
    @Test
    public void testTimePeriodsShouldHaveTwoDatesInOverlap() throws Exception {
        TimePeriod newTimePeriod = new TimePeriod(LocalDate.of(2015,1,5), LocalDate.of(2015,1,16));
        Assert.assertTrue("New time period should overlap with time period",timePeriod.checkDateOverlap(newTimePeriod));
    }

    @Test
    public void testTimePeriodsShouldNotOverlap() throws Exception {
        TimePeriod farAwayTimePeriod = new TimePeriod(LocalDate.of(2100,1,1), LocalDate.of(2200,1,1));
        Assert.assertFalse("New time period should not overlap with time period",timePeriod.checkDateOverlap(farAwayTimePeriod));
    }

    @Test
    public void testTimePeriodShouldBeSubsetOfOtherTimePeriod() throws Exception {
        TimePeriod largeTime = new TimePeriod(LocalDate.of(2013,1,1), LocalDate.of(2016,1,1));
        Assert.assertTrue("Should find an overlap although neither the start or end date is in the time period",timePeriod.checkDateOverlap(largeTime));
    }


    @Test
    public void testTheDateShouldBeInTimePeriodCheckDate() throws Exception {
        Assert.assertTrue("the date should be in the time period",timePeriod.containsDate(
            LocalDate.of(2015, 1, 15)));
    }

    @Test
    public void testTheDateShouldNotBeInTimePeriodCheckDate() throws Exception {
        Assert.assertFalse("The date should not be in the time period",timePeriod.containsDate(
                LocalDate.of(2100, 1, 1)));
    }
}
