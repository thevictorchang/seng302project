package seng302.group3.model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.group3.model.Team;

/**
 * Created by cjm328 on 4/05/15.
 */
public class TeamTest {

    private Team testTeam;

    @Before
    public void setUp() throws Exception{
        testTeam = new Team("nameShort", "longName", "description");
    }

    @After
    public void tearDown() throws Exception {
        testTeam = null;
    }

    @Test
    public void testTeamInitialisedWithZeroParametersIsNotNull() throws Exception {
        Team zeroParamTeam = new Team();
        Assert.assertNotNull(zeroParamTeam);
    }

    @Test
    public void testTeamInitialisedWithThreeParametersIsNotNull() throws Exception {
        Team threeParamTeam = new Team("1","2", "3");
        Assert.assertNotNull(threeParamTeam);
    }

    @Test
    public void testCopyData() throws Exception{
        Team copyTeam = new Team(testTeam);
        Assert.assertTrue("A team made from another should have the same values ",
            (copyTeam.getShortName().equals(testTeam.getShortName()) && copyTeam.getNameLong()
                .equals(testTeam.getNameLong()) && copyTeam.getDescription()
                .equals(testTeam.getDescription())));
    }

    @Test
    public void testValidTeam() throws Exception {
        Person testPO = new Person("PO", "PO", "PO");
        Person testSM = new Person("SM", "SM", "SM");
        testTeam.setProductOwner(testPO);
        testTeam.setScrumMaster(testSM);
        Assert.assertTrue(testTeam.validTeam());
    }

    @Test
    public void testInvalidTeam() throws Exception {
        Person testPO = new Person("PO", "PO", "PO");
        testTeam.setProductOwner(testPO);
        // No Scrummaster
        Assert.assertFalse(testTeam.validTeam());
    }
}

