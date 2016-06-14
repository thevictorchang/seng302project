package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class ReleaseTest {

    Release testRelease;
    Project testProject;

    @Before
    public void setUp() throws Exception {
        testProject = new Project("testProjectNameShort");
        testRelease = new Release("nameShort", LocalDate.now(), testProject);
        testRelease.setDescription("description");
    }

    @After
    public void tearDown() throws Exception {
        testRelease = null;
    }

    @Test
    public void testGetNameShort() throws Exception {
        Assert.assertEquals("Name Short should be 'nameShort'", "nameShort", testRelease.getShortName());
    }

    @Test
    public void testSetShortName() throws Exception {
        Assert.assertEquals("Name Short should be 'nameShort'", "nameShort", testRelease.getShortName());
        testRelease.setShortName("newNameShort");
        Assert.assertEquals("Name Short should now be 'newNameShort'", "newNameShort", testRelease.getShortName());
    }

    @Test
    public void testGetDescription() throws Exception {
        Assert.assertEquals("Description should be 'description'", "description", testRelease.getDescription());
    }

    @Test
    public void testSetDescription() throws Exception {
        Assert.assertEquals("Description should be 'description'", "description", testRelease.getDescription());
        testRelease.setDescription("newDescription");
        Assert.assertEquals("Description should now be 'newDescription'", "newDescription", testRelease.getDescription());
    }

    @Test
    public void testGetReleaseDate() throws Exception {
        Assert.assertEquals("Release Date should be today's date", LocalDate.now(), testRelease.getReleaseDate());
    }

    @Test
    public void testSetReleaseDate() throws Exception {
        Assert.assertEquals("Release Date should be today's date", LocalDate.now(),
            testRelease.getReleaseDate());

        //sets release date to 2000/1/1
        LocalDate testDate = LocalDate.of(2000, 1, 1);
        testRelease.setReleaseDate(testDate);
        Assert.assertEquals("Release Date should now be tomorrow", testDate,
            testRelease.getReleaseDate());

    }

    @Test
    public void testGetProject() throws Exception {
        Assert.assertEquals("Release's Project's nameShort should be 'testProjectNameShort'", "testProjectNameShort",
                testRelease.getProject().getShortName());
    }

    @Test
    public void testSetProject() throws Exception {
        Project anotherTestProject = new Project();
        anotherTestProject.setShortName("testProjectNameShortUpdated");

        Assert.assertEquals("Release's Project's nameShort should be 'testProjectNameShort'",
            "testProjectNameShort", testRelease.getProject().getShortName());

        testRelease.setProject(anotherTestProject);
        Assert.assertEquals(
            "Release's Project's nameShort should be now be 'testProjectNameShortUpdated'",
            "testProjectNameShortUpdated", testRelease.getProject().getShortName());

    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("toString should just return nameShort", testRelease.getShortName() , testRelease.toString());
    }
}
