package SCSTChat.register;

import java.io.Serializable;
import java.security.PublicKey;

public class RequestForServer implements Serializable { // transport an object

    private String purpose;
    private int id;
    private String ip = "";
    private int port = 0;
    private PublicKey publicKey;
    private String name;

    public RequestForServer(String purpose, int id) {
        this.purpose = purpose;
        this.id = id;
    }

    public RequestForServer(String purpose, int id, String name, String ip, int port) {
        this(purpose, id);
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public RequestForServer(String purpose, int id, String name, String ip, int port, PublicKey publicKey) {
        this(purpose, id, name, ip, port);
        this.publicKey = publicKey;
    }

    public String getPurpose() {
        return purpose;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }


}
