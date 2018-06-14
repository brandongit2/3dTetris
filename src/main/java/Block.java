import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * The smallest unit in 3D Tetris.
 */
public class Block {
    private Geometry geometry;
    private int blockCount = 0;
    
    public Block(int x, int y, int z, Color color, TetrisGame instance) {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        geometry = new Geometry("block" + blockCount, box);
        Material mat = new Material(instance.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        String texture = "red.png";
        switch (color) {
            case RED:
                texture = "red.png";
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
        geometry.setLocalTranslation(x, y, z);
        
        instance.getRootNode().attachChild(geometry);
    }
    
    Geometry getGeometry() {
        return geometry;
    }
}
