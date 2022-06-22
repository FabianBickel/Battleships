package players;

import game.*;
import tiles.*;

import java.awt.Point;

import exceptions.TerminateGameException;

public interface PlayerInterface {
    public Point waitForNextShot(Tile[][] tileField) throws TerminateGameException;
    public void sendLog(String message);
    public void sendMessage(String message);
    public void sendErrorMessage(String message);
    public void sendInstrucionManual();
    public void informHitOcean();
    public void informHitShip();
    public void informSunkShip();
}