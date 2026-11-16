package simple;

import simple.dataaccess.config.Databaseconnection;
import simple.presentation.Server;

public class Main {
    public static void main(String[] args) {

        Databaseconnection db = new Databaseconnection();

        // Applies pending database migrations before starting the server.
        db.migrate();

        Server.start(7070);
    }
}
