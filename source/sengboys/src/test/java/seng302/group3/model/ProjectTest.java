package seng302.group3.model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.group3.model.History.Change;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;

public class ProjectTest extends TestCase {


    private Project testProject; // test Project for testing the Project() method

    @Before
    public void setUp() throws Exception {
        //make a test Project
        testProject = new Project("shortName text", "longName text", "description text");
    }

    @After
    public void tearDown() throws Exception {
        //destroys the test Project after test
        testProject = null;
    }

    public void testConstructors() {
        Project zeroParamProject = new Project();
        Project fullParamProject = new Project("fullParamProject", "Full Parameter", "defaultDescription");
        assertNotNull("zeroParamProject should exist", zeroParamProject);
        assertNotNull("fullParamProject should exist", fullParamProject);
    }

    public void testGetNameShort() throws Exception {
        assertEquals("Project's nameShort should be 'shortName text'", "shortName text", testProject.getNameShort());
    }

    public void testGetNameLong() throws Exception {
        assertEquals("Project's nameLong should be 'longName text'", "longName text", testProject.getNameLong());

    }

    public void testGetDescription() throws Exception {
        assertEquals("Project's description should be 'description text'", "description text", testProject.getDescription());

    }

    public void testGetPeople() throws Exception {
        //check if the collection of people is empty
        assertTrue("The collection People should be empty", testProject.getPeople().isEmpty());

    }

    public void testSetNameShort() throws Exception {
        assertTrue("The nameShort should be 'shortName text'", testProject.getNameShort().equals("shortName text"));
        testProject.setShortName("testNameShort");
        assertTrue("The nameShort should be 'testNameShort'", testProject.getNameShort().equals("testNameShort"));
    }

    public void testSetNameLong() throws Exception {
        assertTrue("The nameLong should be 'longName text'", testProject.getNameLong().equals("longName text"));
        testProject.setLongName("testNameLong");
        assertTrue("The nameLong should be 'testNameLong'", testProject.getNameLong().equals("testNameLong"));
    }

    public void testSetDescription() throws Exception {
        assertTrue("The description should be 'description text'", testProject.getDescription().equals("description text"));
        testProject.setDescription("testDescription");
        assertTrue("The description should be 'testDescription'", testProject.getDescription().equals("testDescription"));
    }

    public void testRemovePerson() throws Exception {
        Person testPerson = new Person("test person");
        testProject.addPerson(testPerson); // add a person
        assertTrue("The collection People should contain testPerson", (testProject.getPeople().contains(testPerson)));
        testProject.removePerson(testPerson);
        assertTrue("The collection People should be empty", testProject.getPeople().size() == 0);
    }

    public void testAddPerson() throws Exception {
        Person testPerson = new Person("test person");
        //check if the collection of people is empty first
        assertTrue("The collection People should be empty", testProject.getPeople().size() == 0);
        testProject.addPerson(testPerson); // add a person
        assertTrue("The collection People should contain testPerson", testProject.getPeople().contains(testPerson));

    }

    /*
    public void testGetPastHistory() throws Exception {
        fail("Not yet implemented");
    }
    */

    public void testGetCurrentChange() throws Exception {
        assertNull("The current change should be null by default", testProject.getCurrentChange());
        Change testChange = new Change(Change.changeType.EDIT_PERSON, testProject);
        testProject.addChange(testChange);
        assertNotNull("Current change should not be null when a change is added", testProject.getCurrentChange());
    }
    
    /*
    public void testGetFutureHistory() throws Exception {
        fail("Not yet implemented");
    }
    */

    public void testGetFilename() throws Exception {
        assertEquals("Filename should be 'project1.proj'", testProject.getFilename(), "project1.proj");
    }

    public void testSetFilename() throws Exception {
        testProject.setFilename("project2.proj");
        assertEquals("Filename should be 'project2.proj'", testProject.getFilename(), "project2.proj");
    }

    public void testToString() throws Exception {
        assertTrue(("Project{" +
                "nameShort='" + testProject.getNameShort() + '\'' +
                ", nameLong='" + testProject.getNameLong() + '\'' +
                ", description='" + testProject.getDescription() + '\'' +
                ", people=" + testProject.getPeople() +
                '}').equals(testProject.toString()));
    }

    public void testEquals() throws Exception {
        Project projA = new Project("projA", "Project A", "PA1");
        Project projB = new Project("projB", "Project B", "PB2");
        assertTrue("Two projects that are the same object should equal each other", projA.equals(projA));
        assertFalse("Two projects that are two different objects shouldn't equal each other", projA.equals(projB));
    }

    public void testAddChange() throws Exception {
        assertNull("The current change should be null by default", testProject.getCurrentChange());
        Change testChange = new Change(Change.changeType.EDIT_PERSON, testProject);
        testProject.addChange(testChange);
        assertNotNull("Current change should not be null when a change is added", testProject.getCurrentChange());
    }

    public void testSetCurrentChange() throws Exception {
        Change testChange = new Change(Change.changeType.EDIT_PERSON, testProject);
        testProject.setCurrentChange(testChange);
        assertNotNull("Current change should not be null when a change is added", testProject.getCurrentChange());
    }

    public void testSetPeople() throws Exception {
        Collection<Person> testPeople = new ArrayList<>();
        Person johnDoe = new Person("johnDoe", "John Doe", "JD1");
        Person janeDoe = new Person("janeDoe", "Jane Doe", "JD2");
        testPeople.add(johnDoe);
        testPeople.add(janeDoe);
        testProject.setPeople(testPeople);
        assertTrue("testProject's people collection should be able to set members", testProject.getPeople().equals(testPeople));
    }
}

