package game;

import java.awt.geom.Point2D;
import java.awt.Point;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsolePlayer implements Player{
    public Scanner scanner;

    public ConsolePlayer() {
        this.scanner = new Scanner(System.in);
    }

    public Point2D nextShot() {
        System.out.println("\n------------------------\n");
        System.out.print("Please enter the coordinates of your next shot (ex. B4): ");
        String input = scanner.next().toUpperCase().trim();
        String coordinatesColumn = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int row;
        int column = coordinatesColumn.indexOf((int)input.charAt(0));
        try {
            row = Integer.parseInt(input.substring(1)) - 1;
        } catch (Exception e) {
            throw new InputMismatchException("Substring from index 1 of input cannot be parsed as integer");
        }
        return new Point(row, column);
    }

    public void sendErrorMessage(String message) {
        System.out.println(message);
    }

    public void drawPlayingField(Tile[][] tileField) throws Exception {
        drawColumnNaming(tileField.length);
        for (int row = 0; row < tileField.length; row++) {
            System.out.print((row + 1) + " |");
            for (int column = 0; column < tileField.length; column++) {
                Tile tile = tileField[row][column];
                String className = tile.getClass().getName();
                char symbol = getTileSymbol(tile, className);
                System.out.print(symbol + "|");
            }
            System.out.println();
        }
    }

    private char getTileSymbol(Tile tile, String className) throws Exception {
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
                throw new Exception("No symbol defined for tile " + tile.getClass().getName());
        }
        return symbol;
    }

    private void drawColumnNaming(int tileFieldLength) {
        String coordinatesColumns = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        System.out.print("   ");
        for (int i = 0; i < tileFieldLength; i++) {
            char letter = coordinatesColumns.toCharArray()[i];
            System.out.print(letter + " ");
        }
        System.out.println();
    }
}
