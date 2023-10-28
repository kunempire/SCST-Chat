package SCSTChat;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Parent;

/**
 * app start screen
 */
public class StartScreen extends SplashScreen {

    @Override
    public Parent getParent() {
        return super.getParent();
    }

    @Override
    public boolean visible() {
        return super.visible();
    }

    @Override
    public String getImagePath() {
        return "/images/Welcome.png"; // parent: resources
    }

}
