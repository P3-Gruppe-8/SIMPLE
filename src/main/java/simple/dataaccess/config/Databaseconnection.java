package simple.dataaccess.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import org.flywaydb.core.Flyway;
import simple.domain.exception.DataAccessException;

public class Databaseconnection {

    // Reads local .env values for development
    // or Azure environment variables when live.
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    // Get database username, password and url from environment variables
    // to avoid storing credentials in source code.
    private final String username = dotenv.get("DB_USER");
    private final String password = dotenv.get("DB_PASSWORD");
    private final String url = dotenv.get("DB_URL");

    // Throws error if environment variables are not configured
    public Databaseconnection() {
        if (url == null
                || url.isBlank()
                || username == null
                || username.isBlank()
                || password == null
                || password.isBlank()) {
            throw new IllegalStateException("DB_URL, DB_USER and DB_PASSWORD must be configured.");
        }
    }

    // Applies pending database migrations before the server starts.
    public void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();
    }

    public Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public Connection newConnection(boolean autoCommit) {
        Databaseconnection db = new Databaseconnection();
        try {
            Connection conn = db.getDbConnection();
            conn.setAutoCommit(autoCommit);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException("Unable to connect to database", e);
        }
    }

    public Connection newConnection() {
        return newConnection(true);
    }

    public void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to close connection to database", e);
        }
    }

    public void executeRollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to perform rollback on database", e);
        }
    }

    public void executeCommit(Connection conn) {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to perform commit on database", e);
        }
    }
}
