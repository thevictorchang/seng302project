package seng302.group3.model.sorting;

import javafx.collections.ObservableList;
import seng302.group3.model.Element;
import seng302.group3.model.Story;
import seng302.group3.model.Task;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.search.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Andrew on 21/05/2015.
 */
public class Sort {

    /**
     * Sort the items based on their to string representation into alphabetical order - used by navigator representation
     *
     * @param items - the observable list to sort
     */
    public static void alphabeticalSort(ObservableList<NavigatorItem> items, boolean grouped) {
        Collections.sort(items, new Comparator<NavigatorItem>() {
            @Override
            public int compare(NavigatorItem o1, NavigatorItem o2) {
                Object item1 = o1.getItem();
                Object item2 = o2.getItem();
                if (grouped) {
                    return groupedCompareName(item1, item2);
                }
                return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
            }
        });
    }

    private static int groupedCompareName(Object item1, Object item2) {
        if(item1 instanceof Task && item2 instanceof Task){
            Task task1 = (Task) item1;
            Task task2 = (Task) item2;
            if(task1.getObjectType().equals(task2.getObjectType())){
                return task1.toString().toLowerCase().compareTo(task2.toString().toLowerCase());
            }else{
                Story story1 = (Story) ((Task) item1).getObjectType();
                Story story2 = (Story) ((Task) item2).getObjectType();

                if (story1.getPriority() > story2.getPriority()) {
                    return 1;
                } else if (story1.getPriority() <= story2.getPriority()) {
                    return -1;
                }
            }
        }
        return groupedCompare(item1,item2);
    }


    public static void assignedPeopleSort(ObservableList<NavigatorItem> items, boolean grouped) {
        Collections.sort(items, new Comparator<NavigatorItem>() {
            @Override
            public int compare(NavigatorItem o1, NavigatorItem o2) {
                Object item1 = o1.getItem();
                Object item2 = o2.getItem();
                if (grouped) {
                    return groupedCompareAssignedPeople(item1, item2);
                }else{
                    Task task1 = (Task) item1;
                    Task task2 = (Task) item2;
                    if(task1.getAssignedPeople().size()>task2.getAssignedPeople().size()){
                        return 1;
                    }else if(task1.getAssignedPeople().size()<task2.getAssignedPeople().size()){
                        return -1;
                    }
                }
                return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
            }
        });
    }

    private static int groupedCompareAssignedPeople(Object item1, Object item2) {
        if(item1 instanceof Task && item2 instanceof Task){
            Task task1 = (Task) item1;
            Task task2 = (Task) item2;
            if(task1.getObjectType().equals(task2.getObjectType())){
                if(task1.getAssignedPeople().size()>task2.getAssignedPeople().size()){
                    return 1;
                }else if(task1.getAssignedPeople().size()<task2.getAssignedPeople().size()){
                    return -1;
                }
            }else{
                Story story1 = (Story) ((Task) item1).getObjectType();
                Story story2 = (Story) ((Task) item2).getObjectType();

                if (story1.getPriority() > story2.getPriority()) {
                    return 1;
                } else if (story1.getPriority() <= story2.getPriority()) {
                    return -1;
                }
            }
        }
        return groupedCompare(item1,item2);
    }


    public static int searchResultsCompare(Object item1, Object item2) {
        if (item1 instanceof String && item2 instanceof String) {
            String str = (String) item1;
            String str2 = (String) item2;
            return str.toLowerCase().compareTo(str2.toLowerCase());

        } else if (item1 instanceof SearchResult && item2 instanceof SearchResult) {
            String str = ((SearchResult) item1).getElement().getClass().toString();
            String str2 = ((SearchResult) item2).getElement().getClass().toString();
            return str.toLowerCase().compareTo(str2.toLowerCase());
        } else if (item1 instanceof SearchResult && item2 instanceof String) {
            String str = ((SearchResult) item1).getElement().getClass().toString().substring(27);
            String str2 = (String) item2;
            if (str.substring(0, 3).equals(str2.substring(0, 3))) {
                return 1;
            }
            return str.toLowerCase().compareTo(str2.toLowerCase());
        } else if (item1 instanceof String && item2 instanceof SearchResult) {
            String str = (String) item1;
            String str2 = ((SearchResult) item2).getElement().getClass().toString().substring(27);
            if (str.substring(0, 3) == str2.substring(0, 3))
                return -1;
            return str.toLowerCase().compareTo(str2.toLowerCase());
        }
        return 0;
    }

    public static int groupedCompare(Object item1, Object item2) {

        if (item1 instanceof Task && item2 instanceof Story) {
            Task task1 = (Task) item1;
            Story story2 = (Story) item2;

            Story story1 = (Story) task1.getObjectType();
            if (story1.getPriority() > story2.getPriority())
                return 1;
            else
                return -1;


        } else if (item1 instanceof Story && item2 instanceof Task) {
            Task task2 = (Task) item2;
            Story story1 = (Story) item1;
            Story story2 = (Story) task2.getObjectType();
            if (story1.getPriority() > story2.getPriority())
                return 1;
            else
                return -1;

        } else if (item1 instanceof Story && item2 instanceof Story) {
            Story story1 = (Story) item1;
            Story story2 = (Story) item2;
            if (story1.getPriority() > story2.getPriority())
                return 1;
            else
                return -1;
        }
        return 0;
    }

