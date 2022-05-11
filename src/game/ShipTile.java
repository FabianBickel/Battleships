package game;

public class ShipTile implements Tile{
    
    private boolean shot;

    public ShipTile() {
        this.shot = false;
    }

    public void shoot() {
        this.shot = true;
    }

    public boolean getShotState() {
        return shot;
    }
}
