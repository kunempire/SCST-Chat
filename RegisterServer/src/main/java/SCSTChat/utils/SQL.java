package SCSTChat.utils;

import java.net.InetAddress;
import java.sql.*;

public class SQL {
    private static final String URL = "jdbc:mysql://localhost:PORT/DATABASE";
    private static final String USER = "";
    private static final String PASSWORD = "";

    private Connection connection;

    public SQL() {
        try {
            // "mysql-connector-j-8.1.0.jar" is a library
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int register(String table, String name, String ip, int port) {
        String sql = "INSERT INTO " + table + " (name,ip,port,register_time) VALUES (?,?,?,?)";
        Timestamp registerTime = new Timestamp(System.currentTimeMillis());
        registerTime.setNanos (0);
        try {
            PreparedStatement registerStatement = connection.prepareStatement(sql);
            registerStatement.setString(1, name);
            registerStatement.setInt(2, ipToInt(ip));
            registerStatement.setInt(3, port);
            registerStatement.setTimestamp(4, registerTime);
            registerStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs = select("Users", "id", "register_time='" + registerTime.toString() + "'");
        int id = 0;
        try {
            while (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public ResultSet select(String table, String columns, String where) {
        String sql = "SELECT " + columns + " FROM " + table + " WHERE " + where;
        System.out.println(sql);
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(String table, String set, String where) {
        String sql = "UPDATE " + table + " SET " + set + " WHERE " + where;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String table, String where) {
        String sql = "DELETE FROM " + table + " WHERE " + where;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }  

    private static int ipToInt(String ip) {
        int ipAsInt = 0;
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            byte[] bytes = inetAddress.getAddress();
            ipAsInt = (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipAsInt;
    }
}