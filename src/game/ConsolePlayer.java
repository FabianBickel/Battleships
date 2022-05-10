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
        System.out.print("Please enter the coordinates of your next shot (ex. B4): ");
        String input = scanner.next().toUpperCase();
        String coordinatesY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int x;
        int y = coordinatesY.indexOf((int)input.charAt(0));
        try {
            x = Integer.parseInt(input.substring(1)) - 1;
        } catch (Exception e) {
            throw new InputMismatchException("Substring from index 1 of input cannot be parsed as integer");
        }
        return new Point(x, y);
    }
}
