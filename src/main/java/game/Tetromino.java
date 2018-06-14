package game;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static game.Rotations.CLOCKWISE;

/**
 * A collection of {@see game.Block}s.
 */
class Tetromino {
    /**
     * An array of {@see game.Coordinate}s representative of {@see game.Block}s.
     */
    private        Coordinate[]     tetromino;
    /**
     * Whether this {@see game.Tetromino} is ready to be replaced.
     */
    private        boolean          finished       = false;
    private        Node             node;
    private        TetrisGame       instance;
    private        Coordinate       position       = new Coordinate();
    private static int              tetrominoCount = 0;
    private        ArrayList<Block> blocks         = new ArrayList<>();
    
    Tetromino(Coordinate pos, Tetrominoes type, TetrisGame instance) {
        this.instance = instance;
        
        tetromino = type.getCoords();
        
        node = new Node("tetromino" + tetrominoCount);
        tetrominoCount++;
        
        for (Coordinate coordinate : tetromino) {
            Block newBlock = new Block(coordinate, type.getColor(), instance, this);
            blocks.add(newBlock);
            node.attachChild(newBlock.getGeometry());
            
            try {
                instance.setBlock(coordinate.add(pos), newBlock);
            } catch (BlockMoveException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        translate(pos, true);
        
        instance.getRootNode().attachChild(node);
    }
    
    void translate(Coordinate shift, boolean initialize) {
        Coordinate[] newTetromino = new Coordinate[tetromino.length];
        boolean isSafe = true;
        
        // Copy tetromino to newTetromino.
        for (int i = 0; i < newTetromino.length; i++) {
            newTetromino[i] = new Coordinate(tetromino[i].x, tetromino[i].y, tetromino[i].z);
        }
        
        for (Coordinate coordinate : tetromino) {
            if (!instance.isInRange(position.add(coordinate).add(shift)) && !initialize) {
                isSafe = false;
            }
        }
        
        for (Coordinate coordinate : tetromino) {
            if (!initialize && isSafe) {
                try {
                    instance.moveBlock(position.add(coordinate), position.add(coordinate).add(shift));
                } catch (BlockMoveException e) {
                    e.printStackTrace();
                    if (e.isFinished()) {
                        finished = true;
                        return;
                    }
                }
            }
        }
    
        if (isSafe) {
            node.move(shift.x, shift.y, shift.z);
            position = position.add(shift);
        }
        
        // Check whether the tetromino is finished or not.
        for (int i = 0; i < newTetromino.length; i++) {
            if (instance.isOccupied(tetromino[i].add(position).add(0, -1, 0), this) || tetromino[i].add(position).y == 0) {
                finished = true;
                break;
            }
        }
    }
    
    /**
     * Rotates the tetromino counter-clockwise about the x-axis.
     *
     * @param axis      The axis to rotate about.
     * @param direction The direction to rotate in.
     */
    void rotate(Axes axis, Rotations direction) {
        ArrayList<Coordinate[]> changedBlocks = new ArrayList<>();
        Coordinate[] newTetromino = new Coordinate[tetromino.length];
        for (int i = 0; i < tetromino.length; i++) {
            newTetromino[i] = new Coordinate(tetromino[i].x, tetromino[i].y, tetromino[i].z);
        }
        
        for (int i = 0; i < tetromino.length; i++) {
            switch (axis) {
                case X:
                    if (direction == CLOCKWISE) {
                        newTetromino[i].y = -tetromino[i].z;
                        newTetromino[i].z = tetromino[i].y;
                    } else {
                        newTetromino[i].y = tetromino[i].z;
                        newTetromino[i].z = -tetromino[i].y;
                    }
                    break;
                case Y:
                    if (direction == CLOCKWISE) {
                        newTetromino[i].x = -tetromino[i].z;
                        newTetromino[i].z = tetromino[i].x;
                    } else {
                        newTetromino[i].x = tetromino[i].z;
                        newTetromino[i].z = -tetromino[i].x;
                    }
                    break;
                default: // case Z
                    if (direction == CLOCKWISE) {
                        newTetromino[i].x = tetromino[i].y;
                        newTetromino[i].y = -tetromino[i].x;
                    } else {
                        newTetromino[i].y = tetromino[i].z;
                        newTetromino[i].z = -tetromino[i].y;
                    }
            }
            
            for (Coordinate block : newTetromino) {
                changedBlocks.add(new Coordinate[] {tetromino[i], newTetromino[i]});
                
                // If rotation failed
                if (!(instance.getBlock(position.add(block)) == null || instance.getBlock(position.add(block))
                                                                                .getParent() == this) || !instance.isInRange(position.add(block))) {
                    return;
                }
            }
            
            try {
                instance.moveBlock(position.add(tetromino[i]), position.add(newTetromino[i]));
                changedBlocks.add(new Coordinate[] {position.add(tetromino[i]), position.add(newTetromino[i])});
            } catch (BlockMoveException e) {
                if (e.isFinished()) {
                    finished = true;
                    return;
                }
            }
            
            node.getChild(i).setLocalTranslation(newTetromino[i].x, newTetromino[i].y, newTetromino[i].z);
            
            for (int j = 0; j < tetromino.length; j++) {
                tetromino[j] = new Coordinate(newTetromino[j].x, newTetromino[j].y, newTetromino[j].z);
            }
        }
        
        for (Coordinate[] pair : changedBlocks) {
            try {
                instance.moveBlock(pair[0], pair[1]);
            } catch (BlockMoveException ignored) {}
        }
    }
    
    private void printTetromino(Coordinate[] tetromino) {
        System.out.println("---------------");
        for (Coordinate i : tetromino) {
            System.out.println(i.toString());
        }
    }
    
    boolean isFinished() {
        return finished;
    }
    
    Coordinate getPosition() {
        return position;
    }
    
    ArrayList<Block> getBlocks() {
        return blocks;
    }
}
