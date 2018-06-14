package game;

import com.jme3.math.Vector3f;

/**
 * Represents a coordinate of integers in 3D space. Only used for block positions.
 */
public class Coordinate {
    int x;
    int y;
    int z;
    
    /**
     * Creates a new {@see game.Coordinate} and initializes all values to zero.
     */
    Coordinate() {
        this(0, 0, 0);
    }
    
    Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Adds two {@see game.Coordinate}s.
     *
     * @param x The amount to add to the x coordinate.
     * @param y The amount to add to the y coordinate.
     * @param z The amount to add to the z coordinate.
     *
     * @return The sum of the two {@see game.Coordinate}s.
     */
    Coordinate add(int x, int y, int z) {
        return new Coordinate(
          this.x + x,
          this.y + y,
          this.z + z);
    }
    
    Coordinate add(Coordinate addend) {
        return add(addend.x, addend.y, addend.z);
    }
    
    Coordinate subtract(Coordinate subtrahend) {
        return new Coordinate(this.x - subtrahend.x, this.y - subtrahend.y, this.z - subtrahend.z);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    
    Coordinate copy() {
        return new Coordinate(x, y, z);
    }
}
