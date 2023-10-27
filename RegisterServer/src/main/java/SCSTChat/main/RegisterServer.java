package SCSTChat.main;

import java.net.*;
import java.io.*;

public class RegisterServer {
    public static final int PORT = 8000;
    public static final int MAX_QUEUE_LENGTH = 100;
    private ServerSocket serverSocket;

    public RegisterServer() throws IOException {

    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT, MAX_QUEUE_LENGTH);
            System.out.println("服务器已启动，等待客户端连接...");
            while (true) {
                Socket socket = serverSocket.accept(); // Listening
                Assistant assistant = new Assistant(socket);
                assistant.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() throws IOException {
        serverSocket.close();
    }
}