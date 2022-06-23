import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {
    public static boolean isClass(Node node) {
        return node.getClass().getName().equals(Tile.class.getName());
    }

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
        if (getShotState()) {
            return;
        }
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
        setStyle("-fx-fill: rgba(255, 255, 255, 0.5);");
        lockedIn = true;
    }

    public void unlock() {
        lockedIn = false;
        setFill(Color.TRANSPARENT);
    }

    public void hover() {
        setStyle("-fx-fill: rgba(255, 255, 255, 0.25);");
    }

    public void unhover() {
        setStyle("-fx-fill: rgba(255, 255, 255, 0);");
    }

    private boolean getLockedInState() {
        return lockedIn;
    }

    public boolean getShotState() {
        return shot;
    }

    public boolean getOccupied() {
        return occupied;
    }
}