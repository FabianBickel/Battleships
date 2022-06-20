package game;

import tiles.*;
import players.*;

import java.awt.Point;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class GameManager {
    final private static int PLAYING_FIELD_SIZE = 10;
    final private static int[] SHIP_LENGTHS = { 1, 1, 1, 1, 2, 2, 2, 3, 3, 4 };

    private static Random random = new Random();

    private static Tile[][] playingField;
    private static Player player;

    private static int shotCount = 0;
    private static int sunkCount = 0;

    public static void newGame() throws Exception {
        setupPlayer();
        player.sendLog("Setting up playing field...");
        setupPlayingField();
        player.sendLog("Starting game...");
        player.sendInstrucionManual();
        startGameLoop();
    }

    private static void setupPlayer() {
        player = new ConsolePlayer();
    }

    private static void setupPlayingField() throws Exception {
        playingField = new Tile[PLAYING_FIELD_SIZE][PLAYING_FIELD_SIZE];
        fillWithOcean(playingField);
        player.sendLog("Generating ships...");
        while (true) {
            try {
                placeShips();
                break;
            } catch (TimeoutException e) {
                fillWithOcean(playingField);
                continue;
            }
        }
    }

    private static void fillWithOcean(Tile[][] tileField) {
        for (int row = 0; row < PLAYING_FIELD_SIZE; row++) {
            for (int column = 0; column < PLAYING_FIELD_SIZE; column++) {
                tileField[row][column] = new OceanTile();
            }
        }
    }

    private static void startGameLoop() throws Exception {
        while (true) {
            try {
                player.drawPlayingField(playingField, false);
                playersTurn();
                if (sunkCount >= (SHIP_LENGTHS.length)) {
                    throw new PlayerWonException();
                }
            } catch (TerminateGameException e) {
                player.sendMessage("The game was terminated by the player.");
                break;
            } catch (PlayerWonException e) {
                player.sendMessage("Congratulations! You won the game!\nYou took " + shotCount + " shots to destroy " + sunkCount + " ships.");
                break;
            }
        }
    }

    private static void playersTurn() throws TerminateGameException {
        Point point;
        while (true) {
            try {
                point = player.nextShot(playingField);
                int x = (int)point.getX();
                int y = (int)point.getY();
                int max = PLAYING_FIELD_SIZE;
                if (x >= max || y >= max || x < 0 || y < 0) {
                    throw new IllegalArgumentException("Coordinates (" + point.getX() + "|" + point.getY() + " are out of bounds!");
                }
            } catch (InputMismatchException e) {
                player.sendErrorMessage("Input invalid! Please try again!");
                continue;
            } catch (IllegalArgumentException e) {
                player.sendErrorMessage("Coordinates are invalid!");
                continue;
            }
            Tile tile = getTileFromPoint(point);
            if (tile.getShotState()) {
                player.sendErrorMessage("This tile has already been shot! Please try another one.");
                continue;
            } else {
                tile.shoot();
                if (OceanTile.checkIfOceanTile(tile)) {
                    player.informHitOcean();
                } else {
                    player.informHitShip();
                }
                shotCount++;
                sinkShipIfDestroyed(tile);
                break;
            }

        }
    }

    private static Tile getTileFromPoint(Point point) {
        int row = (int)point.getX();
        int column = (int)point.getY();
        Tile tile = playingField[row][column];
        return tile;
    }

    private static void sinkShipIfDestroyed(Tile tile) {
        if (!OceanTile.checkIfOceanTile(tile)) {
            ShipTile shipTile = (ShipTile)tile;
            Ship ship = shipTile.getParentShip();
            if (ship.getDestroyedState(playingField)) {
                shootOceanAroundShip(ship);
                player.informSunkShip();
                sunkCount++;
            }
        }
    }

    private static void placeShips() throws Exception {
        for (int i = 0; i < SHIP_LENGTHS.length; i++) {
            int attempts = 0;
            while (true) {
                if (attempts >= 100) {
                    throw new TimeoutException("Couldn't place ship. There are likely too many ships to be places on the tile field.");
                }
                Ship ship = getNewRandomShip(SHIP_LENGTHS[i]);
                if (!shipPositionValid(ship)) {
                    attempts++;
                    continue;
                }
                placeShipOnPlayingField(ship);
                occupieTilesAroundShip(ship);
                break;
            }
        }
    }

    private static Ship getNewRandomShip(int length) {
        random = new Random();
        int x = random.nextInt(PLAYING_FIELD_SIZE);
        int y = random.nextInt(PLAYING_FIELD_SIZE);
        boolean isVertical = random.nextBoolean();
        Ship ship = new Ship(x, y, length, isVertical);
        return ship;
    }

    private static boolean shipPositionValid(Ship ship) {
        boolean isVertical = ship.getIsVertical();
        int coordStart = isVertical ? ship.getMinY() : ship.getMinX();
        int coordEnd = isVertical ? ship.getMaxY() : ship.getMaxX();
        for (int i = coordStart; i <= coordEnd; i++) {
            if (i >= PLAYING_FIELD_SIZE) {
                return false;
            } else if (isVertical) {
                if (playingField[i][ship.getMinX()].getOccupied()) {
                    return false;
                }
            } else {
                if (playingField[ship.getMinY()][i].getOccupied()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static void placeShipOnPlayingField(Ship ship) {
        int row = ship.getMinY();
        int column = ship.getMinX();
        for (int i = 0; i < ship.getLength(); i++) {
            playingField[row][column] = new ShipTile(ship);
            if (ship.getIsVertical()) {
                row++;
            } else {
                column++;
            }
        }
    }

    private static void shootOceanAroundShip(Ship ship) {
        int columnStart = (int) ship.getOccupiedMinX();
        int rowStart = (int) ship.getOccupiedMinY();
        int columnEnd = (int) ship.getOccupiedMaxX();
        int rowEnd = (int) ship.getOccupiedMaxY();
        for (int row = rowStart; row <= rowEnd; row++) {
            for (int column = columnStart; column <= columnEnd; column++) {
                int rowClamped = Util.clamp(0, row, PLAYING_FIELD_SIZE - 1);
                int columnClamped = Util.clamp(0, column, PLAYING_FIELD_SIZE - 1);
                shootIfOceanTile(rowClamped, columnClamped);
            }
        }
    }

    private static void shootIfOceanTile(int row, int column) {
        int rowClamped = Util.clamp(0, row, PLAYING_FIELD_SIZE - 1);
        int columnClamped = Util.clamp(0, column, PLAYING_FIELD_SIZE - 1);
        Tile tile = playingField[rowClamped][columnClamped];
        if (OceanTile.checkIfOceanTile(tile)) {
            OceanTile oceanTile = (OceanTile) tile;
            oceanTile.shoot();
            playingField[rowClamped][columnClamped] = oceanTile;
        }
    }

    private static void occupieTilesAroundShip(Ship ship) {
        int columnStart = (int) ship.getOccupiedMinX();
        int rowStart = (int) ship.getOccupiedMinY();
        int columnEnd = (int) ship.getOccupiedMaxX();
        int rowEnd = (int) ship.getOccupiedMaxY();
        for (int row = rowStart; row <= rowEnd; row++) {
            for (int column = columnStart; column <= columnEnd; column++) {
                int rowClamped = Math.min(Math.max(row, 0), PLAYING_FIELD_SIZE - 1);
                int columnClamped = Math.min(Math.max(column, 0), PLAYING_FIELD_SIZE - 1);
                occupieIfOceanTile(rowClamped, columnClamped);
            }
        }
    }

    private static void occupieIfOceanTile(int row, int column) {
        Tile tile = playingField[row][column];
        if (OceanTile.checkIfOceanTile(tile)) {
            OceanTile oceanTile = (OceanTile) tile;
            oceanTile.occupie();
            playingField[row][column] = oceanTile;
        }
    }
}