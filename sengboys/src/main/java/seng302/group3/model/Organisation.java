package seng302.group3.model;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.Navigator;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;

import java.io.*;
import java.util.Collection;

/**
 * Created by epa31 on 12/03/15.
 * Models Organisation class.
 */
@Root(strict = false)
@Order(elements = {"projects", "backlogs", "sprints", "stories", "teams", "people", "skills", "releases", "tags"})
public class Organisation extends Element {

    private static final long serialVersionUID = 1806201500001L;

    @Attribute private String orgName; // non null
    @Attribute private String filename = "organisation1.org";
    private int orgId = 0;
    private boolean isUnsaved = false;
    private boolean wasSaved = false;

    @ElementList(required = false) private SerializableObservableList<Person> people =
            new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Team> teams =
            new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Project> projects =
        new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Skill> skills =
            new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Release> releases =
        new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Backlog> backlogs =
        new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Story> stories =
        new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Sprint> sprints =
        new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Task> tasks =
            new SerializableObservableList<>();
    @ElementList(required = false) private SerializableObservableList<Tag> tags =
            new SerializableObservableList<>();

    private Navigator organisationNavigator;


    /**
     * Organisation constructor
     *
     * @param orgName - the name of the organisation
     */
    public Organisation(String orgName) {
        this.orgName = orgName;
    }

    /**
     * organisation constructor with no arguments
     */
    public Organisation() {
        this.orgName = "";
    }


    /**
     * creates a new navigator for the organisation
     *
     * @param listView - the listview within the GUI
     * @param toolBar  - the tool bar used for the breadcrumb bar
     */
    public void newOrganisationNavigator(ListView<NavigatorItem> listView, ToolBar toolBar,
        ComboBox orderSelectionCombo) {
        this.organisationNavigator = new Navigator(listView, toolBar, orderSelectionCombo, this);
    }


    /**
     * sets this organisations copy of the navigator to reflect the one shown within the GUI. Used when undoing/redoing
     *
     * @param listView - the listview within the GUI
     * @param toolBar  - the tool bar used for the breadcrumb bar
     */
    public void setOrganisationNavigator(ListView<NavigatorItem> listView, ToolBar toolBar,
        ComboBox orderSelectionCombo) {
        this.organisationNavigator.refresh(listView, toolBar, orderSelectionCombo);
    }

    /**
     * Setter for orgName
     *
     * @param nameShort
     */
    @Override public void setShortName(String nameShort) {
        this.orgName = nameShort;
    }


    /**
     * gets the short name of the organisation in this case the organisation name
     *
     * @return
     */
    @Override public String getShortName() {
        return orgName;
    }


    /**
     * Getter for people belonging to the organisation
     *
     * @return collection of people
     */
    public SerializableObservableList<Person> getPeople() {
        return this.people;
    }


    /**
     * Remove a Person from the people list
     *
     * @param person - person to be deleted
     */
    public void removePerson(Person person) {
        this.people.remove(person);
    }

    /**
     * Add a new Person to the people listChange
     *
     * @param person - person to be added
     */
    public void addPerson(Person person) {
        this.people.add(person);
    }

    /**
     * Setter to specify the people involved in a project
     *
     * @param people - collection of people belonging to the organisation
     */
    public void setPeople(Collection<Person> people) {
        this.people.setItems(people);
    }



    public SerializableObservableList<Tag> getTags() {
        return tags;
    }

    public void setTags(SerializableObservableList<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    /**
     * Remove a Project from the people list
     *
     * @param project - project to be removed
     */
    public void removeProject(Project project) {
        this.projects.remove(project);
    }

    /**
     * Getter for projects
     *
     * @return collection of projects belonging to the organisation
     */
    public SerializableObservableList<Project> getProjects() {
        return this.projects;
    }


    /**
     * Adds a project to the projects list
     *
     * @param project - project to be added to the project collection
     */
    public void addProject(Project project) {
        this.projects.add(project);
    }

    /**
     * Setter to specify the projects in the organisation
     *
     * @param projects - collection of projects to be associated with the organisation
     */
    public void setProjects(Collection<Project> projects) {
        this.projects.setItems(projects);
    }


    /**
     * Setter to specify the skills involved in a project
     *
     * @param skills - collection of skills to be associated with the organisation
     */
    public void setSkills(Collection<Skill> skills) {
        this.skills.setItems(skills);
    }

    /**
     * Getter for skills
     *
     * @return collection of skills belonging to the organisation
     */
    public SerializableObservableList<Skill> getSkills() {
        return this.skills;
    }

    /**
     * Adds a given skill to the skills list.
     *
     * @param skill - skill to be added to the organisation skills collection
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    /**
     * Removes a given skill from the skills list.
     *
     * @param skill - removes a skill from the skills collection
     */
    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }


    /**
     * Sets the teams list to a given list of teams.
     *
     * @param teams - collection of teams
     */
    public void setTeams(Collection<Team> teams) {
        this.teams.setItems(teams);
    }

