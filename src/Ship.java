import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ship extends Rectangle {
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
        this.setFill(Color.TRANSPARENT);
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