package seng302.group3.model.io.property_sheets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import seng302.group3.controller.App;
import seng302.group3.model.Organisation;

import java.io.IOException;

/**
 * Created by Eddy on 01-May-15.
 */


public class OrganisationSheet implements EditorSheet {


    /**
     * Created by ntr24 on 24/04/15.
     */



    private static final String errorStyle = "errorTextField";

    private VBox content = new VBox();

    @FXML TextField shortNameTextField;
    @FXML TextField longNameTextField;
    @FXML TextField descriptionTextField;
    @FXML Label errorLabel;

    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public OrganisationSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/OrganisationSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sets the fields of the editor to values fetched from an existing project
     *
     * @param object - expects an object of type Organisation to fetch field values from
     */
    @Override public void fieldConstruct(Object object) {
        if (object instanceof Organisation) {
            Organisation organisation = (Organisation) object;
            shortNameTextField.setText(organisation.getShortName());
        }
    }

    /**
     * returns the content loaded in the constructor
     *
     * @return - VBox containing the editor layout
     */
    @Override public VBox draw() {
        return content;
    }

    /**
     * checks the fields for validity and returns true if are valid. If false updates layout to show missing fields
     *
     * @return
     */
    @Override public boolean validate(Object... args) {

        boolean result = true;
        shortNameTextField.setText(shortNameTextField.getText().trim());

        if (shortNameTextField.getText().length() < 1) {
            result = false;
            shortNameTextField.getStyleClass().add(errorStyle);
        } else {
            shortNameTextField.getStyleClass().removeAll(errorStyle);
        }

        if (!result) {
            errorLabel.setText("Missing or Incorrect Fields");
        }

        return result;
    }

    /**
     * applys the field values to a project
     *
     * @param object
     */
    @Override public void apply(Object object) {
        if (object instanceof Organisation) {
            Organisation organisation = (Organisation) object;
            organisation.setShortName(shortNameTextField.getText());
            App.getMainController().getStage().setTitle(organisation.getShortName());
        }
    }

}

