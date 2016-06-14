package seng302.group3.model.gui.story_workspace;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng302.group3.controller.App;
import seng302.group3.model.Backlog;
import seng302.group3.model.Organisation;
import seng302.group3.model.Story;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.navigator_items.BacklogNavigatorItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by ajd185 on 20/09/15.
 */
public class StoryWorkspaceController {

    @FXML ComboBox BacklogSelection;
    @FXML ListView StorySelection;
    @FXML ZoomableScrollPane CardsScrollPane;
    @FXML StackPane CardsStackPane;

    static enum zoom {
        IN,
        OUT
    }
    private double scrollVal = 1.0;

    static SerializableObservableList<StoryCard> Cards = new SerializableObservableList<>();

    Organisation organisation;

    public StoryWorkspaceController(){
        this.organisation = App.getMainController().currentOrganisation;
    }

    public void initialize() {

        BacklogSelection.valueProperty().addListener(BacklogSelectionChangeListener);

        if (organisation != null) {
            BacklogSelection.setItems(organisation.getBacklogs().getObservableList());
        }

        BacklogSelection.getSelectionModel().select("None");

        if (Cards.isEmpty()) {
            Canvas grid = makeGrid();
            //grid.setCursor(Cursor.CROSSHAIR);

            CardsScrollPane.setPannable(true);
            CardsStackPane.setStyle("-fx-background-color: white");
            CardsStackPane.getChildren().add(grid);
            CardsScrollPane.setCursor(Cursor.CROSSHAIR);
        }

        StorySelection.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (StorySelection.getItems().size() != 0) {

                    // The story selected
                    Story currentStorySelected = (Story) StorySelection.getSelectionModel().getSelectedItem();

                    // Creating the story card
                    if (currentStorySelected != null) {
                        StoryCard storyCard = new StoryCard(CardsScrollPane, CardsStackPane, currentStorySelected);


                        // Add the created story card to observableList Cards
                        Cards.add(storyCard);

                        // Remove the story from the list
                        StorySelection.getItems().remove(currentStorySelected);

                        // Add the card to the stackpane
                        CardsStackPane.getChildren().add(storyCard);
                        CardsScrollPane.updateScrollPane(CardsStackPane, false, null);
                        storyCard.setTranslateX(storyCard.getWidth() / 2 - CardsStackPane.getPrefWidth() / 2);
                        storyCard.setTranslateY(storyCard.getHeight() / 2 - CardsStackPane.getHeight() / 2);

                        // Get every card currently in the list of cards
                        storyCard.removeOverlap(storyCard, Cards);

                        changeRefresh();
                    }
                }
            }
        });

        CardsScrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override public void handle(ScrollEvent event) {
                if (event.isControlDown()) {
                    if (event.getDeltaY() > 0) {
                        CardsScrollPane.updateScrollPane(CardsStackPane, true, zoom.IN);
                    }
                    if (event.getDeltaY() < 0) {
                        if (CardsScrollPane.getHeight()
                            >= App.getMainController().getStage().getHeight() - 206.0) {
                            CardsScrollPane.updateScrollPane(CardsStackPane, true, zoom.OUT);
                        }
                    }
                }
            }
        });
    }

    public static SerializableObservableList<StoryCard> getCards() {
        return Cards;
    }

    private Canvas makeGrid() {
        Canvas grid = new Canvas(CardsStackPane.getPrefWidth(), CardsStackPane.getPrefHeight());
        GraphicsContext gc = grid.getGraphicsContext2D();

        gc.clearRect(0, 0, grid.getWidth(), grid.getHeight());
        gc.setStroke(Color.LIGHTGRAY);
        for (int i=0; i<grid.getWidth(); i=i+50) {
            gc.strokeLine(i, 0, i, grid.getHeight());
        }
        for (int j=0; j<grid.getHeight(); j=j+50) {
            gc.strokeLine(0, j, grid.getWidth(), j);
        }

        return grid;
    }

    ChangeListener BacklogSelectionChangeListener = (observable, oldValue, newValue) -> {

        Object currentlySelected = BacklogSelection.getSelectionModel().getSelectedItem();

        Collection<Story> toBeRemoved = new ArrayList<>();
        if (currentlySelected != null && currentlySelected instanceof BacklogNavigatorItem) {
            Backlog currentBacklog = (Backlog) ((BacklogNavigatorItem) currentlySelected).getItem();
            StorySelection.getItems().setAll(currentBacklog.getStories());
            for (StoryCard card : getCards()) {
                for (Story story : (Collection<Story>) StorySelection.getItems()) {
                    if (card.getStory().getShortName().equals(story.getShortName())) {
                        toBeRemoved.add(story);
                        toBeRemoved.add(card.getStory());
                    }
                }
            }
        }
        StorySelection.getItems().removeAll(toBeRemoved);
    };

    public static void removeCard(StoryCard storyCard) {
        Cards.remove(storyCard);
    }

    public void changeRefresh() {
        this.organisation = App.getMainController().currentOrganisation;

        if (organisation != null) {
            // Manually triggering the listener
            BacklogSelectionChangeListener.changed(BacklogSelection.valueProperty(), null,
                BacklogSelection.getSelectionModel().getSelectedItem());

            StoryWorkspaceController.getCards().stream().forEach(new Consumer<StoryCard>() {
                @Override
                public void accept(StoryCard storyCard) {
                    storyCard.refresh();
                }
            });
        }
    }

    public void refreshBacklogSelection(Backlog newBacklog) {
        BacklogSelection.setItems(organisation.getBacklogs().getObservableList());

        BacklogSelection.setValue("None");
        StorySelection.getItems().clear();
    }
}
