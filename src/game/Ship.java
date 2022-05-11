package game;

public class Ship {
    public int length;
    public boolean isVertical;

    public Ship(int length) {
        this.length = length;
        this.isVertical = false;
    }

    public Ship(int length, boolean isVertical) {
        this.length = length;
        this.isVertical = isVertical;
    }
}