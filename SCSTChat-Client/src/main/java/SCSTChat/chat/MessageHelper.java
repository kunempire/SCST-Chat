package SCSTChat.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MessageHelper {

    public String getTime() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedNow = sdf.format(now);
        return "[ " + formattedNow + " ]";
    }

    public AnchorPane creatMessageBox(String message, String time, String role) {
        AnchorPane an = new AnchorPane();
        an.setPrefWidth(590);

        Label timeLabel = new Label(time);
        timeLabel.setFont(new Font(12));
        timeLabel.setMaxWidth(400);
        AnchorPane.setTopAnchor(timeLabel, 5.0);

        Label msgLabel = new Label(message);
        msgLabel.setFont(new Font(16));
        msgLabel.setPadding(new Insets(5, 10, 5, 10));
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(400);
        msgLabel.setTextFill(Color.WHITE);
        AnchorPane.setTopAnchor(msgLabel, 25.0);

        ImageView headView = null;

        if (role.equals("ME")) {
            AnchorPane.setRightAnchor(timeLabel, 35.0);
            AnchorPane.setRightAnchor(msgLabel, 40.0);
            msgLabel.setTextAlignment(TextAlignment.RIGHT); // default=LEFT
            msgLabel.setStyle("-fx-background-color: rgba(0,86,31,0.75); -fx-background-radius: 16px;");
            headView = new ImageView(new Image("images/Me.png"));
            AnchorPane.setLeftAnchor(headView, 540.0);
        } else if (role.equals("PEER")) {
            AnchorPane.setLeftAnchor(timeLabel, 45.0);
            AnchorPane.setLeftAnchor(msgLabel, 50.0);
            msgLabel.setStyle("-fx-background-color: rgba(116,0,3,0.75); -fx-background-radius: 16px;");
            headView = new ImageView(new Image("images/Peer.png"));
            AnchorPane.setLeftAnchor(headView, 10.0);
        }
        headView.setFitWidth(30);
        headView.setFitHeight(30);
        AnchorPane.setTopAnchor(headView, 10.0);

        an.getChildren().addAll(headView, timeLabel, msgLabel);
        return an;
    }

    public AnchorPane createFileBox(String fileName,String time, String role){
        AnchorPane an = new AnchorPane();
        an.setPrefWidth(590);

        Label timeLabel = new Label(time);
        timeLabel.setFont(new Font(12));
        timeLabel.setMaxWidth(400);
        AnchorPane.setTopAnchor(timeLabel, 5.0);

        ImageView headView=null;

        AnchorPane subAn=new AnchorPane();
        subAn.setMaxWidth(400);
        subAn.setPadding(new Insets(5, 10, 5, 10));
        AnchorPane.setTopAnchor(subAn, 25.0);

        Label fileLabel = new Label(fileName);
        fileLabel.setFont(new Font(16));
        fileLabel.setPadding(new Insets(5, 10, 5, 10));
        fileLabel.setWrapText(true);
        fileLabel.setMaxWidth(400);
        AnchorPane.setTopAnchor(fileLabel,10.0);

        ImageView fileView=null;

        if (role.equals("ME")) {
            AnchorPane.setRightAnchor(timeLabel, 35.0);
            headView = new ImageView(new Image("images/Me.png"));
            AnchorPane.setLeftAnchor(headView, 540.0);

            AnchorPane.setRightAnchor(subAn, 40.0);
            fileLabel.setTextAlignment(TextAlignment.RIGHT);
            fileLabel.setTextFill(Color.color(0.0,0.22,0.122));
            AnchorPane.setRightAnchor(fileLabel,30.0);
            subAn.setStyle("-fx-background-color: rgba(0,86,31,0.25); -fx-background-radius: 16px;");
            fileView=new ImageView(new Image("images/发送文件.png"));
            AnchorPane.setRightAnchor(fileView,0.0);
        } else if (role.equals("PEER")) {
            AnchorPane.setLeftAnchor(timeLabel, 45.0);
            headView = new ImageView(new Image("images/Peer.png"));

            AnchorPane.setLeftAnchor(subAn, 50.0);
            fileLabel.setTextFill(Color.color(0.455,0.0,0.012));
            AnchorPane.setLeftAnchor(fileLabel,30.0);
            subAn.setStyle("-fx-background-color: rgba(116,0,3,0.25); -fx-background-radius: 16px;");
            fileView=new ImageView(new Image("images/接收文件.png"));
            AnchorPane.setLeftAnchor(headView, 10.0);
        }

        headView.setFitWidth(30);
        headView.setFitHeight(30);
        AnchorPane.setTopAnchor(headView, 10.0);
        fileView.setFitHeight(40);
        fileView.setFitWidth(40);
        AnchorPane.setTopAnchor(fileView, 10.0);

        subAn.getChildren().addAll(fileLabel,fileView);
        an.getChildren().addAll(headView, timeLabel, subAn);
        return an;
    }

}
