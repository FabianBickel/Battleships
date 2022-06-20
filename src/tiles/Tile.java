package tiles;

public interface Tile {
    public void shoot();
    public boolean getShotState();
    public void setShot(boolean value);
    public boolean getOccupied();
}
