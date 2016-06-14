package seng302.group3.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BacklogTest {

    private Backlog testBacklog;
    private Story testStory = new Story();

    @Before
    public void setUp() throws Exception {
        testBacklog = new Backlog();
        testBacklog.addStory(testStory);
        testBacklog.setShortName("shortName");
    }

    @After
    public void tearDown() throws Exception {
        testBacklog = null;

    }

    @Test
    //
    public void testAddStory() throws Exception {
        Story testStory2 = new Story();
        testBacklog.addStory(testStory2);
        assertTrue("testStory2 should be in testBacklog", testBacklog.getStories().contains(testStory));


    }

    @Test
    public void testRemoveStory() throws Exception {
        testBacklog.removeStory(testStory);
        assertTrue("testBacklog should now be empty", testBacklog.getStories().isEmpty());

    }



    @Test
    public void testBacklogWithNoParametersIsNotNull() throws Exception {
        Backlog newBacklog = new Backlog();
        assertNotNull("newBacklog shouldn't be null", newBacklog);
    }

    /*
    @Test
    public void testAddingStoryAlreadyInBacklogToAnotherBacklog() throws Exception {
        Backlog testBacklog2 = new Backlog();
        testBacklog.addStory(testStory);
        testBacklog2.addStory(testStory);
        assertFalse("testBacklog2 shouldn't be able to have testStory added", testBacklog2.getStories().contains(testStory));
    }
    */



    @Test
    public void testToString() throws Exception {
        assertEquals("toString should return just shortName", testBacklog.toString(), testBacklog.getShortName());

    }
}