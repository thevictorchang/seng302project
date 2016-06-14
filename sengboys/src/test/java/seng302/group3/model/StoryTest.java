package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class StoryTest {

    private Story testStory;
    private Backlog testBacklog;
    private Person testPerson;

    @Before
    public void setUp() throws Exception {
        testPerson = new Person("nameShort", "fullName", "userID");
        testStory = new Story("testStory", "fullTestStory", "testDesc", testPerson);
        testBacklog = new Backlog("backTest", "backlogTest", testPerson);
        testBacklog.addStory(testStory);
        testStory.setBacklog(testBacklog);

    }

    @After
    public void tearDown() throws Exception {
        testPerson = null;
        testStory = null;
        testBacklog = null;

    }

    @Test
    public void testCyclicDependencyBetweenTwoStories() throws Exception {
        Story testStoryB = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryB);
        testStoryB.setBacklog(testBacklog);
        Collection<Story> testDependencies = new ArrayList<>();
        testDependencies.add(testStoryB);
        testStory.setDependencies(testDependencies);
        Assert.assertTrue("There is a cycle",
                testStory.checkDependencyCycle(testStoryB));
    }

    @Test
    public void testNegativeCyclicDependenciesBetweenTwoStories() throws Exception {
        Story testStoryB = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryB);
        testStoryB.setBacklog(testBacklog);
        Assert.assertFalse("There is a cycle",
                testStory.checkDependencyCycle(testStoryB));
    }

    @Test
    public void testCyclicDependencyWithLongChain() throws Exception {
        Story testStoryB = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryB);
        testStoryB.setBacklog(testBacklog);
        Collection<Story> testDependencies = new ArrayList<>();
        testDependencies.add(testStoryB);
        testStory.setDependencies(testDependencies);

        Story testStoryC = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryC);
        testStoryC.setBacklog(testBacklog);
        Collection<Story> testDependencies2 = new ArrayList<>();
        testDependencies2.add(testStoryC);
        testStoryB.setDependencies(testDependencies2);

        Story testStoryD = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryD);
        testStoryD.setBacklog(testBacklog);
        Collection<Story> testDependencies3 = new ArrayList<>();
        testDependencies3.add(testStoryD);
        testStoryC.setDependencies(testDependencies3);

        Story testStoryE = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryE);
        testStoryE.setBacklog(testBacklog);
        Collection<Story> testDependencies4 = new ArrayList<>();
        testDependencies4.add(testStoryE);
        testStoryD.setDependencies(testDependencies4);

        Story testStoryF = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryF);
        testStoryF.setBacklog(testBacklog);
        Collection<Story> testDependencies5 = new ArrayList<>();
        testDependencies5.add(testStoryF);
        testStoryE.setDependencies(testDependencies5);

        Assert.assertTrue("There is a cycle",
                testStory.checkDependencyCycle(testStoryF));
    }

    @Test
    public void testNegativeCyclicDependencyWithLongChain() throws Exception {
        Story testStoryB = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryB);
        testStoryB.setBacklog(testBacklog);
        Collection<Story> testDependencies = new ArrayList<>();
        testDependencies.add(testStoryB);
        testStory.setDependencies(testDependencies);

        Story testStoryC = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryC);
        testStoryC.setBacklog(testBacklog);
        Collection<Story> testDependencies2 = new ArrayList<>();
        testDependencies2.add(testStoryC);
        testStoryB.setDependencies(testDependencies2);

        Story testStoryD = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryD);
        testStoryD.setBacklog(testBacklog);
        Collection<Story> testDependencies3 = new ArrayList<>();
        testDependencies3.add(testStoryD);
        testStoryC.setDependencies(testDependencies3);

        Story testStoryE = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryE);
        testStoryE.setBacklog(testBacklog);
        Collection<Story> testDependencies4 = new ArrayList<>();
        testDependencies4.add(testStoryE);
        testStoryD.setDependencies(testDependencies4);

        Story testStoryF = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryF);
        testStoryF.setBacklog(testBacklog);
        Collection<Story> testDependencies5 = new ArrayList<>();
        testDependencies5.add(testStoryF);
        testStoryE.setDependencies(testDependencies5);

        Story testStoryG = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryG);
        testStoryG.setBacklog(testBacklog);

        Assert.assertFalse("There is a cycle",
                testStory.checkDependencyCycle(testStoryG));
    }

    @Test
    public void testCyclicDependenciesWithWideSpread() throws Exception {
        Story testStoryB = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryB);
        testStoryB.setBacklog(testBacklog);
        Collection<Story> testDependencies = new ArrayList<>();
        testDependencies.add(testStoryB);
        Story testStoryC = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryC);
        testStoryC.setBacklog(testBacklog);
        testDependencies.add(testStoryC);

        Story testStoryD = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryD);
        testStoryD.setBacklog(testBacklog);
        testDependencies.add(testStoryD);

        Story testStoryE = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryE);
        testStoryE.setBacklog(testBacklog);
        testDependencies.add(testStoryE);

        Story testStoryF = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryF);
        testStoryF.setBacklog(testBacklog);
        testDependencies.add(testStoryF);
        testStory.setDependencies(testDependencies);

        Assert.assertTrue("There is a cycle",
                testStory.checkDependencyCycle(testStoryF));
    }

    @Test
    public void testNegativeCyclicDependenciesWithWideSpread() throws Exception {
        Story testStoryB = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryB);
        testStoryB.setBacklog(testBacklog);
        Collection<Story> testDependencies = new ArrayList<>();
        testDependencies.add(testStoryB);
        Story testStoryC = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryC);
        testStoryC.setBacklog(testBacklog);
        testDependencies.add(testStoryC);

        Story testStoryD = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryD);
        testStoryD.setBacklog(testBacklog);
        testDependencies.add(testStoryD);

        Story testStoryE = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryE);
        testStoryE.setBacklog(testBacklog);
        testDependencies.add(testStoryE);

        Story testStoryF = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryF);
        testStoryF.setBacklog(testBacklog);
        testDependencies.add(testStoryF);
        testStory.setDependencies(testDependencies);

        Story testStoryG = new Story("testStoryB", "fullTestStoryB", "testDesc", testPerson);
        testBacklog.addStory(testStoryG);
        testStoryG.setBacklog(testBacklog);

        Assert.assertFalse("There is a cycle",
                testStory.checkDependencyCycle(testStoryG));
    }

}
