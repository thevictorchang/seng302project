package seng302.group3.model;

import seng302.group3.controller.App;
import seng302.group3.model.io.Dialogs;
import seng302.group3.model.io.SerializableObservableList;

import java.util.Iterator;

/**
 * Created by cjm328 on 6/05/15.
 */
public class Delete {
    private Object deleteObject;
    private Organisation currentOrg;

    /**
     * Constructor, sets what will be deleted, and starts the dialogs
     *
     * @param toDelete - object to be deleted
     * @param org      - organisation the object is in
     */
    public Delete(Object toDelete, Organisation org) {
        deleteObject = toDelete;
        startDeleteDialogs(org);
    }

    /**
     * Alternate constructor, used for unit testing, bypasses the dialogs.
     *
     * @param toDelete - object to be deleted
     * @param org      - organisation the object is is
     * @param fullDel  - whether the objects contained by the toDelete object will be deleted or not.
     */
    public Delete(Object toDelete, Organisation org, Boolean fullDel) {

        deleteObject = toDelete;
        currentOrg = org;
        if (fullDel) {
            executeDelete();
        } else {
            executePartialDelete();
        }

    }

    /**
     * Starts the deletion dialogs, opens the relevant dialog for the object being deleted.
     *
     * @param org - organisation the object is in
     */
    public void startDeleteDialogs(Organisation org) {
        currentOrg = org;
        Dialogs delete = new Dialogs();

        /*
        //TODO sort out deletion of an element with tags!!! don't know how to do it lmao
        if (deleteObject instanceof Element) {
            Element element = (Element) deleteObject;
            if (element.getTags().size() > 0) { //if the element has tags
                DialogSheet sheet = new DialogSheet();
                sheet.setLabelText("This element has " + element.getTags().size() + " tags, which will be " +
                        "unassociated upon delete.");

            }
        }
        */


        if (deleteObject instanceof Project) {
            delete.deleteProjectDialog(this, currentOrg, (Project) deleteObject);
        } else if (deleteObject instanceof Team) {
            delete.deleteTeamDialog(this, currentOrg, (Team) deleteObject);
        } else if (deleteObject instanceof Person) {
            if (((Person) deleteObject).getTeam() != null) {
                if (((Person) deleteObject).getTeam().getProductOwner() == deleteObject) {
                    if (((Person) deleteObject).getTeam().getTimePeriods() != null) {
                        delete.infoDialog(
                            "You cannot delete this person, because he is the Product Owner for a team assigned to a project.",
                            "Error");
                    } else
                        delete.deletePersonDialog(this, currentOrg, (Person) deleteObject);
                } else if (((Person) deleteObject).getTeam().getScrumMaster() == deleteObject) {
                    if (((Person) deleteObject).getTeam().getTimePeriods() != null) {
                        delete.infoDialog(
                            "You cannot delete this person, because he is the Scrum Master for a team assigned to a project.",
                            "Error");
                    } else
                        delete.deletePersonDialog(this, currentOrg, (Person) deleteObject);
                } else {
                    delete.deletePersonDialog(this, currentOrg, (Person) deleteObject);
                }
            } else {
                delete.deletePersonDialog(this, currentOrg, (Person) deleteObject);
            }

        } else if (deleteObject instanceof Skill) {
            if (((Skill) deleteObject).getShortName().equals("SM"))
                new Dialogs().infoDialog("The Scrum Master Skill can not be deleted", "Error");
            else if (((Skill) deleteObject).getShortName().equals("PO"))
                new Dialogs().infoDialog("The Product Owner Skill can not be deleted", "Error");
            else
                delete.deleteSkillDialog(this, currentOrg, (Skill) deleteObject);
        } else if (deleteObject instanceof Release) {
            delete.deleteReleaseDialog(this, currentOrg, (Release) deleteObject);
        } else if (deleteObject instanceof Story) {
            delete.deleteStoryDialog(this, currentOrg, (Story) deleteObject);
        } else if (deleteObject instanceof TimePeriod) {
            delete.deleteTimePeriodDialog(this, currentOrg, (TimePeriod) deleteObject);
        } else if (deleteObject instanceof Backlog) {
            delete.deleteBacklogDialog(this, currentOrg, (Backlog) deleteObject);
        } else if (deleteObject instanceof Sprint) {
            delete.deleteSprintDialog(this, currentOrg, (Sprint) deleteObject);
        } else if (deleteObject instanceof Task) {
            delete.deleteTaskDialog(this, currentOrg, (Task) deleteObject);
        } else if (deleteObject instanceof LoggedTime){
            delete.deleteLoggedTimeDialog(this, currentOrg, (LoggedTime) deleteObject);
        } else if (deleteObject instanceof Tag) {
            delete.deleteTagDialog(this, currentOrg, (Tag) deleteObject);
        }
    }

