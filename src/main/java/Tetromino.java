import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.EnumMap;

public class Tetromino {
    private        Integer[][]      tetromino;
    private        Node             node;
    private static int              tetrominoCount = 0;
    private        TetrisGame       instance;
    private        int              x;
    private        int              y;
    private        int              z;
    private        boolean          finished       = false;
    
    Tetromino(int x, int y, int z, Tetrominoes type, TetrisGame instance) {
        this.instance = instance;
        
        tetromino = type.getCoords();
        tetrominoCount++;
        
        node = new Node("tetromino" + tetrominoCount);
        for (Integer[] block : tetromino) {
            Block newBlock = new Block(block[0], block[1], block[2], type.getColor(), instance);
            node.attachChild(newBlock.getGeometry());
            
            instance.addBlock(x + block[0], y + block[1], z + block[2], newBlock);
        }
        
        translate(x, y, z);
        
        instance.getRootNode().attachChild(node);
    }
    
    void translate(int dx, int dy, int dz) {
        node.move(dx, dy, dz);
        
        ArrayList<int[]> blocksMoved = new ArrayList<>();
        
        for (Integer[] block : tetromino) {
            try {
                instance.moveBlock(x + block[0], y + block[1], z + block[2], dx, dy, dz);
                blocksMoved.add(new int[] {x + dx, y + dy, z + dz});
            } catch (BlockMoveException e) {
                if (!e.shouldIgnore()) {
                    if (e.isVertical()) {
                        finished = true;
                    }
    
                    for (int[] movedBlock : blocksMoved) {
                        try {
                            instance.moveBlock(movedBlock[0], movedBlock[1], movedBlock[2], -dx, -dy, -dz);
                        } catch (Exception ignored) {}
                    }
    
                    node.move(-dx, -dy, -dz);
    
                    x -= dx;
                    y -= dy;
                    z -= dz;
    
                    break;
                }
            }
        }
        
        x += dx;
        y += dy;
        z += dz;
    }
    
    /**
     * Rotates the tetromino counter-clockwise about the x-axis.
     *
     * @param numRotations The multiple of 90 degrees to rotate the tetromino.
     */
    public void rotateX(int numRotations) {
        for (int i = 0; i < tetromino.length; i++) {
            tetromino[i][1] = tetromino[i][1] != 0 ? 0 : -tetromino[i][2];
            tetromino[i][2] = tetromino[i][2] != 0 ? 0 : -tetromino[i][1];
        }
    }
    
    boolean isFinished() {
        return finished;
    }
}
