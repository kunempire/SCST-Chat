package SCSTChat.chat;

import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.*;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;

import SCSTChat.SCSTChatApplication;
import SCSTChat.controller.ChatRoomViewController;
import SCSTChat.utils.AES;
import SCSTChat.utils.RSA;
import SCSTChat.utils.SQLUtil;

public class ClientAssistant implements Runnable {
    private static ChatRoomViewController controller;
    private static Socket socket = null;
    private static SecretKey sessionKey = null;
    private MessageSender messageSender = null;
    private MessageReceiver messageReceiver = null;

    @Autowired
    public ClientAssistant(ChatRoomViewController controller) { // hold the handle of GUI thread
        ClientAssistant.controller = controller;
    }

    @Override
    public void run() {
        generateSocket();

        try {
            generateSessionKey();
        } catch (IOException | ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        messageReceiver = new MessageReceiver();

        try {
            SQLUtil.DBConnect();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        getMessage();
    }

    private void generateSocket() {
        boolean isConnected = false;
        while (!isConnected) {
            try {
                socket = new Socket(SCSTChatApplication.ip, Integer.parseInt(SCSTChatApplication.port));
                isConnected = true;
            } catch (IOException e) {
                System.out.println("\033[31mconnection failed, retrying...\033[0m");
            }
        }
        controller.showPeerIp("");
    }

    private static void generateSessionKey() throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        ObjectInputStream sPublicKeyStream = new ObjectInputStream(socket.getInputStream());
        PublicKey sPublicKey = (PublicKey) sPublicKeyStream.readObject();
        System.out.println("\033[36mServer Public Key Received: \033[0m" + Base64.getEncoder().encodeToString(sPublicKey.getEncoded()));
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey aesKey = keyGenerator.generateKey();
        System.out.println("\033[36mClient Session key: \033[0m" + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
        byte[] encryptedAESKey = RSA.encrypt(aesKey.getEncoded(), sPublicKey);
        System.out.println("\033[36mEncrypted Session Key: \033[0m" + Base64.getEncoder().encodeToString(encryptedAESKey));
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(encryptedAESKey.length);
        dos.flush();
        dos.write(encryptedAESKey);
        dos.flush();
        sessionKey = aesKey;
    }

    public void getMessage() {
        MessageReceiver.Callback callback = (type, cipher, message) -> {
            if (type.equals("FILE")) {
                controller.showReceivedFile(message);
            } else if (type.equals("MSG")) {
                controller.showReceivedData(cipher);
                controller.showReceiveMsg(message);
            }

        };

        try{
            messageReceiver.listening(callback,socket,sessionKey);
        }catch (Exception e){
            try {
                socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public void sendMessage(String message) {
        System.out.println("\033[34mSending Message\033[0m");
        messageSender = new MessageSender(socket, message, sessionKey);
        new Thread(messageSender).start();
        String encryptedData;
        try {
            encryptedData = AES.encrypt(message, sessionKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        controller.showEncrytpedData(encryptedData);
    }

    public void sendFile(String filePath, String fileName) {
        System.out.println("\033[34mSending File\033[0m");
        new Thread(new FileSender(socket, sessionKey, filePath, fileName)).start();
    }


}
