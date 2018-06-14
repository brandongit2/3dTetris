import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEvent;
import de.lessvoid.nifty.NiftyEventAnnotationProcessor;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import com.jme3.niftygui.NiftyJmeDisplay;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.EventSubscriber;

public class TetrisGame extends SimpleApplication{
    private static final int GAME_WIDTH  = 8;
    private static final int GAME_LENGTH = 8;
    private static final int GAME_HEIGHT = 16;
    
    private String highscoresText = "";
    
    @Nullable
    Block[][][] blocks = new Block[GAME_WIDTH][GAME_HEIGHT][GAME_LENGTH];
    @Nullable
    private Block[][][] prevBlocks = new Block[GAME_WIDTH][GAME_HEIGHT][GAME_LENGTH];
    private ArrayList<Tetromino> tetrominoes = new ArrayList<>();
    
    private long startTime   = System.currentTimeMillis();
    private long timeElapsed = startTime;
    private long prevTime    = System.currentTimeMillis();
    
    private GameState curGameState;
    private Nifty nifty;
    
    @Nullable
    private Tetromino active = null;
    
    public void updateBlocks() {
        for (int i = 0; i < GAME_WIDTH; i++) {
            for (int j = 0; j < GAME_LENGTH; j++) {
                for (int k = 0; k < GAME_HEIGHT; k++) {
                    if (prevBlocks[i][j][k] == null && blocks[i][j][k] != null) {
                        rootNode.attachChild(blocks[i][j][k].getGeometry());
                    } else if (prevBlocks[i][j][k] != null) {
                        rootNode.detachChild(prevBlocks[i][j][k].getGeometry());
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
    
    void addBlock(int x, int y, int z, Block block) {
        blocks[x][y][z] = block;
    }
    
    void moveBlock(int x, int y, int z, int dx, int dy, int dz) throws BlockMoveException {
        try {
            System.out.println(blocks[x + dx][y + dy][z + dz].getGeometry().getParent() + " " + blocks[x][y][z].getGeometry().getParent());
        } catch (Exception ignored) {}
        try {
            if (blocks[x + dx][y + dy][z + dz] == null || blocks[x + dx][y + dy][z + dz].getGeometry().getParent() == blocks[x][y][z].getGeometry().getParent()) {
                blocks[x + dx][y + dy][z + dz] = blocks[x][y][z];
                blocks[x][y][z] = null;
            } else {
                throw new BlockMoveException("Attempted to move a block to an occupied spot.", dy != 0, x < 0 || x > GAME_WIDTH || y < 0 || y > GAME_HEIGHT || z < 0 || z > GAME_LENGTH);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BlockMoveException("Attempted to move a block out of the game area: " + (x + dx) + " " + (y + dy) + " " + (z + dz), dy != 0, x < 0 || x > GAME_WIDTH || y < 0 || y > GAME_HEIGHT || z < 0 || z > GAME_LENGTH);
        } catch (NullPointerException ignored) {
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
    
    private void newTetromino() {
        Tetromino tet = new Tetromino(3, 15, 3, Tetrominoes.randomTetromino(), this);
        active = tet;
    }
    
    @Override
    public void simpleInitApp() {
        //initialize
        TetrisGame tetrisGame = this;
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.addScreen("start", new ScreenBuilder("start"){{
            controller(new DefaultScreenController());
        }}.build(nifty));
        guiViewPort.addProcessor(niftyDisplay);
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        //screen
        nifty.addScreen("Screen_ID", new ScreenBuilder("Title Screen"){{
            controller(new StartScreenController(tetrisGame));
            //layer
            layer(new LayerBuilder("Layer_ID"){{
                //layer properties...
                childLayoutHorizontal();
                //panel
                layer(new LayerBuilder("MasterLayer"){{
                    
                    childLayoutHorizontal();
                    
                    panel(new PanelBuilder("Grid") {{
                        childLayoutCenter();
                        alignLeft();
                        width("50%");
                        height("100%");
                        backgroundColor("#000000");
                    }});
    
    
                    panel(new PanelBuilder("Start_Menu") {{
                        
                        childLayoutVertical();
                        alignRight();
                        width("50%");
                        height("100%");
    
                        //GUI
                        control(new LabelBuilder(){{
                            alignCenter();
                            color("#FFFFFF");
                            text("Welcome to Tetris!");
                            height("20%");
                            width("50%");
                        }});
                        control(new ButtonBuilder("Play_Button", "Play"){{
                            alignCenter();
                            height("10%");
                            width("30%");
                            interactOnClick("startButtonClicked()");
                        }});
                        control(new LabelBuilder(){{
                            alignCenter();
                            valignBottom();
                            height("100%");
                            width("100%");
                            text(highscoresText);
                        }});
                    }});
                }}); //panel
            }}); //layer
        }}.build(nifty));
        nifty.addScreen("empty", new ScreenBuilder("empty").build(nifty));
        nifty.gotoScreen("Screen_ID");
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);
    }
    
    
    public void startGame() {
        inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
        drawGrid(0, 0, 0, new Vector3f(1, 0, 0), new Vector3f(0, 0, 1), GAME_WIDTH, GAME_LENGTH, GAME_WIDTH, GAME_LENGTH); // Base
        drawGrid(GAME_WIDTH, 0, 0, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0), GAME_LENGTH, GAME_HEIGHT, GAME_LENGTH,
                 GAME_HEIGHT); // Positive X side
        drawGrid(0, 0, GAME_LENGTH, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH,
                 GAME_HEIGHT); // Positive Z side
        drawGrid(0, 0, 0, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0), GAME_LENGTH, GAME_HEIGHT, GAME_LENGTH, GAME_HEIGHT); // Negative X side
        drawGrid(0, 0, 0, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH, GAME_HEIGHT); // Negative Z side
        newTetromino();
        nifty.gotoScreen("empty");
        curGameState = GameState.PLAYING;
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        switch (curGameState) {
            case PLAYING:
                timeElapsed = System.currentTimeMillis() - prevTime;
                if (timeElapsed > 1000) {
                    active.translate(0, -1, 0);
                    if (active.isFinished()) {
                        newTetromino();
                    }
        
                    timeElapsed = 0;
                    prevTime = System.currentTimeMillis();
                }
                break;
        }
    }
    
    @Override
    public void start() {
        Scanner in = null;
        try {
            in = new Scanner(new File(this.getClass().getClassLoader().getResource("high scores.txt").toURI()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (in.hasNext()) {
            stringBuilder.append(in.nextLine() + "\n");
        }
        highscoresText = stringBuilder.toString();
        curGameState = GameState.MENU;
        //AppSettings appSettings = new AppSettings(true);
        //appSettings.setFullscreen(true);
        //setSettings(appSettings);
        setShowSettings(false);
        super.start();
    }
    
    public void setHighscoresText(String highscoresText) {
        this.highscoresText = highscoresText;
    }
    
    /**
     * Entry point for the application.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        TetrisGame tetrisGame = new TetrisGame();
        tetrisGame.start();
    }
}
