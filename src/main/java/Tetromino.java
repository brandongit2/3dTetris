import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.EnumMap;

public class Tetromino {
    private Integer[][] tetromino;
    private Node node;
    private int tetrominoCount = 0;
    private ArrayList<Block> blocks = new ArrayList<>();
    private ArrayList<Block> prevBlocks = new ArrayList<>();
    private RacingGame instance;
    
    private static EnumMap<Tetrominoes, Integer[][]> tetrominoes = new EnumMap<>(Tetrominoes.class);
    
    static {
        tetrominoes.put(Tetrominoes.O, new Integer[][]{
          {0, 0, 0},
          {0, 0, 1},
          {1, 0, 0},
          {1, 0, 1}
        });
    }
    
    Tetromino(int x, int y, int z, Tetrominoes type, RacingGame instance) {
        this.instance = instance;
        
        tetromino = tetrominoes.get(type);
        
        node = new Node("tetromino" + tetrominoCount);
        for (Integer[] block : tetromino) {
            Block newBlock = new Block(block[0], block[1], block[2], instance);
            blocks.add(newBlock);
            node.attachChild(newBlock.getGeometry());
            
            instance.blocks[block[0]][block[1]][block[2]] = newBlock.getGeometry();
        }
        
        translate(x, y, z);
        
        instance.getRootNode().attachChild(node);
    }
    
    void translate(int dx, int dy, int dz) {
        node.move(dx, dy, dz);
        System.out.println(node.getLocalTranslation());
        
        for (Integer[] block : tetromino) {
            try {
                instance.moveBlock(block[0], block[1], block[2], dx, dy, dz);
            } catch (Exception e) {
            
            }
        }
        
    }
    
    /**
     * Rotates the tetromino counter-clockwise about the x-axis.
     * @param numRotations The multiple of 90 degrees to rotate the tetromino.
     */
    public void rotateX(int numRotations) {
        for (int i = 0; i < tetromino.length; i++) {
            tetromino[i][1] = tetromino[i][1] != 0 ? 0 : -tetromino[i][2];
            tetromino[i][2] = tetromino[i][2] != 0 ? 0 : -tetromino[i][1];
        }
    }
}