    /**
     * Getter for teams
     *
     * @return collection of teams associated with the organisation
     */
    public SerializableObservableList<Team> getTeams() {
        return this.teams;
    }

    /**
     * Adds a team to the team list.
     *
     * @param team - team to be added
     */
    public void addTeam(Team team) {
        this.teams.add(team);
    }

    /**
     * Removes a team from the team list
     *
     * @param team - team to be removed from the collection of teams
     */
    public void removeTeam(Team team) {
        this.teams.remove(team);
    }



    /**
     * Sets the releases list to a given list of releases
     *
     * @param releases - collection of releases to be added to the organisation
     */
    public void setReleases(Collection<Release> releases) {
        this.releases.setItems(releases);
    }

    /**
     * Getter for releases
     *
     * @return collection of releases
     */
    public SerializableObservableList<Release> getReleases() {
        return this.releases;
    }

    /**
     * Adds a team to the team list
     *
     * @param release - release to be added to the release collection
     */
    public void addRelease(Release release) {
        this.releases.add(release);
    }

    /**
     * Removes a release from the releases list.
     *
     * @param release - release to be deleted
     */
    public void removeRelease(Release release) {
        this.releases.remove(release);
    }

    /**
     * Getter for backlogs
     *
     * @return collection of backlogs
     */
    public SerializableObservableList<Backlog> getBacklogs() {
        return backlogs;
    }


    /**
     * Sets the backlogs list to a given list of backlogs
     *
     * @param backlogs list of backlogs
     */
    public void setBacklogs(Collection<Backlog> backlogs) {
        this.backlogs.setItems(backlogs);
    }

    /**
     * Adds a backlog to the list of backlogs
     *
     * @param backlog added backlog
     */
    public void addBacklog(Backlog backlog) {
        this.backlogs.add(backlog);
    }

    /**
     * Adds a sprint to the list of sprints
     * @param sprint added sprint
     */
    public void addSprint(Sprint sprint) {
        this.sprints.add(sprint);
    }

    public void removeSprint(Sprint sprint) {
        this.sprints.remove(sprint);
    }

    public SerializableObservableList<Sprint> getSprints() {
        return sprints;
    }

    public void removeBacklog(Backlog backlog) {
        this.backlogs.remove(backlog);
    }

    /**
     * sets all the stories of the organisation
     *
     * @param stories - new stories to be set
     */
    public void setStories(Collection<Story> stories) {
        this.stories.setItems(stories);
    }


    /**
     * returns the list of stories in the organisation
     *
     * @return - all the stories that organisation keeps track of
     */
    public SerializableObservableList<Story> getStories() {
        return stories;
    }

    /**
     * adds the story to the organisation
     *
     * @param story
     */
    public void addStory(Story story) {
        this.stories.add(story);
    }

    /**
     * removes a given story from the organisations stories
     *
     * @param story - story to remove.
     */
    public void removeStory(Story story) {
        this.stories.remove(story);
    }


    /**
     * Gets the last saved filename of the organisation
     *
     * @return Returns the last saved filename for the project
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the most recent filename of the organisation
     *
     * @param filename - sets the filename of the organisation
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the organisation ID for comparisons
     *
     * @return Returns the orgId
     */
    public int getOrgId() {
        return orgId;
    }

    /**
     * Sets the organisation ID for comparisons
     *
     * @param orgId
     */
    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    /**
     * Increments the organisation ID number to for every change made to the organisation
     */
    public void incrementOrgId() {
        this.orgId++;
    }

    /**
     * Decrements the organisation ID to counteract the increments for when an organisation/revert is made
     */
    public void decrementOrgId() {
        this.orgId--;
    }

    /**
     * String representation of the organisation
     *
     * @return toString of organisation
     */
    @Override public String toString() {
        return this.getShortName();
    }


    public void updatePeopleProjectAllocation(){
        for(Person p:people){
            p.updateProject();
        }
    }

