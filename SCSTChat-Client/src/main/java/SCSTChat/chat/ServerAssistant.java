package SCSTChat.chat;

import SCSTChat.SCSTChatApplication;
import SCSTChat.controller.ChatRoomViewController;
import SCSTChat.utils.AES;
import SCSTChat.utils.RSA;
import SCSTChat.utils.SQLUtil;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.Base64;

public class ServerAssistant implements Runnable{
    private static ChatRoomViewController controller;
    private static Socket socket=null;
    private static SecretKey sessionKey=null;
    private MessageSender messageSender=null;
    private MessageReceiver messageReceiver=null;




    @Autowired
    public ServerAssistant(ChatRoomViewController controller){//(String serverIP,String serverPort, SecretKey sessionKey){
        ServerAssistant.controller =controller;
    }

    @Override
    public void run() {
        generateSocket();
        try {
            generateSessionKey();
        } catch (IOException | ClassNotFoundException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
        messageReceiver=new MessageReceiver();

        try {
           SQLUtil.DBConnect();
         } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        getMessage();

    }

    private void generateSocket(){
        try {
            ServerSocket serverSocket=new ServerSocket(Integer.parseInt(SCSTChatApplication.port));
            socket = serverSocket.accept();
            controller.showPeerIp(socket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateSessionKey() throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, BadPaddingException {

        RSA rsa = new RSA();
        PublicKey serverPublicKey = rsa.getPublicKey();
        PrivateKey serverPrivateKey = rsa.getPrivateKey();
        System.out.println("\033[36mServer Public Key: \033[0m" + Base64.getEncoder().encodeToString(serverPublicKey.getEncoded()));
        System.out.println("\033[36mServer Private Key: \033[0m" + Base64.getEncoder().encodeToString(serverPrivateKey.getEncoded()));
        ObjectOutputStream serverPublicKeyStream = new ObjectOutputStream(socket.getOutputStream());
        serverPublicKeyStream.writeObject(serverPublicKey);
        serverPublicKeyStream.flush();
        System.out.println("\033[32mServer Public Key Sent\033[0m");

        DataInputStream  aseInfor= new DataInputStream(socket.getInputStream());
        int length = aseInfor.readInt();
        byte[] encryptedAESKey = new byte[length];
        aseInfor.readFully(encryptedAESKey);
        System.out.println("\033[36mEncrypted SessionKey Received from Client: \033[0m" + Base64.getEncoder().encodeToString(encryptedAESKey));
        byte[] decryptedAESKey = RSA.decrypt(encryptedAESKey, serverPrivateKey);
        System.out.println("\033[36mDecrypt Session Key:\033[0m " + Base64.getEncoder().encodeToString(decryptedAESKey));
        sessionKey=new SecretKeySpec(decryptedAESKey, "AES");
    }

    public void getMessage(){
        MessageReceiver.Callback callback = (type, cipher, message) -> {
            if(type.equals("FILE")){
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

    public void sendMessage(String message){
        System.out.println("\033[34mSending Message\033[0m");
        messageSender=new MessageSender(socket,message, sessionKey);
        new Thread(messageSender).start();
        String encryptedData;
        try {
            encryptedData = AES.encrypt(message, sessionKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        controller.showEncrytpedData(encryptedData);
    }

    public void sendFile(String filePath, String fileName){
        System.out.println("\033[34mSending File\033[0m");
        new Thread(new FileSender(socket,sessionKey,filePath,fileName)).start();
    }

}