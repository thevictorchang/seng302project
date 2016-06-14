package seng302.group3.model.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import seng302.group3.controller.App;
import seng302.group3.model.Story;

import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * Created by ntr24 on 18/07/15.
 */
public class StoryListCell<T> extends DragListCell<T> {

    Image Red = new Image(getClass().getResourceAsStream("/imgs/highlights/RedB.png"));
    Image Green = new Image(getClass().getResourceAsStream("/imgs/highlights/GreenB.png"));
    Image Orange = new Image(getClass().getResourceAsStream("/imgs/highlights/OrangeB.png"));
    Image Blank = new Image(getClass().getResourceAsStream("/imgs/roles/Blank.png"));

    Map<String, Double> scale;


    /**
     * calls the Drag list cell create factory and gives the cell factory to the list view
     * @param listView - list view we want to add the factory to
     * @param scale - scale that the list cells will be using
     */
    public static void createFactory(ListView listView, Map<String, Double> scale){
        DragListCell.createFactory(listView);
        listView.setCellFactory(param ->
                        new StoryListCell(scale)
        );
    }

    /**
     * Constructor which sets the scale to display on the list items, ie T-Shirts, or Fibonacci
     * @param scale
     */
    private StoryListCell(Map<String, Double> scale){
        setScale(scale);
    }

    /**
     * set the scale to display on the list items, ie T-Shirts, or Fibonacci
     * @param scale - Scale value name - scale value pair
     */
    public void setScale(Map<String, Double> scale){
        this.scale = scale;
    }

    /**
     * The formatter for the cells in a story. Builds the cells dropdown, text and highlighting.
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        if (empty || item == null){
            setGraphic(null);
            super.updateItem(item, empty);
        }else if(item instanceof Story){
            super.updateItem(item, false);
            Story story = (Story) item;

            ComboBox dropdown = new ComboBox();
            dropdown.getStyleClass().add("blankdropdown");
            dropdown.setItems(FXCollections.observableArrayList(scale.keySet()));
            dropdown.getSelectionModel().select(getScaleValue(story.getEstimate()));
            dropdown.setButtonCell(new ListCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty || item==null){

                    } else {
                        getStyleClass().add("blankdropdown");
                        setText(item.toString());

                    }
                }
            });

            final ImageView bImageView = new ImageView(Blank);

            bImageView.setFitHeight(16);
            bImageView.setFitWidth(16);

            dropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
                story.setEstimate(scale.get(newValue));
            });

            if(story.getAcceptanceCriteria().size() < 1){
                dropdown.setDisable(true);
            }

            HBox hBox = new HBox();
            HBox IhBox = new HBox();
            dropdown.setMaxWidth(5);
            hBox.getChildren().add(dropdown);
            Label textLabel = new Label();
            textLabel.setText(story.toString());

            int priority = getLocalPriority(story);
            //Check to see which image to add
            if(checkLocalRedFlag(priority, story)){ //story.getPriority())){
                bImageView.setImage(Red);
            }
            else if(story.isReady()){
                bImageView.setImage(Green);
            }
            else if(story.getAcceptanceCriteria().size() > 0 && story.getEstimate() > -1){
                bImageView.setImage(Orange);
            }

            // If we have selected to show story state highlights
            if(App.getMainController().storyStateVisible){
                IhBox.getChildren().add(bImageView);
            }


            bImageView.setOnMouseClicked(event -> {
                if(bImageView.getImage().equals(Orange)){
                    story.setReady(true);
                    bImageView.setImage(Green);
                }else if(bImageView.getImage().equals(Green)){
                    story.setReady(false);
                    bImageView.setImage(Orange);
                }

            });
            GridPane grid = buildGrid(textLabel,hBox,IhBox);
            setGraphic(grid);
        }
        else{
            setGraphic(null);
            super.updateItem(item, empty);
        }

    }

    /**
     * Builds the grid for the story cell using two HBoxes and a text label. This sets the layout
     * and size of the individual parts
     * @param textLabel - The name of the story in the cell
     * @param hBox - Contains the dropdown box
     * @param IhBox - Contains an image which may be red, orange or green
     * @return
     */
    private GridPane buildGrid(Label textLabel, HBox hBox, HBox IhBox) {
        GridPane grid = new GridPane();

        grid.setHgap(5);

        grid.getChildren().add(textLabel);
        grid.getChildren().add(hBox);
        grid.getChildren().add(IhBox);

        GridPane.setConstraints(textLabel, 1, 0);
        GridPane.setConstraints(hBox, 2, 0);
        GridPane.setConstraints(IhBox, 0, 0);
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        ColumnConstraints c3 = new ColumnConstraints();
        c1.setMaxWidth(20);
        c1.setMinWidth(20);
        c2.setMaxWidth(80);
        c2.setMinWidth(80);
        c3.setPercentWidth(20);
        this.setMaxWidth(140);
        this.setMinWidth(140);
        grid.setMaxWidth(140);
        grid.setMinWidth(140);
        grid.getColumnConstraints().addAll(c1, c2, c3);
        return grid;
    }

    /**
     * given a value it will map to the closest key in the current scale
     * @param value - value to map to key
     * @return - key String which maps closest to the given value
     */
    private String getScaleValue(double value){
        Iterator it = scale.entrySet().iterator();
        Double closest = 0.0;
        String key = null;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            if(abs((Double) pair.getValue() - value) <= abs(closest - value)){
                closest = (Double) pair.getValue();
                key = (String) pair.getKey();
            }
        }
        return key;
    }

    /**
     * A recursive search through the stories that will check for a story which depends on another
     * But has a lower priority. Used for checking the red flag property.
     * @param priority - The priority of the checking story.
     * @param story - The story we are looking at to see if it has a higher priority.
     * @return
     */
    private boolean checkLocalRedFlag(int priority, Story story){
        boolean result = false;
        if(priority < getLocalPriority(story)){
            result = true;
        }
        for(Story s : story.getDependencies()){
            if(result == false)
                result = checkLocalRedFlag(priority, s);
        }
        return result;
    }

    /**
     * Looks through the list to find the priority of a given story inside of the local dialog.
     * @param story - The story we want to know the local priority of.
     * @return
     */
    public int getLocalPriority(Story story) {
        int priority = 0;
        ObservableList<Story> allAvailableStoriesList = (ObservableList) getListView().getItems();
        for (Story s : allAvailableStoriesList) {
            if(s != null && s.equals(story)){
                break;
            }
            priority++;
        }
        return priority;
    }

}
