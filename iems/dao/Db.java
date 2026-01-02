package iems.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private static Connection conn;

    public static Connection getConn() {
        return conn;
    }

    public static void setConn(Connection conn) {
        Db.conn = conn;
    }

    // Initialize MySQL connection
    public static void init(String url, String user, String pass) throws SQLException {
        conn = DriverManager.getConnection(url, user, pass);
    }

    public static Connection get() {
        return conn;
    }
}