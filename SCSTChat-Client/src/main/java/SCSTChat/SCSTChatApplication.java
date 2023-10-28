package SCSTChat;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import SCSTChat.view.LogInView;

@SpringBootApplication
public class SCSTChatApplication extends AbstractJavaFxApplicationSupport {

    // though public variables are simple to operate
    // user's variables should be private
    public static String role;
    public static String ip;
    public static String port;

    public static void main(String[] args) { // VM options "-Dserver.port=xxxx" to start multiple apps
        launch(SCSTChatApplication.class, LogInView.class, new StartScreen(), args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
    }

    @Override
    public Collection<Image> loadDefaultIcons() {
        return Arrays.asList(new Image(this.getClass().getClassLoader().getResource("images/SCSTChat.ico").toExternalForm()));
    }


}
