package seng302.group3.model.search;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class SearchUtilTest{
    private Organisation testOrganisation;
    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    Project project1;
    Project project2;
    Backlog backlog1;
    Backlog backlog2;
    Person person1;
    Person person2;
    Release release1;
    Release release2;
    Skill skill1;
    Skill skill2;
    Sprint sprint1;
    Sprint sprint2;
    Story story1;
    Story story2;
    Team team1;
    Team team2;
    Task task1;
    Task task2;
    Tag tag1;
    Tag tag2;

    Collection abcs;
    Collection defs;

    SearchDialog dialog;

    @Before
    public void setUp() throws Exception {
        dialog = new SearchDialog();
        testOrganisation = new Organisation();
        project1 = new Project("def", "def", "def");
        project2 = new Project("abc", "abc", "abc");
        backlog1 = new Backlog("def", "def");
        backlog2 = new Backlog("abc", "abc");
        person1 = new Person("def", "def", "def");
        person2 = new Person("abc", "abc", "abc");
        release1 = new Release("def", "def", LocalDate.now(), project1);
        release2 = new Release("abc", "abc", LocalDate.now(), project2);
        skill1 = new Skill("def", "def");
        skill2 = new Skill("abc", "abc");
        story1 = new Story("def", "def", "def");
        story2 = new Story("abc", "abc", "abc");
        team1 = new Team("def", "def", "def");
        team2 = new Team("abc", "abc", "abc");
        task1 = new Task("def", "def");
        task2 = new Task("abc", "abc");
        tag1 = new Tag("def", "def", "def");
        tag2 = new Tag("abc", "abc", "abc");
        sprint1 = new Sprint("def", "def", "def", new TimePeriod(LocalDate.now(), LocalDate.now(), project1, team1), team1, release1, backlog1);
        sprint2 = new Sprint("abc", "abc", "abc", new TimePeriod(LocalDate.now(), LocalDate.now(), project2, team2), team2, release2, backlog2);

        testOrganisation.addProject(project1);
        testOrganisation.addProject(project2);
        testOrganisation.addBacklog(backlog1);
        testOrganisation.addBacklog(backlog2);
        testOrganisation.addPerson(person1);
        testOrganisation.addPerson(person2);
        testOrganisation.addRelease(release1);
        testOrganisation.addRelease(release2);
        testOrganisation.addSkill(skill1);
        testOrganisation.addSkill(skill2);
        testOrganisation.addStory(story1);
        testOrganisation.addStory(story2);
        testOrganisation.addTeam(team1);
        testOrganisation.addTeam(team2);
        testOrganisation.addTask(task1);
        testOrganisation.addTask(task2);
        testOrganisation.addTag(tag1);
        testOrganisation.addTag(tag2);
        testOrganisation.addSprint(sprint1);
        testOrganisation.addSprint(sprint2);

        defs = new ArrayList<>();
        defs.add(project1);
        defs.add(backlog1);
        defs.add(person1);
        defs.add(release1);
        defs.add(skill1);
        defs.add(story1);
        defs.add(team1);
        defs.add(task1);
        defs.add(tag1);
        defs.add(sprint1);

        abcs = new ArrayList<>();
        abcs.add(project2);
        abcs.add(backlog2);
        abcs.add(person2);
        abcs.add(release2);
        abcs.add(skill2);
        abcs.add(story2);
        abcs.add(team2);
        abcs.add(task2);
        abcs.add(tag2);
        abcs.add(sprint2);
    }

    @After
    public void tearDown() throws Exception {
        dialog = null;
        testOrganisation = null;
        project1 = null;
        project2 = null;
        backlog1 = null;
        backlog2 = null;
        person1 = null;
        person2 = null;
        release1 = null;
        release2 = null;
        skill1 = null;
        skill2 = null;
        story1 = null;
        story2 = null;
        team1 = null;
        team2 = null;
        task1 = null;
        task2 = null;
        tag1 = null;
        tag2 = null;
        sprint1 = null;
        sprint2 = null;

        abcs = null;
        defs = null;
    }

    @Test
    public void testGeneralSearchReturnsGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", null, dialog);
        for (Object o: searchResults){
            if (o instanceof SearchResult){
                Assert.assertTrue("The search returns the items that match the string", abcs.contains(((SearchResult) o).getElement()));
            }

        }
    }

    @Test
    public void testGeneralSearchNotReturnBadResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", null, dialog);
        for (Object o: searchResults) {
            if (o instanceof SearchResult) {
                Assert.assertFalse("The search doesn't return the items that don't match the string", defs.contains(((SearchResult) o).getElement()));
            }
        }
    }

    @Test
    public void testProjectSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "projects", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Projects");
        Assert.assertTrue("The search returns only the project item that matches the string", project2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testBacklogSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "backlogs", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Backlogs");
        Assert.assertTrue("The search returns only the backlog item that matches the string", backlog2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testPersonSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "people", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("People");
        Assert.assertTrue("The search returns only the person item that matches the string", person2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testReleaseSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "releases", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Releases");
        Assert.assertTrue("The search returns only the release item that matches the string", release2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testSkillSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "skills", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Skills");
        Assert.assertTrue("The search returns only the skill item that matches the string", skill2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testStorySearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "stories", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Stories");
        Assert.assertTrue("The search returns only the story item that matches the string", story2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testTeamSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "teams", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Teams");
        Assert.assertTrue("The search returns only the team item that matches the string", team2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testTaskSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "tasks", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Tasks");
        Assert.assertTrue("The search returns only the task item that matches the string", task2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }

    @Test
    public void testTagsSearchReturnsOnlyGoodResults() throws Exception {
        Collection searchResults = SearchUtil.globalSearch(testOrganisation, "ab", "tags", dialog);
        Assert.assertTrue("The search results only contains 2 items", searchResults.size() == 2);
        searchResults.remove("Tags");
        Assert.assertTrue("The search returns only the tag item that matches the string", tag2 == ((SearchResult) searchResults.toArray()[0]).getElement());
    }
}