package seng302.group3.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vch51 on 17/03/15.
 */
public class Task implements Serializable{
    private String name; // name of task
    private Collection<Person> people = new ArrayList<Person>();; // list of people assigned to this task?
    private double hours; // number of hours assigned to this task?

    /**
     * Constructor for Task
     * @param name
     */
    public Task(String name, double hours) {
        this.name = name;
        this.hours = hours;
    }

    /**
     * Getter for name
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for people in the list of people involved in the task
     * @return People in the collection
     */
    public Collection<Person> getPeople() {
        return people;
    }

    /**
     * Getter for Hours
     * @return number of hours
     */
    public double getHours() {
        return hours;
    }

    /**
     * Setter for hours spent on a task
     * @param hours
     */
    public void setHours(double hours) {
        this.hours = hours;
    }

    /**
     * Remove a Person from the people list
     * @param person
     */
    public void removePerson(Person person) {
        this.people.remove(person);
    }

    /**
     * Add a new Person to the people list
     * @param person
     */
    public void addPerson(Person person) {
        this.people.add(person);
    }

    /**
     * toString
     * @return toString of task
     */
    @Override
    public String toString() {
        return this.getName();
    }

}


