package game;

public class PlayerWonException extends Exception {
    public PlayerWonException() {
        super("An exception for a player winning was thrown.");
    }
}