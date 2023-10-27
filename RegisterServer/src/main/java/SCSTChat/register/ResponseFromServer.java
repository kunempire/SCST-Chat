package SCSTChat.register;

import java.io.Serializable;
import java.security.PublicKey;

public class ResponseFromServer implements Serializable{
    private String responseMsg="";
    private String name;
    private int id=0;
    private String ip="";
    private int port=0;
    private PublicKey publicKey;
    public ResponseFromServer(String responseMsg,int id){
        this.responseMsg=responseMsg;
        this.id=id;
    }

    public ResponseFromServer(String responseMsg,int id, String name,String ip,int port){
        this(responseMsg,id);
        this.ip=ip;
        this.port=port;
        this.name=name;
    }
    
    public ResponseFromServer(String purpose,int id, String name,String ip,int port,PublicKey publicKey){
        this(purpose,id,name,ip,port);
        this.publicKey=publicKey;
    }

    public String getResponseMsg(){
        return responseMsg;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getIp(){
        return ip;
    }
    public int getPort(){
        return port;
    }
    public PublicKey getPublicKey(){
        return publicKey;
    }
}
