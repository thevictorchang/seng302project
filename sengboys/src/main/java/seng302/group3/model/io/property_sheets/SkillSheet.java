package seng302.group3.model.io.property_sheets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import seng302.group3.model.Organisation;
import seng302.group3.model.Skill;

import java.io.IOException;

/**
 * Created by ntr24 on 24/04/15.
 */
public class SkillSheet implements EditorSheet {


    private static final String errorStyle = "errorTextField";

    private VBox content = new VBox();

    @FXML TextField nameTextField;
    @FXML TextField descriptionTextField;
    @FXML Label errorLabel;

    private boolean isDisable = false;

    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public SkillSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/SkillSheet.fxml"));
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
        if (object instanceof Skill) {
            Skill skill = (Skill) object;
            nameTextField.setText(skill.getShortName());
            if (nameTextField.getText().equals("SM") || nameTextField.getText().equals("PO")) {
                nameTextField.setDisable(true);
                isDisable = true;
            }
            descriptionTextField.setText(skill.getDescription());
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
        boolean isSM = false;
        boolean isPO = false;


        descriptionTextField.setText(descriptionTextField.getText().trim());
        if (!isDisable) {
            nameTextField.setText(nameTextField.getText().trim());
            if (nameTextField.getText().length() < 1) {
                result = false;

                nameTextField.getStyleClass().add(errorStyle);
            } else {
                if (nameTextField.getText().toLowerCase().equals("sm")) {
                    isSM = true;
                    nameTextField.getStyleClass().add(errorStyle);
                } else if (nameTextField.getText().toLowerCase().equals("po")) {
                    isPO = true;
                    nameTextField.getStyleClass().add(errorStyle);
                } else {
                    nameTextField.getStyleClass().removeAll(errorStyle);
                }

            }
        }


        if (descriptionTextField.getText().length() < 1) {
            result = false;
            descriptionTextField.getStyleClass().add(errorStyle);
        } else {
            descriptionTextField.getStyleClass().removeAll(errorStyle);
        }

        if (!result) {
            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        }
        if (isSM) {
            errorLabel.setText("Cannot create: SM skill inbuilt");
            return false;
        } else if (isPO) {
            errorLabel.setText("Cannot create: PO skill inbuilt");
            return false;
        }

        // uniqueness check
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];

            if (organisation
                .uniqueName(nameTextField.getText(), Organisation.uniqueType.UNIQUE_SKILL)) {
                nameTextField.getStyleClass().removeAll(errorStyle);
                return true;
            } else {
                //Needs cleaning up
                if (args.length > 1 && args[1] instanceof Skill) {
                    Skill skill = (Skill) args[1];
                    if (nameTextField.getText().equals(skill.getShortName())) {
                        nameTextField.getStyleClass().removeAll(errorStyle);
                        return true;
                    }
                }
                errorLabel.setText("Name non-unique");
                nameTextField.getStyleClass().add(errorStyle);
            }

        }
        return false;

    }

    /**
     * applys the field values to a project
     *
     * @param object
     */
    @Override public void apply(Object object) {
        if (object instanceof Skill) {
            Skill skill = (Skill) object;
            skill.setShortName(nameTextField.getText());
            skill.setDescription(descriptionTextField.getText());
        }
    }
}
