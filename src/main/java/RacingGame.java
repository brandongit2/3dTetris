import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import com.sun.istack.internal.Nullable;

public class RacingGame extends SimpleApplication {
    private static final int GAME_WIDTH  = 8;
    private static final int GAME_LENGTH = 8;
    private static final int GAME_HEIGHT = 16;
    
    @Nullable
    Geometry[][][] blocks = new Geometry[GAME_WIDTH][GAME_LENGTH][GAME_HEIGHT];
    @Nullable
    private Geometry[][][] prevBlocks = new Geometry[GAME_WIDTH][GAME_LENGTH][GAME_HEIGHT];
    
    private long startTime   = System.currentTimeMillis();
    private long timeElapsed = startTime;
    
    @Nullable
    private Tetromino active = null;
    
    public void updateBlocks() {
        for (int i = 0; i < GAME_WIDTH; i++) {
            for (int j = 0; j < GAME_LENGTH; j++) {
                for (int k = 0; k < GAME_HEIGHT; k++) {
                    if (prevBlocks[i][j][k] == null && blocks[i][j][k] != null) {
                        rootNode.attachChild(blocks[i][j][k]);
                    } else if (prevBlocks[i][j][k] != null) {
                        rootNode.detachChild(prevBlocks[i][j][k]);
                    }
                }
            }
        }
        
        // Copy blocks into prevBlocks.
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                for (int k = 0; k < blocks[i][j].length; k++) {
                    prevBlocks[i][j][k] = blocks[i][j][k];
                }
            }
        }
    }
    
    public void addBlock(int x, int y, int z, Geometry block) {
        blocks[x][y][z] = block;
    }
    
    public void moveBlock(int x, int y, int z, int dx, int dy, int dz) throws Exception {
        try {
            if (blocks[x + dx][y + dy][z + dz] == null) {
                blocks[x + dx][y + dy][z + dz] = blocks[x][y][z];
                blocks[x][y][z] = null;
            } else {
                throw new Exception("Attempted to move a block to an occupied spot.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Attempted to move a block out of the game area.");
        }
    }
    
    public void removeBlock(Geometry block) {
        rootNode.detachChild(block);
    }
    
    /**
     * Draws a grid.
     *
     * @param directionX Specify a direction for the x-axis of the grid.
     * @param directionY Specify a direction for the y-axis of the grid.
     * @param width      The width of the grid, or size in y-direction.
     * @param length     The length of the grid, or size in z-direction.
     * @param xDivs      The number of subdivisions in the x direction.
     * @param yDivs      The number of subdivisions in the y direction.
     */
    private void drawGrid(float x, float y, float z, Vector3f directionX, Vector3f directionY, float width, float length, int xDivs, int yDivs) {
        float spacingX = width / xDivs;
        float spacingY = length / yDivs;
        
        directionX = directionX.normalize();
        directionY = directionY.normalize();
        
        x -= 0.5f;
        y -= 0.5f;
        z -= 0.5f;
        
        for (float i = 0; i <= width; i += spacingX) {
            drawLine(x + directionX.x * i, y + directionX.y * i, z + directionX.z * i, x + directionX.x * i + directionY.x * length,
                     y + directionX.y * i + directionY.y * length, z + directionX.z * i + directionY.z * length);
        }
        
        for (float i = 0; i <= length; i += spacingY) {
            drawLine(x + directionY.x * i, y + directionY.y * i, z + directionY.z * i, x + directionY.x * i + directionX.x * width,
                     y + directionY.y * i + directionX.y * width, z + directionY.z * i + directionX.z * width);
        }
    }
    
    private void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
        Line     line = new Line(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
        Geometry geom = new Geometry("line", line);
        Material mat  = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setLineWidth(5.0f);
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }
    
    @Override
    public void simpleInitApp() {
        drawGrid(0, 0, 0, new Vector3f(1, 0, 0), new Vector3f(0, 0, 1), GAME_WIDTH, GAME_LENGTH, GAME_WIDTH, GAME_LENGTH); // Base
        drawGrid(GAME_WIDTH, 0, 0, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0), GAME_LENGTH, GAME_HEIGHT, GAME_LENGTH,
                 GAME_HEIGHT); // Positive X side
        drawGrid(0, 0, GAME_LENGTH, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH,
                 GAME_HEIGHT); // Positive Z side
        drawGrid(0, 0, 0, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0), GAME_LENGTH, GAME_HEIGHT, GAME_LENGTH, GAME_HEIGHT); // Negative X side
        drawGrid(0, 0, 0, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH, GAME_HEIGHT); // Negative Z side
        Tetromino o = new Tetromino(3, 15, 3, Tetrominoes.O, this);
        active = o;
        
        new GameLogic(this);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        timeElapsed = (System.currentTimeMillis() - startTime) % 2000;
        if (timeElapsed > 2000) {
            active.translate(0, -1, 0);
            timeElapsed = 0;
        }
    }
    
    /**
     * Entry point for the application.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        new RacingGame().start();
    }
}
