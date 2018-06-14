package game;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import jdk.nashorn.internal.objects.annotations.Getter;

/**
 * The smallest unit in 3D Tetris.
 */
public class Block {
    /**
     * The geometry associated with this {@see game.Block}.
     */
    private        Geometry   geometry;
    /**
     * The parent {@see game.Tetromino}.
     */
    private        Tetromino  parent;
    private static int        blockCount = 0;
    private        Coordinate position   = new Coordinate();
    
    public Block(Coordinate pos, Color color, TetrisGame instance, Tetromino parent) {
        position = pos;
        this.parent = parent;
        
        Box box = new Box(0.5f, 0.5f, 0.5f);
        geometry = new Geometry("block" + blockCount, box);
        blockCount++;
        
        Material mat = new Material(instance.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        
        String texture = "red.png";
        switch (color) {
            case RED:
                break;
            case ORANGE:
                texture = "orange.png";
                break;
            case YELLOW:
                texture = "yellow.png";
                break;
            case GREEN:
                texture = "green.png";
                break;
            case CYAN:
                texture = "cyan.png";
                break;
            case BLUE:
                texture = "blue.png";
                break;
            case MAGENTA:
                texture = "magenta.png";
                break;
            case LIGHT_GREY:
                texture = "light_grey.png";
                break;
            case DARK_GREY:
                texture = "dark_grey.png";
                break;
            case BROWN:
                texture = "brown.png";
                break;
        }
        mat.setTexture("ColorMap", instance.getAssetManager().loadTexture(texture));
        
        geometry.setMaterial(mat);
        geometry.setLocalTranslation(pos.x, pos.y, pos.z);
        
        instance.getRootNode().attachChild(geometry);
    }
    
    @Getter
    Geometry getGeometry() {
        return geometry;
    }
    
    @Getter
    Coordinate getPosition() {
        return position;
    }
    
    @Getter
    Tetromino getParent() {
        return parent;
    }
    
    @Getter
    Coordinate getGlobalPosition() {
        return position.add(parent.getPosition());
    }
}
