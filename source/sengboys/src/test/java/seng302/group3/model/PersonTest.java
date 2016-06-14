package seng302.group3.model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

public class PersonTest extends TestCase {

    private Person testPerson;
    private Skill testSkill;

    @Before
    public void setUp() throws Exception {
        testPerson = new Person("nameShort", "fullName", "userID");

    }

    @After
    public void tearDown() throws Exception {
        testPerson = null;

    }

    public void testConstructors() throws Exception {
        Person zeroParamPerson = new Person();
        Person oneParamPerson = new Person("oneParam");
        Person twoParamPerson = new Person("twoParam", "Two Parameter");
        Person threeParamPerson = new Person("threeParam", "Three Parameter", "3pr");
        assertNotNull("zeroParamPerson should exist", zeroParamPerson);
        assertNotNull("oneParamPerson should exist", oneParamPerson);
        assertNotNull("twoParamPerson should exist", twoParamPerson);
        assertNotNull("threeParamPerson should exist", threeParamPerson);
    }

    public void testGetFullName() throws Exception {
        assertEquals("Full Name should be 'fullName'", "fullName", testPerson.getFullName());


    }

    public void testSetFullName() throws Exception {
        assertEquals("Full Name should be 'fullName'", "fullName", testPerson.getFullName());

        testPerson.setFullName("newFullName");
        assertEquals("Full Name should now be 'newFullName'", "newFullName", testPerson.getFullName());


    }

    public void testGetNameShort() throws Exception {
        assertEquals("Name Short should be 'nameShort'", "nameShort", testPerson.getNameShort());

    }

    public void testSetNameShort() throws Exception {
        assertEquals("Name Short should be 'nameShort'", "nameShort", testPerson.getNameShort());

        testPerson.setNameShort("newNameShort");
        assertEquals("Name Short should now be 'newNameShort'", "newNameShort", testPerson.getNameShort());

    }

    public void testGetUserID() throws Exception {
        assertEquals("UserID should be 'userID'", "userID", testPerson.getUserID());


    }

    public void testSetUserID() throws Exception {
        //check userID is 'userID' to start with
        assertEquals("UserID should be 'userID'", "userID", testPerson.getUserID());
        testPerson.setUserID("newUserID");
        assertEquals("UserID should now be 'newUserID'", "newUserID", testPerson.getUserID());

    }

    public void testGetSkills() throws Exception {
        //check if skills collection is empty
        assertTrue("Skills collection should be empty", testPerson.getSkills().isEmpty());

    }

    public void testAddSkill() throws Exception {
        testSkill = new Skill("testSkill", "testDescription");
        testPerson.addSkill(testSkill);
        assertTrue("Skills collection should contain testSkill", testPerson.getSkills().contains(testSkill));


    }

    public void testRemoveSkill() throws Exception {
        testSkill = new Skill("testSkill", "testDescription");
        testPerson.addSkill(testSkill);
        assertTrue("Skills collection should contain testSkill", testPerson.getSkills().contains(testSkill));

        testPerson.removeSkill(testSkill);
        assertTrue("Skills collection should now be empty", testPerson.getSkills().isEmpty());

    }

    public void testSetBiography() throws Exception {
        String testBiography = "test biography";
        testPerson.setBiography(testBiography);

        assertEquals("Biography should be 'test biography'", "test biography", testPerson.getBiography());


    }

    public void testGetBiography() throws Exception {

        String testBiography = "test biography";
        testPerson.setBiography(testBiography);

        assertEquals("Biography should be 'test biography'", "test biography", testPerson.getBiography());
    }

    public void testToString() throws Exception {
        assertEquals("toString should only return the 'nameShort'", "nameShort", testPerson.toString());
    }

}