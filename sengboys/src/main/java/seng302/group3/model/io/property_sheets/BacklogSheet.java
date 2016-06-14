package seng302.group3.model.io.property_sheets;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import seng302.group3.controller.App;
import seng302.group3.model.Backlog;
import seng302.group3.model.Organisation;
import seng302.group3.model.Person;
import seng302.group3.model.Story;
import seng302.group3.model.gui.PlainDragListCell;
import seng302.group3.model.gui.StoryListCell;
import seng302.group3.model.io.Editor;
import seng302.group3.model.search.FilterListView;
import seng302.group3.model.search.FilterUtil;

import java.io.IOException;
import java.util.*;

/**
 * Created by vch51 on 15/05/15.
 */

public class BacklogSheet implements EditorSheet {

    private VBox content = new VBox();

    @FXML public TextField shortNameTextField;
    @FXML TextField descriptionTextField;
    @FXML TextField availableStoriesTextField;
    @FXML TextField currentStoriesTextField;
    @FXML ComboBox POChoice;
    @FXML Label errorLabel;
    @FXML Button buttonAddStory;
    @FXML Button buttonRemoveStory;
    @FXML Button buttonNewStory;

    @FXML ListView<Story> allCurrentStoriesListView;
    @FXML ListView<Story> allAvailableStoriesListView;

    @FXML public ComboBox scaleComboBox;


    final ObservableList<Story> allCurrentStoriesList = FXCollections.observableArrayList();
    final ObservableList<Story> allAvailableStoriesList = FXCollections.observableArrayList();

    private FilterListView allAvailableStoriesFilterListView;
    private FilterListView allCurrentStoriesFilterListView;

    private Collection<Story> newCreatedStories = new ArrayList<>();

    private static final String errorStyle = "errorTextField";

    String prevShortName = "";
    int availableStories = 0;

    private Backlog backlogEdit = null;
    private Organisation curOrg = null;

    /**
     * these are the inbuilt estimation scales
     */
    public static final String FIBONACCI = "Fibonacci";
    public static final String TSHIRTS = "T-Shirts";
    public static final String NATURAL = "Natural";

    public static final Map<String, Double> fibonacci;
    public static final Map<String, Double> tShirts;
    public static final Map<String, Double> natural;
    static{
        Map<String, Double> fib = new TreeMap<>();
        fib.put("-", -1.0);
        fib.put("0", 0.0);
        fib.put("1", 1.0 / 7.0);
        fib.put("2", 2.0 / 7.0);
        fib.put("3", 3.0 / 7.0);
        fib.put("5", 4.0 / 7.0);
        fib.put("8", 5.0 / 7.0);
        fib.put("13", 6.0 / 7.0);
        fib.put("20", 1.0);
        fib.put("Epic", 2.0);
        fib = sortByValue(fib);

        fibonacci = Collections.unmodifiableMap(fib);

        Map<String, Double> tshirts = new TreeMap();
        tshirts.put("-", -1.0);
        tshirts.put("0", 0.0);
        tshirts.put("XS", 1.0 / 5.0);
        tshirts.put("S", 2.0 / 5.0);
        tshirts.put("M", 3.0 / 5.0);
        tshirts.put("L", 4.0 / 5.0);
        tshirts.put("XL", 1.0);
        tshirts.put("Epic", 2.0);
        tshirts = sortByValue(tshirts);

        tShirts = Collections.unmodifiableMap(tshirts);

        Map<String, Double> nat = new TreeMap<>();
        nat.put("-", -1.0);
        nat.put("0", 0.0);
        nat.put("1", 1.0 / 9.0);
        nat.put("2", 2.0 / 9.0);
        nat.put("3", 3.0 / 9.0);
        nat.put("4", 4.0 / 9.0);
        nat.put("5", 5.0 / 9.0);
        nat.put("6", 6.0 / 9.0);
        nat.put("7", 7.0 / 9.0);
        nat.put("8", 8.0 / 9.0);
        nat.put("9", 1.0);
        nat.put("Epic", 2.0);

        natural = Collections.unmodifiableMap(nat);
    }

