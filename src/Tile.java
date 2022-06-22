import javafx.beans.property.BooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {
    private boolean shot;
    private boolean occupied;
    private boolean lockedIn;
    Ship occupyingShip;

    public Tile(double width, double height) {
        shot = false;
        occupied = false;
        setWidth(width);
        setHeight(height);
        getStyleClass().add("tile");
    }

    public Tile(Ship occupyingShip) {
        shot = false;
        occupied = false;
        lockedIn = false;
        occupie(occupyingShip);
    }

    public void shoot() {
        unlock();
        if (occupied) {
            setFill(getHitImagePattern());
        } else {
            setFill(getMissedImagePattern());
        }
        this.shot = true;
    }

    private ImagePattern getMissedImagePattern() {
        Image image = new Image("missed.png");
        ImagePattern pattern = new ImagePattern(image);
        return pattern;
    }

    private ImagePattern getHitImagePattern() {
        Image image = new Image("debris.png");
        ImagePattern pattern = new ImagePattern(image);
        return pattern;
    }

    public void occupie(Ship occupyingShip) {
        this.occupyingShip = occupyingShip;
        occupied = true;
        setFill(Color.RED);
    }

    public void lockIn() {
        mark("-fx-fill: rgba(255, 255, 255, 0.5);");
        lockedIn = true;
    }

    public void unlock() {
        lockedIn = false;
        mark("-fx-fill: rgba(255, 255, 255, 0);");
    }

    public void hover() {
        mark("-fx-fill: rgba(255, 255, 255, 0.25);");
    }

    public void unhover() {
        mark("-fx-fill: rgba(255, 255, 255, 0);");
    }

    private void mark(String css) {
        if (getShotState()) {
            System.out.println("Tile cannot be marked because it is shot.");
        } else if (getLockedInState()) {
            System.out.println("Tile cannot be marked because it is locked in");
        } else {
            setStyle(css);
        }
    }

    private boolean getLockedInState() {
        return lockedIn;
    }

    private void mark(Image image) {
        ImagePattern pattern = new ImagePattern(image);
        mark(pattern);
    }

    public void mark(Paint paint) {
        if (getShotState()) {
            System.out.println("Tile cannot be marked because it is shot.");
        } else if (getLockedInState()) {
            System.out.println("Tile cannot be marked because it is locked in");
        } else {
            setFill(paint);
        }
    }

    public boolean getShotState() {
        return shot;
    }

    public boolean getOccupied() {
        return occupied;
    }
}