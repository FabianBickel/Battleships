import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ship extends Rectangle {
    public static ArrayList<Point2D> getValidPoints(PlayingField playingField, ShipShape shipShape) {
        int columnCount = playingField.getColumnCount();
        int rowCount = playingField.getRowCount();
        int width = shipShape.getWidth();
        int height = shipShape.getHeight();
        for (Node node : playingField.getChildren()) {
            if (Tile.isClass(node)) {
                Tile tile = (Tile) node;
                int col = PlayingField.getColumnIndex(tile);
                int row = PlayingField.getRowIndex(tile);
                boolean occupied = tile.getOccupied();
            }
        }
        ArrayList<Point2D> validPoints = new ArrayList<Point2D>();
        int maxCol = columnCount - width + 1;
        int maxRow = rowCount - height + 1;
        for (int col = 0; col < maxCol; col++) {
            for (int row = 0; row < maxRow; row++) {
                
            }
        }
        return validPoints;
    }

    private Tile[] tilesOccupied;
    private boolean destroyed;

    public Ship(Tile[] tilesOccupied) {
        destroyed = false;
        double shipWidth = 0;
        double shipHeight = 0;
        int lastColumn = -1;
        int lastRow = -1;
        for (Tile tile : tilesOccupied) {
            tile.occupie(this);
            int column = PlayingField.getColumnIndex(tile);
            int row = PlayingField.getRowIndex(tile);
            if (column != lastColumn) {
                shipWidth += tile.getWidth();
            }
            if (row != lastRow) {
                shipHeight += tile.getHeight();
            }
            lastColumn = column;
            lastRow = row;
        }
        this.tilesOccupied = tilesOccupied;
        this.setWidth(shipWidth);
        this.setHeight(shipHeight);
        // this.setFill(Color.TRANSPARENT);
    }

    public boolean getDestroyedState() {
        if (destroyed) {
            return destroyed;
        } else {
            for (Tile tile : tilesOccupied) {
                if (tile.getShotState() == false)
                    return false;
            }
            destroyed = true;
            setFill(Color.RED);
            return true;
        }
    }

    public double getDestructionProgressPercent() {
        if (destroyed) {
            return 1;
        } else {
            int hitCount = 0;
            for (Tile tile : tilesOccupied) {
                if (tile.getShotState())
                    hitCount++;
            }
            if (hitCount == 0) {
                return 0;
            } else {
                double destructionProgress = getLength() / hitCount;
                System.out.println("DestructionProgress: " + destructionProgress);
                return destructionProgress;
            }
        }
    }

    public int getLength() {
        return tilesOccupied.length;
    }

    public boolean contains(Tile tile) {
        for (Tile tileOccupied : tilesOccupied) {
            if (tileOccupied == tile) {
                return true;
            }
        }
        return false;
    }
}