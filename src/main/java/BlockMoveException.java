class BlockMoveException extends Exception {
    private boolean isVertical;
    private boolean shouldIgnore;
    
    BlockMoveException(String msg, boolean isVertical, boolean shouldIgnore) {
        super(msg);
        
        this.isVertical = isVertical;
        this.shouldIgnore = shouldIgnore;
    }
    
    boolean isVertical() {
        return isVertical;
    }
    
    boolean shouldIgnore() {
        return shouldIgnore;
    }
}
