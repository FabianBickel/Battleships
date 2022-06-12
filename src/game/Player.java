package game;

import java.awt.geom.Point2D;

public interface Player {
    public Point2D nextShot();
    public void sendErrorMessage(String message);
    public void drawPlayingField(Tile[][] tileField) throws Exception;
}