    /**
     * Executes the deletion of the previously set deleteObject. If the object has contained objects, ie in the case of
     * Project and team, those objects are deleted too.
     */
    public void executeDelete() {
        Element elementToDelete = (Element) deleteObject;
        SerializableObservableList<Tag> listOfTags = new SerializableObservableList<>();
        listOfTags.addAll(elementToDelete.getTags());

        if(elementToDelete.getTags().size() > 0) {
            new Dialogs().infoDialog("The tags associated with this element will be unassociated", "Warning");
        }

        //unassociates all tags
        for (Tag tag : listOfTags) {
            elementToDelete.removeTag(tag);
            tag.removeElement(elementToDelete);
        }

        if (deleteObject instanceof Project) {
            Project p = (Project) deleteObject;
            for (Iterator<Team> iterator = p.getTeams().iterator(); iterator.hasNext(); ) {
                Team t = iterator.next();
                currentOrg.removeTeam(t);
                //Runs through time periods to find the one that matches the project.
                for (Iterator<TimePeriod> it = t.getTimePeriods().iterator(); it.hasNext(); ) {
                    TimePeriod timePeriod = it.next();

                    if (timePeriod.getProject() != p) {
                        timePeriod.getProject().removeTeam(t, timePeriod);
                        timePeriod.getProject().checkLastTeam(timePeriod);
                    }
                    timePeriod.setProject(null);
                    it.remove();
                    //if(timePeriod.getProject() == p){
                    //}
                }
                t.getSkills().clear();
                t.getPeople().clear();
            }
            p.getTeams().clear();
            for (Person pe : p.getPeople()) {
                currentOrg.removePerson(pe);
                pe.setTeam(null);
                pe.setProject(null);
            }
            for (Release r : p.getReleases()) {
                currentOrg.removeRelease(r);
                r.setProject(null);
            }
            p.getReleases().clear();
            p.getSkills().clear();
            p.getPeople().clear();


            currentOrg.removeProject(p);
        } else if (deleteObject instanceof Team) {
            Team t = (Team) deleteObject;

            //TODO fix this completely - 26/6 not sure if actually needs fixing - epa31
            for (Person pe : t.getPeople()) {
                currentOrg.removePerson(pe);
                if (t.getTimePeriods().size() > 0)
                    for (TimePeriod timePeriod : t.getTimePeriods()) {
                        timePeriod.getProject().removePerson(pe);
                    }
                pe.setTeam(null);
                pe.setProject(null);
            }
            t.getPeople().clear();
            t.getSkills().clear();
            if (t.getTimePeriods().size() > 0) {
                for (TimePeriod timePeriod : t.getTimePeriods()) {
                    timePeriod.getProject().removeTeam(t, timePeriod);
                }
            }
            t.setTimePeriod(null);
            currentOrg.removeTeam(t);
        } else if (deleteObject instanceof Person) {
            Person p = (Person) deleteObject;

            if (p.getTeam() != null) {
                if (p.getTeam().getProductOwner() == p)
                    p.getTeam().setProductOwner(null);
                if (p.getTeam().getScrumMaster() == p)
                    p.getTeam().setScrumMaster(null);
                if (p.getTeam().getDevTeamMembers().contains(p))
                    p.getTeam().removeDevTeamMember(p);

                p.getTeam().removePerson(p);
                p.setTeam(null);
            }
            if (p.getProject() != null) {
                p.getProject().removePerson(p);
                p.setProject(null);
            }

            currentOrg.removePerson(p);
        } else if (deleteObject instanceof Skill) {
            Skill s = (Skill) deleteObject;
            for (Person p : currentOrg.getPeople()) {
                if (p.getSkills().contains(s)) {
                    p.removeSkill(s);
                }
            }
            currentOrg.removeSkill(s);
        } else if (deleteObject instanceof Release) {
            Release r = (Release) deleteObject;
            if (r.getProject() != null) {
                r.getProject().removeRelease(r);
                r.setProject(null);
            }
            currentOrg.removeRelease(r);
        } else if (deleteObject instanceof Story) {
            Story story = (Story) deleteObject;
            currentOrg.removeStory(story);

            // if the story is assigned to a backlog then remove them from the backlog
            if (story.getBacklog() != null) {
                Backlog backlog = story.getBacklog();
                backlog.removeStory(story);

                //resets the priorities of the stories this is so that there isnt a gap within the priorities
                backlog.resetPriorities();
            }
            if(story.getBacklog() != null){
                for(Story s : story.getBacklog().getStories()){
                    if(s.getDependencies().contains(story)) {
                        s.removeDependency(story);
                    }
                }
            }

            for(Sprint sprint:currentOrg.getSprints()){
                if(sprint.getStories().contains(story)){
                    sprint.getStories().remove(story);
                    sprint.getUnallocatedTasks().removeAll(story.getTasks());
                }
            }
            for(Task t: story.getTasks()){
                deleteObject = t;
                executeDelete();
            }

            story.getDependencies().clear();
            //TODO: DOUBLE CHECK THIS DELETE CODE FOR SPRINT
        } else if (deleteObject instanceof Sprint) {
            Sprint sprint = (Sprint) deleteObject;
            currentOrg.removeSprint(sprint);
            for(Task t: sprint.getTasks()){
                deleteObject = t;
                executeDelete();
            }

        } else if (deleteObject instanceof TimePeriod) {
            TimePeriod t = (TimePeriod) deleteObject;
            t.getProject().getTimePeriods().remove(t);
            t.getTeam().removeTimePeriod(t);
            t.getProject().checkLastTeam(t);
        } else if (deleteObject instanceof Backlog) {
            Backlog backlog = (Backlog) deleteObject;
            // reset all the stories
            for (Story story : backlog.getStories()) {
                story.setPriority(-1);
                story.setBacklog(null);
            }
            // remove the backlog from the organisation
            currentOrg.removeBacklog(backlog);
        } else if(deleteObject instanceof Task){
            Task task = (Task) deleteObject;
            for(Story s:currentOrg.getStories()){
                if(s.getTasks().contains(task)){
                    s.getTasks().remove(task);
                    break;
                }
            }
            for(LoggedTime time:task.getLoggedHours()){
                time.getPerson().getLoggedTimes().remove(time);
                time.setPerson(null);
            }
            currentOrg.removeTask(task);
        } else if(deleteObject instanceof LoggedTime){
            LoggedTime time = (LoggedTime) deleteObject;
            for(Task t:currentOrg.getTasks()){
                if(t.getLoggedHours().contains(time)){
                    t.getLoggedHours().remove(time);
                    break;
                }
            }
            time.getPerson().getLoggedTimes().remove(time);
            time.setPerson(null);
        } else if (deleteObject instanceof Tag) {
            Tag tag = (Tag) deleteObject;
            SerializableObservableList<Element> elements = tag.getElements();

            for (Element element : elements) {
                element.removeTag(tag);
            }

            currentOrg.removeTag(tag);





        }

    }

