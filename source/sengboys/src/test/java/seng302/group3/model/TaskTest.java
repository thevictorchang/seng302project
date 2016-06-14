package seng302.group3.model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class TaskTest extends TestCase {

    private Task testTask;

    @Before
    public void setUp() throws Exception {
        //make a test Project
        testTask = new Task("name text", 1);
    }

    @After
    public void tearDown() throws Exception {
        //destroys the test Project after test
        testTask = null;
    }

    public void testGetName() throws Exception {
        assertEquals("Task's name should be 'name text'", "name text", testTask.getName());

    }

    public void testGetPeople() throws Exception {
        assertTrue("The collection People should be empty", testTask.getPeople() != null);

    }


    public void testGetHours() throws Exception {

        assertTrue("Task's hours should be 1", testTask.getHours() == 1);

    }

    public void testSetHours() throws Exception {
        assertTrue("Task's hours should be 1", testTask.getHours() == 1);
        testTask.setHours(2.5);
        assertTrue("Task's hours should now be 2.5", testTask.getHours() == 2.5);
    }

    public void testSetName() throws Exception {
        assertEquals("Task's name should be 'name text'", "name text", testTask.getName());
        testTask.setName("SetNameTest");
        assertEquals("Task's name should now be 'SetNameTest'", "SetNameTest", testTask.getName());

    }

    public void testRemovePerson() throws Exception {
        Person testPerson = new Person("test person");
        testTask.addPerson(testPerson); // add a person
        assertTrue("The collection People should contain testPerson", (testTask.getPeople().contains(testPerson)));
        testTask.removePerson(testPerson);
        assertTrue("The collection People should be empty", testTask.getPeople().size() == 0);

    }

    public void testAddPerson() throws Exception {
        Person testPerson = new Person("test person");
        //check if the collection of people is empty first
        assertTrue("The collection People should be empty", testTask.getPeople().size() == 0);
        testTask.addPerson(testPerson); // add a person
        assertTrue("The collection People should contain testPerson", testTask.getPeople().contains(testPerson));
    }

    public void testToString() throws Exception {
        assertEquals("toString should only return the 'name text'", "name text", testTask.toString());
    }
}