package game;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.concurrent.TimeoutException;

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

    private static void setupPlayingField() throws Exception {
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

    private static void placeShipArrayRandom(Ship[] shipArray) throws Exception {
        Tile[][] tempTileField = playingField.clone();
        int attemptNr = 0;
        while (true) {
            try {
                for (int i = 0; i < shipArray.length; i++) {
                    Ship ship = shipArray[i];
                    while(true) {
                        try {
                            int row = random.nextInt(playingField.length);
                            int column = random.nextInt(playingField.length);
                            ship.place(tempTileField, row, column);
                            break;
                        } catch (IllegalArgumentException e) {}
                    }
                }
            } catch (TimeoutException e) {
                attemptNr++;
                if (attemptNr > 10) {
                    throw new IllegalArgumentException("Ships could not be placed. There are likely too many ships to fit on the playing field.");
                }
                continue;
            }
        }
    }

    private static void setupPlayer() {
        player = new ConsolePlayer();
    }

    private static void startGameLoop() throws Exception {
        while (true) {
            player.drawPlayingField(playingField);
            playersTurn();
        }
    }

    private static void playersTurn() {
        Point2D point;
        try {
            point = player.nextShot();
            System.out.println("Zeile: " + point.getX() + " Spalte: " + point.getY());
            playingField[(int)point.getX()][(int)point.getY()].shoot();
        } catch (Exception e) {
            player.sendErrorMessage("Input invalid! Please try again!");
        }
    }
}