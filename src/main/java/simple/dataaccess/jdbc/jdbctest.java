package simple.dataaccess.jdbc;

import simple.dataaccess.config.Databaseconnection;
import java.sql.*;

public class jdbctest {

    public jdbctest() {

        Databaseconnection db = new Databaseconnection();

        try {
            Connection conn = db.getDbConnection();
            Statement st = conn.createStatement();
            String sql = "INSERT INTO offices (office_address, office_name) VALUES ('Jernbanegade', 'Aalborg')";
            int rowsAffected = st.executeUpdate(sql);
            System.out.println("Rows affected: "+ rowsAffected);
            

        } catch (SQLException e) {
            System.out.println(e);
        }

    }
}
