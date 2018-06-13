import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Tetrominoes {
    O(new Integer[][] {
      {0, 0, 0},
      {0, 0, 1},
      {1, 0, 0},
      {1, 0, 1}
    }, Color.YELLOW),
    I(new Integer[][] {
      {-1, 0, 0},
      {0, 0, 0},
      {1, 0, 0},
      {2, 0, 0}
    }, Color.CYAN),
    T(new Integer[][] {
      {-1, 0, 0},
      {0, 0, 0},
      {1, 0, 0},
      {0, 0, 1}
    }, Color.MAGENTA),
    J(new Integer[][] {
      {0, 0, -1},
      {0, 0, 0},
      {0, 0, 1},
      {-1, 0, 1}
    }, Color.BLUE),
    S(new Integer[][] {
      {-1, 0, 1},
      {0, 0, 1},
      {0, 0, 0},
      {1, 0, 0}
    }, Color.GREEN),
    RIGHT_SCREW(new Integer[][] {
      {0, 0, 0},
      {0, -1, 0},
      {0, -1, -1},
      {1, -1, -1}
    }, Color.DARK_GREY),
    LEFT_SCREW(new Integer[][] {
      {0, 0, 0},
      {0, -1, 0},
      {-1, -1, 0},
      {-1, -1, 1}
    }, Color.DARK_GREY),
    BRANCH(new Integer[][] {
      {0, 0, 0},
      {0, -1, 0},
      {0, -1, 1},
      {1, -1, 0}
    }, Color.BROWN);
    
    private static final List<Tetrominoes> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    
    public static Tetrominoes randomTetromino() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
    
    private final Integer[][] coords;
    private final Color color;
    
    Tetrominoes(Integer[][] coords, Color color) {
        this.coords = coords;
        this.color = color;
    }
    
    public Integer[][] getCoords() {
        return coords;
    }
    
    public Color getColor() {
        return color;
    }
}
