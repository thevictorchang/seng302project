package seng302.group3.model.search;

import seng302.group3.model.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cjm328 on 11/09/15.
 */
public class SearchUtil {


    /**
     * Searches through the entire organisation and returns an collection of every item that matches the search terms.
     * @param org - the organisation to search
     * @param searchTerm - the string that will be searched for
     * @return
     */
    public static Collection globalSearch(Organisation org, String searchTerm, String hashtagVal, SearchDialog searchDialog){
        boolean backlog = false;
        boolean people= false;
        boolean projects = false;
        boolean releases = false;
        boolean skills = false;
        boolean sprints = false;
        boolean stories = false;
        boolean teams = false;
        boolean tasks= false;
        boolean tags = false;

        boolean onlyShort = false;

        if (hashtagVal != null && hashtagVal.equals("name"))
            onlyShort = true;

        Collection searchResults = new ArrayList<>();
        for (Person p : org.getPeople()){
            SearchResult res = lookPerson(p, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("people") || hashtagVal.equals("name"))){
                if(searchDialog.peopleExpanded)
                    searchResults.add(res);
                people = true;
            }
        }
        if(people)
            searchResults.add("People");
        for (Team t : org.getTeams()){
            SearchResult res = lookTeam(t, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("teams") || hashtagVal.equals("name"))){
                if(searchDialog.teamsExpanded)
                    searchResults.add(res);
                teams = true;
            }
        }
        if (teams)
            searchResults.add("Teams");
        for (Project p : org.getProjects()){
            SearchResult res = lookProj(p, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("projects") || hashtagVal.equals("name"))){
                if(searchDialog.projectsExpanded)
                    searchResults.add(res);
                projects = true;
            }
        }
        if (projects)
            searchResults.add("Projects");
        for (Skill s : org.getSkills()){
            SearchResult res = lookSkill(s, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("skills") || hashtagVal.equals("name"))){
                if(searchDialog.skillsExpanded)
                    searchResults.add(res);
                skills =true;
            }
        }
        if (skills)
            searchResults.add("Skills");
        for (Release r : org.getReleases()){
            SearchResult res = lookRelease(r, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("releases") || hashtagVal.equals("name"))){
                if(searchDialog.releasesExpanded)
                    searchResults.add(res);
                releases = true;
            }
        }
        if (releases)
            searchResults.add("Releases");
        for (Backlog b: org.getBacklogs()){
            SearchResult res = lookBacklog(b, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("backlogs") || hashtagVal.equals("name"))){
                if(searchDialog.backlogExpanded)
                    searchResults.add(res);
                backlog = true;
            }
        }
        if(backlog)
            searchResults.add("Backlogs");
        for (Story s : org.getStories()){
            SearchResult res = lookStory(s, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("stories") || hashtagVal.equals("name"))){
                if(searchDialog.storiesExpanded)
                    searchResults.add(res);
                stories = true;
            }
        }
        if (stories)
            searchResults.add("Stories");
        for (Sprint s : org.getSprints()){
            SearchResult res = lookSprint(s, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("sprints") || hashtagVal.equals("name"))){
                if(searchDialog.sprintsExpanded)
                    searchResults.add(res);
                sprints = true;
            }
        }
        if (sprints)
            searchResults.add("Sprints");
        for (Task t : org.getTasks()){
            SearchResult res = lookTask(t, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("tasks") || hashtagVal.equals("name"))){
                if(searchDialog.tasksExpanded)
                    searchResults.add(res);
                tasks = true;
            }
        }
        if (tasks)
            searchResults.add("Tasks");
        for (Tag t : org.getTags()){
            SearchResult res = lookTag(t, searchTerm, onlyShort);
            if (res.getElement() !=null && (hashtagVal == null || hashtagVal.equals("tags") || hashtagVal.equals("name"))){
                if(searchDialog.tagsExpanded)
                    searchResults.add(res);
                tags  =true;
            }
        }
        if (tags)
            searchResults.add("Tags");
        return searchResults;
    }
    /**
     * Used for checking if a tag contains a search term
     * @param s - the tag
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookTag(Tag s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }

    /**
     * Used for checking if a task contains a search term
     * @param s - the task
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookTask(Task s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }
    /**
     * Used for checking if a sprint contains a search term
     * @param s - the sprint
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookSprint(Sprint s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getFullName().toLowerCase().contains(searchTerm.toLowerCase())){
            longName = true;
            matchString+= "longName, ";
        }
        if(s.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }
    /**
     * Used for checking if a story contains a search term
     * @param s - the story
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookStory(Story s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getFullName().toLowerCase().contains(searchTerm.toLowerCase())){
            longName = true;
            matchString+= "longName, ";
        }
        if(s.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }

    /**
     * Used for checking if a backlog contains a search term
     * @param s - the backlog
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookBacklog(Backlog s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }

    /**
     * Used for checking if a release contains a search term
     * @param s - the release
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookRelease(Release s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }

    /**
     * Used for checking if a skill contains a search term
     * @param s - the skill
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookSkill(Skill s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }

    /**
     * Used for checking if a person contains a search term
     * @param s - the person
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookPerson(Person s, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(s.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(s.getFullName().toLowerCase().contains(searchTerm.toLowerCase())){
            longName = true;
            matchString+= "longName, ";
        }
        if(s.getUserID().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "user id, ";
        }
        for (Tag t: s.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(s, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(s, matchString);
            }
        }
        return searchResult;
    }

    /**
     * Used for checking if a team contains a search term
     * @param team - the team
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookTeam(Team team, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        String matchString = "(Match on: ";
        if(team.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(team.getNameLong().toLowerCase().contains(searchTerm.toLowerCase())){
            longName = true;
            matchString+= "longName, ";
        }
        if(team.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: team.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(team, "");
            }
        }
        else {
            if (shortName == true || longName == true || description == true || tags == true) {
                matchString = matchString.substring(0, matchString.length() - 2);
                matchString += ")";
                searchResult = new SearchResult(team, matchString);
            }
        }
        return searchResult;
    }

    /**
     * Used for checking if a project contains a search term
     * @param project - the project
     * @param searchTerm - the search term
     * @return
     */
    public static SearchResult lookProj(Project project, String searchTerm, boolean onlyShort){
        boolean shortName = false;
        boolean longName = false;
        boolean description = false;
        boolean tags = false;
        SearchResult searchResult = new SearchResult(null, null);
        //boolean teams = false;
        //boolean timePeriods = false;
        //boolean releases = false;
        //boolean people = false;
        //boolean skills = false;
        String matchString = "(Match on: ";
        if(project.getShortName().toLowerCase().contains(searchTerm.toLowerCase())){
            shortName = true;
            matchString+= "shortName, ";
        }
        if(project.getNameLong().toLowerCase().contains(searchTerm.toLowerCase())){
            longName = true;
            matchString+= "longName, ";
        }
        if(project.getDescription().toLowerCase().contains(searchTerm.toLowerCase())){
            description = true;
            matchString+= "description, ";
        }
        for (Tag t: project.getTags()){
            if (t.toString().toLowerCase().contains(searchTerm.toLowerCase())){
                tags = true;
            }
        }
        if (tags)
            matchString += "tags, ";
        if (onlyShort){
            if (shortName==true){
                searchResult = new SearchResult(project, "");
            }
        }
        else{
            if (shortName==true||longName==true||description==true||tags==true){
                matchString = matchString.substring(0, matchString.length()-2);
                matchString+=")";
                searchResult = new SearchResult(project, matchString);
            }
        }


        return searchResult;
    }

}