    /**
     * constructor, loads the fxml and sets the controller to this class
     */
    public BacklogSheet(Organisation org) {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/BacklogSheet.fxml"));
        try {

            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
            allAvailableStoriesFilterListView = new FilterListView(allAvailableStoriesListView, availableStoriesTextField, FilterUtil.AutoCompleteMode.CONTAINING);
            allCurrentStoriesFilterListView = new FilterListView(allCurrentStoriesListView, currentStoriesTextField, FilterUtil.AutoCompleteMode.CONTAINING);
            for (Person person : org.getPeople()) {
                // This one line adds people only with the Product Owner skill to the combo box
                person.getSkills().stream().filter(skill -> skill.getShortName().equals("PO"))
                    .forEach(skill -> POChoice.getItems().add(person));
            }
            FilterUtil.autoCompleteComboBox(POChoice, FilterUtil.AutoCompleteMode.CONTAINING);
            POChoice.getItems().add("None");
            POChoice.getSelectionModel().select("None");

            scaleComboBox.getItems().addAll(FIBONACCI, TSHIRTS, NATURAL);
            scaleComboBox.getSelectionModel().select(FIBONACCI);
            scaleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                switch ((String) newValue){
                    case FIBONACCI:
                        if (backlogEdit != null) {
                            backlogEdit.setScale(FIBONACCI);
                        }
                        StoryListCell.createFactory(allCurrentStoriesListView, fibonacci);
                        break;
                    case TSHIRTS:
                        if (backlogEdit != null) {
                            backlogEdit.setScale(TSHIRTS);
                        }
                        StoryListCell.createFactory(allCurrentStoriesListView, tShirts);
                        break;
                    case NATURAL:
                        if (backlogEdit != null) {
                            backlogEdit.setScale(NATURAL);
                        }
                        StoryListCell.createFactory(allCurrentStoriesListView, natural);
                        break;
                    default:
                        if (backlogEdit != null) {
                            backlogEdit.setScale(FIBONACCI);
                        }
                        StoryListCell.createFactory(allCurrentStoriesListView, fibonacci);
                        break;
                }

            });

            PlainDragListCell.createFactory(allAvailableStoriesListView);
            allAvailableStoriesListView.placeholderProperty().setValue(new Label("No Available Stories"));


            StoryListCell.createFactory(allCurrentStoriesListView, fibonacci);
            allCurrentStoriesListView.placeholderProperty().setValue(new Label("No Current Stories"));


            curOrg = org;
            storiesConstruct(org);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * used when editing a backlog, all the attributes from the release are put into the fields for editing
     *
     * @param object, a backlog
     */
    public void fieldConstruct(Object object) {
        if (object instanceof Backlog) {
            Backlog backlog = (Backlog) object;

            //Set text fields
            shortNameTextField.setText(backlog.getShortName());
            prevShortName = backlog.getShortName();
            descriptionTextField.setText(backlog.getDescription());

            //Add a backlogs current stories to the left list
            allCurrentStoriesList
                .addAll(backlog.getStories()); //add stories in the backlog to current
            allCurrentStoriesFilterListView.setData(allCurrentStoriesList);

            if (backlog.getProductOwner() != null) {
                POChoice.getSelectionModel().select(backlog.getProductOwner());
                POChoice.getItems().remove("None");
            }

            // use the backlogs scale if there is one set, otherwise init to the Fib scale
            if(backlog.getScale() == null){
                backlog.setScale(FIBONACCI);
            }else{
                scaleComboBox.getSelectionModel().select(backlog.getScale());
            }

            backlogEdit = backlog;

        }
    }


    /**
     * Used when making new backlog
     *
     * @param args
     */
    public void storiesConstruct(Object... args) {
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];
            String noneChoice = "None";
            if (allCurrentStoriesList.size() < 1) {
                allCurrentStoriesFilterListView.setData(allCurrentStoriesList);

            }

            //loop through every story in organisation
            for (Story story : organisation.getStories()) {
                if (story.getBacklog() == null) { // if story doesn't have a backlog
                    allAvailableStoriesList.add(story); //add that story
                    availableStories ++;
                }
            }

