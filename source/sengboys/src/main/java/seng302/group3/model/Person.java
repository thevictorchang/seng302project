package seng302.group3.model;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vch51 on 17/03/15.
 *
 * Structure for the Person object. a Person can be added to a Project. Each Person has a nameShort, which is a shorter,
 * codename-like identifier (which is unique), a fullName, a userID and a biography. Each Person also has a collection of
 * Skills (to be further implemented in a future story) which can help a manager sort their staff.
 *
 */
public class Person implements Serializable {
    private String nameShort = ""; // non null, unique
    private String fullName = "";
    private String userID = "";
    private Collection<Skill> skills = new ArrayList<>();
    private String biography;

    /**
     * Constructor for empty person class
     */
    public Person(){

    }

    /**
     * This copys the given person into a new person object
     * @param copyData - the person to be copied
     */
    public Person(Person copyData){
        this.nameShort = copyData.getNameShort();
        this.fullName = copyData.getFullName();
        this.userID = copyData.getUserID();
        this.skills = copyData.getSkills();
        this.biography = copyData.getBiography();
    }

    /**
     * Constructor for person
     * @param name
     */
    public Person (String name) {
        this.nameShort = name;
    }

    /**
     * Constructor for person
     * @param name
     * @param full
     */
    public Person (String name, String full) {

        this.nameShort = name;
        this.fullName = full;
    }

    /**
     * Constructor for person
     * @param name
     * @param full
     * @param user
     */
    public Person (String name, String full, String user ) {

        this.nameShort = name;
        this.fullName = full;
        this.userID = user;
    }

    /**
     * Getter for the full name of the person
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Setter for the full name
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Getter for the nameShort
     * @return the short namenameShort
     */
    public String getNameShort() {
        return nameShort;
    }

    /**
     * Setter for the nameShort
     * @param nameShort
     */
    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    /**
     * Getter for the user ID
     * @return the user ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Setter for the user ID
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Getter for the list of skills for a person
     * @return collection of skills
     */
    public Collection<Skill> getSkills() {
        return skills;
    }

    /**
     * Method for adding a skill to the skills collection for a person
     * @param skill
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    /**
     * Method for removing a skill from the collection of skills
     * @param skill
     */
    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }

    /**
     * Getter for the biography of a person
     * @return
     */
    public String getBiography() {
        return biography;
    }

    /**
     * Setter for the biography of a person
     * @param biography
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * toString
     * @return toString of Person
     */
    @Override
    public String toString() {
        return this.getNameShort();
    }
}