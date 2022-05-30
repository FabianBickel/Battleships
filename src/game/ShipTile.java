package game;

public class ShipTile implements Tile{
    
    private boolean shot;
    private boolean occupied;

    public ShipTile() {
        this.shot = false;
        this.occupied = true;
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
}