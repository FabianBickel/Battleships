package players;

import game.*;
import tiles.*;

import game.*;
import tiles.*;
import players.*;

import java.awt.Point;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsolePlayer implements Player {
    public Scanner scanner;

    public ConsolePlayer() {
        this.scanner = new Scanner(System.in);
    }

    public Point nextShot(Tile[][] tileField) throws TerminateGameException {
        String input = getAndPreProcessInput(tileField);
        String coordinatesColumn = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int row = getIntegerFromInputString(input);
        int column = coordinatesColumn.indexOf((int)input.charAt(0));
        return new Point(row, column);
    }

    private int getIntegerFromInputString(String input) {
        int result;
        try {
            result = Integer.parseInt(input.substring(1)) - 1;
        } catch (Exception e) {
            throw new InputMismatchException("Substring from index 1 of input cannot be parsed as integer");
        }
        return result;
    }

    private String getAndPreProcessInput(Tile[][] tileField) throws TerminateGameException {
        String input;
        while (true) {
            System.out.println();
            System.out.print("Please enter the coordinates of your next shot (ex. B4): ");
            input = scanner.next().toUpperCase().trim();
            if (input.equals("-1")) {
                throw new TerminateGameException();
            } else if (input.equals("?")) {
                drawPlayingField(tileField, true);
                continue;
            }
            break;
        }
        return input;
    }

    public void sendLog(String message) {
        System.out.println(message);
    }

    public void sendMessage(String message) {
        System.out.println();
        System.out.println(message);
    }

    public void sendErrorMessage(String message) {
        System.out.println();
        System.out.println("\u001B[31m" + message + "\u001B[0m");
    }

    public void drawPlayingField(Tile[][] tileField, boolean shipsVisible) {
        System.out.println("\n------------------------\n");
        drawSpaces(0 + 1, tileField.length);
        drawColumnNaming(tileField.length);
        for (int row = 0; row < tileField.length; row++) {
            drawSpaces(row + 1, tileField.length);
            System.out.print((row + 1) + " |");
            for (int column = 0; column < tileField.length; column++) {
                Tile tile = tileField[row][column];
                char symbol = getTileSymbol(tile, shipsVisible);
                System.out.print(symbol + "|");
            }
            System.out.println();
        }
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

    private void drawSpaces(int row, int maxRow) {
        int fillTo = Integer.toString(maxRow).length();
        int length = Integer.toString(row).length();
        String result = "";
        for (int i = 0; i < fillTo - length; i++) {
            result += " ";
        }
        System.out.print(result);
    }

    private char getTileSymbol(Tile tile, boolean shipsVisible) {
        char symbol = 'E';
        char ocean;
        char ship;
        if (tile.getShotState()) {
            ocean = 'O';
            ship = 'X';
        } else {
            ocean = '~';
            ship = shipsVisible ? '?' : '~';
        }
        switch (tile.getClass().getName()) {
            case "game.OceanTile":
                symbol = ocean;
                break;
            case "game.ShipTile":
                symbol = ship;
                break;
        }
        return symbol;
    }

    public void informHitOcean() {
        sendMessage("Pity, you didn't hit anything.");
    }

    public void informHitShip() {
        System.out.println();
        System.out.println("\u001B[32m" + "You hit an enemy ship!" + "\u001B[0m");
    }

    public void informSunkShip() {
        System.out.println("\u001B[32m" + "Nice shot! You sunk the ship!" + "\u001B[0m");
    }

    public void sendInstrucionManual() {
        System.out.println();
        System.out.println("\"B4\" - shoot at coordinate B4");
        System.out.println("\" ?\" - unhide remaining ships");
        System.out.println("\"-1\" - end the game");
    }
}