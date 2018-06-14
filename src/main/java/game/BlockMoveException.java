package game;

/**
 * Used for illegal {@see game.Block} movements.
 */
class BlockMoveException extends Exception {
    private BlockMoveExceptionType type;
    private boolean isFinished;
    
    BlockMoveException(String msg, BlockMoveExceptionType type, boolean isFinished) {
        super(msg);
        
        this.type = type;
        this.isFinished = isFinished;
    }
    
    BlockMoveExceptionType getType() {
        return type;
    }
    
    boolean isFinished() {
        return isFinished;
    }
}
