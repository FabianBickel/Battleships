package game;

import java.awt.geom.Point2D;
import java.util.Random;

public class GameManager {
    final public static int PLAYING_FIELD_SIZE = 10;
    final public static int[] SHIP_LENGTHS = { 2, 2, 3, 3, 4, 4 };

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
        placeShips(ships);
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

    private static void placeShips(Ship[] shipArray) {
        for (int i = 0; i < shipArray.length; i++) {
            Ship ship = shipArray[i];
            while (true) {
                int x = random.nextInt(PLAYING_FIELD_SIZE);
                int y = random.nextInt(PLAYING_FIELD_SIZE);
                if (ship.isVertical) {
                    if ((y + ship.length - 1) >= PLAYING_FIELD_SIZE) {
                        continue;
                    }
                } else {
                    if ((x + ship.length - 1) >= PLAYING_FIELD_SIZE) {
                        continue;
                    }
                }
                for (int addLength = 0; addLength < ship.length; addLength++) {
                    if (!ship.isVertical) {
                        playingField[x + addLength][y] = new ShipTile();
                    } else {

                        playingField[x][y + addLength] = new ShipTile();
                    }
                }
                break;
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
        if (tile.getShotState()) {
            ocean = 'O';
            ship = 'X';
        } else {
            ocean = 'o';
            ship = 'x';
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
