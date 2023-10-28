package SCSTChat.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import de.felixroske.jfxsupport.FXMLController;

import SCSTChat.register.RequestForServer;
import SCSTChat.register.ResponseFromServer;
import SCSTChat.SCSTChatApplication;
import SCSTChat.view.LogInView;
import SCSTChat.view.QuickStartView;

@FXMLController
public class LogInViewController implements Initializable {

    private int clientId;
    private Stage stage;


    @Autowired // auto implement LogInView handle into logInView
    LogInView logInView;

    // fxml component
    @FXML
    private Button quickStart;
    @FXML
    private Label register;

    // onaction of component
    @FXML
    void OpenQuickStartView() {
        SCSTChatApplication.showView(QuickStartView.class);
    }

    @FXML
    void openRegisterWindow() {
        // show register dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(this.stage);
        dialog.setTitle("Register A SCST ID");
        dialog.setContentText("Please enter your username:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String username = result.get();
            String hostName = "register server ip address";
            int portNumber = 8000;
            try {
                // forward request to RegisterServer
                Socket socket = new Socket(hostName, portNumber);
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                RequestForServer request = new RequestForServer("REGISTER", -1, username, "100.123.32.70", 8000);
                output.writeObject(request);
                try {
                    // receive and parse reponse
                    ResponseFromServer response;
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    response = (ResponseFromServer) input.readObject();
                    String msg = response.getResponseMsg();
                    clientId = response.getId();
                    System.out.println(msg + ": your id " + clientId);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                socket.close();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // alert window
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("REMAINDING");
            alert.setHeaderText("You must keep in mind your SCST ID!");
            alert.setContentText("your id is: " + clientId);
            alert.showAndWait();
        }
    }

    /**
     * initialize
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();

        // change cursor style
        quickStart.setOnMouseMoved(event -> quickStart.setCursor(Cursor.HAND));
        register.setOnMouseMoved(event -> register.setCursor(Cursor.HAND));
    }

    /**
     * start initial components
     */
    void init() {
        Platform.runLater(() -> { // run when GUI thread is available
            Parent parent = logInView.getView();
            LogInViewController.this.stage = (Stage) parent.getScene().getWindow();
            stage.setTitle("SCST Chat");
            stage.setResizable(false);
            stage.setOnCloseRequest(event -> System.exit(-1));
        });
    }

}
