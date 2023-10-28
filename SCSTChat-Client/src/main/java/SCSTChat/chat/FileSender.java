package SCSTChat.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import javax.crypto.SecretKey;

import SCSTChat.utils.AES;

public class FileSender implements Runnable {
    private final Socket socket;
    private final SecretKey sessionKey;
    private final String plainTextFile;
    private final String fileName;

    public FileSender(Socket socket, SecretKey sessionKey, String filePath, String fileName) {
        this.socket = socket;
        this.sessionKey = sessionKey;
        this.plainTextFile = new String(readFileData(filePath));
        this.fileName = fileName;
    }

    private byte[] readFileData(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        try {
            String encryptedFile = AES.encrypt(plainTextFile + "FILENAME:" + fileName, sessionKey);
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            String messagewithType = "FIL:" + encryptedFile;
            out.println(messagewithType);
            System.out.println("\033[32mFile sent\033[0m");
        } catch (Exception var4) {
            System.out.println("\033[31m!!PEER DISCONNECTED!!\033[0m");
            var4.printStackTrace();
            try {
                this.socket.close();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
    }
}
