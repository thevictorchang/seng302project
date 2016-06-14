package seng302.group3.model;

import seng302.group3.model.History.Change;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * Created by epa31 on 12/03/15.
 * Models Project class.
 */
public class Project implements Serializable {
    private String nameShort; // non null, unique
    private String nameLong;
    private String description;
    private String filename = "project1.proj";
    private Collection<Person> people = new ArrayList<>();
    private Collection<Task> tasks = new ArrayList<>();

    private Stack<Change> pastHistory = new Stack<>();
    private Change currentChange;
    private Stack<Change> futureHistory = new Stack<>();

    /**
     * Project constructor
     * @param shortName
     * @param longName
     * @param description
     */
    public Project(String shortName, String longName, String description){
        this.nameShort = shortName;
        this.nameLong = longName;
        this.description = description;
    }

    public Project(){
        this.nameShort = "";
        this.nameLong = "";
        this.description = "";
    }

    /**
     * Getter for nameShort
     * @return nameShort
     */
    public String getNameShort() {
        return nameShort;
    }
    /**
     * Setter for person's name
     * @param name
     */

    /**
     * Getter for nameLong
     * @return nameLong
     */
    public String getNameLong() {
        return nameLong;
    }

    /**
     * Getter for description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for people
     * @return
     */
    public Collection<Person> getPeople() {
        return this.people;
    }

    /**
     * Setter for nameShort
     * @param nameShort
     */
    public void setShortName(String nameShort) {
        this.nameShort = nameShort;
    }

    /**
     * Setter for nameLong
     * @param nameLong
     */
    public void setLongName(String nameLong) {
        this.nameLong = nameLong;
    }

    /**
     * Setter for description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Remove a Person from the people list
     * @param person
     */
    public void removePerson(Person person) {
        this.people.remove(person);
    }

    /**
     * Add a new Person to the people listChange
     * @param person
     */
    public void addPerson(Person person) {
        this.people.add(person);
    }


    /**
     * Returns the collection of changes on a project, used for undo/redo
     * @return returns a stack of the past history to undo
     */
    public Stack<Change> getPastHistory(){
        return this.pastHistory;
    }


    /**
     * Gets the most current change
     * @return returns most current change in project
     */
    public Change getCurrentChange(){
        return this.currentChange;
    }

    /**
     * Gets the future history that the redo function can call upon
     * @return returns the future history for the redos
     */
    public Stack<Change> getFutureHistory(){
        return this.futureHistory;
    }

    /**
     * Gets the last saved filename of the project
     * @return Returns the last saved filename for the project
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the most recent filename of the project
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * toString
     * @return toString of Project
     */
    @Override
    public String toString() {
        return "Project{" +
                "nameShort='" + this.getNameShort() + '\'' +
                ", nameLong='" + this.getNameLong() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", people=" + this.getPeople() +
                '}';
    }

    /**
     * Override method of equals function to compare two Project object
     * @param object
     * @return boolean result
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Project && object.toString().equals(this.toString())) {
            return true;
        }
        return false;
    }


    /**
     * adding a new change to the project, pushes the last most recent change to the history stack and clears
     * the future changes stack
     * @param change - the new change in the project
     */
    public void addChange(Change change) {
        if(this.currentChange != null)
            this.pastHistory.push(this.currentChange);
        this.currentChange = change;
        this.futureHistory = new Stack<>();
    }


    /**
     * Setter for the current change, used for the undo/redo functionality
     * @param currentChange
     */
    public void setCurrentChange(Change currentChange) {
        this.currentChange = currentChange;
    }

    /**
     * Setter to specify the people involved in a project
     * @param people
     */
    public void setPeople(Collection<Person> people) {
        this.people = new ArrayList<>(people);
    }
}