package seng302.group3.model.io;

import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jwc78 on 25/05/15.
 */
public class ParameterisedReport {

    @Root private Collection<seng302.group3.model.Element> elements =
        new ArrayList<>();

    public ParameterisedReport(Collection<seng302.group3.model.Element> elements) {
        this.elements = elements;
    }

    public Collection<seng302.group3.model.Element> getElements() {
        return this.elements;
    }

}
