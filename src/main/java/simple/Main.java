package simple;

import simple.dataaccess.config.Databaseconnection;
import simple.presentation.Server;

public class Main {
    public static void main(String[] args) {

        Databaseconnection db = new Databaseconnection();

        // Applies pending database migrations before starting the server.
        db.migrate();

        // Use port from environment variables if available,
        // Azure supplies this port because it is what it opens
        String portValue = System.getenv("PORT");
        int port = (portValue == null || portValue.isBlank())
                ? 7070
                : Integer.parseInt(portValue);

        Server.start(port);
    }
}
