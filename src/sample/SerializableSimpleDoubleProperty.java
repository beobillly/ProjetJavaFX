package sample;

import javafx.beans.property.SimpleDoubleProperty;

import java.io.Serializable;

public class SerializableSimpleDoubleProperty extends SimpleDoubleProperty implements Serializable {
    public SerializableSimpleDoubleProperty() {
        super();
    }

    public SerializableSimpleDoubleProperty(double d) {
        super(d);
    }
}
