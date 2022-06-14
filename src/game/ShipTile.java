package game;

public class ShipTile implements Tile{
    
    private boolean shot;
    private boolean occupied;
    public Ship parentShip;

    public ShipTile(Ship parentShip) {
        this.shot = false;
        this.occupied = true;
        this.parentShip = parentShip;
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

    public Ship getParentShip() {
        return parentShip;
    }
}