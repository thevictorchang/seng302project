package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Skill;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by ntr24 on 4/05/15.
 */
public class SkillNavigatorItem implements NavigatorItem {

    Skill skill;
    private int depth;

    public SkillNavigatorItem(Skill skill) {
        this.skill = skill;
    }

    @Override public String toString() {
        return skill.getShortName();
    }

    @Override public void setNavigatorDepth(int pos) {
        depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {
    }

    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return null;
    }

    @Override public Skill getItem() {
        return skill;
    }

    @Override public String getName() {
        return skill.getShortName();
    }

    @Override public String getDescription() {
        String description = skill.getPaneString();
        return description;
    }

    @Override public Node getGrid() {
        return skill.getOverviewPaneGrid();
    }
}
