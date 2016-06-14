package seng302.group3.model.io.property_sheets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import seng302.group3.model.Organisation;
import seng302.group3.model.Project;
import seng302.group3.model.Release;
import seng302.group3.model.search.FilterUtil;

import java.io.IOException;


public class ReleaseSheet implements EditorSheet {


    private VBox content = new VBox();

    @FXML TextField shortNameTextField;
    @FXML TextField descriptionTextField;
    @FXML DatePicker releaseDatePicker;
    @FXML ComboBox projectChoice;
    @FXML Label projectErrorLabel;
    @FXML Label errorLabel;
    @FXML Label dateErrorLabel;


    /**
     * constructor, loads the fxml and sets the controller to this class
     */
    public ReleaseSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/ReleaseSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static final String errorStyle = "errorTextField";

    /**
     * used when editing a release, all the attributes from the release are put into the fields for editing
     *
     * @param object, a release
     */
    public void fieldConstruct(Object object) {
        if (object instanceof Release) {
            Release release = (Release) object;

            shortNameTextField.setText(release.getShortName());
            descriptionTextField.setText(release.getDescription());
            releaseDatePicker.setValue(release.getReleaseDate());

            projectChoice.setValue(release.getProject());
        }
    }

    /**
     * sets up the dialog for first time use (not editing)
     *
     * @param organisation
     */
    public void projectConstruct(Organisation organisation) {
        String noneChoice = "None";
        FilterUtil.autoCompleteComboBox(projectChoice, FilterUtil.AutoCompleteMode.CONTAINING);
        projectChoice.getItems().addAll(organisation.getProjects());
        projectChoice.getItems().add(noneChoice);

        if (shortNameTextField.getText().equals("")) {
            projectChoice.setValue(noneChoice);
        }


    }

    /**
     * Prepares the VBox
     * @return
     */
    @Override public VBox draw() {
        return content;
    }

    @Override
    /**
     * checks that all the values in the fields are all good:
     * Text fields must have something in them
     * Date selected must not be before today's date
     * Project selection must not be "None" or non selected
     */
    public boolean validate(Object... args) {

        boolean result = true;
        shortNameTextField.setText(shortNameTextField.getText().toString().trim());
        descriptionTextField.setText(descriptionTextField.getText().toString().trim());

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

        //after checking the description, checks the date...
        if (releaseDatePicker.getValue() == null) {
            result = false;
            releaseDatePicker.getStyleClass().add(errorStyle);
            dateErrorLabel.setText("Please select a valid date");

        } else {
            releaseDatePicker.getStyleClass().removeAll(errorStyle);
            dateErrorLabel.setText("");
        }

        //check a project choice (that isn't None) has been selected
        if ((FilterUtil.getComboBoxValue(projectChoice)) == null || (FilterUtil.getComboBoxValue(projectChoice) == "None")) {
            result = false;
            projectChoice.getStyleClass().add(errorStyle);
            projectErrorLabel.setText("Release must have a project");
        } else {
            projectChoice.getStyleClass().removeAll(errorStyle);
            projectErrorLabel.setText("");
        }

        if (!result) {

            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        }


        // uniqueness check
        if (args.length > 0 && args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];

            if (organisation
                .uniqueName(shortNameTextField.getText(), Organisation.uniqueType.UNIQUE_RELEASE)) {
                shortNameTextField.getStyleClass().removeAll(errorStyle);
                return true;
            } else {
                //Needs cleaning up
                if (args.length > 1 && args[1] instanceof Release) {
                    Release release = (Release) args[1];
                    if (shortNameTextField.getText().equals(release.getShortName())) {
                        shortNameTextField.getStyleClass().removeAll(errorStyle);
                        return true;
                    }
                }
                errorLabel.setText("Short name non-unique");
                shortNameTextField.getStyleClass().add(errorStyle);
            }

        }
        return false;
    }

    @Override
    /** `
     *Sets all the values from the fields to a created Release,also gets the project selected and adds thisrelease to
     * it's collection of releases
     */ public void apply(Object object) {
        if (object instanceof Release) {
            Release release = (Release) object;
            Project selectedProject = (Project) FilterUtil.getComboBoxValue(projectChoice);
            release.setShortName(shortNameTextField.getText());
            release.setDescription(descriptionTextField.getText());
            release.setReleaseDate(releaseDatePicker.getValue());
            if (release.getProject() != selectedProject) {
                release.setProject(selectedProject);
                selectedProject.addRelease(release);
            }

            /*if(projectChoice.getSelectionModel().getSelectedItem() != "None"){
                if(projectChoice.getSelectionModel().getSelectedItem() instanceof Project){
                    Project project = (Project) projectChoice.getSelectionModel().getSelectedItem();
                    release.setProject(project);
                    project.addRelease(release);

                }
            }*/


        }
    }
}
