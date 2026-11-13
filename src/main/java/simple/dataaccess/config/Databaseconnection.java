package simple.dataaccess.config;

import java.sql.*;

public class Databaseconnection {

    private final String url = "jdbc:postgresql://127.0.0.1:5432/dbs";
    private final String uname = System.getenv("DB_USER");
    private final String pass = System.getenv("DB_PASSWORD");

    public Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(url, uname, pass);
    }
}
