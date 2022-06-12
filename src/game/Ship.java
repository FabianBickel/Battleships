package game;

import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.awt.geom.Point2D;
import java.awt.Point;

public class Ship {
    public int length;
    public boolean isVertical;

    public Ship(int length) {
        this.length = length;
        this.isVertical = false;
    }

    public Ship(int length, boolean isVertical) {
        this.length = length;
        this.isVertical = isVertical;
    }

    private Tile[][] tileField;
    private Random random = new Random();

    
    //#region Ship placing logic
    public void place(Tile[][] tileArray, int row, int column) throws Exception {
        tileField = tileArray;
        int i = 0;
        while (i++ < 1000) {
            row = random.nextInt(tileField.length);
            column = random.nextInt(tileField.length);
            if (!shipPositionValid(row, column)) { throw new IllegalArgumentException("Position " + row + ", " + column + " is invalid!");}
            break;
        }
        if (i < 1000) {
            throw new TimeoutException();
        }
        placeShipTiles(row, column);
        Point[] points = getCornerPointsOfOccupiedArea(row, column);
        occupieAreaOfTiles(points[0], points[1]);
    }


    private boolean shipPositionValid(int row, int column) {
        Point pointStart = new Point(row, column);
        Point pointEnd = getShipEndPoint(row, column);
        if (!shipInBounds(row, column)) return false;
        if (pointOccupied(pointStart) && pointOccupied(pointEnd)) return false;
        return true;
    }

    private boolean shipInBounds(int row, int column) {
        if (isVertical && (row + (length - 1)) >= tileField.length) {
            return false;
        } else if (!isVertical && (column + (length - 1)) >= tileField.length) {
            return false;
        }
        return true;
    }

    private Point getShipEndPoint(int row, int column) {
        Point pointEnd = new Point(
            isVertical ? row + (length - 1) : row,
            isVertical ? column : column + (length - 1)
        );
        return pointEnd;
    }

    private boolean pointOccupied(Point point) {
        Tile tile = tileField[(int)point.getX()][(int)point.getY()];
        return tile.getOccupied();
    }

    private void placeShipTiles(int row, int column) {
        for (int addLength = 0; addLength < length; addLength++) {
            if (isVertical) {
                tileField[row + addLength][column] = new ShipTile();
            } else {
                tileField[row][column + addLength] = new ShipTile();
            }
        }
    }

    private Point[] getCornerPointsOfOccupiedArea(int x, int y) {
        Point point1 = getCornerPointTopLeft(x, y);
        Point point2 = getCornerPointBottomRight(x, y);
        return new Point[]{point1, point2};
    }

    private Point getCornerPointTopLeft(int row, int column) {
        Point point1;
        int row1 = row - 1;
        int column1 = column - 1;
        row1 = Math.max(0, row);
        column1 = Math.max(0, column);
        point1 = new Point(row1, column1);
        return point1;
    }

    private Point getCornerPointBottomRight(int row, int column) {
        Point point2;
        int row2;
        int column2;
        if (isVertical) {
            row2 = row + length + 1;
            column2 = column + 1;
        } else {
            row2 = row + 1;
            column2 = column + length + 1;
        }
        row2 = Math.min(row2, tileField.length - 1);
        column2 = Math.min(column2, tileField.length - 1);
        point2 = new Point(row2, column2);
        return point2;
    }


    private void occupieAreaOfTiles(Point2D point1, Point2D point2) {
        occupieAreaOfTiles(
            (int)point1.getX(), (int)point1.getY(),
            (int)point2.getX(), (int)point2.getY());
    }

    private void occupieAreaOfTiles(int row1, int column1, int row2, int column2) {
        for (int row = row1; row <= row2; row++)
            for (int column = column1; column <= column2; column++)
                occupieTile(row, column);
    }

    private void occupieTile(int row, int column) {
        Tile tile = tileField[row][column];
        if (OceanTile.checkIfOceanTile(tile)) {
            OceanTile oceanTile = (OceanTile)tile;
            oceanTile.occupie();
            tileField[row][column] = oceanTile;
        }
    }

    //#endregion Ship placing logic
}