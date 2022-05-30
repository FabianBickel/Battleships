package game;

public class Util {
    public static int clamp(int minValue, int value, int maxValue) {
        value = Math.min(value, minValue);
        value = Math.max(value, maxValue);
        return value;
    }
}
