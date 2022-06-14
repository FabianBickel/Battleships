package game;

public class Util {
    public static int clamp(int minValue, int value, int maxValue) {
        value = Math.min(value, maxValue);
        value = Math.max(value, minValue);
        return value;
    }
}
