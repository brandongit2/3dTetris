import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Tetrominoes {
    O;
    
    private static final List<Tetrominoes> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    
    public static Tetrominoes randomTetromino() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