    /**
     * used for the organisation navigator. Returns a SerializableObservableList of the navigator items for this
     * organisation. This includes the folder items; Project, Releases, Teams, People, Skills
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
            new SerializableObservableList<>();

        FolderNavigatorItem projectsItem = new FolderNavigatorItem("Projects");
        projectsItem.setNext(this.projects);

        FolderNavigatorItem releasesItem = new FolderNavigatorItem("Releases");
        releasesItem.setNext(this.releases);

        FolderNavigatorItem teamsItem = new FolderNavigatorItem("Teams");
        teamsItem.setNext(this.teams);

        FolderNavigatorItem peopleItem = new FolderNavigatorItem("People");
        peopleItem.setNext(this.people);

        FolderNavigatorItem skillsItem = new FolderNavigatorItem("Skills");
        skillsItem.setNext(this.skills);

        FolderNavigatorItem backlogsItem = new FolderNavigatorItem("Backlogs");
        backlogsItem.setNext(this.backlogs);

        FolderNavigatorItem storiesItem = new FolderNavigatorItem("Stories");
        storiesItem.setNext(this.stories);

        FolderNavigatorItem sprintsItem = new FolderNavigatorItem("Sprints");
        sprintsItem.setNext(this.sprints);

        FolderNavigatorItem tagsItem = new FolderNavigatorItem("Tags");
        tagsItem.setNext(this.tags);

        navigatorItems.add(projectsItem);
        navigatorItems.add(releasesItem);
        navigatorItems.add(teamsItem);
        navigatorItems.add(peopleItem);
        navigatorItems.add(skillsItem);
        navigatorItems.add(backlogsItem);
        navigatorItems.add(storiesItem);
        navigatorItems.add(sprintsItem);
        navigatorItems.add(tagsItem);

        return navigatorItems;
    }

    /**
     * Add the given task to the organisations list of tasks
     * @param task
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Get the collection of tasks from the organisation.
     * @return
     */
    public SerializableObservableList<Task> getTasks() {
        return tasks;
    }

    /**
     * Gets all of the stories from sprints which are current
     * @return
     */
    public SerializableObservableList<Story> getCurrentStories() {
        SerializableObservableList<Story> s = new SerializableObservableList<>();
        sprints.stream().filter(sprint -> sprint.isActive()).forEach(sprint -> s.addAll(sprint.getStories()));
        return s;
    }

    /**
     * Gets all of the sprints which are current
     * @return
     */
    public SerializableObservableList<Sprint> getCurrentSprints(){
        SerializableObservableList<Sprint> s = new SerializableObservableList<>();
        for(Sprint sprint:sprints){
            if(sprint.isActive()){
                s.add(sprint);
            }
        }
        return s;
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    /**
     * enum used for the uniqueName method to give the requested unique type
     */
    public static enum uniqueType {
        UNIQUE_PERSON, UNIQUE_SKILL, UNIQUE_PROJECT, UNIQUE_RELEASE, UNIQUE_TEAM, UNIQUE_BACKLOG, UNIQUE_STORY,
        UNIQUE_SPRINT, UNIQUE_TAG
    }

    /**
     * Takes the type of the object for which the string belongs to and checks if it is unique.
     *
     * @param shortName
     * @param type
     * @return
     */
    public boolean uniqueName(String shortName, uniqueType type) {
        boolean bool = true;
        switch (type) {
            case UNIQUE_PERSON:
                for (Person currentPerson : people) {
                    if (currentPerson.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_PROJECT:
                for (Project currentProject : projects) {
                    if (shortName.equals(currentProject.getShortName())) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_SKILL:
                for (Skill currentSkill : skills) {
                    if (currentSkill.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_TEAM:
                for (Team currentTeam : teams) {
                    if (currentTeam.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_RELEASE:
                for (Release release : releases) {
                    if (release.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_BACKLOG:
                for (Backlog backlog : backlogs) {
                    if (backlog.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_STORY:
                for (Story story : stories) {
                    if (story.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_SPRINT:
                for (Sprint sprint : sprints) {
                    if (sprint.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
                break;
            case UNIQUE_TAG:
                for (Tag tag : tags) {
                    if (tag.getShortName().equals(shortName)) {
                        bool = false;
                        break;
                    }
                }
        }
        return bool;
    }


    /**
     * produces a complete serialised copy of the organisation, including all objects and references. Used for undo/redo
     *
     * @return - complete copy of the original Organisation
     */
    public Organisation serializedCopy() {
        Organisation o = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);

            //De-serialization of object
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            o = (Organisation) in.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * Gets the navigator of the organisation
     *
     * @return The Navigator
     */
    public Navigator getNavigator() {
        return this.organisationNavigator;
    }

    public void setSaved(boolean saved) {
        this.wasSaved = saved;
    }

    public boolean getSaved() {
        return this.wasSaved;
    }

    public void setUnsaved(boolean bool){
        this.isUnsaved = bool;
    }

    public boolean getUnsaved(){
        return this.isUnsaved;
    }


    /**
     * When reading the object we need to make sure none of the attributes have lost their values
     * @param stream
     */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.people == null)
            this.people = new SerializableObservableList<>();
        if (this.teams == null)
            this.teams = new SerializableObservableList<>();
        if (this.projects == null)
            this.projects = new SerializableObservableList<>();
        if (this.skills == null)
            this.skills = new SerializableObservableList<>();
        if (this.releases == null)
            this.releases = new SerializableObservableList<>();
        if (this.backlogs == null)
            this.backlogs = new SerializableObservableList<>();
        if (this.stories == null)
            this.stories = new SerializableObservableList<>();
        if (this.sprints == null)
            this.sprints = new SerializableObservableList<>();
        if (this.tasks == null)
            this.tasks = new SerializableObservableList<>();
        if (this.tags == null)
            this.tags = new SerializableObservableList<>();
    }

}
