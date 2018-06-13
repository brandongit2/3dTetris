import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.EnumMap;

public class Tetromino {
    private        Integer[][]      tetromino;
    private        Node             node;
    private static int              tetrominoCount = 0;
    private        ArrayList<Block> blocks         = new ArrayList<>();
    private        ArrayList<Block> prevBlocks     = new ArrayList<>();
    private        TetrisGame       instance;
    private        int              x;
    private        int              y;
    private        int              z;
    private boolean finished = false;
    
    private static EnumMap<Tetrominoes, Integer[][]> tetrominoes = new EnumMap<>(Tetrominoes.class);
    
    static {
        tetrominoes.put(Tetrominoes.O, new Integer[][] {
          {0, 0, 0},
          {0, 0, 1},
          {1, 0, 0},
          {1, 0, 1}
        });
    }
    
    Tetromino(int x, int y, int z, Tetrominoes type, TetrisGame instance) {
        this.instance = instance;
        
        tetromino = tetrominoes.get(type);
        tetrominoCount++;
        
        node = new Node("tetromino" + tetrominoCount);
        for (Integer[] block : tetromino) {
            Block newBlock = new Block(block[0], block[1], block[2], instance);
            blocks.add(newBlock);
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
                System.out.println("up top " + blocksMoved.size());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("down low " + blocksMoved.size());
                for (int[] movedBlock : blocksMoved) {
                    try {
                        instance.moveBlock(movedBlock[0], movedBlock[1], movedBlock[2], -dx, -dy, -dz);
                    } catch (Exception ignored) {
                    }
                }
                
                node.move(-dx, -dy, -dz);
    
                x -= dx;
                y -= dy;
                z -= dz;
                
                finished = true;
                break;
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
