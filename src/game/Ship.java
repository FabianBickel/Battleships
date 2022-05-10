package game;

public class Ship implements Tile{
    
    private boolean shot;

    public Ship() {
        this.shot = false;
    }

    public void shoot() {
        this.shot = true;
    }

    public boolean getShotState() {
        return shot;
    }
}
