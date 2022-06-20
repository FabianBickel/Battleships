package game;

import game.*;
import tiles.*;
import players.*;

public class OceanTile implements Tile{

    private boolean shot;
    private boolean occupied;

    public OceanTile() {
        this.shot = false;
        this.occupied = false;
    }

    public void shoot() {
        this.shot = true;
    }

    public boolean getShotState() {
        return shot;
    }

    public void setShot (boolean value) {
        this.shot = value;
    }

    public boolean getOccupied() {
        return occupied;
    }

    public void occupie () {
        this.occupied = true;
    }

    public static boolean checkIfOceanTile(Tile tile) {
        String thisClassName = OceanTile.class.getName();
        String tileClassName = tile.getClass().getName();
        if (thisClassName.equals(tileClassName)) {
            return true;
        } else {
            return false;
        }
    }
}