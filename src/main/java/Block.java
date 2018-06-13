import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * The smallest unit in 3D Tetris.
 */
public class Block {
    private Geometry geometry;
    private int blockCount = 0;
    
    public Block(int x, int y, int z, TetrisGame instance) {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        geometry = new Geometry("block" + blockCount, box);
        Material mat = new Material(instance.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", instance.getAssetManager().loadTexture("red.png"));
        geometry.setMaterial(mat);
        geometry.setLocalTranslation(x, y, z);
        
        instance.getRootNode().attachChild(geometry);
    }
    
    Geometry getGeometry() {
        return geometry;
    }
}
