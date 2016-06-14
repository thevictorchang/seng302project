package seng302.group3.model.io;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.Format;
import seng302.group3.model.Organisation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jwc78 on 15/05/15.
 */
public class StatusReport {

    /**
     * Generates a report for the entire organisation
     *
     * @param filepath - the filepath to generate the file to
     * @param org      - the current organisation which is being exported
     */
    public static void generateOrganisationReport(String filepath, Organisation org) {
        if (org != null) {
            String listString = "";
            listString += "<?xml version=\"1.0\"?>\n<!DOCTYPE organisation SYSTEM \"organisation.dtd\">";
            Strategy strategy = new CycleStrategy("id", "ref");
            Format format = new Format(listString);
            Serializer serializer = new Persister(strategy, format);
            File file = new File(filepath);
            try {
                serializer.write(org, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Generates a subset report using a given report object
     *
     * @param filepath - When to generate to
     * @param report   - A container which has all of the picked objects
     */
    public static void generateReport(String filepath, ParameterisedReport report) {
        if (report != null) {
            String listString = "";
            listString += "<?xml version=\"1.0\"?>\n<!DOCTYPE parameterisedReport SYSTEM \"parameterisedReport.dtd\">";
            Strategy strategy = new CycleStrategy("id", "ref");
            Format format = new Format(listString);
            Serializer serializer = new Persister(strategy, format);
            File file = new File(filepath);
            try {
                serializer.write(report, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gives the ability to import an Organisation from a generated XML file.
     *
     * @param filepath - The location of the file.
     * @return - An organisation generated from the file.
     */
    public static Organisation importOrganisationXML(String filepath) {
        Organisation org = new Organisation();
        if (filepath != null) {
            Strategy strategy = new CycleStrategy("id", "ref");
            Serializer serializer = new Persister(strategy);
            File file = new File(filepath);
            try {
                org = serializer.read(Organisation.class, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return org;
    }
}
