package game;

import java.awt.geom.Point2D;
import java.awt.Point;
import java.util.Random;

public class GameManager {
    final private static int PLAYING_FIELD_SIZE = 10;
    final private static int[] SHIP_LENGTHS = { 1, 1, 1, 1, 2, 2, 2, 3, 3, 4 };

    public static Random random = new Random();

    public static Tile[][] playingField;
    public static Player player;

    public static void newGame() throws Exception {
        setupPlayingField();
        setupPlayer();
        startGameLoop();
    }

    private static void setupPlayingField() {
        playingField = new Tile[PLAYING_FIELD_SIZE][PLAYING_FIELD_SIZE];
        fillWithOcean();
        Ship[] ships = getShips();
        placeShipArrayRandom(ships);
    }

    private static void fillWithOcean() {
        for (int row = 0; row < PLAYING_FIELD_SIZE; row++) {
            for (int column = 0; column < PLAYING_FIELD_SIZE; column++) {
                playingField[row][column] = new OceanTile();
            }
        }
    }

    private static Ship[] getShips() {
        Ship[] shipArray = new Ship[SHIP_LENGTHS.length];
        for (int i = 0; i < shipArray.length; i++) {
            Ship ship = new Ship(SHIP_LENGTHS[i]);
            ship.isVertical = random.nextBoolean();
            shipArray[i] = ship;
        }
        return shipArray;
    }

    private static void placeShipArrayRandom(Ship[] shipArray) {
        for (int i = 0; i < shipArray.length; i++) {
            Ship ship = shipArray[i];
            placeShip(ship);
        }
    }

    private static void placeShip(Ship ship) {
        int length = ship.length;
        int x;
        int y;
        while (true) {
            x = random.nextInt(PLAYING_FIELD_SIZE);
            y = random.nextInt(PLAYING_FIELD_SIZE);
            if (!shipPositionValid(ship, x, y)) { continue;}
            break;
        }
        placeShipTiles(ship, x, y);
        Point[] points = getCornerPointsOfOccupiedArea(ship, x, y);
        occupieAreaOfTiles(points[0], points[1]);
    }

    private static Point[] getCornerPointsOfOccupiedArea(Ship ship, int x, int y) {
        Point point1;
        Point point2;
        int x1 = Math.max(0, x - 1);
        int y1 = Math.max(0, y - 1);
        int x2;
        int y2;
        if (!ship.isVertical) {
            x2 = x + ship.length + 1;
            y2 = y + 1;
        } else {
            x2 = x + 1;
            y2 = y + ship.length + 1;
        }
        x2 = Math.min(x2, PLAYING_FIELD_SIZE - 1);
        y2 = Math.min(y2, PLAYING_FIELD_SIZE - 1);
        point1 = new Point(x1, y1);
        point2 = new Point(x2, y2);
        return new Point[]{point1, point2};
    }

    private static boolean shipPositionValid(Ship ship, int x, int y) {
        Point pointStart = new Point(x, y);
        Point pointEnd = new Point(
            ship.isVertical ? x : x + ship.length,
            ship.isVertical ? y + ship.length : y
        );
        ShipTile shipStart = (ShipTile)playingField[(int)pointStart.getX()][(int)pointStart.getY()];
        ShipTile shipEnd = (ShipTile)playingField[(int)pointEnd.getX()][(int)pointEnd.getY()];
        if (ship.isVertical && (y + ship.length) >= PLAYING_FIELD_SIZE) {
            return false;
        } else if ((x + ship.length) >= PLAYING_FIELD_SIZE){
            return false;
        } else if (playingField[x][y].getOccupied()) {
            return false;
        } else if (playingField[(int)pointEnd.getX()][(int)pointEnd.getY()].getOccupied()) {
            return false;
        }
        return true;
    }

    private static void placeShipTiles(Ship ship, int x, int y) {
        for (int addLength = 0; addLength < ship.length; addLength++) {
            if (!ship.isVertical) {
                playingField[x + addLength][y] = new ShipTile();
            } else {
                playingField[x][y + addLength] = new ShipTile();
            }
        }
    }

    private static void occupieAreaOfTiles(Point2D point1, Point2D point2) {
        occupieAreaOfTiles(
            (int)point1.getX(), (int)point1.getY(),
            (int)point2.getX(), (int)point2.getY());
    }

    private static void occupieAreaOfTiles(int x1, int y1, int x2, int y2) {
        for (int row = y1; row <= y2; row++) {
            for (int column = x1; column <= x2; column++) {
                Tile tile = playingField[row][column];
                if (OceanTile.checkIfOceanTile(tile)) {
                    OceanTile oceanTile = (OceanTile)tile;
                    oceanTile.setOccupied(true);
                    playingField[row][column] = oceanTile;
                }
            }
        }
    }

    private static void setupPlayer() {
        player = new ConsolePlayer();
    }

    private static void startGameLoop() throws Exception {
        while (true) {
            drawPlayingField();
            playersTurn();
        }
    }

    private static void playersTurn() {
        Point2D point;
        try {
            point = player.nextShot();
            System.out.println("X: " + point.getX() + " Y: " + point.getY());
            playingField[(int)point.getX()][(int)point.getY()].shoot();
        } catch (Exception e) {
            player.sendErrorMessage("Input invalid! Please try again!");
        }
    }

    private static void drawPlayingField() throws Exception {
        drawXAxis();
        for (int row = 0; row < PLAYING_FIELD_SIZE; row++) {
            System.out.print((row + 1) + " |");
            for (int column = 0; column < PLAYING_FIELD_SIZE; column++) {
                Tile tile = playingField[row][column];
                String className = tile.getClass().getName();
                char symbol = getTileSymbol(tile, className);
                System.out.print(symbol + "|");
            }
            System.out.println();
        }
    }

    private static char getTileSymbol(Tile tile, String className) throws Exception {
        char symbol;
        char ocean;
        char ship;
        if (tile.getShot()) {
            ocean = 'O';
            ship = 'X';
        } else {
            ocean = '~';
            ship = '-';
        }
        switch (className) {
            case "game.OceanTile":
                symbol = ocean;
                break;
            case "game.ShipTile":
                symbol = ship;
                break;
            default:
                throw new Exception("No Symbol defined for Tile");
        }
        return symbol;
    }

    private static void drawXAxis() {
        String coordinatesX = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        System.out.print("   ");
        for (int i = 0; i < PLAYING_FIELD_SIZE; i++) {
            char letter = coordinatesX.toCharArray()[i];
            System.out.print(letter + " ");
        }
        System.out.println();
    }
}
