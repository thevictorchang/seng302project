package seng302.group3.model.gui.story_workspace;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seng302.group3.controller.App;
import seng302.group3.model.Story;
import seng302.group3.model.io.Editor;
import seng302.group3.model.io.SerializableObservableList;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Andrew on 20/09/2015.
 */
public class StoryCard extends VBox {

    double cursorPosX, cursorPosY;
    double cardTranslateX, cardTranslateY;
    double cardWidth = 150, cardHeight = 100;
    ZoomableScrollPane CardsScrollPane;
    StackPane CardsStackPane;
    Story story;
    StoryCard card = null;
    MenuItem edit = new MenuItem("Edit");
    MenuItem remove = new MenuItem("Remove");
    ContextMenu context = new ContextMenu(edit, remove);
    Label label = new Label();
    Label estimateLabel = new Label();

    /**
     * Constructor for the card
     */
    public StoryCard(ZoomableScrollPane CardsScrollPane, StackPane CardsStackPane, Story story) {

        // Setting JavaFX elements
        this.CardsScrollPane = CardsScrollPane;
        this.CardsStackPane = CardsStackPane;
        this.story = story;

        // Creating label
        label.setText(story.getShortName() + "\n" + story.getFullName());
        label.setStyle("-fx-font-size: 15");
        this.getChildren().add(label);

        estimateLabel.setText(story.getBacklog().scaleToString(story.getEstimate()));
        estimateLabel.setStyle("-fx-font-size: 20");
        this.getChildren().add(estimateLabel);

        // Setting up the card preferences
        this.setMaxSize(cardWidth, cardHeight);
        this.setPadding(new Insets(2,0,0,5));
        this.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, grey, 10, 0.1, 0, 0);");

        // Setting handlers
        this.setOnMousePressed(cardOnMousePressedEventHandler);
        this.setOnMouseDragged(cardOnMouseDraggedEventHandler);
        this.setOnMouseReleased(cardOnMouseReleasedEventHandler);
        this.setOnMouseEntered(cardOnMouseEnteredEventHandler);

