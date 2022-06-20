package game;

import tiles.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

public class Ship {
    private int length;
    private boolean isVertical;
    private Rectangle hitBox;
    private Rectangle occupiedArea;

    public Ship(int x, int y, int length, boolean isVertical) {
        int width = isVertical ? 0 : (length - 1);
        int height = isVertical ? (length - 1) : 0;
        this.hitBox = new Rectangle(x, y, width, height);
        this.occupiedArea = getOccupiedAreaRectangle(hitBox);
        this.length = length;
        this.isVertical = isVertical;
    }

    private Rectangle getOccupiedAreaRectangle(Rectangle rectangle) {
        int x = (int)rectangle.getX() - 1;
        int y = (int)rectangle.getY() - 1;
        int width = (int)rectangle.getWidth() + 2;
        int height = (int)rectangle.getHeight() + 2;
        return new Rectangle(x, y, width, height);
    }

    List<Integer> shipContainsCoordinatesX = new ArrayList<Integer>();
    List<Integer> shipContainsCoordinatesY = new ArrayList<Integer>();
    public boolean contains(Point point) {
        int x = (int)point.getX();
        int y = (int)point.getY();
        if (shipContainsCoordinatesX.contains(x) && shipContainsCoordinatesY.contains(y)) {
            return true;
        } else if (
        (int)getMinX() <= x &&
        (int)getMinY() <= y &&
        (int)getMaxX() >= x &&
        (int)getMaxY() >= y) {
            shipContainsCoordinatesX.add(x);
            shipContainsCoordinatesY.add(y);
            return true;
        } else {
            return false;
        }
    }

    List<Integer> shipOccupiedAreaContainsCoordinatesX = new ArrayList<Integer>();
    List<Integer> shipOccupiedAreaContainsCoordinatesY = new ArrayList<Integer>();
    public boolean occupiedAreaContains(Point point) {
        int x = (int)point.getX();
        int y = (int)point.getY();
        if (shipOccupiedAreaContainsCoordinatesX.contains(x) && shipOccupiedAreaContainsCoordinatesY.contains(y)) {
            return true;
        } else if (
        (int)getMinX() <= x &&
        (int)getMinY() <= y &&
        (int)getMaxX() >= x &&
        (int)getMaxY() >= y) {
            shipOccupiedAreaContainsCoordinatesX.add(x);
            shipOccupiedAreaContainsCoordinatesY.add(y);
            return true;
        } else {
            return false;
        }
    }

    public boolean getDestroyedState(Tile[][] tileField) {
        for (int row = getMinY(); row <= getMaxY(); row++) {
            for (int col = getMinX(); col <= getMaxX(); col++) {
                if (!tileField[row][col].getShotState()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getsHitAt(Point point) {
        return contains(point);
    }

    public boolean occupies(Point point) {
        return occupiedAreaContains(point);
    }

    public boolean getIsVertical() {
        return isVertical;
    }

    public int getLength() {
        return length;
    }

    public int getMinX() {
        return (int)hitBox.getMinX();
    }

    public int getMinY() {
        return (int)hitBox.getMinY();
    }

    public int getMaxX() {
        return (int)hitBox.getMaxX();
    }

    public int getMaxY() {
        return (int)hitBox.getMaxY();
    }

    public int getOccupiedMinX() {
        return (int)occupiedArea.getMinX();
    }

    public int getOccupiedMinY() {
        return (int)occupiedArea.getMinY();
    }

    public int getOccupiedMaxX() {
        return (int)occupiedArea.getMaxX();
    }

    public int getOccupiedMaxY() {
        return (int)occupiedArea.getMaxY();
    }

    public Point getMinPoint() {
        return new Point(getMinX(), getMinY());
    }

    public Point getMaxPoint() {
        return new Point(getMaxX(), getMaxY());
    }
}