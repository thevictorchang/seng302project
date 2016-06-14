package seng302.group3.model.GUI;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.Organisation;
import seng302.group3.model.gui.StoryListCell;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by epa31 on 26/07/15.
 */
public class StoryListCelltest {
    private Organisation testOrganisation;
    private StoryListCell storyListCell;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setup(){
        final Map<String, Double> fibonacci = new TreeMap<>();;
        testOrganisation = new Organisation("Test Org");
        //storyListCell = new StoryListCell(fibonacci);
    }

    @Test
    public void checkLocalRedFlag(){

    }

    @Test
    public void getLocalPriority(){

    }


}
