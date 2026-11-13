package simple.dataaccess.jdbc;

import simple.dataaccess.config.Databaseconnection;
import java.sql.*;

public class jdbchej {

    public jdbchej() {

        System.out.println("HJE MED DIG HVOR ER DU1");
        Databaseconnection db = new Databaseconnection();

        try {
            Connection conn = db.getDbConnection();
            Statement st = conn.createStatement();
            String sql = "INSERT INTO offices (office_address, office_name) VALUES ('APPLE', 'ER SKRALD')";
            int kebab = st.executeUpdate(sql);
            System.out.println(kebab);
            

        } catch (SQLException e) {
            System.out.println(e);
        }

    }
}
