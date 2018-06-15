package game;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.core.GuiLayout;
import com.simsilica.lemur.style.BaseStyles;
import javafx.scene.layout.Pane;
import jdk.nashorn.internal.objects.annotations.Getter;

import static game.Rotations.CLOCKWISE;
import static game.Rotations.COUNTERCLOCKWISE;

/**
 * The main class for the game.
 */
public class TetrisGame extends SimpleApplication {
    private static final int       GAME_WIDTH          = 6;
    private static final int       GAME_LENGTH         = 6;
    private static final int       GAME_HEIGHT         = 16;
    private static final float     GRID_LINE_THICKNESS = 5.0f;
    private static final ColorRGBA GRID_COLOR          = new ColorRGBA(1, 1, 1, 0.1f);
    private int clear = 0;
    private GameState curGameState;
    
    /**
     * The 3D array of {@see game.Block}s used for game logic. Not used for rendering.
     */
    private Block[][][] blocks = new Block[GAME_WIDTH][GAME_HEIGHT][GAME_LENGTH];
    
    private long timeElapsed = 0;
    private long prevTime    = System.currentTimeMillis();
    
    /**
     * The active {@see game.Tetromino}.
     */
    private Tetromino active = null;
    
    /**
     * Gets a {@see game.Block} from the game area.
     *
     * @param coordinate The coordinate to query.
     *
     * @return The {@see game.Block} at the queried position. Can be null.
     */
    Block getBlock(Coordinate coordinate) {
        try {
            return blocks[coordinate.x][coordinate.y][coordinate.z];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    /**
     * Sets a position in the game area to {@see game.Block}.
     *
     * @param coordinate The {@see game.Coordinate} for the block to be placed.
     * @param block      The {@see game.Block} to be placed.
     */
    void setBlock(Coordinate coordinate, Block block) throws BlockMoveException {
        try {
            if (getBlock(coordinate) == null || getBlock(coordinate).getParent() == block.getParent()) {
                blocks[coordinate.x][coordinate.y][coordinate.z] = block;
            } else {
                throw new BlockMoveException("Attempted to set a block on an occupied spot: " + coordinate.toString(), BlockMoveExceptionType.SPACE_OCCUPIED, false);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BlockMoveException("Attempted to set a block out of the game area: " + coordinate.toString(), BlockMoveExceptionType.OUT_OF_BOUNDS, false);
        } catch (NullPointerException ignored) {}
    }
    
    /**
     * Moves a {@see game.Block} from one position to another.
     *
     * @param from The initial coordinates of the {@see game.Block}.
     * @param to   The final coordinates of the {@see game.Block}.
     */
    void moveBlock(Coordinate from, Coordinate to) throws BlockMoveException {
        try {
            Block bl = getBlock(from);
            removeBlock(from);
            setBlock(to, bl);
        } catch (BlockMoveException e) {
            throw new BlockMoveException(e.getMessage(), e.getType(), from.subtract(to).y > 0);
        }
    }
    
    /**
     * Moves a block in relative coordinates.
     *
     * @param start The starting position of the {@see game.Block}.
     * @param shift The amount to shift the {@see game.Block}.
     */
    void shiftBlock(Coordinate start, Coordinate shift) throws BlockMoveException {
        moveBlock(start, start.add(shift));
    }
    
    /**
     * Removes a block from the {@code blocks} array.
     *
     * @param coordinate The {@see game.Coordinate} of the block to be removed.
     */
    void removeBlock(Coordinate coordinate) {
        if (getBlock(coordinate) != null) {
            rootNode.detachChild(getBlock(coordinate).getGeometry());
            blocks[coordinate.x][coordinate.y][coordinate.z] = null;
        }
    }
    
    /**
     * Checks whether a space in the game area is occupied or not by a {@see game.Block} of another {@code Tetromino}.
     *
     * @param coordinate The coordinate in space to be queried.
     * @param tetromino  The {@code Tetromino} type to be queried.
     *
     * @return {@code true} if the space is occupied, {@code} false if it is not.
     */
    boolean isOccupied(Coordinate coordinate, Tetromino tetromino) {
        boolean result;
        if (getBlock(coordinate) == null) {
            result = false;
        } else {
            result = getBlock(coordinate).getParent() != tetromino;
        }
        
        return result;
    }
    
    /**
     * Checks whether a given coordinate is within the game area.
     *
     * @param coordinate The coordinate to be queried.
     *
     * @return {@code true} if the coordinate is in the game area, {@code false} if it is not.
     */
    boolean isInRange(Coordinate coordinate) {
        return coordinate.x >= 0 && coordinate.x < GAME_WIDTH && coordinate.y >= 0 && coordinate.y < GAME_HEIGHT && coordinate.z >= 0 && coordinate.z < GAME_LENGTH;
    }
    
    /**
     * Draws a grid.
     *
     * @param directionX Specifies a direction for the x-axis of the grid.
     * @param directionY Specifies a direction for the y-axis of the grid.
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
    
    /**
     * Draws a line between two coordinates.
     *
     * @param x1 The x coordinate of the beginning of the line.
     * @param y1 The y coordinate of the beginning of the line.
     * @param z1 The z coordinate of the beginning of the line.
     * @param x2 The x coordinate of the end of the line.
     * @param y2 The y coordinate of the end of the line.
     * @param z2 The z coordinate of the end of the line.
     */
    private void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
        Line     line = new Line(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
        Geometry geom = new Geometry("line", line);
        Material mat  = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        mat.setColor("Color", GRID_COLOR);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setLineWidth(GRID_LINE_THICKNESS);
        
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }
    
    /**
     * Creates a new active tetromino.
     */
    private void newTetromino() {
        active = new Tetromino(new Coordinate(GAME_WIDTH / 2, GAME_HEIGHT - 2, GAME_LENGTH / 2), Tetrominoes.randomTetromino(), this);
    }
    
    @Getter
    public Block[][][] getBlocks() {
        return blocks;
    }
    
    public void showMenu() {
        
        curGameState = GameState.MENU;
        int width = settings.getWidth();
        int height = settings.getHeight();
        
        // Lemur Setup
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
    
        Container root = new Container();
        BorderLayout borderLayout = new BorderLayout();
        root.setLayout(borderLayout);
        root.setLocalTranslation(0, height/2, 0);
    
        Container middle = new Container();
    
        Label title = new Label("Welcome to 3D Tetris");
        title.setFontSize(40);
        title.setTextVAlignment(VAlignment.Center);
        middle.addChild(title);
        
        Button playButton = new Button("Play");
        playButton.setFontSize(20);
        playButton.setTextHAlignment(HAlignment.Center);
        middle.addChild(playButton);
        
        Container instructionsPanel = new Container();
        RollupPanel rollupPanel = new RollupPanel("Instructions", instructionsPanel, "");
        rollupPanel.getTitleElement().setTextHAlignment(HAlignment.Center);
        instructionsPanel.addChild(new Label("Here are the instructions."));
        rollupPanel.setOpen(false);
        middle.addChild(rollupPanel);
        
    
        borderLayout.addChild(BorderLayout.Position.Center, middle);
        
        playButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button button) {
                curGameState = GameState.PLAYING;
                guiNode.detachChild(root);
                return;
            }
        });
        
        guiNode.attachChild(root);
    }
    
