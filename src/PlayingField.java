import java.util.ArrayList;
import java.util.Random;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class PlayingField{
    public static int getColumnIndex(Tile tile) {
        return GridPane.getColumnIndex(tile);
    }

    public static int getRowIndex(Tile tile) {
        return GridPane.getRowIndex(tile);
    }

    private final int COLUMN_COUNT;
    private final int ROW_COUNT;
    private final double TILE_WIDTH;
    private final double TILE_HEIGHT;

    private GridPane gridPane;
    private ArrayList<Ship> ships;
    private Tile lockedInTile;
    private int lockedInTileX = -1;
    private int lockedInTileY = -1;

    public PlayingField(double width, double height, int columnCount, int rowCount) {
        this.COLUMN_COUNT = columnCount;
        this.ROW_COUNT = rowCount;
        this.TILE_WIDTH = width / columnCount;
        this.TILE_HEIGHT = height / rowCount;
        this.gridPane = getGridPane();
        this.ships = new ArrayList<Ship>();
        fillGridPaneWithTiles();
    }

    private GridPane getGridPane() {
        if (this.gridPane == null) {
            GridPane gridPane = new GridPane();
            gridPane.setGridLinesVisible(true);
            gridPane.setCursor(Cursor.CROSSHAIR);
            gridPane.setAlignment(Pos.TOP_LEFT);
            return gridPane;
        } else {
            return gridPane;
        }

    }

    private void fillGridPaneWithTiles() {
        for (int col = 0; col < this.COLUMN_COUNT; col++) {
            for (int row = 0; row < this.ROW_COUNT; row++) {
                Tile tile = getNewTileWithEvHas(TILE_WIDTH, TILE_HEIGHT);
                gridPane.add(tile, col, row);
            }
        }
    }
    
    private Tile getNewTileWithEvHas(double width, double height) {
        Tile tile = new Tile(width, height);
        tile.setOnMouseEntered(getEvHaTileHighlight());
        tile.setOnMouseExited(getEvHaTileReset());
        tile.setOnMouseClicked(getEvHaTileInteractWith());
        return tile;
    }

    public void addShip(ShipShape shipShape) {

        ArrayList<Point2D> validPoints = Ship.getValidPoints(this, shipShape);
        Random random = new Random();
        Point2D randomValidPoint = validPoints.get(random.nextInt(validPoints.size()));
        
    }

    public void addShip(int columnIndex, int rowIndex, int colSpan, int rowSpan) {
        // if (!shipPositionValid(columnIndex, rowIndex, colSpan, rowSpan)) {
        //     throw new IllegalArgumentException("Can't place a ship there. There is another ship too close to it.");
        // }
        Tile[] tilesAffected = new Tile[colSpan * rowSpan];
        int maxColumnIndex = columnIndex + colSpan;
        int maxRowIndex = rowIndex + rowSpan;
        int i = 0;
        for (int column = columnIndex; column < maxColumnIndex; column++) {
            for (int row = rowIndex; row < maxRowIndex; row++) {
                for (Node node : gridPane.getChildren()) {
                    if (node.getClass().getName().equals(Tile.class.getName())) {
                        Tile tile = (Tile) node;
                        int childColumn = GridPane.getColumnIndex(tile);
                        int childRow = GridPane.getRowIndex(tile);
                        if (childColumn == column && childRow == row) {
                            tilesAffected[i++] = (Tile)tile;
                        }
                    }
                }
            }
        }
        Ship ship = new Ship(tilesAffected);
        gridPane.add(ship, columnIndex, rowIndex, colSpan, rowSpan);
        ships.add(ship);
        ship.toBack();
    }
    
    private EventHandler<MouseEvent> getEvHaTileHighlight() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Tile source = (Tile) event.getSource();
                if (lockedInTile != source) {
                    source.hover();
                }
            }
        };
    }

    private EventHandler<MouseEvent> getEvHaTileReset() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Tile source = (Tile) event.getSource();
                if (lockedInTile != source) {
                    source.unhover();
                }
            }
        };
    }

    private EventHandler<MouseEvent> getEvHaTileInteractWith() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Tile source = (Tile) event.getSource();
                int column = (int) (GridPane.getColumnIndex(source));
                int row = (int) (GridPane.getRowIndex(source));
                if (event.getClickCount() == 1) {
                    unlockLockedInTile();
                    if (!(lockedInTileX == column && lockedInTileY == row)) {
                        lockInTile(source);
                    }
                } else if (event.getClickCount() == 2){
                    unlockLockedInTile();
                    if (lockedInTile != source) {
                        lockInTile(source);
                        shootLockedInTile();
                    } else {
                        source.shoot();
                    }
                }
            }
        };
    }

    private void lockInTile(Tile tile) {
        int x = (int) (GridPane.getColumnIndex(tile));
        int y = (int) (GridPane.getRowIndex(tile));
        lockedInTile = tile;
        lockedInTileX = x;
        lockedInTileY = y;
        tile.lockIn();
    }

    private void unlockLockedInTile() {
        if (lockedInTile != null) {
            lockedInTile.unlock();
            lockedInTile = null;
            lockedInTileX = -1;
            lockedInTileY = -1;
        }
    }

    public void shootLockedInTile() {
        if (lockedInTile != null) {
            Tile tile = lockedInTile;
            unlockLockedInTile();
            tile.shoot();
            for (Node node : gridPane.getChildren()) {
                if (node.getClass().getName().equals(Ship.class.getName())) {
                    Ship ship = (Ship) node;
                    if (ship.contains(tile)) {
                        System.out.println("Ship destroyed?" + ship.getDestroyedState());
                    }
                }
            }
        }
    }

    public void addPlayingFieldTo(Pane pane) {
        pane.getChildren().add(gridPane);
    }

    public ObservableList<Node> getChildren() {
        return gridPane.getChildren();
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public int getRowCount() {
        return ROW_COUNT;
    }
}