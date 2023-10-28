package SCSTChat.controller;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import SCSTChat.chat.ClientAssistant;
import SCSTChat.chat.ServerAssistant;
import SCSTChat.chat.MessageHelper;
import SCSTChat.view.ChatRoomView;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import SCSTChat.SCSTChatApplication;

@FXMLController
public class ChatRoomViewController implements Initializable {
    private ServerAssistant serverAssistant = null;
    private ClientAssistant clientAssistant = null;
    private String sendType = "Message";
    private String fileName = null;
    private String filePath = null;

    private Stage stage;

    @Autowired
    ChatRoomView chatRoomView;

    // fxml component
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messageBox;
    @FXML
    private Label myIP;
    @FXML
    private Label peerIP;
    @FXML
    private TextArea receivedTextField;
    @FXML
    private TextArea encryptedTextField;
    @FXML
    private TextField inputMessage;
    @FXML
    private Button file;

    // onaction of component
    @FXML
    void sendMessage() {
        showData();
    }

    @FXML
    void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a File to Send");
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
            fileName = selectedFile.getName();
            inputMessage.setText("< " + filePath + " > selected");
            sendType = "File";
        } else {
            System.out.println("\033[32mNo file selected\033[0m");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();

        // bind keyboard
        inputMessage.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                showData();
            }
        });
        file.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                showData();
            }
        });
        // set component
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            myIP.setText(localhost.getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        if (SCSTChatApplication.role.equals("Client")) { // create assistant thread
            clientAssistant = new ClientAssistant(this);
        } else if (SCSTChatApplication.role.equals("Server")) {
            serverAssistant = new ServerAssistant(this);

        }
        Platform.runLater(() -> {
            if (SCSTChatApplication.role.equals("Client")) {
                new Thread(clientAssistant).start();
            } else if (SCSTChatApplication.role.equals("Server")) {
                new Thread(serverAssistant).start();
            }
        });

        System.out.println("\033[32mChatRoomViewController初始化完毕\033[0m");
    }

    void init() {
        Platform.runLater(() -> {
            Parent parent = chatRoomView.getView();
            ChatRoomViewController.this.stage = (Stage) parent.getScene().getWindow();
            stage.setTitle("SCST ChatRoom");
            stage.setResizable(false);
            stage.setOnCloseRequest(event -> System.exit(-1));
        });
    }

    private void showData() {
        if (sendType.equals("Message")) {
            showMsg();
        } else if (sendType.equals("File")) {
            showFile(fileName);
        }
        inputMessage.clear();

    }

    private void showFile(String fileName) {
        MessageHelper helper = new MessageHelper();
        if (SCSTChatApplication.role.equals("Client")) {
            clientAssistant.sendFile(filePath, fileName);
        } else if (SCSTChatApplication.role.equals("Server")) {
            serverAssistant.sendFile(filePath, fileName);
        }
        messageBox.getChildren().add(helper.createFileBox(fileName, helper.getTime(), "ME"));

    }

    private void showMsg() {
        String msg = inputMessage.getText();
        MessageHelper helper = new MessageHelper();
        if (SCSTChatApplication.role.equals("Client")) {
            clientAssistant.sendMessage(msg);
        } else if (SCSTChatApplication.role.equals("Server")) {
            serverAssistant.sendMessage(msg);
        }
        messageBox.getChildren().add(helper.creatMessageBox(msg, helper.getTime(), "ME"));
    }

    // called by assistant thread
    // remember that ONLY GUI thread can change its components
    public void showPeerIp(String clientIp) {
        Platform.runLater(() -> {
            if (SCSTChatApplication.role.equals("Client")) {
                peerIP.setText(SCSTChatApplication.ip);
            } else if (SCSTChatApplication.role.equals("Server")) {
                peerIP.setText(clientIp);
            }
        });
    }

    public void showEncrytpedData(String encrytpedData) {
        Platform.runLater(() -> encryptedTextField.setText(encrytpedData));
    }

    public void showReceivedData(String receivedData) {
        Platform.runLater(() -> receivedTextField.setText(receivedData));
    }

    public void showReceiveMsg(String msg) {
        Platform.runLater(() -> {
            MessageHelper helper = new MessageHelper();
            messageBox.getChildren().add(helper.creatMessageBox(msg, helper.getTime(), "PEER"));
        });
    }

    public void showReceivedFile(String fileName) {
        Platform.runLater(() -> {
            MessageHelper helper = new MessageHelper();
            messageBox.getChildren().add(helper.createFileBox(fileName, helper.getTime(), "PEER"));
        });
    }

}