    @Override
    public void simpleInitApp() {
    
        showMenu();
        
        flyCam.setEnabled(false);
        Node cameraNode = new Node("cameraOrbit");
        cameraNode.setLocalTranslation(GAME_WIDTH / 2.0f - 0.5f, GAME_HEIGHT / 3.0f - 0.5f, GAME_LENGTH / 2.0f - 0.5f);
        rootNode.attachChild(cameraNode);
        ChaseCamera chaseCamera = new ChaseCamera(cam, cameraNode, inputManager);
        
        chaseCamera.setInvertVerticalAxis(true);
        chaseCamera.setMinVerticalRotation((float) (-Math.PI / 2));
        chaseCamera.setDefaultDistance(20);
        chaseCamera.setMinDistance(Math.max(GAME_WIDTH, GAME_LENGTH));
        chaseCamera.setMaxDistance(Math.max(GAME_WIDTH, GAME_LENGTH) * 5);
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setRotationSpeed(1.5f);
        
        drawGrid(0, 0, 0, new Vector3f(1, 0, 0), new Vector3f(0, 0, 1), GAME_WIDTH, GAME_LENGTH, GAME_WIDTH, GAME_LENGTH); // Base
        drawGrid(GAME_WIDTH, 0, 0, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0), GAME_LENGTH, GAME_HEIGHT, GAME_LENGTH,
                 GAME_HEIGHT); // Positive X side
        drawGrid(0, 0, GAME_LENGTH, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH,
                 GAME_HEIGHT); // Positive Z side
        drawGrid(0, 0, 0, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0), GAME_LENGTH, GAME_HEIGHT, GAME_LENGTH, GAME_HEIGHT); // Negative X side
        drawGrid(0, 0, 0, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH, GAME_HEIGHT); // Negative Z side
        newTetromino();
        timeElapsed = 0;
        prevTime = System.currentTimeMillis();
        
        inputManager.addMapping("rotateCwX", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("rotateCwY", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("rotateCwZ", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("rotateCcwX", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("rotateCcwY", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("rotateCcwZ", new KeyTrigger(KeyInput.KEY_Q));
        
        inputManager.addMapping("movePosX", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("movePosZ", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("moveNegX", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("moveNegZ", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("moveDown", new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addListener(actionListener, "rotateCwX", "rotateCwY", "rotateCwZ", "rotateCcwX", "rotateCcwY", "rotateCcwZ",
                                 "movePosX", "movePosZ", "moveNegX", "moveNegZ", "moveDown");
    }
    
    private final ActionListener actionListener = (String name, boolean keyPressed, float tpf) -> {
        if (name.equals("rotateCwX") && keyPressed) {
            active.rotate(Axes.X, CLOCKWISE);
        } else if (name.equals("rotateCwY") && keyPressed) {
            active.rotate(Axes.Y, CLOCKWISE);
        } else if (name.equals("rotateCwZ") && keyPressed) {
            active.rotate(Axes.Z, CLOCKWISE);
        } else if (name.equals("rotateCcwX") && keyPressed) {
            active.rotate(Axes.X, COUNTERCLOCKWISE);
        } else if (name.equals("rotateCcwY") && keyPressed) {
            active.rotate(Axes.Y, COUNTERCLOCKWISE);
        } else if (name.equals("rotateCcwZ") && keyPressed) {
            active.rotate(Axes.Z, COUNTERCLOCKWISE);
        } else if (name.equals("movePosX") && keyPressed) {
            active.translate(new Coordinate(1, 0, 0), false);
        } else if (name.equals("movePosZ") && keyPressed) {
            active.translate(new Coordinate(0, 0, 1), false);
        } else if (name.equals("moveNegX") && keyPressed) {
            active.translate(new Coordinate(-1, 0, 0), false);
        } else if (name.equals("moveNegZ") && keyPressed) {
            active.translate(new Coordinate(0, 0, -1), false);
        } else if (name.equals("moveDown") && keyPressed) {
            active.translate(new Coordinate(0, -1, 0), false);
        }
    };
    
    @Override
    public void simpleUpdate(float tpf) {
        timeElapsed = System.currentTimeMillis() - prevTime;
        if (timeElapsed > 1000) {
            active.translate(new Coordinate(0, -1, 0), false);
            
            // Check for full layers
            for (int k = 0; k < GAME_HEIGHT; k++) {
                int numBlocks = 0;
                for (int i = 0; i < GAME_WIDTH; i++) {
                    for (int j = 0; j < GAME_LENGTH; j++) {
                        Coordinate a = new Coordinate(i, clear, j);
                        System.out.println(a + " " + getBlock(a));
                        if (getBlock(a) != null) {
                            numBlocks++;
                        }
                    }
                }
                System.out.println(numBlocks);
                if (numBlocks == GAME_LENGTH * GAME_WIDTH) {
                    clear++;
                }
            }
            //System.out.println(clear);
            
            if (active.isFinished()) {
                newTetromino();
            }
            
            timeElapsed = 0;
            prevTime = System.currentTimeMillis();
        }
    }
    
    @Override
    public void start() {
        setShowSettings(false);
        super.start();
    }
    
    /**
     * Entry point for the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new TetrisGame().start();
    }
}
