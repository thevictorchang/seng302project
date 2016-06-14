package seng302.group3.model.io.property_sheets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import seng302.group3.model.Organisation;
import seng302.group3.model.Tag;

import java.io.IOException;


/**
 * Created by vch51 on 9/09/15.
 */
public class AddTagSheet implements EditorSheet{

    @FXML public TextField nameTextField;
    @FXML public TextField descriptionTextField;
    @FXML public ComboBox colourComboBox;
    @FXML public Label errorLabel;

    private static final String boxErrorStyle = "errorBox";
    private static final String errorStyle = "errorTextField";
    private static final String errorStyleButton = "errorButton";

    private VBox content = new VBox();

    /**
     * Initialises the data into the separated lists. Loads the GUI layout.
     */
    public AddTagSheet() {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/EditorSheets/AddTagSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.colourComboBox.getItems().add("No Colour");
        this.colourComboBox.getItems().add("Red");
        this.colourComboBox.getItems().add("Orange");
        this.colourComboBox.getItems().add("Yellow");
        this.colourComboBox.getItems().add("Green");
        this.colourComboBox.getItems().add("Blue");
        this.colourComboBox.getItems().add("Purple");
        colourComboBox.getSelectionModel().select("No Colour");

    }

    /**
     * If the task is being edited we need to fill out its data.
     */
    @Override public void fieldConstruct(Object object) {
        if (object instanceof Tag) {
            Tag tag = (Tag) object;
            nameTextField.setText(tag.getShortName());
            descriptionTextField.setText(tag.getDescription());
            colourComboBox.setValue(tag.getColour());
        }

    }


    @Override public VBox draw(){
        return content;
    }

    /**
     * Checks the data fields to see if they are correct otherwise lets the user know and does not
     * proceed.
     * @param args
     * @return
     */
    @Override public boolean validate(Object... args){

        boolean result = true;
        nameTextField.setText(nameTextField.getText().trim());

        if (nameTextField.getText().length() < 1) {
            result = false;
            nameTextField.getStyleClass().add(errorStyle);
        } else {
            nameTextField.getStyleClass().removeAll(errorStyle);
        }
        if(colourComboBox.getSelectionModel().getSelectedItem().equals("No Colour")){
            colourComboBox.getStyleClass().add(errorStyle);
            result = false;
        }else{
            colourComboBox.getStyleClass().removeAll(errorStyle);
        }

        if (result)
            errorLabel.setText("");
        else {
            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        }


        // uniqueness check
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];

            if (organisation
                    .uniqueName(nameTextField.getText(), Organisation.uniqueType.UNIQUE_TAG)) {
                nameTextField.getStyleClass().removeAll(errorStyle);
                return true;
            } else {
                if (args.length > 1 && args[1] instanceof Tag) {
                    Tag tag = (Tag) args[1];
                    if (nameTextField.getText().equals(tag.getShortName())) {
                        nameTextField.getStyleClass().removeAll(errorStyle);
                        return true;
                    }
                }
                errorLabel.setText("Short name non-unique");
                nameTextField.getStyleClass().add(errorStyle);
            }

        }
        return false;
    }

    /**
     * Applies the gui fields the the correct object in the data model.
     * @param object
     */
    @Override public void apply(Object object){
        if (object instanceof Tag) {
            Tag tag = (Tag) object;
            tag.setShortName(nameTextField.getText());
            tag.setDescription(descriptionTextField.getText());
            tag.setColour((String) colourComboBox.getSelectionModel().getSelectedItem());
        }

    }
}
