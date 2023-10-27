package SCSTChat.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import SCSTChat.register.*;
import SCSTChat.utils.SQL;

public class Assistant implements Runnable {
    private Socket socket;
    private Thread listener;
    private int clientId = 0;
    private String issue = "";
    private RequestForServer request;
    private ResponseFromServer response;
    private SQL mySql;

    public Assistant(Socket socket) {
        this.socket = socket;
        System.out.println("客户端已连接，IP地址为：" + socket.getInetAddress().getHostAddress());

    }

    public synchronized void start() {
        if (listener == null) {
            listener = new Thread(this);
            listener.start();
        }
    }

    public synchronized void stop() {
        if (listener != null) {
            try {
                listener.interrupt();
                listener = null;
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        mySql = new SQL();
        try {
            parseRequest();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!issue.isEmpty()) {
            if (issue.equals("REGISTER")) {
                register();
            } else if (issue.equals("SEARCH")) {
                search();
            } else if (issue.equals("DELETE")) {
                delete();
            }
        }
        sendResponse();
        stop();
    }

    private void parseRequest() throws ClassNotFoundException {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            request = (RequestForServer) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        issue = request.getPurpose();
        clientId = request.getId();
        System.out.println(issue + " from " + clientId);

    }

    private void register() {
        if (clientId != -1) {
            return;
        }
        clientId=mySql.register("Users", request.getName(), request.getIp(), request.getPort());
        System.out.println(clientId);
        response = new ResponseFromServer("Registered",clientId );
    }

    private void search() {
        System.out.println("SEARCH");
    }

    private void delete() {
        System.out.println("DELETE");
    }

    private void sendResponse() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
