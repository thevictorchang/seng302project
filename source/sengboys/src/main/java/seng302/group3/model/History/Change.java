package seng302.group3.model.History;

import seng302.group3.model.Person;
import seng302.group3.model.Project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ntr24 on 23/03/15.
 */
public class Change implements Serializable {

    private changeType type;

    //Edit Project
    private String shortName;
    private String longName;
    private String description;


    //Add Person
    private Collection<Person> people;


    /**
     * Defines the type of Change. For instance
     * EDIT_PROJECT is for any change to the project details
     * EDIT_PERSON is for any adding or editing of people
     */
    public static enum changeType {
        EDIT_PROJECT, EDIT_PERSON
    }


    /**
     * Constructor checks the chosen type and saves the relevant information for the undo/redo
     * @param type - type of change made
     * @param project - project the change was made on
     * @param args - some types of changes may require extra optional argument. For instance
     *             add person change takes a person as an extra
     */
    public Change(changeType type, Project project, Object... args){
        this.type = type;

        switch (type){
            case EDIT_PROJECT:
                this.shortName = project.getNameShort();
                this.longName = project.getNameLong();
                this.description = project.getDescription();
                break;
            case EDIT_PERSON:
                if(args.length > 0 && args[0] instanceof Person){

                    if(project.getPeople().size() < 1){
                        this.people = new ArrayList<>();
                    }else{
                        // This is to make a copy of the changed person
                        Person oldPerson = (Person) args[0];
                        this.people = new ArrayList<>(project.getPeople());
                        this.people.remove(oldPerson);
                        this.people.add(new Person(oldPerson));
                    }
                }
                break;
        }
    }


    /**
     * applies the change to a given project
     * @param project - project to apply the change to
     * @return - project with the applied change
     */
    public Project applyChange(Project project){
        switch (this.getType()){
            case EDIT_PROJECT:
                project.setShortName(this.shortName);
                project.setLongName(this.longName);
                project.setDescription(this.description);
                break;
            case EDIT_PERSON:
                project.setPeople(this.people);
                break;
        }
        return project;
    }


    /**
     * returns the change type
     * @return - the type of change this refers to
     */
    public changeType getType(){
        return this.type;
    }


    /**
     * String version of the change type, used for undo/redo printout to the bottom dialog
     * @return - String representation of the change type.
     */
    public String toString(){
        switch (this.getType()){

            case EDIT_PROJECT:
                return "Project changes";
            case EDIT_PERSON:
                return "Person changes";
        }
        return null;
    }

}
