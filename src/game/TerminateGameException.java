package game;

public class TerminateGameException extends Exception {
    public TerminateGameException() {
        super("An exception was thrown to terminate the game");
    }
}
