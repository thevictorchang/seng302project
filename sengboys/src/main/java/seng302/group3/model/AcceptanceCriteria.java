package seng302.group3.model;


/**
 * Created by cjm328 on 18/07/15.
 */
public class AcceptanceCriteria extends Element {
    private static final long serialVersionUID = 2107201500000L;


    private String shortName;

    /**
     * Constructor which sets the shortName to blank
     */
    public AcceptanceCriteria() {
        this.shortName = "";
    }

    /**
     * Constructor which sets the shortName to the given string
     * @param shortName
     */
    public AcceptanceCriteria(String shortName) {
        this.shortName = shortName;
    }

    /**
     * A setter for shortName
     * @param shortName
     */
    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * A getter for shortName
     * @return
     */
    @Override
    public String getShortName() {
        return this.shortName;
    }

    /**
     * A toString method which returns the shortName
     * @return
     */
    @Override
    public String toString() {
        return this.shortName.split("\n")[0];
    }
}
