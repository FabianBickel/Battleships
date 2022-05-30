package game;

import java.lang.invoke.MethodHandles;

public class OceanTile implements Tile{

    private boolean shot;
    private boolean occupied;

    public OceanTile() {
        this.shot = false;
    }

    public void shoot() {
        this.shot = true;
    }

    public boolean getShot() {
        return shot;
    }

    public void setShot (boolean value) {
        this.shot = value;
    }

    public boolean getOccupied() {
        return occupied;
    }

    public void setOccupied (boolean value) {
        this.occupied = value;
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
