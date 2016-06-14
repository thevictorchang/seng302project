package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersonTest {

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

    @Test
    public void testConstructors() throws Exception {
        Person zeroParamPerson = new Person();
        Person oneParamPerson = new Person("oneParam");
        Person twoParamPerson = new Person("twoParam", "Two Parameter");
        Person threeParamPerson = new Person("threeParam", "Three Parameter", "3pr");
        Assert.assertNotNull("zeroParamPerson should exist", zeroParamPerson);
        Assert.assertNotNull("oneParamPerson should exist", oneParamPerson);
        Assert.assertNotNull("twoParamPerson should exist", twoParamPerson);
        Assert.assertNotNull("threeParamPerson should exist", threeParamPerson);
    }

    @Test
    public void testGetFullName() throws Exception {
        Assert.assertEquals("Full Name should be 'fullName'", "fullName", testPerson.getFullName());


    }

    @Test
    public void testSetFullName() throws Exception {
        Assert.assertEquals("Full Name should be 'fullName'", "fullName", testPerson.getFullName());

        testPerson.setFullName("newFullName");
        Assert.assertEquals("Full Name should now be 'newFullName'", "newFullName",
            testPerson.getFullName());


    }

    @Test
    public void testGetNameShort() throws Exception {
        Assert.assertEquals("Name Short should be 'nameShort'", "nameShort",
            testPerson.getShortName());

    }

    @Test
    public void testSetShortName() throws Exception {
        Assert.assertEquals("Name Short should be 'nameShort'", "nameShort",
            testPerson.getShortName());

        testPerson.setShortName("newNameShort");
        Assert.assertEquals("Name Short should now be 'newNameShort'", "newNameShort",
            testPerson.getShortName());

    }

    @Test
    public void testGetUserID() throws Exception {
        Assert.assertEquals("UserID should be 'userID'", "userID", testPerson.getUserID());


    }

    @Test
    public void testSetUserID() throws Exception {
        //check userID is 'userID' to start with
        Assert.assertEquals("UserID should be 'userID'", "userID", testPerson.getUserID());
        testPerson.setUserID("newUserID");
        Assert.assertEquals("UserID should now be 'newUserID'", "newUserID", testPerson.getUserID());

    }

    @Test
    public void testGetSkills() throws Exception {
        //check if skills collection is empty
        Assert.assertTrue("Skills collection should be empty", testPerson.getSkills().isEmpty());

    }

    @Test
    public void testAddSkill() throws Exception {
        testSkill = new Skill("testSkill", "testDescription");
        testPerson.addSkill(testSkill);
        Assert.assertTrue("Skills collection should contain testSkill",
            testPerson.getSkills().contains(testSkill));


    }

    @Test
    public void testRemoveSkill() throws Exception {
        testSkill = new Skill("testSkill", "testDescription");
        testPerson.addSkill(testSkill);
        Assert.assertTrue("Skills collection should contain testSkill",
            testPerson.getSkills().contains(testSkill));

        testPerson.removeSkill(testSkill);
        Assert.assertTrue("Skills collection should now be empty", testPerson.getSkills().isEmpty());

    }

    @Test
    public void testSetBiography() throws Exception {
        String testBiography = "test biography";
        testPerson.setBiography(testBiography);

        Assert.assertEquals("Biography should be 'test biography'", "test biography",
            testPerson.getBiography());


    }

    @Test
    public void testGetBiography() throws Exception {

        String testBiography = "test biography";
        testPerson.setBiography(testBiography);

        Assert.assertEquals("Biography should be 'test biography'", "test biography",
            testPerson.getBiography());
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("toString should only return the 'nameShort'", "nameShort",
            testPerson.toString());
    }

}
