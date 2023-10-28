package SCSTChat.chat;

import java.io.*;
import java.net.Socket;
import javax.crypto.SecretKey;

import SCSTChat.utils.AES;
import SCSTChat.utils.SQLUtil;

public class MessageReceiver {
    private final String fileDirectory = "ReceivedFiles";
    private String cipher;
    private String message;


    public interface Callback {
        void onMessageReceived(String type, String cipher, String message);
    }

    public void listening(Callback callback, Socket socket, SecretKey sessionKey) {
        try {
            while (true) {
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String encryptedData = in.readLine();
                String flag = encryptedData.substring(0, 4);
                cipher = encryptedData.substring(4);
                message = AES.decrypt(cipher, sessionKey);

                if (flag.equals("MSG:")) {
                    SQLUtil.insertEncryptMessages(message);
                    callback.onMessageReceived("MSG", cipher, message);
                } else if (flag.equals("FIL:")) {
                    String fileName = String.valueOf(System.currentTimeMillis());

                    int index = message.indexOf("FILENAME");
                    if (index != -1) {
                        fileName = message.substring(index + "FILENAME:".length());  // 提取文件名
                    }
                    message = message.substring(0, index);

                    fileSave(fileName);
                    callback.onMessageReceived("FILE", "", fileName);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fileSave(String fileName) {
        File file = new File(fileDirectory, fileName);
        if (fileDirectory != null && fileName != null && !fileName.isEmpty()) {
            String fileName_cipher = fileName + ".cipher";
            File file_cipher = new File(fileDirectory + "/Encrypted", fileName_cipher);
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(message);
                writer.close();
                FileWriter writer_cipher = new FileWriter(file_cipher);
                writer_cipher.write(cipher);
                writer_cipher.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("\\033[31mInvalid file or directory.\\033[0m");
        }
    }


}
