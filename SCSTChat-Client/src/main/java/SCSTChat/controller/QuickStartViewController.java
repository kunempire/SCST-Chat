package SCSTChat.controller;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import de.felixroske.jfxsupport.FXMLController;

import SCSTChat.SCSTChatApplication;
import SCSTChat.view.ChatRoomView;
import SCSTChat.view.QuickStartView;

@FXMLController
public class QuickStartViewController implements Initializable {

    private String roleString = null;

    private Stage stage;

    @Autowired
    QuickStartView quickStartView;

    // fxml component
    @FXML
    private ToggleGroup role;
    @FXML
    private TextField ListeningPort;
    @FXML
    private TextField YourAddress;
    @FXML
    private TextField ServerPort;
    @FXML
    private TextField ServerAddress;

    // onaction of component
    @FXML
    void getMyIp() {
        YourAddress.clear();
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            YourAddress.setText(localhost.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ConnectPeer() {
        RadioButton selectedRadioButton = (RadioButton) role.getSelectedToggle();

        roleString = selectedRadioButton.getText();
        if (roleString.equals("Server")) {
            System.out.println("\033[31mServer\033[0m");
            SCSTChatApplication.ip = YourAddress.getText();
            SCSTChatApplication.port = ListeningPort.getText();
        } else if (roleString.equals("Client")) {
            System.out.println("\033[31mClient\033[0m");
            SCSTChatApplication.ip = ServerAddress.getText();
            SCSTChatApplication.port = ServerPort.getText();
        }

        if (checkInput()) { // check connection
            SCSTChatApplication.showView(ChatRoomView.class);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Connection Failed!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    void init() {
        Platform.runLater(() -> {
            Parent parent = quickStartView.getView();
            QuickStartViewController.this.stage = (Stage) parent.getScene().getWindow();
            stage.setTitle("Chat DIY");
            stage.setResizable(false);
            stage.setOnCloseRequest(event -> System.exit(-1));
        });
    }

    boolean checkInput() {
        // check input
        if (roleString == null || (SCSTChatApplication.ip.equals("") || SCSTChatApplication.port.equals(""))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid Input!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return false;
        }
        SCSTChatApplication.role = roleString;
        // try connection
        try {
            if (roleString.equals("Client")) {
                Socket socket = new Socket(SCSTChatApplication.ip, Integer.parseInt(SCSTChatApplication.port));
                socket.close();
            } else if (roleString.equals("Server")) {
                ServerSocket serverSocket = new ServerSocket(Integer.parseInt(SCSTChatApplication.port));
                final boolean[] isClientConnected = {false};
                Thread acceptThread = new Thread(() -> {
                    try {
                        serverSocket.accept();
                        isClientConnected[0] = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                acceptThread.start();
                acceptThread.join(20000);  // wait client
                if (!isClientConnected[0]) {
                    serverSocket.close();
                    acceptThread.interrupt();
                    return false;
                }
                serverSocket.close(); // if not, the port is occupied
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

}

