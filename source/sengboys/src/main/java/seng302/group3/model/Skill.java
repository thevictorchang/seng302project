package seng302.group3.model;

/**
 * Created by vch51 on 24/03/15.
 * People will have skills, and in the 'edit person' part of the app skills can be added/removed
 */
public class Skill {
    private String name;
    private String description;

    /**
     * Constructor for Skill
     * @param name
     * @param description
     */
    public Skill(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Getter for the name of the skill
     * @return the name of the skill
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * toString
     * @return toString of skill
     */
    @Override
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
