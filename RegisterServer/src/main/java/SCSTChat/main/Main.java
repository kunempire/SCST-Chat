package SCSTChat.main;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        RegisterServer server = null;
        try {
            server = new RegisterServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.closeServerSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}