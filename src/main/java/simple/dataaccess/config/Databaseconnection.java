package simple.dataaccess.config;

import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class Databaseconnection {

    // Reads local .env values for development
    // or Azure environment variables when live.
    private final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    // Uses the local Docker database by default, but allows
    // overriding the URL through an environment variable.
    private final String url = dotenv.get(
            "DB_URL",
            "jdbc:postgresql://127.0.0.1:5432/dbs");

    // Get database username and password from environment variables
    // to avoid storing credentials in source code.
    private final String uname = dotenv.get("DB_USER");
    private final String pass = dotenv.get("DB_PASSWORD");

    public Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(url, uname, pass);
    }
}
