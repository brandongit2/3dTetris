import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.ScreenController;

public class StartScreenController extends DefaultScreenController implements ScreenController {
    
    private final TetrisGame tetrisGame;
    
    public StartScreenController(TetrisGame tetrisGame) {
        this.tetrisGame = tetrisGame;
    }
    
    public void startButtonClicked() {
        tetrisGame.startGame();
    }
}
