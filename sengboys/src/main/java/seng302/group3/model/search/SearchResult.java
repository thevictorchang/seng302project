package seng302.group3.model.search;

import seng302.group3.model.Element;

/**
 * Created by cjm328 on 19/09/15.
 */
public class SearchResult {
    private Element element;
    private String howFound;

    /**
     * Constructor for searchresult object, which holds an element and how it was matched.
     * @param element - the found element
     * @param howFound - the string of how it was found.
     */
    public SearchResult(Element element, String howFound){
        this.element = element;
        this.howFound = howFound;
    }

    /**
     * returns the element
     * @return
     */
    public Element getElement() {
        return element;
    }


    @Override
    public String toString() {
        String toStr = "";
        if(element!=null){
            toStr = element.getShortName() + " " + howFound;
        }
        return toStr;
    }
}
