package seng302.group3.model;

/**
 * Estimate object so a task can keep a record of past estimates - potentially new functionality for burndowns
 * Created by jwc78 on 4/08/15.
 */
public class Estimate extends Element {

    private static final long serialVersionUID = 408201500007L;

    private String shortName = null;
    private Double value = 0.0;


    public Estimate() {

    }

    public Estimate(Double value) {
        this.shortName = "Value: "+value.toString();
        this.value = value;
    }

    @Override public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override public String getShortName() {
        return shortName;
    }

    public Double getValue() {
        return this.value;
    }
}
