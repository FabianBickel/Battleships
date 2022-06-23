public class ShipShape {
    private static boolean O = false;
    private static boolean X = true;

    public static final ShipShape ROCKET = new ShipShape(new boolean[][] {
        {O, X, O}, 
        {O, X, O}, 
        {X, X, X}
    }, false);
    public static final ShipShape CARRIER = new ShipShape(new boolean[][] {
        {X, X},
        {X, X},
        {X, X},
        {X, X}
    }, false);
    public static final ShipShape SUBMARINE = new ShipShape(new boolean[][] {
        {X, O},
        {X, X},
        {X, O}
    }, true);
    public static final ShipShape CATAMARAN = new ShipShape(new boolean[][] {
        {X, O},
        {X, X},
        {O, X}
    }, true);
    public static final ShipShape BATTLESHIP = new ShipShape(new boolean[][] {
        {X},
        {X},
        {X},
        {X}
    }, false);
    public static final ShipShape CRUISER = new ShipShape(new boolean[][] {
        {X},
        {X},
        {X}
    }, false);
    public static final ShipShape DESTROYER = new ShipShape(new boolean[][] {
        {X},
        {X}
    }, false);

    private boolean[][] shape;
    private int width = -1;
    private int height = -1;
    private int tileCount = -1;
    private boolean supportsMirroring;

    private ShipShape(boolean[][] shape, boolean supportsMirroring) {
        this.shape = shape;
        this.width = getWidth();
        this.height = getHeight();
        this.tileCount = getTileCount();
        this.supportsMirroring = getSupportsMirroringInitial();
    }

    public int getWidth() {
        if (width == -1) {
            width = shape[0].length;
        }
        return width;
    }

    public int getHeight() {
        if (height == -1) {
            height = shape.length;
        }
        return height;
    }

    private boolean getSupportsMirroringInitial() {
        for (boolean[] row : shape) {
            int halfLength = row.length / 2;
            for (int i = 0; i < (halfLength); i++) {
                if (row[i] != row[(row.length - 1) - i]) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getSupportsMirroring() {
        return supportsMirroring;
    }

    public boolean getOccupiedAt(int columnIndex, int rowIndex) {
        return shape[rowIndex][columnIndex];
    }

    public int getTileCount() {
        if (this.tileCount == -1) {
            int tileCount = 0;
            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    if (shape[row][col]) {
                        tileCount++;
                    }
                }
            }
            return tileCount;
        } else {
            return tileCount;
        }
    }

    public ShipShape mirror() {
        if (supportsMirroring) {
            for (boolean[] row : shape) {
                boolean storage;
                for (int i = 0; i < row.length; i++) {
                    int i2 = row.length - i - 1;
                    storage = row[i];
                    row[i] = row[i2];
                    row[i2] = storage;
                }
            }
        }
        return this;
    }

    public ArrayList<Point2D> getValidPointsToFit(PlayingField playingField) {
        
    }
}