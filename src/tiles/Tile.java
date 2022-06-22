package tiles;

import javafx.beans.property.BooleanProperty;
import javafx.scene.shape.Rectangle;

public abstract class Tile extends Rectangle {
    private BooleanProperty shot;
    private boolean occupied;

    public void shoot() {
        shot.set(true);
    }

    public boolean getShotState() {
        return false;
    }

    public boolean getOccupied() {
        return false;
    }
}