package simple.dataaccess.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import org.flywaydb.core.Flyway;

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
}