            allAvailableStoriesListView.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
            allCurrentStoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            allAvailableStoriesFilterListView.setData(allAvailableStoriesList);

        }
    }


    /**
     * Will open a new story dialog
     *
     * @param actionEvent - pressing the plus(new) button.
     */
    public void buttonNewStory(ActionEvent actionEvent) {
        // create a new story to be added to the list
        Story newStory = new Story();

        new Editor().CreateStoryFromBacklog(newStory, newCreatedStories, this);

        // The cancel button was clicked
        if (newStory.getShortName() == null) {

        }
        // the okay button was clicked
        else {
            // add to the current stories list
            Integer index = allCurrentStoriesListView.getSelectionModel().getSelectedIndex() + 1;
            if(index > 0) {
                allCurrentStoriesList.add(index, newStory);
            }
            else {
                allCurrentStoriesList.add(newStory);
            }
            //also add to a list of the newly added stories
            newCreatedStories.add(newStory);
        }

    }

    public void updateStories() {
        allAvailableStoriesFilterListView.setData(allAvailableStoriesList);
        allCurrentStoriesFilterListView.setData(allCurrentStoriesList);
    }

    /**
     * Adds a story from available (right) to current (left)
     *
     * @param actionEvent
     */

    public void buttonAddStory(ActionEvent actionEvent) {
        Collection<Story> currentStories =
            allAvailableStoriesListView.getSelectionModel().getSelectedItems();
        if (currentStories != null) {
            Integer index = allCurrentStoriesListView.getSelectionModel().getSelectedIndex() + 1;
            if (index > 0) {
                allCurrentStoriesList.addAll(index, currentStories);
            }
            else {
                allCurrentStoriesList.addAll(currentStories);
            }
            allAvailableStoriesList.removeAll(currentStories);
        }
        updateStories();
        currentStoriesTextField.setText("");
        availableStoriesTextField.setText("");
    }

    /**
     * Brings up the edit story dialog which will then push information to the backlog dialog
     * @param actionEvent
     */
    public void buttonEditStory(ActionEvent actionEvent) {
        if (allCurrentStoriesListView.getSelectionModel().getSelectedItem() instanceof Story) {
            Story selectedStory = allCurrentStoriesListView.getSelectionModel().getSelectedItem();
            new Editor().EditStoryFromBacklog(App.getMainController().currentOrganisation, selectedStory, this);
            Integer index = allCurrentStoriesListView.getSelectionModel().getSelectedIndex();
            allCurrentStoriesListView.getItems().removeAll(selectedStory);
            allCurrentStoriesListView.getItems().add(index,selectedStory);
        }else if(allAvailableStoriesListView.getSelectionModel().getSelectedItem() instanceof  Story){
            Story selectedStory = allAvailableStoriesListView.getSelectionModel().getSelectedItem();
            new Editor().EditStory(App.getMainController().currentOrganisation, selectedStory);
            Integer index = allAvailableStoriesListView.getSelectionModel().getSelectedIndex();
            allAvailableStoriesListView.getItems().removeAll(selectedStory);
            allAvailableStoriesListView.getItems().add(index,selectedStory);
        }
        updateStories();
    }

    /**
     * Deletes the currently selected story from the backlog
     * @param actionEvent
     */
    public void buttonRemoveStory(ActionEvent actionEvent) {
        Collection<Story> currentStories =
            allCurrentStoriesListView.getSelectionModel().getSelectedItems();
        for(Story s : allCurrentStoriesList){
            for(Story s2: currentStories){
                if(s.getDependencies().contains(s2)){
                    s.removeDependency(s2);
                }
            }
        }
        for(Story s3: currentStories){
            s3.getDependencies().clear();
        }
        if (currentStories != null) {
            allAvailableStoriesList.addAll(currentStories);
            allCurrentStoriesList.removeAll(currentStories);

        }
        updateStories();
        currentStoriesTextField.setText("");
        availableStoriesTextField.setText("");
    }


    @Override
    /**
     * prepares the vbox
     */ public VBox draw() {
        return content;
    }

    @Override
    /**
     * checks that all the values in the fields are all good:
     * Text fields must have something in them
     * Date selected must not be before today's date
     * Project selection must not be "None" or non selected
     */ public boolean validate(Object... args) {
        Organisation org;
        if(args[0] instanceof Organisation){
            org = (Organisation) args[0];
        }
        else{
            org = App.getMainController().currentOrganisation;
        }
        boolean result = true;
        shortNameTextField.setText(shortNameTextField.getText().trim());
        descriptionTextField.setText(descriptionTextField.getText().trim());

        //checks the shortname text field is at least one character long
        if (shortNameTextField.getText().length() < 1) {
            result = false;
            shortNameTextField.getStyleClass().add(errorStyle);
        } else {

            shortNameTextField.getStyleClass().removeAll(errorStyle);
        }

        //after checking the shortname, checks the description
        if (descriptionTextField.getText().length() < 1) {
            result = false;
            descriptionTextField.getStyleClass().add(errorStyle);
        } else {
            descriptionTextField.getStyleClass().removeAll(errorStyle);
        }

        if (!result) {
            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        } else {
            if (FilterUtil.getComboBoxValue(POChoice).equals("None")) {
                result = false;
                POChoice.getStyleClass().add(errorStyle);
            } else {
                POChoice.getStyleClass().removeAll(errorStyle);
            }
        }

        if (!result) {
            errorLabel.setText("Must select a Product Owner");
            return result;
        } else {
            if (shortNameTextField.getText().equals(prevShortName) || org
                .uniqueName(shortNameTextField.getText(), Organisation.uniqueType.UNIQUE_BACKLOG)) {
                shortNameTextField.getStyleClass().removeAll(errorStyle);
            } else {
                shortNameTextField.getStyleClass().add(errorStyle);
                result = false;
            }
        }

        if (!result) {
            errorLabel.setText("Short Name must be unique");
        }

        return result;

    }

    /**
     * Pushes the data from the dialog out to the lower level.
     * @param object
     */
    public void apply(Object object) {
        if (object instanceof Backlog) {
            Backlog backlog = (Backlog) object;

            backlog.setShortName(shortNameTextField.getText());
            backlog.setDescription(descriptionTextField.getText());

            //If moved from right ot left
            backlog.setProductOwner((Person) FilterUtil.getComboBoxValue(POChoice));

            for (Story story : allAvailableStoriesList) {
                story.setBacklog(null);
                // story without a backlog has priority -1
                story.setPriority(-1);
            }


            // we clear the backlog of stories first so that the they remain in priority order within the backlog
            backlog.getStories().clear();

            int priority = 0;

            for (Story story : allCurrentStoriesList) {
                backlog.addStory(story); //add it
                story.setBacklog(backlog);

                // the lower the number is the higher the priority of that story
                story.setPriority(priority);
                priority++;
            }

            // add the newly created stories to the organisation
            newCreatedStories.forEach(App.getMainController().currentOrganisation::addStory);

            if (backlog.getStories().size() > 0) {
                // make copy of the backlog's story collection so we don't get concurrent editing in the for loop
                Collection<Story> stories = new ArrayList<>(backlog.getStories());
                for (Story story : stories) {
                    if (!allCurrentStoriesList.contains(story)) {
                        backlog.removeStory(story);
                    }
                    story.setBacklog(backlog); // set the stories backlog
                }
            }

            backlog.setScale((String) scaleComboBox.getSelectionModel().getSelectedItem());

        }
    }

    /**
     * Used for dialogs with double boxes and will tell if the dialog has had changes made.
     * @return
     */
    public boolean hasChangedOnAdd() {
        //If we are editing or adding
        boolean changed = false;
        if (!shortNameTextField.getText().trim().equals("")) {
            changed = true;
        }
        else if (!descriptionTextField.getText().trim().equals("")) {
            changed = true;
        }
        else if (!FilterUtil.getComboBoxValue(POChoice).equals("None")) {
            changed = true;
        }
        else if (allCurrentStoriesList.size() > 0) {
            changed = true;
        }
        else if (allAvailableStoriesList.size() != availableStories) {
            changed = true;
        }
        return changed;
    }

    /**
     * Checks (on an edit operation of a backlog dialog) that there have been changes made to the
     * dialog.
     * @return
     */
    public boolean hasChangedOnEdit(){
        boolean changed = false;
        if(!shortNameTextField.getText().trim().equals(backlogEdit.getShortName())){
            changed = true;
        }
        else if (!FilterUtil.getComboBoxValue(POChoice).equals(backlogEdit.getProductOwner())){
            changed = true;
        }
        else if(!descriptionTextField.getText().trim().equals(backlogEdit.getDescription())){
            changed = true;
        }
        else if(!allCurrentStoriesList.equals(backlogEdit.getStories())){
            changed = true;
        }
        else if(allAvailableStoriesList.size() != availableStories){
            changed = true;
        }
        else if(!allCurrentStoriesList.containsAll(backlogEdit.getStories())){
            changed = true;
        }
        return changed;
    }


    public void buttonHighlight(ActionEvent actionEvent) {
        //Toggle here
    }

    /**
     * Used to sort a map by value
     * @param unsortedMap - map to be sorted
     * @return the sorted map
     */
    public static Map sortByValue(Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

}
