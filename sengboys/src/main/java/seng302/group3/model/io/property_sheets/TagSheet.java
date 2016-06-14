package seng302.group3.model.io.property_sheets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import seng302.group3.controller.App;
import seng302.group3.model.*;
import seng302.group3.model.io.Dialogs;
import seng302.group3.model.io.Editor;
import seng302.group3.model.io.SerializableObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vch51 on 15/09/15.
 */
public class TagSheet implements EditorSheet {

    @FXML public ListView<Tag> currentTagListView;
    @FXML public ListView<Tag> availableTagListView;
    @FXML public Button buttonNewTag;
    @FXML public Button buttonEditTag;
    @FXML public Button buttonDeleteTag;
    @FXML public Button buttonAddTag;
    @FXML public Button buttonRemoveTag;

    private VBox content = new VBox();

    private Element elementToEdit;
    private Organisation organisation = App.getMainController().currentOrganisation;

    public TagSheet(Object object) {
        elementToEdit = (Element) object;
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/EditorSheets/TagSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);

            currentTagListView.getItems().clear();
            currentTagListView.getItems().addAll(elementToEdit.getTags());

            availableTagListView.getItems().clear();
            availableTagListView.getItems().addAll(organisation.getTags());

            for (Tag tag : currentTagListView.getItems()) {
                availableTagListView.getItems().remove(tag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Element getElementToEdit() {
        return elementToEdit;
    }

    public void setElementToEdit(Element elementToEdit) {
        this.elementToEdit = elementToEdit;
    }

    public void fieldConstruct(Object object){

    }

    public void buttonNewTag(ActionEvent actionEvent) {
        Editor e = new Editor();
        Tag newTag = new Tag();
        if (currentTagListView.getItems().size() >= 7) {
            new Dialogs().infoDialog("Elements cannot have more than 7 tags\nAdding to available tags",
                "Tag limit reached");
            e.NewTagFromTagSheet(newTag, organisation, this.elementToEdit);
            if(newTag.getShortName() != null){
                availableTagListView.getItems().add(newTag);
            }
        }else{
            e.NewTagFromTagSheet(newTag, organisation, this.elementToEdit);
            if(newTag.getShortName() != null){
                currentTagListView.getItems().add(newTag);
            }
        }
    }

    public void buttonEditTag(ActionEvent actionEvent) {
        Tag selectedTag = currentTagListView.getSelectionModel().getSelectedItem();
        if (!(selectedTag == null)) {
            currentTagListView.getItems().remove(selectedTag);
            Editor e = new Editor();
            e.EditTag(App.getMainController().currentOrganisation, selectedTag);
            currentTagListView.getItems().add(selectedTag);
        }
    }

    //TODO: need to make it so that deleting a tag from an element will also remove it from every element with that tag
    //have removed button for now, as i think the user shouldn't be able to delete a tag from the entire system
    //when they're in the tag dialog for another element.
    public void buttonDeleteTag(ActionEvent actionEvent) {
        Tag selectedTag = availableTagListView.getSelectionModel().getSelectedItem();
        if ((selectedTag == null)) {
            selectedTag = currentTagListView.getSelectionModel().getSelectedItem();
        }
        if (!(selectedTag == null)) {
            new Delete(selectedTag, organisation);
        }
    }

    public void buttonAddTag(ActionEvent actionEvent) {
        if (currentTagListView.getItems().size() >= 7) {
            new Dialogs().infoDialog("Elements cannot have more than 7 tags",
                    "Tag limit reached");
        } else {
            Tag selectedTag = availableTagListView.getSelectionModel().getSelectedItem();
            if (!(selectedTag == null)) {
                currentTagListView.getItems().add(selectedTag);
                availableTagListView.getItems().remove(selectedTag);

            }
        }

    }

    public void buttonRemoveTag(ActionEvent actionEvent) {
        Tag selectedTag = currentTagListView.getSelectionModel().getSelectedItem();
        if (!(selectedTag == null)) {
            availableTagListView.getItems().add(selectedTag);
            currentTagListView.getItems().remove(selectedTag);
        }

    }

    public VBox draw(){
        return content;
    }

    public boolean validate(Object... args){
        return true;

    }

    public void apply(Object object){
        elementToEdit.setTags(currentTagListView.getItems());
        for (Tag tag : currentTagListView.getItems()) {
            if (!(tag.getElements().contains(elementToEdit))) {
                tag.addElement(elementToEdit);
            }
        }
        for (Tag tag : availableTagListView.getItems()) {
            if (tag.getElements().contains(elementToEdit)) {
                tag.removeElement(elementToEdit);
            }
        }
    }

    /**
     * used to get a new editor so that it can be mocked with Mockito
     * @return
     */
    public Editor makeEditor(){
        return new Editor();
    }
}
