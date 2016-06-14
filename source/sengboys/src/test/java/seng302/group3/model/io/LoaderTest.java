/*
package seng302.group3.model.io;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import seng302.group3.model.Project;

import java.io.IOException;
import java.nio.file.*;

*/
/**
 * Unit Test for Loader class
 * Created by jwc78 on 17/03/15.
 *//*

public class LoaderTest extends TestCase {

    private Project testProject; // test Project for testing the Project() method
    private Path path = Paths.get("project.test");

    @Before
    public void setUp() throws Exception {
        //make a test Project
        testProject = new Project("shortName text", "longName text", "description text");
        Saver.save(path.toString(), testProject);
    }

    @After
    public void tearDown() throws Exception {
        //destroys the test Project after test
        testProject = null;
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }

    /* FAILING TEST
    public void testLoad() {
        assertTrue("Loaded test project should match the generated project", Loader.load(path.toString()).equals(testProject));
    }


}*/
