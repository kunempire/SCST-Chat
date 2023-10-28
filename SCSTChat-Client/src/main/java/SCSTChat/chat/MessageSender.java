package SCSTChat.chat;

import java.io.PrintWriter;
import java.net.Socket;
import javax.crypto.SecretKey;

import SCSTChat.utils.AES;

public class MessageSender implements Runnable {
    private static Socket socket;
    private final String message;
    private static SecretKey sessionKey;

    public MessageSender(Socket socket, String message, SecretKey sessionKey) {
        MessageSender.socket = socket;
        this.message = message;
        MessageSender.sessionKey = sessionKey;
    }

    @Override
    public void run() {
        try {
            System.out.println("\033[34mEncrypting Data\033[0m");

            String encryptedData = AES.encrypt(this.message, sessionKey);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String messagewithType = "MSG:" + encryptedData;
            out.println(messagewithType);
            System.out.println("\033[32mMessage sent\033[0m");

        } catch (Exception var4) {
            System.out.println("\033[31m!!PEER DISCONNECTED!!\033[0m");
            var4.printStackTrace();
            try {
                socket.close();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
    }
}
