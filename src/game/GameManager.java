package game;

import java.awt.Point;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class GameManager {
    final private static int PLAYING_FIELD_SIZE = 10;
    final private static int[] SHIP_LENGTHS = { 1, 1, 1, 1, 2, 2, 2, 3, 3, 4 };

    private static Random random = new Random();

    private static Tile[][] playingField;
    private static Ship[] ships = new Ship[SHIP_LENGTHS.length];
    private static Player player;

    private static int shotCount = 0;
    private static int sunkCount = 0;

    public static void newGame() throws Exception {
        setupPlayer();
        player.sendLog("Setting up playing field...");
        setupPlayingField();
        player.sendLog("Starting game...");
        startGameLoop();
    }

    private static void setupPlayer() {
        player = new ConsolePlayer();
    }

    private static void setupPlayingField() throws Exception {
        playingField = new Tile[PLAYING_FIELD_SIZE][PLAYING_FIELD_SIZE];
        fillWithOcean(playingField);
        player.sendLog("Generating ships...");
        tryGetShips();
        placeShips(ships);
    }

    private static void tryGetShips() {
        int attempt = 0;
        while (attempt < 100) {
            try {
                ships = getShips();
                break;
            } catch (TimeoutException e) {}
            attempt++;
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
                if (sunkCount == (SHIP_LENGTHS.length - 1)) {
                    throw new PlayerWonException();
                }
            } catch (TerminateGameException e) {
                player.sendMessage("The game was terminated by the player.");
                break;
            } catch (PlayerWonException e) {
                player.sendMessage("Congratulations! You won the game!\nYou took " + shotCount + " shots to destroy " + sunkCount + " ships.");
            }
        }
    }

    private static void playersTurn() throws TerminateGameException {
        Point point;
        while (true) {
            try {
                point = player.nextShot(playingField);
                if (point.getX() >= PLAYING_FIELD_SIZE || point.getY() >= PLAYING_FIELD_SIZE) {
                    throw new IllegalArgumentException("Coordinates (" + point.getX() + "|" + point.getY() + " are out of bounds!");
                }
            } catch (InputMismatchException e) {
                player.sendErrorMessage("Input invalid! Please try again!");
                continue;
            } catch (IllegalArgumentException e) {
                player.sendErrorMessage("Coordinates are out of bounds!");
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

    private static Ship[] getShips() throws TimeoutException {
        Ship[] shipArray = new Ship[SHIP_LENGTHS.length];
        for (int i = 0; i < shipArray.length; i++) {
            Ship ship;
            int shipLength = SHIP_LENGTHS[i];
            int attempt = 0;
            do {
                if (attempt >= 100) {
                    throw new TimeoutException("Couldn't find a suitable location for ship. There are likely too many ships to be places on the playing field.");
                }
                ship = getNewRandomShip(shipLength);
                attempt++;
            } while (!shipPositionValid(shipArray.clone(), ship));
            shipArray[i] = ship;
        }
        return shipArray;
    }

    private static Ship getNewRandomShip(int length) {
        int row;
        int column;
        boolean isVertical;
        Ship ship;
        row = random.nextInt(PLAYING_FIELD_SIZE);
        column = random.nextInt(PLAYING_FIELD_SIZE);
        isVertical = random.nextBoolean();
        ship = new Ship(column, row, length, isVertical);
        return ship;
    }    
    
    private static boolean shipPositionValid(Ship[] shipArray, Ship ship) {
        Point pointStart = ship.getMinPoint();
        Point pointEnd = ship.getMaxPoint();
        if (!shipInBounds(ship)) {
            return false;
        } else if (pointOccupiedInShipArray(pointStart, shipArray) || pointOccupiedInShipArray(pointEnd, shipArray)) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean shipInBounds(Ship ship) {
        int row = ship.getMinY();
        int column = ship.getMinX();
        if (ship.getIsVertical() && ((row + (ship.getLength() - 1)) >= playingField.length)) {
            return false;
        } else if (!ship.getIsVertical() && (column + (ship.getLength() - 1)) >= playingField.length) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean pointOccupiedInShipArray(Point point, Ship[] shipArray) {
        System.out.print("");
        for (Ship ship : shipArray) {
            if (ship == null) {
                continue;
            } else if (ship.occupiedAreaContains(point)) {
                return true;
            }
        }
        return false;
    }

    private static void placeShips(Ship[] shipArray) throws Exception {
        for (Ship ship : shipArray) {
            placeShipOnPlayingField(ship);
            occupieTilesAroundShip(ship);
        }
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