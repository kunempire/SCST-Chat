package SCSTChat.utils;

import javax.crypto.spec.SecretKeySpec;
import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SQLUtil {
    private static Connection connection;
    private static PreparedStatement insertStatement;
    private static final String tableName = "messages";
    private static final byte[] aesKeyBytes = "0123456789abcdef".getBytes(); // database key
    private static final SecretKeySpec DBKey = new SecretKeySpec(aesKeyBytes, "AES");

    public static void DBConnect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC"); // depend "sqlite-jdbc-3.39.4.1.jar"
        String url = "jdbc:sqlite:src/main/resources/database/chatroom.db";
        connection = DriverManager.getConnection(url);
        // create table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, cipher TEXT)";
        connection.createStatement().executeUpdate(createTableQuery);
        // insert statement
        String insertQuery = "INSERT INTO " + tableName + " (cipher) VALUES (?)";
        insertStatement = connection.prepareStatement(insertQuery);
    }

    public static void insertEncryptMessages(String decryptedData) throws Exception {
        String dataToDB = AES.encrypt(decryptedData, DBKey);
        insertStatement.setString(1, dataToDB);
        insertStatement.executeUpdate();
    }

    public static void closeDB() throws SQLException {
        connection.close();
    }

    public static ObservableList<String> getMessagesFromDB() {
        ObservableList<String> messages = FXCollections.observableArrayList();
        try {
            String url = "jdbc:sqlite:src/main/resources/chatroom.db";
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT cipher FROM messages");
            while (resultSet.next()) {
                String encryptedData = resultSet.getString("cipher");
                String message = AES.decrypt(encryptedData, DBKey);
                messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messages;
    }
}