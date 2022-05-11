package game;

public class Ocean implements Tile{

    private boolean shot;

    public Ocean() {
        this.shot = false;
    }

    public void shoot() {
        this.shot = true;
    }

    public boolean getShotState() {
        return shot;
    }
}
