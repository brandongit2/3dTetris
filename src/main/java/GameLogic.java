import com.jme3.app.state.RootNodeAppState;

public class GameLogic {
    RacingGame instance;
    
    GameLogic(RacingGame instance) {
        this.instance = instance;
        
        gameLoop();
    }
    
    private void gameLoop() {
        System.out.println(instance.getStateManager().getState(RootNodeAppState.class));
    }
}