    /**
     * Executes the deletion of the previously set deleteObject. If the object has contained objects, ie in the case of
     * Project and team, those objects are not deleted.
     */
    public void executePartialDelete() {
        if (deleteObject instanceof Project) {
            Project p = (Project) deleteObject;

            for (Iterator<TimePeriod> it = p.getTimePeriods().iterator(); it.hasNext(); ) {
                TimePeriod timePeriod = it.next();
                timePeriod.getTeam().removeTimePeriod(timePeriod);
                it.remove();
            }

            p.getTeams().clear();
            for (Person pe : p.getPeople()) {
                pe.setProject(null);
            }
            for (Release r : p.getReleases()) {
                currentOrg.removeRelease(r);
                r.setProject(null);
            }
            p.getReleases().clear();
            p.getPeople().clear();
            p.getSkills().clear();
            currentOrg.removeProject(p);
        } else if (deleteObject instanceof Team) {
            Team t = (Team) deleteObject;
            for (Iterator<TimePeriod> it = t.getTimePeriods().iterator(); it.hasNext(); ) {
                TimePeriod timePeriod = it.next();
                timePeriod.getProject().removeTeam(t, timePeriod);
                it.remove();
                timePeriod.getProject().checkLastTeam(timePeriod);
            }
            for (Iterator<Person> it = t.getPeople().iterator(); it.hasNext(); ) {
                Person pe = it.next();
                pe.setTeam(null);
                it.remove();
            }
            /*for (Person pe : t.getPeople()) {
                pe.setTeam(null);
            }*/
            t.getPeople().clear();
            currentOrg.removeTeam(t);
        }
    }

    /**
     * Counts the number of things that reference or are contained by the object to be deleted, to inform the user in
     * the deletion dialogs.
     */
    public String thingsToBeDeleted() {
        String deletionString = "\n";
        int numObjs = 0;
        Organisation currentOrg = App.getMainController().currentOrganisation;
        if (deleteObject instanceof Project) {
            Project p = (Project) deleteObject;
            for (Team t : p.getTeams()) {
                numObjs++;
            }
            for (Person pe : p.getPeople()) {
                numObjs++;
            }

            for (Release r : p.getReleases()) {
                numObjs++;
            }
        } else if (deleteObject instanceof Team) {
            Team t = (Team) deleteObject;
            for (Person pe : t.getPeople()) {
                numObjs++;
            }
        } else if (deleteObject instanceof Person) {
            Person p = (Person) deleteObject;

            if (p.getTeam() != null) {
                numObjs++;
            }
            if (p.getProject() != null) {
                numObjs++;
            }
        } else if (deleteObject instanceof Skill) {
            Skill s = (Skill) deleteObject;
            for (Person p : currentOrg.getPeople()) {
                if (p.getSkills().contains(s)) {
                    //deletionString += p.getNameShort() + "\n";
                    numObjs++;
                }
            }
        }
        deletionString = String.valueOf(numObjs);
        return deletionString;
    }
}