        //Context Menu Listeners
        edit.setOnAction(cardOnEditButtonPressed);
        remove.setOnAction(cardOnRemoveButtonPressed);
    }

    /**
     * Getter for story
     * @return
     */
    public Story getStory() {
        return story;
    }

    /**
     * Button action event for remove
     */
    EventHandler<ActionEvent> cardOnRemoveButtonPressed = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            StoryWorkspaceController.removeCard(card);
            CardsStackPane.getChildren().remove(card);
            StoryWorkspace.getInstance().refresh();
        }
    };

    /**
     * Button action event for edit
     */
    EventHandler<ActionEvent> cardOnEditButtonPressed = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Editor e = new Editor();
            e.edit(App.getMainController().currentOrganisation, card.getStory());
        }
    };

    /**
     * Mouse event handler on press
     */
    EventHandler<MouseEvent> cardOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            card = (StoryCard) event.getSource();
            context.hide();
            if (event.getClickCount() == 2) {
                if (event.isPrimaryButtonDown()) {
                    Editor e = new Editor();
                    e.edit(App.getMainController().currentOrganisation, card.getStory());
                }
            }
            else {
                if (event.isPrimaryButtonDown()) {
                    cursorPosX = event.getSceneX();
                    cursorPosY = event.getSceneY();
                    cardTranslateX = ((StoryCard) (event.getSource())).getTranslateX();
                    cardTranslateY = ((StoryCard) (event.getSource())).getTranslateY();

                    CardsScrollPane.setPannable(false);
                    ((StoryCard) (event.getSource())).toFront();
                    //getScene().setCursor(Cursor.NONE);
                }
            }
            if (event.isSecondaryButtonDown()) {
                context.show(card, event.getScreenX(), event.getScreenY());
            }
        }
    };

    /**
     * Mouse event handler on drag
     */
    EventHandler<MouseEvent> cardOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.isPrimaryButtonDown()) {
                // Calculation for mouse movements
                double offsetX = event.getSceneX() - cursorPosX;
                double offsetY = event.getSceneY() - cursorPosY;
                double newTranslateX = cardTranslateX + offsetX;
                double newTranslateY = cardTranslateY + offsetY;

                // Applying boundaries
                if (newTranslateX < -(CardsStackPane.getPrefWidth() / 2 - cardWidth / 2)) {
                    newTranslateX = -(CardsStackPane.getPrefWidth() / 2 - cardWidth / 2);
                }
                if (newTranslateX > (CardsStackPane.getPrefWidth() / 2 - cardWidth / 2)) {
                    newTranslateX = (CardsStackPane.getPrefWidth() / 2 - cardWidth / 2);
                }
                if (newTranslateY < -(CardsStackPane.getPrefHeight() / 2 - cardHeight / 2)) {
                    newTranslateY = -(CardsStackPane.getPrefHeight() / 2 - cardHeight / 2);
                }
                if (newTranslateY > (CardsStackPane.getPrefHeight() / 2 - cardHeight / 2)) {
                    newTranslateY = (CardsStackPane.getPrefHeight() / 2 - cardHeight / 2);
                }

                // Setting the new position
                ((StoryCard) (event.getSource())).setTranslateX(newTranslateX);
                ((StoryCard) (event.getSource())).setTranslateY(newTranslateY);
                //getScene().setCursor(Cursor.NONE);
            }
        }
    };

    /**
     * Mouse event handler on release
     */
    EventHandler<MouseEvent> cardOnMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            // The card that is being handled (this card)
            StoryCard frontCard = (StoryCard) (event.getSource());

            // Remove the overlap of cards underneath
            removeOverlap(frontCard, StoryWorkspaceController.getCards());

            // Set the scroll pane to pannable
            CardsScrollPane.setPannable(true);
        }
    };

    EventHandler<MouseEvent> cardOnMouseEnteredEventHandler = new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent event) {
            //getScene().setCursor(Cursor.CROSSHAIR);
        }
    };

    /**
     * Takes two cards, and if the frontCard overlaps with the backCard, the frontCard will snap to stack at the bottom right
     * corner rather than completely obscuring another card.
     * @param frontCard
     * @param backCard
     */
    public void removeOverlap(StoryCard frontCard, SerializableObservableList<StoryCard> cards) {

        for (StoryCard backCard : cards) {

            if (backCard != frontCard) {

                // If the two cards overlap...
                if ((Math.abs(frontCard.getTranslateX() - backCard.getTranslateX()) < (frontCard.cardWidth + backCard.cardWidth) / 8)
                        && (Math.abs(frontCard.getTranslateY() - backCard.getTranslateY()) < (frontCard.cardHeight + backCard.cardHeight) / 8)) {

                    // Snap them to stack appropriately
                    frontCard.setTranslateX(backCard.getTranslateX() + (frontCard.cardWidth + backCard.cardWidth) / 8);
                    frontCard.setTranslateY(backCard.getTranslateY() + (frontCard.cardHeight + backCard.cardHeight) / 8);

                    // Apply boundaries
                    if (frontCard.getTranslateX() > (CardsStackPane.getPrefWidth() / 2 - cardWidth / 2)) {
                        frontCard.setTranslateX(CardsStackPane.getPrefWidth() / 2 - cardWidth / 2);
                    }
                    if (frontCard.getTranslateY() > (CardsStackPane.getPrefHeight() / 2 - cardHeight / 2)) {
                        frontCard.setTranslateY(CardsStackPane.getPrefHeight() / 2 - cardHeight / 2);
                    }

                    removeOverlap(frontCard, cards);
                }
            }
        }
    }

    public void refresh() {
        label.setText(story.getShortName() + "\n" + story.getFullName());
    }
}
