package game;

public class OceanTile implements Tile{

    private boolean shot;

    public OceanTile() {
        this.shot = false;
    }

    public void shoot() {
        this.shot = true;
    }

    public boolean getShotState() {
        return shot;
    }
}
