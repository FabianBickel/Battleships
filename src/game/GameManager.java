package game;

import java.awt.geom.Point2D;
import java.util.Random;

public class GameManager {
    final public static int PLAYING_FIELD_SIZE = 10;

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
        placeShips();
    }

    private static void placeShips() {
        playingField = new Tile[][] {
            { o(),o(),o(),o(),o(),o(),o(),o(),o(),o() },
            { o(),s(),o(),o(),o(),o(),o(),o(),o(),o() },
            { o(),s(),o(),o(),o(),s(),s(),s(),s(),o() },
            { o(),s(),o(),o(),o(),o(),o(),o(),o(),o() },
            { o(),o(),o(),o(),o(),s(),s(),o(),o(),o() },
            { o(),o(),o(),o(),o(),o(),o(),o(),o(),o() },
            { o(),o(),o(),o(),s(),o(),o(),o(),s(),o() },
            { o(),o(),o(),o(),s(),o(),o(),o(),s(),o() },
            { o(),o(),o(),o(),s(),o(),o(),o(),o(),o() },
            { o(),o(),o(),o(),o(),o(),s(),s(),s(),s() }
        };
    }

    private static Ocean o() {
        return new Ocean();
    }
    
    private static Ship s() {
        return new Ship();
    }

    private static void setupPlayer() {
        player = new ConsolePlayer();
    }

    private static void startGameLoop() throws Exception {
        while (true) {
            drawPlayingField();
            Point2D point = player.nextShot();
            System.out.println("X: " + point.getX() + " Y: " + point.getY());
            playingField[(int)point.getX()][(int)point.getY()].shoot();
        }
    }

    private static void drawPlayingField() throws Exception {
        drawXAxis();
        for (int row = 0; row < PLAYING_FIELD_SIZE; row++) {
            System.out.print((row + 1) + " |");
            for (int column = 0; column < PLAYING_FIELD_SIZE; column++) {
                Tile tile = playingField[row][column];
                String className = tile.getClass().getName();
                char symbol;
                char ocean;
                char ship;
                if (tile.getShotState()) {
                    ocean = 'O';
                    ship = 'X';
                } else {
                    ocean = '~';
                    ship = '~';
                }
                switch (className) {
                    case "game.Ocean":
                        symbol = ocean;
                        break;
                    case "game.Ship":
                        symbol = ship;
                        break;
                    default:
                        throw new Exception("No Symbol defined for Tile");
                }

                System.out.print(symbol + "|");
            }
            System.out.println();
        }
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
