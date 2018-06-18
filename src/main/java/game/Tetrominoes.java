package game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Tetrominoes {
    O(new Coordinate[] {
      new Coordinate(0, 0, 0),
      new Coordinate(0, 0, 1),
      new Coordinate(1, 0, 0),
      new Coordinate(1, 0, 1)
    }, Color.YELLOW),
    I(new Coordinate[] {
      new Coordinate(-1, 0, 0),
      new Coordinate(0, 0, 0),
      new Coordinate(1, 0, 0),
      new Coordinate(2, 0, 0)
    }, Color.CYAN),
    T(new Coordinate[] {
      new Coordinate(-1, 0, 0),
      new Coordinate(0, 0, 0),
      new Coordinate(1, 0, 0),
      new Coordinate(0, 0, 1)
    }, Color.MAGENTA),
    J(new Coordinate[] {
      new Coordinate(0, 0, -1),
      new Coordinate(0, 0, 0),
      new Coordinate(0, 0, 1),
      new Coordinate(-1, 0, 1)
    }, Color.BLUE),
    S(new Coordinate[] {
      new Coordinate(-1, 0, 1),
      new Coordinate(0, 0, 1),
      new Coordinate(0, 0, 0),
      new Coordinate(1, 0, 0)
    }, Color.GREEN),
    RIGHT_SCREW(new Coordinate[] {
      new Coordinate(0, 0, 0),
      new Coordinate(0, -1, 0),
      new Coordinate(0, -1, -1),
      new Coordinate(1, -1, -1)
    }, Color.DARK_GREY),
    LEFT_SCREW(new Coordinate[] {
      new Coordinate(0, 0, 0),
      new Coordinate(0, -1, 0),
      new Coordinate(-1, -1, 0),
      new Coordinate(-1, -1, 1)
    }, Color.LIGHT_GREY),
    BRANCH(new Coordinate[] {
      new Coordinate(0, 0, 0),
      new Coordinate(0, -1, 0),
      new Coordinate(0, -1, 1),
      new Coordinate(1, -1, 0)
    }, Color.BROWN);
    
    private static final List<Tetrominoes> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int               SIZE   = VALUES.size();
    private static final Random            RANDOM = new Random();
    
    public static Tetrominoes randomTetromino() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
    
    private final Coordinate[] coords;
    private final Color        color;
    
    Tetrominoes(Coordinate[] coords, Color color) {
        this.coords = coords;
        this.color = color;
    }
    
    public Coordinate[] getCoords() {
        return coords;
    }
    
    public Color getColor() {
        return color;
    }
}
