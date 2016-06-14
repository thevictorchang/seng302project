package seng302.group3.model;


import seng302.group3.model.io.SerializableObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by vch51 on 22/05/15.
 * <p>
 * Class that all element classes will inherit from (extend), implements element interface and serializable
 */
abstract public class Element implements ElementInterface, Serializable {

    private static final long serialVersionUID = -2462888695017991925L;

    private SerializableObservableList<Tag> tags = new SerializableObservableList<>();

    public LocalDateTime dateCreated = LocalDateTime.now(); //

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void setTags(Collection<Tag> collectionOfTags) {
        tags.setItems(collectionOfTags);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public SerializableObservableList<Tag> getTags() {
        return tags;
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.tags == null)
            tags = new SerializableObservableList<>();

    }
}
