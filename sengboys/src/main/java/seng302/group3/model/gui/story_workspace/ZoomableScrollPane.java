package seng302.group3.model.gui.story_workspace;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Scale;

/**
 * Created by jwc78 on 24/09/15.
 */
public class ZoomableScrollPane extends ScrollPane {

    Group zoomGroup;
    Scale scaleTransform;
    Node content;
    double scaleValue = 1.0;
    public ZoomableScrollPane() {
        zoomGroup = new Group();
    }

    public ZoomableScrollPane(Node content) {
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(content);
        setContent(contentGroup);
        scaleTransform = new Scale(scaleValue, scaleValue, 0, 0);
        zoomGroup.getTransforms().add(scaleTransform);
    }

    public void updateScrollPane(Node nodeContent, boolean isZoom, StoryWorkspaceController.zoom zoom) {
        Group contentGroup = new Group();
        zoomGroup.getChildren().clear();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(nodeContent);
        setContent(contentGroup);
        if (isZoom){

            if (zoom == StoryWorkspaceController.zoom.IN){
                scaleTransform = new Scale(scaleValue*1.01, scaleValue*1.01, 0, 0);
            }
            else {
                scaleTransform = new Scale(scaleValue/1.01, scaleValue/1.01, 0, 0);
            }
            zoomGroup.getTransforms().add(scaleTransform);
        }
    }

}
