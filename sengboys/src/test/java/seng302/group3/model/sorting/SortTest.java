package seng302.group3.model.sorting;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.group3.model.Story;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

public class SortTest {
    static SerializableObservableList<Story> stories = new SerializableObservableList<>();

    @BeforeClass
    public static void setupBeforeClass() throws Exception{
        // create some stories that we will be sorting
        Story story1 = new Story("AA", "AA full name", "desc");
        story1.setPriority(3);

        Story story2 = new Story("BB", "BB full name", "desc");
        story2.setPriority(2);

        Story story3 = new Story("BB", "BB full name", "desc");
        story3.setPriority(2);

        Story story4 = new Story("DD", "DD full name", "desc");
        story4.setPriority(4);

        Story story5 = new Story("EE", "EE full name", "desc");
        story5.setPriority(5);

        stories.add(story5);
        stories.add(story4);
        stories.add(story3);
        stories.add(story1);
        stories.add(story2);
    }


    @Test
    public void testAlphabeticalSortingMethodOnStories(){
        Sort.alphabeticalSort(stories.getObservableList(),false);
        boolean sorted = true;
        String previousStr = null;

        for(NavigatorItem story : stories.getObservableList()){
            if(previousStr != null){
                if(story.toString().compareTo(previousStr) < 0){
                    sorted = false;
                }
            }
            previousStr = story.toString();
        }

        assertTrue("The stories have been sorted into alphabetical order properly", sorted);
    }

    @Test
    public void testChronologicalSortMethodOnStories(){
        Sort.chronologicalSort(stories.getObservableList(),false);
        boolean sorted = true;
        LocalDateTime previousDate = null;

        for(NavigatorItem navStory : stories.getObservableList()){
            Story story = (Story) navStory.getItem();
            if(previousDate != null){
                if(story.getDateCreated().isAfter(previousDate)){
                    sorted = false;
                }
            }
            previousDate = story.getDateCreated();
        }

        assertTrue("The stories have been sorted into chronological order properly", sorted);
    }

    @Test
    public void testPrioritySortMethodOnStories(){
        Sort.prioritySort(stories.getObservableList(),false);
        boolean sorted = true;
        int previousPriority = -1;

        for(NavigatorItem navStory : stories.getObservableList()){
            Story story = (Story) navStory.getItem();
            if(previousPriority != -1){
                if(previousPriority > story.getPriority()){
                    sorted = false;
                }
            }
            previousPriority = story.getPriority();
        }

        assertTrue("The stories have been sorted into priority order properly", sorted);
    }

}
