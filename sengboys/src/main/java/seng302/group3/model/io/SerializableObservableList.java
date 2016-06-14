package seng302.group3.model.io;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng302.group3.model.*;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by ntr24 on 7/05/15.
 */
public class SerializableObservableList<T> extends ArrayList<T> implements Serializable {

    private transient ObservableList<NavigatorItem> observableList =
        FXCollections.observableArrayList();

    @Override public boolean add(T t) {
        super.add(t);
        if(observableList != null)
            observableList.add(createNavItem(t));
        return true;
    }


    @Override public void clear() {
        super.clear();
        if(observableList != null)
            observableList.clear();
    }


    @Override public boolean remove(Object o) {
        super.remove(o);
        resetObservableList();
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        super.addAll(c);
        resetObservableList();
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        super.addAll(index, c);
        resetObservableList();
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        super.removeAll(c);
        resetObservableList();
        return true;
    }

    /**
     * set the items of this collection
     *
     * @param items - the new items to be set
     */
    public void setItems(Collection<T> items) {
        this.clear();
        items.forEach(this::add);
    }

    /**
     * resets the observable list with the items in the arraylist
     */
    private void resetObservableList() {
        if(observableList == null)
            this.observableList = FXCollections.observableArrayList();

        this.observableList.clear();
        this.observableList
            .addAll(this.stream().map(this::createNavItem).collect(Collectors.toList()));
    }

    /**
     * Gets the navigator item for the type of object being added to the collection
     *
     * @param o - the object that is being added to the arraylist. Need this for the navigator item
     * @return - the navigator item that should appear in the list
     */
    private NavigatorItem createNavItem(Object o) {
        if (o instanceof Project)
            return new ProjectNavigatorItem((Project) o);
        else if (o instanceof Release)
            return new ReleaseNavigatorItem((Release) o);
        else if (o instanceof Team)
            return new TeamNavigatorItem((Team) o);
        else if (o instanceof Person)
            return new PersonNavigatorItem((Person) o);
        else if (o instanceof Skill)
            return new SkillNavigatorItem((Skill) o);
        else if (o instanceof Backlog)
            return new BacklogNavigatorItem((Backlog) o);
        else if (o instanceof Story)
            return new StoryNavigatorItem((Story) o);
        else if (o instanceof Sprint)
            return new SprintNavigatorItem((Sprint) o);
        else if (o instanceof Task)
            return new TaskNavigatorItem((Task) o);
        else if (o instanceof LoggedTime)
            return new LoggedTimeNavigatorItem((LoggedTime) o);
        else if (o instanceof NavigatorItem)
            return (NavigatorItem) o;
        else if (o instanceof TimePeriod)
            return new TimePeriodNavigatorItem((TimePeriod) o);
        else if (o instanceof Tag)
            return new TagNavigatorItem((Tag) o);
        else
            return null;
    }


    public ObservableList<NavigatorItem> getObservableList() {
        // make sure observable list isnt null
        if(observableList == null)
            resetObservableList();

        return observableList;
    }

    /**
     * Provides a hook into the serialized reader and will also reinitialize the observable lists
     *
     * @param in - the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