    /**
     * Sort the items based on their to string representation into chronological order - used by navigator representation
     *
     * @param items - the observable list to sort
     */
    public static void chronologicalSort(ObservableList<NavigatorItem> items, boolean grouped) {
        Collections.sort(items, new Comparator<NavigatorItem>() {
            @Override
            public int compare(NavigatorItem o1, NavigatorItem o2) {
                Object item1 = o1.getItem();
                Object item2 = o2.getItem();
                if (grouped) {
                    return groupedCompareChronological(item1, item2);
                } else if (item1 instanceof Element && item2 instanceof Element) {
                    Element element1 = (Element) item1;
                    Element element2 = (Element) item2;

                    // element 1 is newer that element 2
                    if (element1.getDateCreated().isAfter(element2.getDateCreated())) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return 0;
            }
        });
    }

    private static int groupedCompareChronological(Object item1, Object item2) {
        if(item1 instanceof Task && item2 instanceof Task){
            Task task1 = (Task) item1;
            Task task2 = (Task) item2;
            if(task1.getObjectType().equals(task2.getObjectType())){
                if (task1.getDateCreated().isAfter(task2.getDateCreated())) {
                    return -1;
                } else {
                    return 1;
                }
            }else{
                Story story1 = (Story) ((Task) item1).getObjectType();
                Story story2 = (Story) ((Task) item2).getObjectType();

                if (story1.getPriority() > story2.getPriority()) {
                    return 1;
                } else if (story1.getPriority() <= story2.getPriority()) {
                    return -1;
                }
            }
        }
        return groupedCompare(item1,item2);
    }

    /**
     * this method will sort navigator items by there priority. Can only sort Navigator items which are stories however
     *
     * @param items - the observable list to sort
     */
    public static void prioritySort(ObservableList<NavigatorItem> items, boolean grouped) {
        Collections.sort(items, new Comparator<NavigatorItem>() {
            @Override
            public int compare(NavigatorItem o1, NavigatorItem o2) {
                Object item1 = o1.getItem();
                Object item2 = o2.getItem();
                if (item1 instanceof Story && item2 instanceof Story) {
                    Story story1 = (Story) item1;
                    Story story2 = (Story) item2;

                    if (story1.getPriority() > story2.getPriority()) {
                        return 1;
                    } else if (story1.getPriority() <= story2.getPriority()) {
                        return -1;
                    }
                } else if (grouped) {
                    return groupedComparePriority(item1, item2);
                }
                return -1;
            }
        });
    }

    private static int groupedComparePriority(Object item1, Object item2) {
        if(item1 instanceof Task && item2 instanceof Task){
            Task task1 = (Task) item1;
            Task task2 = (Task) item2;
            if(task1.getObjectType().equals(task2.getObjectType())){
                return task1.toString().toLowerCase().compareTo(task2.toString().toLowerCase());
            }else{
                Story story1 = (Story) ((Task) item1).getObjectType();
                Story story2 = (Story) ((Task) item2).getObjectType();

                if (story1.getPriority() > story2.getPriority()) {
                    return 1;
                } else if (story1.getPriority() <= story2.getPriority()) {
                    return -1;
                }
            }
        }
        return groupedCompare(item1,item2);
    }

    public static void taskEstimateSort(ObservableList<NavigatorItem> items, boolean grouped) {
        Collections.sort(items, new Comparator<NavigatorItem>() {
            @Override
            public int compare(NavigatorItem o1, NavigatorItem o2) {
                Object item1 = o1.getItem();
                Object item2 = o2.getItem();
                if (item1 instanceof Task && item2 instanceof Task){
                    Task task1 = (Task) item1;
                    Task task2 = (Task) item2;
                    if(task1.getObjectType().equals(task2.getObjectType())){
                        if(task1.getCurrentEstimate().getValue()>task2.getCurrentEstimate().getValue()){
                            return 1;
                        } else if(task1.getCurrentEstimate().getValue() <= task2.getCurrentEstimate().getValue()){
                            return -1;
                        }
                    }else{
                        Story story1 = (Story) ((Task) item1).getObjectType();
                        Story story2 = (Story) ((Task) item2).getObjectType();

                        if (story1.getPriority() > story2.getPriority()) {
                            return 1;
                        } else if (story1.getPriority() <= story2.getPriority()) {
                            return -1;
                        }
                    }

                }
                return groupedCompare(item1,item2);
            }
        });
    }

    public static void sprintStoryPriority(SerializableObservableList<Story> items){
        Collections.sort(items, new Comparator<Story>() {
            @Override public int compare(Story o1, Story o2) {
                if (o1.getPriority() > o2.getPriority()) {
                    return 1;
                } else if (o1.getPriority() <= o2.getPriority()) {
                    return -1;
                }
                return 0;
            }
        });
    }
}
